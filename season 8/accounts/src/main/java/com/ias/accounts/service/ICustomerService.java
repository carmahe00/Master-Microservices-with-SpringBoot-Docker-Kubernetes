package com.ias.accounts.service;

import com.ias.accounts.dto.CustomerDetailsDto;

public interface ICustomerService {
    /**
     *
     * @param mobileNumber - Input mobile number
     * @return Customer Details based on given mobileNumber
     */
    CustomerDetailsDto  fetchCustomerDetails(String mobileNumber);
}
