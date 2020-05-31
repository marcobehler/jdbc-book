package com.jdbc.solutions;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationV10Solved {

    public static void main(String[] args) throws SQLException {
        DataSource ds = createDataSource();

        final List<Integer> ids = List.of(1, 2, 3, 4, 5);

        try (Connection connection = ds.getConnection()) {

            try (PreparedStatement stmt = connection.prepareStatement("select * " +
                    "from users where id in (" + expandQuestionMarks(ids) + ")")) {

                setParams(stmt, ids);

                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    LocalDateTime registrationDate = resultSet.getObject(
                            "registration_date", LocalDateTime.class);

                    System.out.println("Found user: " + id + " | " + firstName +
                            " | " + lastName + " | " + registrationDate);
                }
            }
        }
    }

    /**
     * Will turn an array of, e.g. 1,2,3,4 -> ?,?,?,?
     */
    public static String expandQuestionMarks(List<?> params) {
        return params.stream().map(i -> "?").collect(Collectors.joining(","));
    }

    public static void setParams(PreparedStatement stmt, List<Integer> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            stmt.setInt(i + 1, params.get(i));
        }
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
