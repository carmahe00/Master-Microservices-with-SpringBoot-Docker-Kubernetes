package com.ias.accounts.service.impl;

import com.ias.accounts.constans.AccountsConstants;
import com.ias.accounts.dto.AccountDto;
import com.ias.accounts.dto.AccountsMsgDto;
import com.ias.accounts.dto.CustomerDto;
import com.ias.accounts.entity.Accounts;
import com.ias.accounts.entity.Customer;
import com.ias.accounts.exception.CustomerAlreadyExistException;
import com.ias.accounts.exception.ResourceNotFoundException;
import com.ias.accounts.mapper.AccountsMapper;
import com.ias.accounts.mapper.CustomerMapper;
import com.ias.accounts.repository.AccountRepository;
import com.ias.accounts.repository.CustomerRepository;
import com.ias.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
@Slf4j
@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;

    private final StreamBridge streamBridge;

    /**
     * @param customerDto
     */
    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        var optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if (optionalCustomer.isPresent())
            throw new CustomerAlreadyExistException("Customer already registered with given mobile number: " + customerDto.getMobileNumber());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setCreatedBy("Anonymous");
        var savedCustomer = customerRepository.save(customer);
        var savedAccount = accountRepository.save(createNewAccount(savedCustomer));
        sendCommunication(savedAccount, savedCustomer);
    }

    public void sendCommunication(Accounts accounts, Customer customer) {
        var accountsMsgDto =  new AccountsMsgDto(accounts.getAccountNumber(), customer.getName(), customer.getEmail(), customer.getMobileNumber());
        log.info("Sending Communication request for the details: {}", accountsMsgDto);
        var result = streamBridge.send("sendCommunication-out-0", accountsMsgDto);
        log.info("It is Communication request successfully triggered?: {}", result);
    }

    /**
     * @param mobileNumber
     * @return Account Details based on a given mobileNumber
     */
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        var customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobile number", mobileNumber)
        );
        var account = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "account", customer.getCustomerId().toString())
        );
        var customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(account, new AccountDto()));
        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountDto accountDto = customerDto.getAccountsDto();
        if (accountDto != null) {
            var accounts = accountRepository.findById(accountDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "Account Number", accountDto.getAccountNumber().toString())
            );
            AccountsMapper.mapToAccounts(accountDto, accounts);
            accounts = accountRepository.save(accounts);
            var customerId = accounts.getCustomerId();
            var customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString())
            );

            CustomerMapper.mapToCustomer(customerDto, customer);
            customerRepository.save(customer);
            isUpdated = true;

        }


        return isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        var customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()-> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        accountRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated = false;
        if(accountNumber != null){
            var  accounts =  accountRepository.findById(accountNumber).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "AccountNumber", accountNumber.toString())
            );
            accounts.setCommunicationSw(true);
            accountRepository.save(accounts);
            isUpdated = true;
        }

        return isUpdated;
    }

    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setCreatedBy(customer.getCreatedBy());
        return newAccount;
    }
}
