package com.ums.system;

import com.ums.system.utils.DatabaseConnection;
import java.sql.Connection;

public class Main {
     static void main(String[] args) {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        Connection conn = dbConnection.getConnection();

        if (conn != null) {
            System.out.println("Connection test successful!");
            DatabaseConnection dbConnection2 = DatabaseConnection.getInstance();
            System.out.println("Same instance? " + (dbConnection == dbConnection2));
            dbConnection.closeConnection();
        } else {
            System.out.println("Connection test failed!");
        }
    }
}
