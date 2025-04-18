package com.ias.accounts.service.impl;

import com.ias.accounts.dto.AccountDto;
import com.ias.accounts.dto.CardsDto;
import com.ias.accounts.dto.CustomerDetailsDto;
import com.ias.accounts.dto.LoansDto;
import com.ias.accounts.entity.Accounts;
import com.ias.accounts.entity.Customer;
import com.ias.accounts.exception.ResourceNotFoundException;
import com.ias.accounts.mapper.AccountsMapper;
import com.ias.accounts.mapper.CustomerMapper;
import com.ias.accounts.repository.AccountRepository;
import com.ias.accounts.repository.CustomerRepository;
import com.ias.accounts.service.ICustomerService;
import com.ias.accounts.service.client.CardFeignClient;
import com.ias.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CardFeignClient cardFeignClient;
    private final LoansFeignClient loansFeignClient;
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts
                 = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountDto()));


        ResponseEntity<LoansDto> loansDtoResponseEntity =  loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if(loansDtoResponseEntity != null)
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardDetails =  cardFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if(cardDetails !=null)
            customerDetailsDto.setCardsDto(cardDetails.getBody());
        return customerDetailsDto;
    }
}
