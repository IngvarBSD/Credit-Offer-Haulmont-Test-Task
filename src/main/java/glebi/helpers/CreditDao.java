package glebi.helpers;

import glebi.objects.Credit;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreditDao implements Dao<Credit> {
    private List<Credit> credits = new ArrayList<>();
    private Connection connection;

    @Override
    public Optional<Credit> get(String id) {
        fillCreditsList();

        int idToGetFromList;
        for (Credit credit : credits) {
            if (credit.hashCode() == id.hashCode()) {
                return Optional.of(credit);
            }
        }
        return Optional.empty();
    }

    /**
     * Метод производит заполнение списка экземпляров Credit данными из БД.
     */
    private void fillCreditsList() {
        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM \"PUBLIC\".\"CREDIT\"");
            while (resultSet.next()) {
                credits.add(processRow(resultSet));
            }
        } catch (SQLException e) {
            // если исключение выброшено в методе getConnection(), то информация о нём выведется два раза, т.к. в том методе есть такая же обработка
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(connection);
        }
    }

    /**
     * Разбор ResultSet'а в экземпляр Credit
     * @param resultSet Резалт сет запроса
     * @return Заполненный экземпляр Credit
     */
    private Credit processRow(ResultSet resultSet) {
        Credit credit = new Credit();
        try {
            credit.setId(resultSet.getString("id"));
            credit.setCreditLimit(resultSet.getBigDecimal("credit_limit"));
            credit.setInterestRate(resultSet.getBigDecimal("interest_rate"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return credit;
    }

    @Override
    public List<Credit> getAll() {
        fillCreditsList();
        return this.credits;
    }

    @Override
    public void insert(Credit creditToInsert) {
        String creditLimit = String.valueOf(creditToInsert.getCreditLimit().doubleValue());
        String interestRate = String.valueOf(creditToInsert.getInterestRate().doubleValue());

        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();

            int resultSet = statement.executeUpdate("INSERT INTO \"PUBLIC\".\"CREDIT\"\n" +
                    "( \"ID\", \"CREDIT_LIMIT\", \"INTEREST_RATE\" )\n" +
                    "VALUES (UUID() , "+ creditLimit +", "+ interestRate +");");
            if (resultSet == 1) {
                System.out.println("Запись кредита добавлена!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(connection);
        }
    }

    @Override
    public void delete(Credit creditToDelete) throws SQLIntegrityConstraintViolationException {
        String id = creditToDelete.getId();
        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();

            int resultSet = statement.executeUpdate("DELETE FROM \"PUBLIC\".\"CREDIT\" WHERE \"ID\" = '"+ id +"';");
            if (resultSet == 1) {
                System.out.println("Запись кредита удалена!");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Нельзя удалить данную запись, так как она связана с кредитным предложением!", ButtonType.CLOSE);
            alert.showAndWait();
            throw e;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(connection);
        }
    }

    /**
     * На вход должен приходить экземпляр Credit с прежним id, но с обновлёнными атрибутами
     */
    @Override
    public void update(Credit credit) {
        String id = credit.getId();
        String creditLimit = String.valueOf(credit.getCreditLimit().doubleValue());
        String interestRate = String.valueOf(credit.getInterestRate().doubleValue());

        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();

            int resultSet = statement.executeUpdate("UPDATE \"PUBLIC\".\"CREDIT\" SET \"CREDIT_LIMIT\" = "+ creditLimit +", " +
                    "\"INTEREST_RATE\" = "+ interestRate +" WHERE \"ID\" = '"+ id +"';");
            if (resultSet == 1) {
                System.out.println("Запись кредита обновлена!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(connection);
        }
    }
}
