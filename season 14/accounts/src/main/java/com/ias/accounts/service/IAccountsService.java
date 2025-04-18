package com.ias.accounts.service;

import com.ias.accounts.dto.CustomerDto;

public interface IAccountsService {
    /**
     *
     * @param customerDto
     */
    void createAccount(CustomerDto customerDto);

    CustomerDto fetchAccount(String mobileNumber);


    /**
     *
     * @param customerDto
     * @return boolean indicating if the update of Account details is successful or not
     */
    boolean updateAccount(CustomerDto customerDto);

    /**
     *
     * @param mobileNumber
     * @return boolean indicating if the deleted of Account details is successful or not
     */
    boolean deleteAccount(String mobileNumber);


    boolean updateCommunicationStatus(Long accountNumber);
}
