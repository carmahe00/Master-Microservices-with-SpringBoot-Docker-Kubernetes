package com.ias.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(
        name = "CustomerDetails",
        description = "Schema to hold Customer, Loan and Account information"
)
public class CustomerDetailsDto {
    @Schema(
            description = "Name of customer", example = "IAS Bytes"
    )
    @NotEmpty(message = "Name can not be a null or empty")
    @Size(min = 5, max = 30, message = "The length of customer name should be between 5 and 30")
    private String name;

    @Schema(
            description = "Email of customer", example = "iasByte@mail.es"
    )
    @NotEmpty(message = "Email address can not be a null or empty")
    @Email(message = "Email address should be a valid value")
    private String email;

    @Schema(
            description = "Mobile Number of customer", example = "9345432123"
    )
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;


    @Schema(
            description = "Account details"
    )
    private AccountDto accountsDto;

    @Schema(
            description = "Loan details of customer"
    )
    private LoansDto loansDto;

    @Schema(
            description = "Cards details of customer"
    )
    private CardsDto cardsDto;
}
