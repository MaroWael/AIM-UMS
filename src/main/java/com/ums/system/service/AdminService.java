package com.ums.system.service;

import com.ums.system.model.Admin;
import java.util.List;

public interface AdminService {
    boolean addAdmin(Admin admin);
    void updateAdmin(Admin admin);
    void deleteAdmin(int id);
    Admin getAdminById(int id);
    Admin getAdminByEmail(String email);
    List<Admin> getAllAdmins();
}
