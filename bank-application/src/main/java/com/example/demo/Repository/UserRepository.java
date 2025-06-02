package com.example.demo.Repository;

import com.example.demo.Entity.Transaction;
import com.example.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // @Query(value = "SELECT COUNT(1) FROM users WHERE email =:email  ", nativeQuery = true )
     Boolean existsByEmail(String email);

    // @Query(value = "SELECT * FROM users WHERE account_number =:accountNumber  ", nativeQuery = true )
     Boolean existsByAccountNumber(String accountNumber);

     @Query(value = "SELECT * FROM users WHERE account_number =:accountNumber  ", nativeQuery = true )
     User findByAccountNumber(@Param("accountNumber")String accountNumber);

     @Query(value = "SELECT * FROM users WHERE email =:email  ", nativeQuery = true )
     Optional<User> findByEmail(@Param("email") String email);

     @Query(value = "SELECT u.* FROM users u WHERE u.email =:username AND u.account_number =:accountNumber ", nativeQuery = true)
     User findByUserIdAndAccountNumber(@Param("username") String username, @Param("accountNumber") String accountNumber);
}
