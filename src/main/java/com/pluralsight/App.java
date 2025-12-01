package com.pluralsight;

import java.sql.*;

public class App {

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

        try(
            //Create the connection(similar to opening mySQL workbench)
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", username, password);

            PreparedStatement q = c.prepareStatement("""
                    SELECT
                        ProductName
                    FROM
                        Products               
                    """);
            ResultSet results = q.executeQuery();
        ){
            printResults(results);

        }catch(SQLException e){
            System.out.println("Something went wrong" + e);
        }
    }

    //This method will be used inside of the display methods to print results to the screen
    public static void printResults(ResultSet r) throws SQLException {
        //Get the meta data so we have access to the field names
        ResultSetMetaData metaData = r.getMetaData();
        //Get the number of rows returned
        int columnCount = metaData.getColumnCount();

        while(r.next()){

            //loop over each column in the row and display the data
            for(int i = 1; i <= columnCount; i++){
                //Get the current column value
                String value = r.getString(i);
                //print out the column name and value
                System.out.println(value);
            }

            //print empty line to make results display prettier
            System.out.println();
        }

    }
}
