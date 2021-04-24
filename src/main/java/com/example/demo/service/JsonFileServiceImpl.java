package com.example.demo.service;

import com.example.demo.domain.Customer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

@Service
public class JsonFileServiceImpl implements JsonFileService{

    private SortedSet<Customer> customers;

    @Override
    public SortedSet<Customer> getCustomers() {
        if (this.customers == null){
            this.customers = new TreeSet<>(Comparator.comparing(o -> o.getDueTime().toInstant()));
        }
        return this.customers;
    }

    @Override
    @Async
    public CompletableFuture<Integer> uploadCustomersJson() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(false));
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        TypeReference<List<Customer>> typeReference = new TypeReference<List<Customer>>(){};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/customers.json");
        try {
            List<Customer> customerList = mapper.readValue(inputStream,typeReference);
            getCustomers().addAll(customerList);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(this.customers.size());
    }

    public Customer addCustomer(Customer customer){
        getCustomers().add(customer);
        return customer;
    }

    @Override
    public List<Customer> addCustomerList(List<Customer> customers) {
        getCustomers().addAll(customers);
        return customers;
    }
}

