package glebi.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    // lock_file=false (неблокирующий доступ к файлу БД, чтобы можно было тестировать программу с одновременно запущенной утилитой HSQL DB Manager)
    private final String url = "jdbc:hsqldb:file:src/main/resources/bank-db/bank;hsqldb.lock_file=false";
    private final String login = "SA";
    private final String password = "";

    private DBConnection() { };

    public static Connection getConnection() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
        }

        try {
            return DriverManager.getConnection(instance.url, instance.login, instance.password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
