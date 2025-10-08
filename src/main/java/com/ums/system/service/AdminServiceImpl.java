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
    public void addAdmin(Admin admin) {
        adminDAO.insert(admin);
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
    public List<Admin> getAllAdmins() {
        return adminDAO.getAll();
    }
}
