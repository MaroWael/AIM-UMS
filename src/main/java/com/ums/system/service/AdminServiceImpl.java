package com.ums.system.service;

import com.ums.system.dao.AdminDAOImpl;
import com.ums.system.model.Admin;

import java.sql.Connection;
import java.util.List;

public class AdminServiceImpl implements AdminService {
    private final AdminDAOImpl adminDAO;

    public AdminServiceImpl(Connection connection) {
        this.adminDAO = new AdminDAOImpl(connection);
    }

    @Override
    public boolean addAdmin(Admin admin) {
        // Check if admin with this email already exists before insertion
        Admin existing = adminDAO.getByEmail(admin.getEmail());
        if (existing != null) {
            System.out.println("⚠️ Admin with email " + admin.getEmail() + " already exists.");
            return false; // Email already exists, creation failed
        }
        adminDAO.insert(admin);
        // Verify the admin was actually created
        Admin created = adminDAO.getByEmail(admin.getEmail());
        return created != null;
    }

    @Override
    public void updateAdmin(Admin admin) {
        adminDAO.update(admin);
    }

    @Override
    public void deleteAdmin(int id) {
        adminDAO.delete(id);
    }

    @Override
    public Admin getAdminById(int id) {
        return adminDAO.getById(id);
    }

    @Override
    public Admin getAdminByEmail(String email) {
        return adminDAO.getByEmail(email);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminDAO.getAll();
    }
}
