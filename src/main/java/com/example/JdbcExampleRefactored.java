package com.example;

import com.example.dao.PersonDao;
import com.example.domain.Person;

import java.sql.SQLException;

import static com.example.domain.Gender.Vrouw;
import static java.lang.IO.println;

public class JdbcExampleRefactored {
    void main() {
        try {
            PersonDao.createDatabase();

            PersonDao personDao = new PersonDao(); // data access object
            int insertCount = personDao.insert(new Person("Mieke", 40, Vrouw));
            println(insertCount);
            personDao.getPersons().forEach(IO::println);
            personDao.getPersonsWithGender().forEach(IO::println);
            personDao.getPersonsByName("Bram").forEach(IO::println);
            boolean removed = personDao.remove(1);
            println("Remove succeeded? " + removed);
        } catch (SQLException e) {
            System.err.println("Query ging fout: " + e.getMessage());
        }
    }
}
