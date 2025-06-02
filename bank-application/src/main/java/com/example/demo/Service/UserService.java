package com.example.demo.Service;

import com.example.demo.BankApplication;
import com.example.demo.Dto.*;
import org.springframework.stereotype.Service;


public interface UserService {

    BankResponse createAccount(UserDto userDto);
    BankResponse login(LoginDto loginDto);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    BankResponse creditAccount(CreditDebitDto creditDebitDto);
    BankResponse debitAccount(CreditDebitDto creditDebitDto);
    BankResponse transferAccount(Long id ,CreditDebitDto creditDebitDto);

   // BankApplication viewAccountDetails(Long id);
   // BankApplication updateAccountDetails(Long id , UserDto userDto);
   // BankApplication deleteAccountDetails(Long id);

}
