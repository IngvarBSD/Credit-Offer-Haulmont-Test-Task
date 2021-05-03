package glebi.helpers;

import glebi.objects.Client;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ClientDao implements Dao<Client> {
    private List<Client> clients = new ArrayList<>();
    private Connection connection;

    @Override
    public Optional<Client> get(String id) {
        fillClientsList();

        int idToGetFromList;
        for (Client client : clients) {
            if (client.hashCode() == id.hashCode()) {
                return Optional.of(client);
            }
        }
        return Optional.empty();
    }

    /**
     * Метод производит заполнение списка экземпляров Client данными из БД.
     */
    private void fillClientsList() {
        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM \"PUBLIC\".\"CLIENT\"");
            while (resultSet.next()) {
                clients.add(processRow(resultSet));
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
     * Разбор ResultSet'а в экземпляр Client
     * @param resultSet Резалт сет запроса
     * @return Заполненный экземпляр Client
     */
    private Client processRow(ResultSet resultSet) {
        Client client = new Client();
        try {
            client.setId(resultSet.getString("id"));
            client.setFio(resultSet.getString("fio"));
            client.setTelephoneNumber(resultSet.getString("telephone_number"));
            client.setEmail(resultSet.getString("email"));
            client.setPassportNumber(resultSet.getString("passport_number"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return client;
    }

    @Override
    public List<Client> getAll() {
        fillClientsList();
        return this.clients;
    }

    @Override
    public void insert(Client clientToInsert) {
        String fio = clientToInsert.getFio();
        String telephoneNumber = clientToInsert.getTelephoneNumber();
        String email = clientToInsert.getEmail();
        String passportNumber = clientToInsert.getPassportNumber();

        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();

            int resultSet = statement.executeUpdate("INSERT INTO \"PUBLIC\".\"CLIENT\"\n" +
                    "( \"ID\", \"FIO\", \"TELEPHONE_NUMBER\", \"EMAIL\", \"PASSPORT_NUMBER\" )\n" +
                    "VALUES (UUID() , '"+ fio +"', '"+ telephoneNumber +"', '"+ email +"', '"+ passportNumber +"');");
            if (resultSet == 1) {
                System.out.println("Запись клиента добавлена!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(connection);
        }
    }

    @Override
    public void delete(Client clientToDelete) throws SQLIntegrityConstraintViolationException {
        String id = clientToDelete.getId();
        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();

            int resultSet = statement.executeUpdate("DELETE FROM \"PUBLIC\".\"CLIENT\" WHERE \"ID\" = '"+ id +"';");
            if (resultSet == 1) {
                System.out.println("Запись клиента удалена!");
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
     * На вход должен приходить экземпляр Client с прежним id, но с обновлёнными атрибутами
     */
    @Override
    public void update(Client client) {
        String id = client.getId();
        String fio = client.getFio();
        String telephoneNumber = client.getTelephoneNumber();
        String email = client.getEmail();
        String passportNumber = client.getPassportNumber();

        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();

            int resultSet = statement.executeUpdate("UPDATE \"PUBLIC\".\"CLIENT\" SET \"FIO\" = '"+ fio +"', " +
                    "\"TELEPHONE_NUMBER\" = '"+ telephoneNumber +"', \"EMAIL\" = '"+ email +"', " +
                    "\"PASSPORT_NUMBER\" = '"+ passportNumber +"' WHERE \"ID\" = '"+ id +"';");
            if (resultSet == 1) {
                System.out.println("Запись клиента обновлена!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(connection);
        }
    }
}
