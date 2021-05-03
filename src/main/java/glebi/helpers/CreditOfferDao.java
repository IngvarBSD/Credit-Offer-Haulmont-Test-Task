package glebi.helpers;

import glebi.objects.CreditOffer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CreditOfferDao implements Dao<CreditOffer> {
    private List<CreditOffer> offers = new ArrayList<>();
    private Connection connection;

    @Override
    public Optional<CreditOffer> get(String id) {
        fillCreditsList();

        int idToGetFromList;
        for (CreditOffer offer : offers) {
            if (offer.hashCode() == id.hashCode()) {
                return Optional.of(offer);
            }
        }
        return Optional.empty();
    }

    /**
     * Метод производит заполнение списка экземпляров CreditOffer данными из БД.
     */
    private void fillCreditsList() {
        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM \"PUBLIC\".\"CREDIT_OFFER\"");
            while (resultSet.next()) {
                offers.add(processRow(resultSet));
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
     * Разбор ResultSet'а в экземпляр CreditOffer
     * @param resultSet Резалт сет запроса
     * @return Заполненный экземпляр CreditOffer
     */
    private CreditOffer processRow(ResultSet resultSet) {
        ClientDao clientDao = new ClientDao();
        CreditDao creditDao = new CreditDao();

        CreditOffer offer = new CreditOffer();
        try {
            offer.setId(resultSet.getString("id"));
            offer.setCreditSum(resultSet.getBigDecimal("credit_sum"));
            offer.setClient(clientDao.get(resultSet.getString("client_id")).get());
            offer.setCredit(creditDao.get(resultSet.getString("credit_id")).get());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return offer;
    }

    @Override
    public List<CreditOffer> getAll() {
        fillCreditsList();
        return this.offers;
    }

    @Override
    public void insert(CreditOffer offerToInsert) {
        String creditSum = String.valueOf(offerToInsert.getCreditSum().doubleValue());
        String clientId = offerToInsert.getClient().getId();
        String creditId = offerToInsert.getCredit().getId();
        // Генерация UUID в Java, а не в HSQL, чтобы потом передать его в экземпляр CreditOffer для
        // создания графика палтежей.
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        offerToInsert.setId(uuidAsString);

        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();

            int resultSet = statement.executeUpdate("INSERT INTO \"PUBLIC\".\"CREDIT_OFFER\"\n" +
                    "( \"ID\", \"CREDIT_SUM\", \"CLIENT_ID\", \"CREDIT_ID\" )\n" +
                    "VALUES ('"+ uuidAsString +"' , "+ creditSum +", '"+ clientId +"', '"+ creditId +"');");
            if (resultSet == 1) {
                System.out.println("Запись кредитного предложения добавлена!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(connection);
        }
    }

    @Override
    public void delete(CreditOffer offerToDelete) {
        String id = offerToDelete.getId();
        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();

            int resultSet = statement.executeUpdate("DELETE FROM \"PUBLIC\".\"CREDIT_OFFER\" WHERE \"ID\" = '"+ id +"';");
            if (resultSet == 1) {
                System.out.println("Запись кредитного предложения удалена!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(connection);
        }
    }

    /**
     * На вход должен приходить экземпляр CreditOffer с прежним id, но с обновлёнными атрибутами.
     * Этот метод не подразумевается к использованию, просто заглушка к интерфейсу.
     */
    @Override
    public void update(CreditOffer offer) { }
}
