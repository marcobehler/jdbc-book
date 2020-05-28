package com.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

public class ApplicationV21 {

    private static DataSource ds = createDataSource();

    public static void main(String[] args) throws SQLException {
        // database locking

        int senderId = createUser();  // default balance = 100
        int transactionAmount = 50;

        Connection connection = ds.getConnection();
        try (connection) {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(
                    "update users set balance = (balance - ?) where id = ?")) {
                stmt.setInt(1, transactionAmount);
                stmt.setInt(2, senderId);
                stmt.executeUpdate();
            }

            Connection connection2 = ds.getConnection();
            try (connection2) {
                connection2.setAutoCommit(false);

                try (PreparedStatement stmt = connection2.prepareStatement(
                        "update users set balance = (balance - ?) where id = ?")) {
                    stmt.setInt(1, transactionAmount);
                    stmt.setInt(2, senderId);
                    stmt.executeUpdate();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                connection2.rollback();
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        }


        int senderBalance = getBalance(senderId);
        System.out.println("senderBalance = " + senderBalance);
    }

    private static Integer getBalance(int userId) throws SQLException {
        Connection connection = ds.getConnection();
        Integer balance = null;

        try (connection; PreparedStatement stmt = connection.prepareStatement(
                "select balance " +
                "from users where id = ?")) {

            stmt.setInt(1, userId);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                balance = resultSet.getInt("balance");
                break;
            }
        }
        return balance;
    }


    private static int createUser() throws SQLException {
        Connection connection = ds.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement("insert into " +
                        "users (first_name, last_name, registration_date) values " +
                        "(?,?,?)"
                , Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "[Some FirstName]");
            stmt.setString(2, "[Some LastName]");
            stmt.setObject(3, LocalDateTime.now());
            stmt.executeUpdate();

            final ResultSet keysResultSet = stmt.getGeneratedKeys();
            keysResultSet.next();
            return keysResultSet.getInt(1);
        }
    }

    private static int sendMoney(Connection connection, int senderId,
                                 int receiverId, int amount,
                                 Runnable parallelAction) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("update users " +
                "set balance = (balance - ?) where id = ?")) {
            stmt.setInt(1, amount);
            stmt.setInt(2, senderId);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = connection.prepareStatement("update users " +
                "set balance = (balance + ?) where id = ?")) {
            stmt.setInt(1, amount);
            stmt.setInt(2, receiverId);
            stmt.executeUpdate();
        }

        parallelAction.run();

        try (PreparedStatement stmt = connection.prepareStatement("insert into " +
                        "transactions (sender, receiver, amount) values (?,?,?)"
                , Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setInt(3, amount);
            stmt.executeUpdate();

            final ResultSet keysResultSet = stmt.getGeneratedKeys();
            keysResultSet.next();
            return keysResultSet.getInt(1);
        }
    }

    private static DataSource createDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:~/mydatabase");
        ds.setUsername("sa");
        ds.setPassword("s3cr3tPassword");

        DataSource dataSource =
                ProxyDataSourceBuilder.create(ds)  // pass original datasource
                        .logQueryToSysOut()
                        // .logQueryByJUL()
                        //.logQueryBySlf4j()
                        //.logQueryByCommons()
                        .build();

        return dataSource;
    }
}
