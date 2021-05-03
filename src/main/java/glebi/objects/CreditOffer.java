package glebi.objects;

import glebi.helpers.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreditOffer {
    private String id;
    private BigDecimal creditSum;
    private Client client;
    private Credit credit;
    // Срок кредита в месяцах. Участвует в формировании графика выплат при создании предложения.
    // Никак не используется при парсинге экземпляра CreditOffer из базы данных.
    private int months;
    // Список с графиком выплат. Формируется из базы данных при его получении через геттер.
    private List<PaymentSchedule> paymentScheduleList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getCreditSum() {
        return creditSum;
    }

    public void setCreditSum(BigDecimal creditSum) { this.creditSum = creditSum; }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) { this.client = client; }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) { this.credit = credit; }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public List<PaymentSchedule> getPaymentScheduleList() {
        if (paymentScheduleList == null) {
            paymentScheduleList = getPaymentScheduleFromDB();
        }
        return paymentScheduleList;
    }

    /**
     * Возврат графика платежей из БД, учитывая id данного кредитного предложения.
     * @return График платежей (список).
     */
    private List<PaymentSchedule> getPaymentScheduleFromDB() {
        List<PaymentSchedule> scheduleListToReturn = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM \"PUBLIC\".\"PAYMENT_SCHEDULE\" WHERE \"CREDIT_OFFER_ID\" = '"+ this.id +"';");
            while (resultSet.next()) {
                scheduleListToReturn.add(processRow(resultSet));
            }
        } catch (SQLException e) {
            // если исключение выброшено в методе getConnection(), то информация о нём выведется два раза, т.к. в том методе есть такая же обработка
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(connection);
        }

        return scheduleListToReturn;
    }

    // Обработка строк, такая же как в DAO-классах.
    // По сути, внутри класса CreditOffer тоже сформировался мини-DAO для PaymentSchedule класса.
    private PaymentSchedule processRow(ResultSet resultSet) {
        PaymentSchedule schedule = null;
        try {
            LocalDate paymentDate = LocalDate.parse(resultSet.getString("payment_date"));
            BigDecimal paymentSum = resultSet.getBigDecimal("payment_sum");
            BigDecimal creditRepayment = resultSet.getBigDecimal("credit_repayment");
            BigDecimal interestRepayment = resultSet.getBigDecimal("interest_repayment");

            schedule = new PaymentSchedule(paymentDate, paymentSum, creditRepayment, interestRepayment);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return schedule;
    }

    /**
     * Формирование графика платежей по кредиту и занесение всех записей-платежей в базу данных.
     * Расчёт формул ведётся здесь и один раз в момент создания кредитного предложения (нужно вызвать этот метод явно).
     * Далее поле paymentScheduleList будет хранить в себе уже составленный график из базы данных.
     */
    public void insertPaymentScheduleToDB() {
        List<PaymentSchedule> scheduleList = formPaymentScheduleList(this.months);
        Connection connection = null;

        for (PaymentSchedule schedule : scheduleList) {
            String paymentDate = schedule.getPaymentDate().toString();
            String paymentSum = String.valueOf(schedule.getPaymentSum().doubleValue());
            String creditRepayment = String.valueOf(schedule.getCreditRepayment().doubleValue());
            String interestRepayment = String.valueOf(schedule.getInterestRepayment().doubleValue());

            try {
                connection = DBConnection.getConnection();
                Statement statement = connection.createStatement();

                int resultSet = statement.executeUpdate("INSERT INTO \"PUBLIC\".\"PAYMENT_SCHEDULE\"\n" +
                        "( \"ID\", \"PAYMENT_DATE\", \"PAYMENT_SUM\", \"CREDIT_REPAYMENT\", \"INTEREST_REPAYMENT\", \"CREDIT_OFFER_ID\")\n" +
                        "VALUES (UUID() , '"+ paymentDate +"', "+ paymentSum +", "+ creditRepayment +", "+ interestRepayment +", '"+ this.id +"');");
                if (resultSet == 1) {
                    System.out.println("Запись платежа добавлена!");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } finally {
                DBConnection.close(connection);
            }
        }
    }

    /**
     * Метод, заполняющий график платежей по кредиту и возвращающий его в виде списка.
     * @param months Срок, на который был взят кредит.
     * @return Список с информацией по выплате кредита, который пойдёт потом в БД.
     */
    private List<PaymentSchedule> formPaymentScheduleList(int months) {
        // Формулы взяты отсюда: https://www.raiffeisen.ru/wiki/kak-rasschitat-procenty-po-kreditu/
        List<PaymentSchedule> listToReturn = new ArrayList<>();

        // сотая часть процентной ставки (используется далее в формуле аннуитетного графика)
        double p = credit.getInterestRate().doubleValue() / 100 / credit.getInterestRate().doubleValue();
        // ежемесячный платёж
        double monthlyRepayment = creditSum.doubleValue() * (p + (p / (Math.pow(1 + p, months) - 1)));

        LocalDate paymentDate = LocalDate.now();
        double creditRepayment;
        double interestRepayment;
        double remainder = creditSum.doubleValue(); // здесь хранится остаток после ежемесячной оплаты
        for (int i = 0; i < months; ++i) {
            // формирование месячной статистики
            paymentDate = paymentDate.plusMonths(1);
            interestRepayment = remainder * p;
            creditRepayment = monthlyRepayment - interestRepayment;
            remainder -= creditRepayment;

            listToReturn.add(new PaymentSchedule(
                    paymentDate,
                    new BigDecimal(monthlyRepayment).setScale(2, RoundingMode.DOWN),
                    new BigDecimal(creditRepayment).setScale(2, RoundingMode.DOWN),
                    new BigDecimal(interestRepayment).setScale(2, RoundingMode.DOWN)
            ));
        }

        return listToReturn;
    }

    /**
     * Внутренний класс кредитного предложения, описывающий одну выплату из графика платежей.
     */
    public class PaymentSchedule {
        private LocalDate paymentDate; // дата платежа
        private BigDecimal paymentSum; // сумма платежа
        private BigDecimal creditRepayment; // сумма гашения тела кредита
        private BigDecimal interestRepayment; // сумма гашения процентов

        public PaymentSchedule(LocalDate paymentDate, BigDecimal paymentSum, BigDecimal creditRepayment, BigDecimal interestRepayment) {
            this.paymentDate = paymentDate;
            this.paymentSum = paymentSum;
            this.creditRepayment = creditRepayment;
            this.interestRepayment = interestRepayment;
        }

        public LocalDate getPaymentDate() {
            return paymentDate;
        }

        public BigDecimal getPaymentSum() {
            return paymentSum;
        }

        public BigDecimal getCreditRepayment() {
            return creditRepayment;
        }

        public BigDecimal getInterestRepayment() {
            return interestRepayment;
        }
    }
}