package kz.ex05_repositories.for_remove;


import kz.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface UsersDao {
    List<User> findAll() throws SQLException;
}
