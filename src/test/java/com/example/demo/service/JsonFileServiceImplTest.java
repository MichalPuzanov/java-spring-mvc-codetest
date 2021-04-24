package com.example.demo.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class JsonFileServiceImplTest {

    @TestConfiguration
    static class JsonFileServiceImplTestConfig {
        @Bean
        public JsonFileService jsonFileService() {
            return new JsonFileServiceImpl();
        }
    }

    @Autowired
    private JsonFileService jsonFileService;

    @Test
    public void uploadCustomersJson() {
        Integer numberOfRecords = null;
        try {
            numberOfRecords = jsonFileService.uploadCustomersJson().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        assertThat(numberOfRecords).isPositive();
        assertThat(jsonFileService.getCustomers().first().getId()).isEqualTo(10000053);
        assertThat(jsonFileService.getCustomers().first().getName()).isEqualTo("Anthony Chase");
        assertThat(jsonFileService.getCustomers().first().getDueTime()).isEqualTo("2013-12-14T03:37:10-08:00");
        assertThat(jsonFileService.getCustomers().first().getJoinTime()).isEqualTo("2015-08-18T00:29:11-07:00");
    }
}
