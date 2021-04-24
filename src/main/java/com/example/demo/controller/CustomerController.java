package com.example.demo.controller;

import com.example.demo.domain.Customer;
import com.example.demo.service.JsonFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
public class CustomerController {

    private final JsonFileService jsonFileService;

    public CustomerController(JsonFileService jsonFileService) {
        this.jsonFileService = jsonFileService;
    }

    @GetMapping("/customers")
    @Async
    public CompletableFuture<Set<Customer>> findAllCustomers(){
        return CompletableFuture.completedFuture(jsonFileService.getCustomers());
    }

    @PostMapping("/customers")
    @Async
    public CompletableFuture<ResponseEntity<Customer>> addCustomer(
            @RequestBody Customer customer){
        jsonFileService.addCustomer(customer);
        return CompletableFuture.completedFuture(new ResponseEntity<>(customer, HttpStatus.CREATED));
    }

    @PostMapping("/customers/upload")
    @Async
    public CompletableFuture<ResponseEntity<List<Customer>>> addCustomers(
            @RequestBody List<Customer> customers){
        jsonFileService.addCustomerList(customers);
        return CompletableFuture.completedFuture(new ResponseEntity<>(customers, HttpStatus.CREATED));
    }


}
