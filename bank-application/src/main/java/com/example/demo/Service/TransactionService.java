package com.example.demo.Service;

import com.example.demo.Dto.BankStatementDto;
import com.example.demo.Dto.TransactionDto;
import com.example.demo.Entity.Transaction;
import com.itextpdf.text.DocumentException;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;


public interface TransactionService {

    void saveTransaction(TransactionDto transactionDto);
    List<Transaction> findAllTransactions(BankStatementDto bankStatementDto) throws FileNotFoundException, DocumentException, MessagingException;
    Page<Transaction> listOfTransactionsByPage(String accountNumber );
}
