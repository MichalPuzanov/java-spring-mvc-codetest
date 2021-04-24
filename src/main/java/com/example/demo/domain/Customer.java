package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Customer {
    private long id;
    private String name;
    @JsonProperty("duetime")
    private ZonedDateTime dueTime;
    @JsonProperty("jointime")
    private ZonedDateTime joinTime;
}
