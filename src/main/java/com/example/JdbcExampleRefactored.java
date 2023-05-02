package com.example;

import com.example.dao.PersonDao;
import com.example.domain.Person;

import java.sql.SQLException;

import static com.example.domain.Gender.Vrouw;

public class JdbcExampleRefactored {
    public static void main(String[] args) {
        PersonDao personDao = new PersonDao(); // data access object

        try {
            personDao.createDatabase();
            personDao.getPersons().forEach(System.out::println);
            personDao.getPersonsWithGender().forEach(System.out::println);
            personDao.getPersonsByName("Bram").forEach(System.out::println);
            int mieke = personDao.insert(new Person("Mieke", 40, Vrouw));
            System.out.println(mieke);
        } catch (SQLException e) {
            System.err.println("Query ging fout: " + e.getMessage());
        }
    }
}
