package com.jdbc;

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ApplicationV5 {

    public static void main(String[] args) throws SQLException {

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:~/mydatabase");
        ds.setUser("sa");
        ds.setPassword("s3cr3tPassword");

        try (Connection connection = ds.getConnection()) {
            System.out.println("connection.isValid(0) = " + connection.isValid(0));
        }
    }
}
