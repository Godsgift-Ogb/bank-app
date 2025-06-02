package com.example.demo.Controller;

import com.example.demo.Dto.BankStatementDto;
import com.example.demo.Entity.Transaction;
import com.example.demo.Service.TransactionService;
import com.itextpdf.text.DocumentException;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/bankStatement")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/generate")
    public List<Transaction> generateBankStatement(@RequestBody BankStatementDto bankStatementDto) throws DocumentException, FileNotFoundException, MessagingException {
        return transactionService.findAllTransactions(bankStatementDto);
    }

    @PostMapping("/pageGenerate")
    public Page<Transaction> pageRequestList(@RequestBody String accountNumber) {
        return transactionService.listOfTransactionsByPage(accountNumber);
    }
}
