package com.jdbc.solutions;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

public class ApplicationV99Solved {

    public static void main(String[] args) throws SQLException {
        DataSource ds = createDataSource();

        Connection connection = ds.getConnection();

        int senderId = createUser(connection);  // default balance = 100
        int receiverId = createUser(connection); // default balance = 100
        int amount = 99;
                
        try (connection) {
            connection.setAutoCommit(false);
            
            try (PreparedStatement stmt = connection.prepareStatement("insert into " +
                            "transfers (sender, receiver, amount, reference_id) " +
                            "values (?,?,?,?)"
                    , Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, senderId);
                stmt.setInt(2, receiverId);
                stmt.setInt(3, amount);
                stmt.setString(4, "SOME_REF_ID_10");
                stmt.executeUpdate();
            }

            Connection connection2 = ds.getConnection();

            try (connection2) {
                connection2.setAutoCommit(false);

                try (PreparedStatement stmt2 = connection2.prepareStatement("insert into " +
                                "transfers (sender, receiver, amount, reference_id) " +
                                "values (?,?,?,?)"
                        , Statement.RETURN_GENERATED_KEYS)) {
                    stmt2.setInt(1, senderId);
                    stmt2.setInt(2, receiverId);
                    stmt2.setInt(3, amount);
                    stmt2.setString(4, "SOME_REF_ID_10");
                    stmt2.executeUpdate();
                }
                connection2.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                connection2.rollback();
            }

            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        }
    }

    private static DataSource createDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:~/mydatabase");
        ds.setUsername("sa");
        ds.setPassword("s3cr3tPassword");
        return ds;
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
}