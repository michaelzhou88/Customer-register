package com.michaelzhou.fullstackregister.customer;

import com.michaelzhou.fullstackregister.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/*
    Customer controller class:
    1. Create server endpoint to retrieve customers
    2. Return to the client a list of customers in JSON format
 */
@RestController
@RequestMapping("customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        try {
            return customerService.getAllCustomers();
        }
        catch(Exception e) {
            throw new ApiRequestException("Oops cannot get all customers. " + e);
        }
    }

    @PostMapping
    public void addNewCustomer(@RequestBody @Valid Customer customer) {
        customerService.addNewCustomer(customer);
    }

    @PutMapping(path = "{customerId}")
    public void updateCustomer(@PathVariable("customerId") UUID customerId,
                              @RequestBody Customer customer) {
        customerService.updateCustomer(customerId, customer);
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") UUID customerId) {
        customerService.deleteCustomer(customerId);
    }

}
