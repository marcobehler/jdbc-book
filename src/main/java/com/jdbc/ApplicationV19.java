package com.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

public class ApplicationV19 {

    public static void main(String[] args) throws SQLException {
        // read uncommitted

        DataSource ds = createDataSource();

        int senderId = -1;
        int receiverId = -1;

        Connection connection1 = ds.getConnection();
        try (connection1) {
            connection1.setAutoCommit(false);

            senderId = createUser(connection1);
            receiverId = createUser(connection1);

            connection1.commit();
        } catch (SQLException e) {
            connection1.rollback();
        }


        Connection connection2 = ds.getConnection();

        try (connection2) {
            connection2.setAutoCommit(false);

            Connection connection3 = ds.getConnection();
            try (connection3) {
                connection3.setAutoCommit(false);
                connection3.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

                Integer connection3BalanceBefore = getBalance(connection3, senderId);
                System.out.println("connection3BalanceBefore = "
                        + connection3BalanceBefore);

                try (PreparedStatement stmt = connection2.prepareStatement(
                        "update users set balance = (balance - ?) where id = ?")) {
                    stmt.setInt(1, 99);
                    stmt.setInt(2, senderId);
                    stmt.executeUpdate();
                }
                // TODO update into receiver's balance
                // TODO insert into transactions table

                Integer connection3BalanceAfter = getBalance(connection3, senderId);
                System.out.println("connection3BalanceAfter = "
                        + connection3BalanceAfter);
                connection3.commit();
            }
            connection2.commit();

        } catch (SQLException e) {
            connection2.rollback();
        }
    }

    private static int createUser(Connection connection) throws SQLException {
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


    private static Integer getBalance(Connection connection, int userId) throws SQLException {
        Integer balance = null;

        try (PreparedStatement stmt = connection.prepareStatement(
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
