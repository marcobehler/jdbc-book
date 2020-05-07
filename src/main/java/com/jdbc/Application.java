package com.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Application {

    public static void main(String[] args) throws SQLException {

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:")) {
            System.out.println("connection.isValid(0) = " + connection.isValid(0));
        }
    }
}
