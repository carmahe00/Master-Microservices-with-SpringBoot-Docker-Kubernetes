package com.ias.accounts.functions;

import com.ias.accounts.service.IAccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class AccountsFunction {

     @Bean
    public Consumer<Long> updateCommunication(IAccountsService iAccountsService){
        return accountNumber ->{
            log.info("Updating Communication status for the account number: {}", accountNumber.toString());
            iAccountsService.updateCommunicationStatus(accountNumber);
        };
    }
}
