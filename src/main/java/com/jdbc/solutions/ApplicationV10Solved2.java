package com.jdbc.solutions;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ApplicationV10Solved2 {

    public static class User {
        String firstName, lastName;
        LocalDateTime registrationDate;

        public User(String firstName, String lastName,
                    LocalDateTime registrationDate) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.registrationDate = registrationDate;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                    .add("firstName='" + firstName + "'")
                    .add("lastName='" + lastName + "'")
                    .add("registrationDate=" + registrationDate)
                    .toString();
        }
    }

    public static void main(String[] args) throws SQLException {
        DataSource ds = createDataSource();

        List<User> users = new ArrayList<>();

        try (Connection connection = ds.getConnection()) {

            try (PreparedStatement stmt = connection.prepareStatement("select * " +
                    "from users")) {

                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    LocalDateTime registrationDate = resultSet.getObject(
                            "registration_date", LocalDateTime.class);

                    User u = new User(firstName, lastName,
                            registrationDate);
                    users.add(u);

                    System.out.println("found user = " + u);
                }
            }
        }

        System.out.println("users.size() = " + users.size());
    }

    private static DataSource createDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:~/mydatabase;INIT=RUNSCRIPT FROM 'classpath:schema" +
                ".sql'");
        ds.setUsername("sa");
        ds.setPassword("s3cr3tPassword");
        return ds;
    }
}
