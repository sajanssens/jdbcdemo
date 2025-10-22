package com.example;

import com.example.dao.PersonDao;
import com.example.domain.Person;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

import static java.lang.IO.println;

public class JdbcExampleExtended {

    private final Random random = new Random();

    void main() {
        new JdbcExampleExtended().start();
    }

    private void start() {

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbcdemo", "root", "root");
             Statement statementFWD = connection.createStatement();
             Statement statementSCROLL = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            PersonDao.createDatabase();
            var randomAge = random.nextInt(100);

            deleteSomeRows(connection, randomAge);
            insertSomeRows(statementFWD, randomAge);
            insertSomeRowsTransactional(connection, randomAge, statementFWD);
            showSomeData(statementSCROLL);
            showSomeDatabaseMetadata(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSomeRows(Connection connection, int randomAge) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("delete from person where age>?");
        preparedStatement.setInt(1, randomAge);
        int deleted = preparedStatement.executeUpdate();
        println("Rows deleted: " + deleted);
        preparedStatement.close();
    }

    private void insertSomeRows(Statement statement, int randomAge) throws SQLException {
        int i = statement.executeUpdate("insert into person VALUES ('Bram', " + randomAge + ", 1)");
        println("Rows i inserted: " + i);
    }

    private void insertSomeRowsTransactional(Connection connection, int randomAge, Statement statement) throws SQLException {
        try {
            connection.setAutoCommit(false);

            int i = statement.executeUpdate("insert into person VALUES ('Bram', " + randomAge + ", 1)");
            println("Rows i inserted: " + i);

            // typo in query: abort transaction via catch --> rollback
            int j = statement.executeUpdate("ins ert into person VALUES ('Bram', " + randomAge + ", 1)");
            println("Rows j inserted: " + j);

            connection.commit(); // if everything was ok.
        } catch (SQLException e) {
            System.err.println("Transaction aborted. Something in the database went wrong: " + e.getMessage());
            connection.rollback(); // if something went wrong
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void showSomeData(Statement statement) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT * FROM person");

        showMetadata(result);
        List<Person> persons = showData(result);
        showPersons(persons);

        // rewind, retry:
        result.beforeFirst();
        showData(result);
    }

    private void showMetadata(ResultSet result) throws SQLException {
        ResultSetMetaData metaData = result.getMetaData();
        String catalogName = metaData.getCatalogName(1);
        String tableName = metaData.getTableName(1);
        println("Database: " + catalogName);
        println("Table: " + tableName);

        StringJoiner columnJoiner = new StringJoiner(", ");
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            JDBCType jdbcType = JDBCType.valueOf(metaData.getColumnType(i));
            String newElement = metaData.getColumnName(i) + " (" + jdbcType + ")";
            columnJoiner.add(newElement);
        }
        println("Columns: " + columnJoiner);
    }

    private List<Person> showData(ResultSet result) throws SQLException {
        println("Rows:");
        List<Person> persons = new ArrayList<>();
        while (result.next()) {
            String name = result.getString("name");
            int age = result.getInt("age");
            persons.add(new Person(name, age));
            println("\t" + name + ", " + age);
        }
        return persons;
    }

    private void showPersons(List<Person> persons) {
        println("Persons:");
        for (Person person : persons) {
            println("\t" + person);
        }
    }

    private void showSomeDatabaseMetadata(Connection connection) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        String databaseProductName = databaseMetaData.getDatabaseProductName();
        ResultSet result = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
        while (result.next()) {
            String name = result.getString(1);
            println(name);
        }
        println("DatabaseProductName: " + databaseProductName);
    }
}
