package com.michaelzhou.fullstackregister.customer;

import com.michaelzhou.fullstackregister.EmailValidator;
import com.michaelzhou.fullstackregister.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Service Layer executes all the business logic
@Service
public class CustomerService {

    private final CustomerDataAccessService customerDataAccessService;
    private final EmailValidator emailValidator;

    @Autowired
    public CustomerService(CustomerDataAccessService customerDataAccessService, EmailValidator emailValidator) {
        this.customerDataAccessService = customerDataAccessService;
        this.emailValidator = emailValidator;
    }

    void addNewCustomer(Customer customer) {
        addNewCustomer(null ,customer);
    }

    void addNewCustomer(UUID customerId, Customer customer) {
        UUID newCustomerId = Optional.ofNullable(customerId).orElse(UUID.randomUUID());

        // Validate email
        if (!emailValidator.test(customer.getEmail())) {
            throw new ApiRequestException(customer.getEmail() + " is not valid");
        };

        // Verify that email is not taken
        if (customerDataAccessService.isEmailTaken(customer.getEmail())) {
            throw new ApiRequestException(customer.getEmail() + " has already been taken");
        }

        customerDataAccessService.insertCustomer(newCustomerId, customer);
    }

    List<Customer> getAllCustomers() {
        return customerDataAccessService.selectAllCustomers();
    }


    public void updateCustomer(UUID customerId, Customer customer) {
        Optional.ofNullable(customer.getEmail())
                .ifPresent(email -> {
                    boolean taken = customerDataAccessService.selectExistsEmail(customerId, email);
                    if (!taken) {
                        customerDataAccessService.updateEmail(customerId, email);
                    } else {
                        throw new IllegalStateException("Email is already in use: " + customer.getEmail());
                    }
                });

        Optional.ofNullable(customer.getFirstName())
                .filter(firstName -> !StringUtils.isEmpty(firstName))
                .map(StringUtils::capitalize)
                .ifPresent(firstName -> customerDataAccessService.updateFirstName(customerId, firstName));

        Optional.ofNullable((customer.getLastName()))
                .filter(lastName -> !StringUtils.isEmpty(lastName))
                .map(StringUtils::capitalize)
                .ifPresent(lastName -> customerDataAccessService.updateLastName(customerId, lastName));
    }

    void deleteCustomer(UUID customerId) {
        customerDataAccessService.deleteCustomerById(customerId);
    }
}
