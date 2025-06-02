package com.example.demo.Utils;

import java.time.Year;

public class AccountUtils {
    public static final String AccountExistCode = "001";
    public static final String AccountExistMessage = "This User Already Has An Account!";

    public static final String AccountSavedCode = "200";
    public static final String AccountSavedMessage = "User Saved Successfully!";

    public static final String AccountNotExistCode = "003";
    public static final String AccountNotExistMessage = "USer With The Provided Account Does Not Exist";

    public static final String AccountFoundCode = "004";
    public static final String AccountFoundMessage = "User Account Found";

    public static final String AccountCreditedSuccessCode = "005";
    public static final String AccountCreditedSuccessMessage = "User Has Been Successfully Credited Successfully";

    public static final String AccountInsufficientBalanceCode = "006";
    public static final String AccountInsufficientBalanceMessage = "Sorry! Insufficient Balance";

    public static final String AccountDebitedSuccessCode = "007";
    public static final String AccountDebitedSuccessMessage = "User Has Been Successfully Debited Successfully";

    public static final String AccountSameCode = "008";
    public static final String AccountSameMessage = "Is Your Account Number Please Send To A Different Account Number";


    public static String generateAccountNumber(){
        /**
         * 2023 + randomSixDigits
         */
        Year currentYear = Year.now();
        int min =100000;
        int max = 999999;
        // generate a random number between min and max
        int random = (int) (Math.random() * (max - min + 1) + min);
        //convert the current year and random to strings, then concatenate
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(random);
        StringBuilder accountNumber = new StringBuilder();
        accountNumber.append(year).append(randomNumber);
        return accountNumber.toString();

    }
}
