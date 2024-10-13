package com.project.shopapp.repository;

import com.project.shopapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    @Query("select u from User u where (:keyword is null or :keyword = '' or " +
            "u.fullName like %:keyword% or " +
            "u.phoneNumber like %:keyword% or " +
            "u.email like %:keyword% or " +
            "u.address like %:keyword%)")
    Page<User> findAll(@Param("keyword") String keyword, Pageable pageable);

}
