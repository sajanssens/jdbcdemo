package com.example.dao;

import com.example.domain.Gender;
import com.example.domain.Person;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.example.DatabaseProperties.get;
import static java.lang.IO.println;

public class PersonDao { // Repo

    public static void createDatabase() throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute("drop table if exists person;");
            statement.execute("drop table if exists gender;");
            statement.execute("create table person(name varchar(255) null, age int null, genderId int null);");
            statement.execute("create table gender(id int null, name varchar(255) null);");
            statement.execute("insert into gender(id, name) values (1, 'Man')");
            statement.execute("insert into gender(id, name) values (2, 'Vrouw')");
        }
    }

    public List<Person> getPersons() throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery("SELECT * from person");
            return toPersons(result);
        }
    }

    public List<Person> getPersonsWithGender() throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery("""
                    select person.name, person.age, gender.name
                    from person
                        join gender
                            on person.genderId = gender.id
                    where age > 0
                    """
            );

            return toPersons(result);
        }
    }

    public List<Person> getPersonsByName(String name) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("SELECT * from person where name LIKE ?")) {
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            return toPersons(result);
        }
    }

    public int insert(Person p) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("INSERT into person(name, age, genderId) VALUES (?, ?,  ?)")) {
            statement.setString(1, p.name);
            statement.setInt(2, p.age);
            statement.setInt(3, p.gender.ordinal() + 1);
            return statement.executeUpdate();
        }
    }

    public boolean remove(int id) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM person where id = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(get("database.url"), get("database.user"), get("database.password"));
    }

    private List<Person> toPersons(ResultSet result) throws SQLException {
        List<Person> persons = new ArrayList<>();
        while (result.next()) {
            String name = result.getString("person.name");
            int age = result.getInt("person.age");
            Gender gender = getGender(result);

            Person person = new Person(name, age, gender);
            persons.add(person);
        }
        return persons;
    }

    private Gender getGender(ResultSet result) {
        try {
            return Gender.valueOf(result.getString("gender.name"));
        } catch (SQLException e) {
            println("getGender - SQLException: " + e.getMessage());
            return Gender.Onbekend;
        }
    }
}
