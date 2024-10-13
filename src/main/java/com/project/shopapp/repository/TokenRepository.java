package com.project.shopapp.repository;

import com.project.shopapp.model.Token;
import com.project.shopapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByUser(User user);

    Token findByToken(String token);

}
