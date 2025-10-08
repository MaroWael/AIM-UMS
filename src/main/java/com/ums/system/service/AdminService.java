package com.ums.system.service;

import com.ums.system.model.Admin;
import java.util.List;

public interface AdminService {
    void addAdmin(Admin admin);
    void updateAdmin(Admin admin);
    void deleteAdmin(int id);
    Admin getAdminById(int id);
    List<Admin> getAllAdmins();
}
