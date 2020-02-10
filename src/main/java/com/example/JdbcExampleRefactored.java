package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class JdbcExampleRefactored {
    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbcdemo", "root", "root");
             Statement statement = connection.createStatement()) {
            int randomAge = (int) (Math.random() * 100);
            statement.executeUpdate("insert into PERSON VALUES ('Bram', " + randomAge + ")");
            ResultSet result = statement.executeQuery("SELECT * FROM PERSON");

            ResultSetMetaData metaData = result.getMetaData();
            String catalogName = metaData.getCatalogName(1);
            String tableName = metaData.getTableName(1);
            System.out.println("Database: " + catalogName);
            System.out.println("Table: " + tableName);

            StringJoiner columnJoiner = new StringJoiner(", ");
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnJoiner.add(metaData.getColumnName(i));
            }
            System.out.println("Columns: " + columnJoiner);

            System.out.println("Rows:");
            List<Person> persons = new ArrayList<>();
            while (result.next()) {
                String name = result.getString("name");
                int age = result.getInt("age");
                persons.add(new Person(name, age));
                System.out.println(name + ", " + age);
            }

            System.out.println("Persons:");
            for (Person person : persons) {
                System.out.println(person);
            }
        }
    }
}
