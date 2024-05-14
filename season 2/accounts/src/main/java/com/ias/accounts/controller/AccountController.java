package com.ias.accounts.controller;

import com.ias.accounts.constans.AccountsConstants;
import com.ias.accounts.dto.CustomerDto;
import com.ias.accounts.dto.ErrorResponseDto;
import com.ias.accounts.dto.ResponseDto;
import com.ias.accounts.service.IAccountsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST API",
        description = "CRUD REST API in IAS BANK TO CREATE, UPDATE, DELETE account details"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class AccountController {

    private IAccountsService iAccountsService;

    @Operation(
            summary = "Create Account Rest Api",
            description = "Rest Api to create new account inside Bank"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Https created"
    )
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto) {
        iAccountsService.createAccount(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_200));
    }

    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccountDetails(
            @RequestParam
            @Pattern(regexp = "(^$|[0-9]{10})", message = "AccountNumber must be 10 digits")
            String mobileNumber) {
        var customerDto = iAccountsService.fetchAccount(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Https updated"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Https Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@RequestBody CustomerDto customerDto) {
        boolean isUpdated = iAccountsService.updateAccount(customerDto);
        if (isUpdated)
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        else
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_DELETE));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Https updated"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Exception Failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Https Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccountDetails(
            @RequestParam
            @Pattern(regexp = "(^$|[0-9]{10})", message = "AccountNumber must be 10 digits")
            String mobileNumber) {
        boolean isDeleted = iAccountsService.deleteAccount(mobileNumber);
        if (isDeleted)
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        else
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_DELETE));
    }
}
