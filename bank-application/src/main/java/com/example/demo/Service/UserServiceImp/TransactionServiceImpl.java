package com.example.demo.Service.UserServiceImp;

import com.example.demo.Dto.BankStatementDto;
import com.example.demo.Dto.EmailDetails;
import com.example.demo.Dto.TransactionDto;
import com.example.demo.Entity.Transaction;
import com.example.demo.Entity.User;
import com.example.demo.Repository.TransactionRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.TransactionService;
import com.itextpdf.text.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository, EmailService emailService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public void saveTransaction(TransactionDto transactionDto) {

        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .amount(transactionDto.getAmount())
                .accountNumber(transactionDto.getAccountNumber())
                .status("Success")
                .build();

        transactionRepository.save(transaction);
        System.out.println("Transaction saved");

    }

    @Override
    public List<Transaction> findAllTransactions(BankStatementDto bankStatementDto) throws FileNotFoundException, DocumentException, MessagingException {
        long random10Digit = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        StringBuilder sb = new StringBuilder();
        String random10DigitStr = String.valueOf(random10Digit);
        String time =LocalDateTime.now().toString().replace(":","").replace(".","");
        sb.append(random10DigitStr);
        sb.append(time);

        String file = "C:\\Users\\hp\\Documents\\sb\\"+ sb+".pdf";
        List<Transaction> transactions = transactionRepository.getTransactionsByAccountNumber(bankStatementDto.getAccountNumber(), bankStatementDto.getStartDate(), bankStatementDto.getEndDate());

        User user = userRepository.findByAccountNumber(bankStatementDto.getAccountNumber());
        String customerName = user.getFirstName()+ " " + user.getLastName() + " " + user.getOtherName();
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("Setting size of document");
        OutputStream outputStream = new FileOutputStream(file);
        PdfWriter.getInstance(document,outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("The Bank Application"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("72, Freedom Street, Abuja Nigeria"));
        bankAddress.setBorder(0);

        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: " + bankStatementDto.getStartDate()));
         customerInfo.setBorder(0);
         PdfPCell statement = new PdfPCell(new Phrase("Statement Of Account"));
         statement.setBorder(0);
         PdfPCell stopDate = new PdfPCell(new Phrase("End Date:" + bankStatementDto.getEndDate()));
         stopDate.setBorder(0);
         PdfPCell name = new PdfPCell(new Phrase("Customer Name: " + customerName));
         name.setBorder(0);
         PdfPCell space = new PdfPCell();
         space.setBorder(0);
         PdfPCell address = new PdfPCell(new Phrase("CustomerAddress: " + user.getAddress()));
         address.setBorder(0);

         PdfPTable transactionTable = new PdfPTable(4);

         PdfPCell date = new PdfPCell(new Phrase("Date"));
         date.setBackgroundColor(BaseColor.BLUE);
         date.setBorder(0);

         PdfPCell transactionType = new PdfPCell(new Phrase("Transaction Type:"));
         transactionType.setBackgroundColor(BaseColor.BLUE);
         transactionType.setBorder(0);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("Transaction Amount:"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);

        PdfPCell transactionStatus = new PdfPCell(new Phrase("Transaction Status:"));
        transactionStatus.setBackgroundColor(BaseColor.BLUE);
        transactionStatus.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(transactionStatus);

        transactions.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionTable);
        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Statement Of Account")
                .messageBody("Kindly find Your Requested Account Statement Attached!")
                .attachment(file)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);
        return transactions;

    }

    @Override
    public Page<Transaction> listOfTransactionsByPage(String accountNumber) {
        Pageable pageable = PageRequest.of(0,5);
        return transactionRepository.listOfTransactionsByPage(accountNumber, pageable);
    }

}
