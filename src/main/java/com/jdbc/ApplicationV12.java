package com.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariProxyPreparedStatement;
import org.h2.jdbc.JdbcPreparedStatement;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

public class ApplicationV12 {

    public static void main(String[] args) throws SQLException {

        DataSource ds = createDataSource();

        try (Connection connection = ds.getConnection()) {

            try (PreparedStatement stmt = connection.prepareStatement("insert into users (first_name, last_name, registration_date) values (?,?,?)"
                                                    , Statement.RETURN_GENERATED_KEYS)) {


            }

            try (PreparedStatement stmt = connection.prepareStatement("insert into users (first_name, last_name, registration_date) values (?,?,?)"
                    , Statement.RETURN_GENERATED_KEYS)) {


                // depending on your database driver and configuration,
                // this will return the same / a cached preparedstatem[ent
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
