package com.personal_expense_tracker.main.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static String URL = "jdbc:postgresql://localhost:5433/expensedb";
    private static String USER = "madina";
    private static String PASSWORD = "m123";

    public static Connection connect() throws SQLException {
    		Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
    		return connection;
    		
   
     }
}

