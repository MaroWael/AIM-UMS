package com.ums.system;

import com.ums.system.model.Admin;
import com.ums.system.model.Role;
import com.ums.system.service.AdminService;
import com.ums.system.service.AdminServiceImpl;
import com.ums.system.utils.DatabaseConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            AdminService adminService = new AdminServiceImpl(conn);

            Admin admin = new Admin("Marwan", "marwan@example.com", "12345", Role.ADMIN);
            adminService.addAdmin(admin);
            System.out.println("Admin inserted.");

            adminService.getAllAdmins().forEach(a ->
                    System.out.println(a.getName() + " - " + a.getEmail())
            );

            DatabaseConnection.getInstance().closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
