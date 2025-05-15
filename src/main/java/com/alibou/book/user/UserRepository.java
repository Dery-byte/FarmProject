package com.alibou.book.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
//    Optional<User> findByEmail(String username);

    Optional<User> findByUsername(String username);

//    Optional<Object> findByEmail(String userEmail);


    // Most efficient approach - single query
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.roles r " +
            "WHERE r.name != 'ADMIN' OR r.name IS NULL")
    List<User> findAllNonAdminUsers();

    // Alternative if you need to exclude users with ANY admin role
    @Query("SELECT u FROM User u WHERE NOT EXISTS " +
            "(SELECT 1 FROM u.roles r WHERE r.name = 'ADMIN')")
    List<User> findAllUsersWithoutAdminRole();










    @Query("SELECT COUNT(u) FROM User u WHERE NOT EXISTS " +
            "(SELECT 1 FROM u.roles r WHERE r.name = 'ADMIN')")
    long countNonAdminUsers();

    // Alternative with query method
    long countByRoles_NameNot(String roleName);


}




