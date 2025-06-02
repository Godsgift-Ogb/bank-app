package com.example.demo.Service.UserServiceImp;

import com.example.demo.Config.JwtTokenProvider;
import com.example.demo.Dto.*;
import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.TransactionService;
import com.example.demo.Service.UserService;
import com.example.demo.Utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TransactionService transactionService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService, TransactionService transactionService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.transactionService = transactionService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public BankResponse createAccount(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.AccountExistCode)
                    .responseMessage(AccountUtils.AccountExistMessage)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .otherName(userDto.getOtherName())
                .gender(userDto.getGender())
                .address(userDto.getAddress())
                .stateOfOrigin(userDto.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .phoneNumber(userDto.getPhoneNumber())
                .alternativePhoneNumber(userDto.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();
        User saved = userRepository.save(newUser);
        EmailDetails details = EmailDetails.builder()
                .recipient(saved.getEmail())
                .subject("Account Creation")
                .messageBody("Congratulations! Your Account Has Been Successfully Created. \n Your Account Details: \n" +
                        "Accont Name: " + saved.getFirstName()+ " " + saved.getLastName()+ " " + saved.getOtherName() + " \n" +
                        "Account Number: " + saved.getAccountNumber())
                .build();
        emailService.sendEmailAlert(details);
        return BankResponse.builder()
                .responseCode(AccountUtils.AccountSavedCode)
                .responseMessage(AccountUtils.AccountSavedMessage)
                .accountInfo(AccountInfo.builder()
                        .accountName(saved.getLastName() + " " + saved.getFirstName() + " " + saved.getOtherName())
                        .accountBalance(saved.getAccountBalance())
                        .accountNumber(saved.getAccountNumber())
                        .build())
                .build();

    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));

        EmailDetails loginAlert = EmailDetails.builder()
                .recipient(loginDto.getEmail())
                .subject("LOGIN ALERT")
                .messageBody("you Logged In Successfully, If you Did Not Initiate This request Please Contact Your Bank")
                .build();

        emailService.sendEmailAlert(loginAlert);

        return  BankResponse.builder()
                .responseCode("Login success 209")
                .responseMessage(jwtTokenProvider.generateToken((Authentication) authentication))
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean accountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!accountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.AccountNotExistCode)
                    .responseMessage(AccountUtils.AccountNotExistMessage)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        AccountInfo accountInfo = AccountInfo.builder()
                .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                .accountNumber(foundUser.getAccountNumber())
                .accountBalance(foundUser.getAccountBalance())
                .build();
        return BankResponse.builder()
                .responseCode(AccountUtils.AccountFoundCode)
                .responseMessage(AccountUtils.AccountFoundMessage)
                .accountInfo(accountInfo)
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean accountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!accountExist){
            return AccountUtils.AccountNotExistMessage;
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());

        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();

    }

    @Override
    public BankResponse creditAccount(CreditDebitDto creditDebitDto) {
        boolean accountExist = userRepository.existsByAccountNumber(creditDebitDto.getAccountNumber());
        if (!accountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.AccountNotExistCode)
                    .responseMessage(AccountUtils.AccountNotExistMessage)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(creditDebitDto.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitDto.getAmount()));
        User saved = userRepository.save(userToCredit);
        //saves Transaction
        TransactionDto transaction = TransactionDto.builder()
                .transactionType("Credit Transaction")
                .accountNumber(saved.getAccountNumber())
                .amount(creditDebitDto.getAmount())
                .build();
        transactionService.saveTransaction(transaction);
        //sends email
        EmailDetails details = EmailDetails.builder()
                .recipient(saved.getEmail())
                .subject("Credit Alert")
                .messageBody("Credit Alert: \n" + "You Have Successfully Received Amount "  + creditDebitDto.getAmount() + " Into Your Account \n" +
                        "Current Account Balance: " + saved.getAccountBalance())
                .build();
        emailService.sendEmailAlert(details);
        return BankResponse.builder()
                .responseCode(AccountUtils.AccountCreditedSuccessCode)
                .responseMessage(AccountUtils.AccountCreditedSuccessMessage)
                .accountInfo(AccountInfo.builder()
                        .accountName(saved.getFirstName() + " " + saved.getLastName() + " " + saved.getOtherName())
                        .accountNumber(saved.getAccountNumber())
                        .accountBalance(saved.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitDto creditDebitDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userToDebit = userRepository.findByUserIdAndAccountNumber(username, creditDebitDto.getAccountNumber());

        if(userToDebit ==null){
            log.error("Account number {} is invalid", creditDebitDto.getAccountNumber());
            throw new RuntimeException("User not found");
        }
        log.info("Trying to debit {} {} with account number {}", userToDebit.getFirstName(),userToDebit.getLastName(), userToDebit.getAccountNumber());
        System.err.println(userToDebit);
        boolean accountExist = userRepository.existsByAccountNumber(creditDebitDto.getAccountNumber());

        if (!accountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.AccountNotExistCode)
                    .responseMessage(AccountUtils.AccountNotExistMessage)
                    .accountInfo(null)
                    .build();
        }

        if (userToDebit.getAccountBalance().compareTo(creditDebitDto.getAmount()) < 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.AccountInsufficientBalanceCode)
                    .responseMessage(AccountUtils.AccountInsufficientBalanceMessage)
                    .accountInfo(AccountInfo.builder()
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitDto.getAmount()));
        User saved = userRepository.save(userToDebit);
        log.info("Account Debited Successfully");
        //saves transaction
        TransactionDto transaction = TransactionDto.builder()
                .transactionType("Debit Transaction")
                .accountNumber(saved.getAccountNumber())
                .amount(creditDebitDto.getAmount())
                .build();
        transactionService.saveTransaction(transaction);
        EmailDetails details = EmailDetails.builder()
                .recipient(saved.getEmail())
                .subject("Debit Alert")
                .messageBody("Debit Alert: \n" + "You Have Successfully Debited Amount "  + creditDebitDto.getAmount() + " From Your Account \n" +
                        "Current Account Balance: " + saved.getAccountBalance())
                .build();
        emailService.sendEmailAlert(details);

        return BankResponse.builder()
                .responseCode(AccountUtils.AccountDebitedSuccessCode)
                .responseMessage(AccountUtils.AccountDebitedSuccessMessage)
                .accountInfo(AccountInfo.builder()
                        .accountName(saved.getFirstName() + " " + saved.getLastName() + " " + saved.getOtherName())
                        .accountNumber(saved.getAccountNumber())
                        .accountBalance(saved.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse transferAccount(Long id ,CreditDebitDto creditDebitDto) {
        boolean accountExist = userRepository.existsByAccountNumber(creditDebitDto.getAccountNumber());
        if (!accountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.AccountNotExistCode)
                    .responseMessage(AccountUtils.AccountNotExistMessage)
                    .accountInfo(null)
                    .build();
        }
        Optional<User> sender = userRepository.findById(id);
        User receiver = userRepository.findByAccountNumber(creditDebitDto.getAccountNumber());
        if (sender.get().getAccountNumber().equals(creditDebitDto.getAccountNumber())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.AccountSameCode)
                    .responseMessage(sender.get().getAccountNumber() + " " + AccountUtils.AccountSameMessage)
                    .accountInfo(AccountInfo.builder()
                            .accountName(sender.get().getFirstName() + " " + sender.get().getLastName() + " " + sender.get().getOtherName())
                            .accountNumber(sender.get().getAccountNumber())
                            .accountBalance(sender.get().getAccountBalance())
                            .build())
                    .build();
        }
        if (sender.get().getAccountBalance().compareTo(creditDebitDto.getAmount()) < 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.AccountInsufficientBalanceCode)
                    .responseMessage(AccountUtils.AccountInsufficientBalanceMessage)
                    .accountInfo(AccountInfo.builder()
                            .accountName(sender.get().getFirstName() + " " + sender.get().getLastName() + " " + sender.get().getOtherName())
                            .accountNumber(sender.get().getAccountNumber())
                            .accountBalance(sender.get().getAccountBalance())
                            .build())
                    .build();
        }
        sender.get().setAccountBalance(sender.get().getAccountBalance().subtract(creditDebitDto.getAmount()));
        User savedSender = userRepository.save(sender.get());
        //saves transaction
        TransactionDto transaction = TransactionDto.builder()
                .transactionType("Debit Transaction")
                .accountNumber(savedSender.getAccountNumber())
                .amount(creditDebitDto.getAmount())
                .build();
        transactionService.saveTransaction(transaction);

        receiver.setAccountBalance(receiver.getAccountBalance().add(creditDebitDto.getAmount()));
        User savedReceiver = userRepository.save(receiver);
        //saves transaction
        TransactionDto transaction2 = TransactionDto.builder()
                .transactionType("Credit Transaction")
                .accountNumber(savedReceiver.getAccountNumber())
                .amount(creditDebitDto.getAmount())
                .build();
        transactionService.saveTransaction(transaction2);

        // debit Alert
        EmailDetails details = EmailDetails.builder()
                .recipient(savedSender.getEmail())
                .subject("Debit Alert")
                .messageBody("Debit Alert: \n" + "You Have Successfully Debited Amount "  + creditDebitDto.getAmount() + " From Your Account \n" +
                        " To " + savedReceiver.getFirstName() + " " + savedReceiver.getLastName() + " " + savedReceiver.getOtherName() + " \n" +
                        "Current Account Balance: " + savedSender.getAccountBalance())
                .build();
        emailService.sendEmailAlert(details);

        // Credit Alert
        EmailDetails details2 = EmailDetails.builder()
                .recipient(savedReceiver.getEmail())
                .subject("Credit Alert")
                .messageBody("Credit Alert: \n" + "You Have Successfully Received Amount "  + creditDebitDto.getAmount() + " Into Your Account \n" +
                        " From " + savedSender.getFirstName() + " " + savedSender.getLastName() + " " + savedSender.getOtherName() + " \n" +
                        "Current Account Balance: " + savedReceiver.getAccountBalance())
                .build();
        emailService.sendEmailAlert(details2);
        return BankResponse.builder()
                .responseCode("009")
                .responseMessage("you Have Successfully Sent " + creditDebitDto.getAmount() + " To " + savedReceiver.getFirstName() + " " + savedReceiver.getLastName() + " " + savedReceiver.getOtherName())
                .accountInfo(AccountInfo.builder()
                        .accountName(savedSender.getFirstName() + " " + savedSender.getLastName() + " " + savedSender.getOtherName())
                        .accountNumber(savedSender.getAccountNumber())
                        .accountBalance(savedSender.getAccountBalance())
                        .build())
                .build();

    }
}
