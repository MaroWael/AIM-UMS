package com.ums.system.dao;

import com.ums.system.model.User;
import java.util.List;

public interface UserDAO<T extends User> {
    void insert(T user);
    void update(T user);
    void delete(int id);
    T getById(int id);
    List<T> getAll();
}
