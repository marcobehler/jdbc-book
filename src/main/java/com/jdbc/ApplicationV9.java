package com.jdbc;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Arrays;

public class ApplicationV9 {

    public static void main(String[] args) throws SQLException {

        DataSource ds = createDataSource();

        try (Connection connection = ds.getConnection()) {

            try (PreparedStatement stmt = connection.prepareStatement("insert into users (first_name, last_name, registration_date) values (?,?,?)"
                                                    , Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, "[Some FirstName]");
                stmt.setString(2, "[Some LastName]");
                stmt.setObject(3, LocalDateTime.now());

                stmt.addBatch();

                // stmt.clearParameters();

                stmt.setString(1, "[Some Other FirstName]");
                stmt.setString(2, "[Some Other LastName]");
                stmt.setObject(3, LocalDateTime.now());

                stmt.addBatch();

                final int[] insertCounts = stmt.executeBatch();
                System.out.println("I inserted " + Arrays.toString(insertCounts) + " rows");
            }
        }
    }

    private static DataSource createDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:~/mydatabase;INIT=RUNSCRIPT FROM 'classpath:schema.sql'");
        ds.setUsername("sa");
        ds.setPassword("s3cr3tPassword");
        return ds;
    }
}
