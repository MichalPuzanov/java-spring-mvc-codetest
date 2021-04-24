package com.example.demo.service;

import com.example.demo.domain.Customer;

import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.CompletableFuture;

public interface JsonFileService {

    CompletableFuture<Integer> uploadCustomersJson();
    SortedSet<Customer> getCustomers();
    Customer addCustomer(Customer customer);
    List<Customer> addCustomerList(List<Customer> customers);
}
