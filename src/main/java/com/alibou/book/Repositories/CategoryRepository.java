package com.alibou.book.Repositories;

import com.alibou.book.Entity.Category;
import com.alibou.book.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;


@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Collection<Category> findByUserRole(String userRole);
}
