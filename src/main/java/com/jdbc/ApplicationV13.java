package com.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ApplicationV13 {

    public static void main(String[] args) throws SQLException {

        DataSource ds = createDataSource();

        try (Connection connection = ds.getConnection()) {

            try (PreparedStatement stmt = connection.prepareStatement("select * from users where id > ?")) {
                stmt.setInt(1,5);

                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    // do something useful here.
                    System.out.println(resultSet.getInt("id"));
                }

            }
        }
    }

    private static DataSource createDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:~/mydatabase;INIT=RUNSCRIPT FROM 'classpath:schema.sql'");
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
