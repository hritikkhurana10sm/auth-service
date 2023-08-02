package org.example.repository;

import org.example.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<AuthUser,String> {
    AuthUser  findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<AuthUser> findAll();

    void deleteByRegisterTimeBefore(LocalDateTime time);


    @Query("SELECT u from AuthUser u WHERE (u.username = :usernameOrEmail OR u.email = :usernameOrEmail)")
    AuthUser findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    @Modifying
    @Query("DELETE from AuthUser u WHERE (u.activationId IS NOT NULL OR u.activationId != '') AND u.registerTime < :currentTimeFifteenMinAgo")
    void deleteExpiredUsers(@Param("currentTimeFifteenMinAgo") LocalDateTime currentTimeFifteenMinAgo);


}
