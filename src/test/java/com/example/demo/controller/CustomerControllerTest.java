package com.example.demo.controller;

import com.example.demo.domain.Customer;
import com.example.demo.service.JsonFileService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
@ActiveProfiles("test")
public class CustomerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JsonFileService jsonFileService;

    private SortedSet<Customer> customers;

    @Test
    public void findAllCustomers() throws Exception {
        customers = new TreeSet<>(Comparator.comparing(o -> o.getDueTime().toInstant()));
        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("John David");
        customer.setDueTime(ZonedDateTime.of(2010,7,15,10,43,23,0, ZoneOffset.ofHours(7)));
        customer.setJoinTime(ZonedDateTime.of(2012,5,16,11,34,23,0, ZoneOffset.ofHours(-5)));
        customers.add(customer);
        customer = new Customer();
        customer.setId(2);
        customer.setName("Ian Hope");
        customer.setDueTime(ZonedDateTime.of(2000,5,15,10,43,23,0, ZoneOffset.ofHours(8)));
        customer.setJoinTime(ZonedDateTime.of(2002,5,16,11,34,23,0, ZoneOffset.ofHours(-6)));
        customers.add(customer);
        when(jsonFileService.getCustomers()).thenReturn(customers);

        MvcResult mvcResult = mvc.perform(get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();
        mvcResult.getAsyncResult();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is("Ian Hope")))
                .andExpect(jsonPath("$[0].duetime", is("2000-05-15T10:43:23+08:00")))
                .andExpect(jsonPath("$[0].jointime", is("2002-05-16T11:34:23-06:00")));
    }

    @Test
    public void addCustomer() throws Exception {
        Customer customer = new Customer();
        customer.setId(3);
        customer.setName("Jack New");
        customer.setDueTime(ZonedDateTime.of(2013,7,15,10,43,23,0, ZoneOffset.ofHours(7)));
        customer.setJoinTime(ZonedDateTime.of(2013,5,16,11,34,23,0, ZoneOffset.ofHours(-5)));
        when(jsonFileService.addCustomer(any())).thenReturn(customer);

        MvcResult mvcResult = mvc.perform(post("/customers")
                .content(asJsonString(customer))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();
        mvcResult.getAsyncResult();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Jack New")))
                .andExpect(jsonPath("$.duetime", is("2013-07-15T10:43:23+07:00")))
                .andExpect(jsonPath("$.jointime", is("2013-05-16T11:34:23-05:00")));
    }

    @Test
    public void addCustomers() throws Exception {
        Customer customer = new Customer();
        customer.setId(3);
        customer.setName("Jack New");
        customer.setDueTime(ZonedDateTime.of(2013,7,15,10,43,23,0, ZoneOffset.ofHours(7)));
        customer.setJoinTime(ZonedDateTime.of(2013,5,16,11,34,23,0, ZoneOffset.ofHours(-5)));
        List<Customer> customersList = new ArrayList<>();
        customersList.add(customer);
        when(jsonFileService.addCustomerList(any())).thenReturn(customersList);

        MvcResult mvcResult = mvc.perform(post("/customers/upload")
                .content(asJsonString(customersList))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();
        mvcResult.getAsyncResult();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id", is(3)))
                .andExpect(jsonPath("$[0].name", is("Jack New")))
                .andExpect(jsonPath("$[0].duetime", is("2013-07-15T10:43:23+07:00")))
                .andExpect(jsonPath("$[0].jointime", is("2013-05-16T11:34:23-05:00")));
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(false));
            mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
