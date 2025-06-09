package com.pluralsight;

import java.sql.*;

public class App {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/northwind",
                "root",
                "Northwest101$"
        );


             Statement statement = connection.createStatement()
        ) {
            String query = "SELECT * FROM products; ";
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                System.out.println(results.getString("ProductName"));
            }

        } catch (SQLException ex){
            System.err.println("Database Error: " + ex.getMessage());
            ex.printStackTrace();
        }










    }
}
