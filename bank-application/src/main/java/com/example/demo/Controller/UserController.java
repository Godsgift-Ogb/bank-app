package com.example.demo.Controller;

import com.example.demo.Dto.*;
import com.example.demo.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management Apis")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Create New User Account",
            description = "Creating a new User And Assigning  An Account Id"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 Created"
    )
    @PostMapping("/create")
    public ResponseEntity<BankResponse> createUser(@RequestBody UserDto userDto){
        BankResponse userResponse = userService.createAccount(userDto);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<BankResponse> loginUser(@RequestBody LoginDto loginDto){
        BankResponse userResponse = userService.login(loginDto);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(
            summary = "Balance Enquiry",
            description = "Given The Account Number, Checks How Much The User Has"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 Success"
    )
    @GetMapping("/balanceEnquiry")
    public ResponseEntity<BankResponse> balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        BankResponse balance = userService.balanceEnquiry(enquiryRequest);
        return ResponseEntity.ok(balance);
    }

    @Operation(
            summary = "Name Enquiry",
            description = "Given The Account Number, Checks The User Name"
    )
    @ApiResponse(
            responseCode = "202",
            description = "Http Status 202 Success"
    )
    @GetMapping("/nameEnquiry")
    public ResponseEntity<String> nameEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        String name = userService.nameEnquiry(enquiryRequest);
        return ResponseEntity.ok(name);
    }

    @Operation(
            summary = "Credit A User Account",
            description = "Given The Account Number And Amount, Adds The Amount to The User"
    )
    @ApiResponse(
            responseCode = "203",
            description = "Http Status 203 Success"
    )
    @PostMapping("/creditAccount")
    public ResponseEntity<BankResponse> accountCredit(@RequestBody CreditDebitDto creditDebitDto){
        BankResponse credit = userService.creditAccount(creditDebitDto);
        return ResponseEntity.ok(credit);
    }

    @Operation(
            summary = "Debit A User Account",
            description = "Given The Account Number And Amount, Deduct The Amount From The User"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Http Status 204 Success"
    )
    @PostMapping("/debitAccount")
    public ResponseEntity<BankResponse> accountDebit(@RequestBody CreditDebitDto creditDebitDto){
        BankResponse debit = userService.debitAccount(creditDebitDto);
        return ResponseEntity.ok(debit);
    }

    @Operation(
            summary = "Transfer To A New User Account",
            description = "Given The ID Of Sender, Transfers To A Different User Using Account Number Given And Amount To The User"
    )
    @ApiResponse(
            responseCode = "205",
            description = "Http Status 205 Success"
    )
    @PutMapping("/transferAccount/{id}")
    public ResponseEntity<BankResponse> accountTransfer(@PathVariable Long id,@RequestBody CreditDebitDto creditDebitDto){
        BankResponse transfer = userService.transferAccount(id,creditDebitDto);
        return ResponseEntity.ok(transfer);
    }
}
