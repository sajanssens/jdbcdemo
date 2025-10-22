package com.example;

import com.example.dao.PersonDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.IO.println;

public class JdbcExample {
    void main() throws SQLException, ClassNotFoundException {
        PersonDao.createDatabase();

        // 1
        Class.forName("com.mysql.cj.jdbc.Driver"); // not necessary since Java 6 (with the SPI)

        // 2
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbcdemo", "root", "root");

        // 3
        Statement statement = connection.createStatement();

        // 4
        ResultSet result = statement.executeQuery("SELECT * FROM person");

        // 5
        while (result.next()) {
            String name = result.getString("name");
            int age = result.getInt("age");

            println(name + " - " + age);
        }

        // 6
        // result.close(); // done by:
        statement.close();
        connection.close();
    }
}
