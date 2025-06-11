package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class App {

    static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        // Class.forName("com.mysql.cj.jdbc.Driver");

        try (BasicDataSource dataSource = new BasicDataSource()) {

            dataSource.setUrl("jdbc:mysql://localhost:3306/sakila?user=root&password=Northwest101$");


            Connection connection = dataSource.getConnection();

            homeScreen(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void homeScreen(Connection connection) throws SQLException {
        while (true) {

            // printBanner();
            System.out.println("\nEnter actors last name: ");
            System.out.println("1) Display all actors by last name");
            System.out.println("2) Display all films by actor");
            System.out.println("0) Exit");
            System.out.print("Select an option: ");

            String line = userInput.nextLine();        // read the whole line
            int choice;
            try {
                choice = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    displayAllActors(connection);
                    break;
                case 2:
                    displayAllFilmsByActor(connection);
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    return;  // exit the menu loop
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void displayAllActors(Connection connection) throws SQLException {
        String query = """
                SELECT last_name,
                actor_id
                FROM actor;
                
                """;

        try (
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet results = statement.executeQuery()
        ) {
            System.out.println("\nAll Actors By Last Name:\n");
            while (results.next()) {
                System.out.println("ID: " + results.getInt("actor_id"));
                System.out.println("Actor: " + results.getString("last_name"));
                System.out.println("----------------------------");
            }

        }


    }


    public static void displayAllFilmsByActor(Connection connection) throws SQLException {

        System.out.println("Enter actor first name:  ");
        String firstName = userInput.nextLine().trim().toUpperCase();
        if ("0".equals(firstName)) return;

        System.out.println("Enter actor last name: ");
        String lastName = userInput.nextLine().trim().toUpperCase();
        if ("0".equals(lastName)) return;


        String query = """
                 SELECT\s
                   f.title,
                   a.first_name,
                   a.last_name
                 FROM actor AS a
                 INNER JOIN film_actor AS fa
                   ON a.actor_id = fa.actor_id
                 INNER JOIN film AS f
                   ON fa.film_id = f.film_id
                 WHERE a.first_name = ?\s
                   AND a.last_name  = ?
                 ORDER BY f.title;
                """;


        Set<String> validName = new HashSet<>();

        try (PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);


            try (

                    ResultSet results = statement.executeQuery()
            ) {


                System.out.println("\nAll Films By Actor:\n");

                boolean foundName = false;
                while (results.next()) {
                    foundName = true;
                    System.out.println("Name: " + results.getString("first_name") + " " + results.getString("last_name"));
                    System.out.println("Film: " + results.getString("title"));
                    System.out.println("----------------------------");
                }
                if (!foundName) {
                    System.out.println("No films available by that actor. Please try again.");
                }
            }
        }
    }
}