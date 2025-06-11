package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class App {

    // Scanner for reading user input:
    static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // 1) Check for username/password arguments
        if (args.length != 2) {
            System.out.println("Usage: java com.pluralsight.App <username> <password>");
            System.exit(1);
        }
        String username = args[0];
        String password = args[1];

        // 2) Load the MySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 3) Open the connection once, and pass it into our menu loop
        try (BasicDataSource dataSource = new BasicDataSource()) {

            dataSource.setUrl("jdbc:mysql://localhost:3306/northwind");
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            Connection connection = dataSource.getConnection();

            homeScreen(connection);
        }
    }


     // Displays the main menu and dispatches to the chosen action.

    public static void homeScreen(Connection connection) throws SQLException {
        while (true) {

            printBanner();
            System.out.println("\nWhat do you want to do?");
            System.out.println("1) Display all products");
            System.out.println("2) Display all customers");
            System.out.println("3) Display all categories");
            System.out.println("0) Exit");
            System.out.print("Select an option: ");

            int choice = userInput.nextInt();

            switch (choice) {
                case 1:
                    displayAllProducts(connection);
                    break;
                case 2:
                    displayAllCustomers(connection);
                    break;
                case 3:
                    displayAllCategories(connection);
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    return;  // exit the menu loop
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    //  Option 1: Query and print all products.

    public static void displayAllProducts(Connection connection) throws SQLException {
        String query = """
            SELECT ProductID, ProductName, UnitPrice, UnitsInStock
            FROM Products
            ORDER BY ProductID
            """;

        try (
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet results = statement.executeQuery()
        ) {
            System.out.println("\nAll Products:");
            while (results.next()) {
                System.out.println("Product ID:      " + results.getInt("ProductID"));
                System.out.println("Product Name:    " + results.getString("ProductName"));
                System.out.println("Unit Price:      " + results.getDouble("UnitPrice"));
                System.out.println("Units in Stock:  " + results.getInt("UnitsInStock"));
                System.out.println("----------------------------");
            }
        }
    }


     // Option 2: Query and print all customers.

    public static void displayAllCustomers(Connection connection) throws SQLException {
        String query = """
            SELECT CustomerID, CompanyName
            FROM Customers
            ORDER BY CustomerID
            """;

        try (
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet results = statement.executeQuery()
        ) {
            System.out.println("\nAll Customers:");
            while (results.next()) {
                System.out.println("Customer ID:   " + results.getString("CustomerID"));
                System.out.println("Company Name:  " + results.getString("CompanyName"));
                System.out.println("----------------------------");
            }
        }
    }


    // Option 3: Show all categories, then prompt for one and display its products.

    public static void displayAllCategories(Connection connection) throws SQLException {
        String query = """
            SELECT CategoryID, CategoryName
            FROM Categories
            ORDER BY CategoryID
            """;

        Set<Integer> validIds = new HashSet<>();

        try (
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet rs = statement.executeQuery()
        ) {
            System.out.println("\nCategories:");
            while (rs.next()) {
                System.out.println("Category ID:    " + rs.getInt("CategoryID"));
                System.out.println("Category Name:  " + rs.getString("CategoryName"));
                System.out.println("----------------------------");
            }
        }

        // Prompt for which category to drill into:

        int categoryId;

       while(true)
       {

           System.out.print("Enter a Category ID to view its products: ");
           categoryId = userInput.nextInt();

           if (categoryId == 0){
               return;
           }

           if(validIds.contains(categoryId)){
               break;
           }

           System.out.println("Category ID not available. Please try again.");
       }

        displayProductsByCategory(connection, categoryId);
    }


     // Helper for Option 3: Query and print products in a given category.
    public static void displayProductsByCategory(Connection connection, int categoryId) throws SQLException {
        String sql = """
            SELECT ProductID, ProductName, UnitPrice, UnitsInStock
            FROM Products
            WHERE CategoryID = ?
            ORDER BY ProductID
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet results = stmt.executeQuery()) {
                System.out.println("\nProducts in Category " + categoryId + ":");
                while (results.next()) {
                    System.out.println("Product ID:      " + results.getInt("ProductID"));
                    System.out.println("Product Name:    " + results.getString("ProductName"));
                    System.out.println("Unit Price:      " + results.getDouble("UnitPrice"));
                    System.out.println("Units in Stock:  " + results.getInt("UnitsInStock"));
                    System.out.println("----------------------------");
                }
            }
        }
    }



    public static void printBanner() {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║                                                        ║");
        System.out.println("║          ★ Northwind Traders Database ★               ║");
        System.out.println("║                                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
    }

}
