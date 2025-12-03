package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class App {

    public static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        //did we pass in a username and password
        //if not, the application must die
        if(args.length != 2){
            //display a message to the user
            System.out.println("Application needs two args to run: A username and a password for the db");
            //exit the app due to failure because we dont have a username and password from the command line
            System.exit(1);
        }

        //get the username and password from args[]
        String username = args[0];
        String password = args[1];


        try(Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", username, password)){

            while(true){

                System.out.println("""
                        What do you want to do?
                            1) Display All Products
                            2) Display All Customers
                            3) Display All Categories
                            0) Exit App                        
                        """);

                switch(scan.nextInt()){
                    case(1):
                        displayAllProducts(c);
                        break;
                    case(2):
                        displayAllCustomers(c);
                        break;
                    case(3):
                        displayAllCategories(c);
                    case(0):
                        System.out.println("Bye");
                        System.exit(0);
                    default:
                        System.out.println("Try Again");
                }
            }

        }catch(SQLException e){
            System.out.println("Something went wrong" + e);
        }
    }

    public static void displayAllCategories(Connection c){
        try(

                PreparedStatement q = c.prepareStatement("""
                        SELECT 
                            CategoryID
                            ,CategoryName
                        FROM 
                            Categories
                        """);

                ResultSet result = q.executeQuery();
                ){

            printResults(result);

        }catch(SQLException e){
            System.out.println("Error with query");
        }

        System.out.println("Enter a CategoryID to view related products");
        int catID = scan.nextInt();

        displayCategoryProducts(c , catID);
    }

    public static void displayCategoryProducts(Connection c, int i){

        try(
                PreparedStatement q = c.prepareStatement("""
                        SELECT 
                            ProductID
                            ,ProductName
                            ,UnitPrice
                            ,UnitsInStock
                        FROM
                            Products
                        WHERE
                            CategoryID = ?
                        """);
                ){

            q.setInt(1, i);

            try(
                    ResultSet result = q.executeQuery()
                    ){
                System.out.println("Products in category " + i + ": ");
                printResults(result);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving products from category " + i);
        }

    }
    public static void displayAllProducts(Connection c){

        try(

                PreparedStatement q = c.prepareStatement("""
                    
                    SELECT
                        ProductID
                        ,ProductName
                        ,UnitPrice
                        ,UnitsInStock
                    FROM
                        Products
                    
                    """);

                ResultSet results = q.executeQuery();
        ){
            printResults(results);
        } catch (SQLException e) {
            System.out.println("Could not get all the products");
            System.exit(1);
        }
    }

    public static void displayAllCustomers(Connection c){

        try(
                PreparedStatement q = c.prepareStatement("""
                        
                        SELECT *
                        FROM Customers
                        
                        """);

                    ResultSet results = q.executeQuery();
                ){
            printResults(results);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //This method will be used inside of the display methods to print results to the screen
    public static void printResults(ResultSet r) throws SQLException {

        //Get the meta data so we have access to the field names
        ResultSetMetaData metaData = r.getMetaData();

        //Get the number of rows returned
        int columnCount = metaData.getColumnCount();

        while(r.next()){

            for(int i = 1; i<=columnCount; i++){
                String columnName = metaData.getColumnName(i);
                String value = r.getString(i);
                System.out.println(columnName + ": " + value);
            }

            System.out.println("------------------------------");

        }

    }
}
