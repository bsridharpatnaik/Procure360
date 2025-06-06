package com.gb.p360.repository;

import java.util.List;
import java.util.Optional;

import com.gb.p360.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gb.p360.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.factories WHERE u.username = :username")
    Optional<User> findByUsernameWithFactories(@Param("username") String username);

    List<User> findByRole(Role role);
    List<User> findByFactoriesId(Long factoryId);
}
