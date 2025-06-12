package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.*;
import java.util.Scanner;

public class App {

    static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        try (BasicDataSource dataSource = new BasicDataSource()) {
            dataSource.setUrl("jdbc:mysql://localhost:3306/sakila?user=root&password=Northwest101$");

            try (Connection connection = dataSource.getConnection()) {
                homeScreen(connection);
            }
        }
    }

    public static void homeScreen(Connection connection) throws SQLException {
        while (true) {
            System.out.println("\n1) Search actors by name");
            System.out.println("2) Display all films by actor ID");
            System.out.println("0) Exit");
            System.out.print("Select an option: ");

            String line = userInput.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
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
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void displayAllActors(Connection connection) throws SQLException {
        System.out.print("Enter actor name to search (first or last, or 0 to return): ");
        String namePattern = userInput.nextLine().trim();
        if ("0".equals(namePattern)) {
            return;
        }

        String query = """
            SELECT actor_id, first_name, last_name
            FROM actor
            WHERE first_name LIKE ?
               OR last_name  LIKE ?
            ORDER BY last_name, first_name;
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String pattern = "%" + namePattern + "%";
            statement.setString(1, pattern);
            statement.setString(2, pattern);

            try (ResultSet rs = statement.executeQuery()) {
                boolean found = false;
                System.out.println("\nSearch Results:");
                while (rs.next()) {
                    found = true;
                    int actorId = rs.getInt("actor_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    System.out.println("ID: " + actorId);
                    System.out.println("Name: " + firstName + " " + lastName);
                    System.out.println("----------------------------");
                }
                if (!found) {
                    System.out.println("No actors found matching '" + namePattern + "'.");
                }
            }
        }
    }

    public static void displayAllFilmsByActor(Connection connection) throws SQLException {
        System.out.print("Enter an actor ID to list their films (or 0 to return): ");
        String line = userInput.nextLine().trim();
        if ("0".equals(line)) {
            return;
        }

        int actorId;
        try {
            actorId = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Returning to menu.");
            return;
        }

        String query = """
            SELECT f.film_id, f.title, f.description, f.release_year, f.length
            FROM film_actor fa
            JOIN film f ON fa.film_id = f.film_id
            WHERE fa.actor_id = ?
            ORDER BY f.title;
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, actorId);

            try (ResultSet rs = statement.executeQuery()) {
                boolean found = false;
                System.out.println("\nFilms for Actor ID " + actorId + ":");
                while (rs.next()) {
                    found = true;
                    int filmId = rs.getInt("film_id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    int releaseYear = rs.getInt("release_year");
                    int length = rs.getInt("length");
                    System.out.println("ID: " + filmId);
                    System.out.println("Title: " + title);
                    System.out.println("Description: " + description);
                    System.out.println("Year: " + releaseYear);
                    System.out.println("Length: " + length + " min");
                    System.out.println("----------------------------");
                }
                if (!found) {
                    System.out.println("No films found for actor ID " + actorId + ".");
                }
            }
        }
    }
}
