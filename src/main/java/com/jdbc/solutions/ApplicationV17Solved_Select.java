package com.jdbc.solutions;

import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

public class ApplicationV17Solved_Select {

    public static void main(String[] args) throws SQLException {
        DataSource ds = createDataSource();

        Integer userId1 = 342; // put your own ids here
        Integer userId2 = 343; // put your own ids here

        try (Connection connection = ds.getConnection()) {

            try (PreparedStatement stmt = connection.prepareStatement("select * " +
                    "from users where id in (?,?)")) {

                stmt.setInt(1, userId1);
                stmt.setInt(2, userId2);

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


    private static DataSource createDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:~/mydatabase;INIT=RUNSCRIPT FROM 'classpath:schema" +
                ".sql'");
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
