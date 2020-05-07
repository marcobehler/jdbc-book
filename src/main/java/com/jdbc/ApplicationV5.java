package com.jdbc;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ApplicationV5 {

    public static void main(String[] args) throws SQLException {

        DataSource ds = createDataSource();

        try (Connection connection = ds.getConnection()) {
            System.out.println("connection.isValid(0) = " + connection.isValid(0));
        }
    }

    private static DataSource createDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:~/mydatabase");
        ds.setUser("sa");
        ds.setPassword("s3cr3tPassword");
        return ds;
    }
}
