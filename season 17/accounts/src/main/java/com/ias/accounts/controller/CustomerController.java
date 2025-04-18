package com.ias.accounts.controller;

import com.ias.accounts.dto.CustomerDetailsDto;
import com.ias.accounts.dto.CustomerDto;
import com.ias.accounts.dto.ErrorResponseDto;
import com.ias.accounts.service.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;

import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "REST API for customer in IAS Bank",
        description = "REST API in IAS BANK to fetch customer details"
)
@RestController

@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})

@Validated
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final ICustomerService iCustomerService;

    public CustomerController(ICustomerService iCustomerService) {
        this.iCustomerService = iCustomerService;
    }

    @Operation(
            summary = "Fetch Customer Details Rest Api",
            description = "Get Java Information that is deployed"
    )
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
    @GetMapping("/fetch-customer-details")
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(
            @RequestHeader("ias-bank-correlation-id") String correlationId,
            @RequestParam
            @Pattern(regexp = "(^$|[0-9]{10})", message = "AccountNumber must be 10 digits")
            String mobileNumber) {
        logger.debug("Ias-bank-correlation-id-found: {}", correlationId);
        CustomerDetailsDto customerDetailsDto = iCustomerService.fetchCustomerDetails(mobileNumber, correlationId);
        logger.debug("fetch customer detail method end {}", correlationId);
        return ResponseEntity.status(HttpStatus.SC_OK).body(customerDetailsDto);
    }
}
