package com.alibou.book.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
//    Optional<User> findByEmail(String username);

    Optional<User> findByUsername(String username);

//    Optional<Object> findByEmail(String userEmail);
}
