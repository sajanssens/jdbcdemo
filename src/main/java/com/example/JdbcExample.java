package com.example;

import java.sql.*;

public class JdbcExample {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbcdemo", "root", "root");
        Statement statement = connection.createStatement();
        int i = statement.executeUpdate("insert into person VALUES ('Bram', 40)");
        System.out.println(i);
        ResultSet result = statement.executeQuery("SELECT * FROM PERSON");
        while (result.next()) {
            String name = result.getString("name");
            int age = result.getInt("age");

            System.out.println(name + " - " + age);

            Person p = new Person(name, age);
            System.out.println(p);

        }

        connection.close();
    }
}
