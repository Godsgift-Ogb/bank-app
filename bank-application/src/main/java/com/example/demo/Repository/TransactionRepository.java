package com.example.demo.Repository;

import com.example.demo.Entity.Transaction;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String > {
    @Query(value = "SELECT * FROM transactions WHERE account_number =:accountNumber AND created_at BETWEEN :startDate AND :endDate ", nativeQuery = true)
    List<Transaction> getTransactionsByAccountNumber(@Param("accountNumber") String accountNumber, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT * FROM transactions WHERE account_number =:accountNumber", nativeQuery = true)
    Page<Transaction> listOfTransactionsByPage(@Param("accountNumber") String accountNumber , Pageable pageable);
}
