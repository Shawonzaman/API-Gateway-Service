package com.ibcs.idsdp.controller;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@RestController
public class SecondController {
    private static final Logger LOG = Logger.getLogger(SecondController.class.getName());


    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/employeeDetails/{employeeid}", method = RequestMethod.GET)
    @CircuitBreaker(name="statusBreak", fallbackMethod = "fallbackMethod")
    public String getStudents(@PathVariable int employeeid)
    {
        LOG.info("Inside zipkinService 1..");
        System.out.println("Getting Employee details for " + employeeid);

        String response = restTemplate.exchange("http://employee-service/findEmployeeDetails/{employeeid}",
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {}, employeeid).getBody();

        System.out.println("Response Body " + response);

        return "Employee Id -  " + employeeid + " [ Employee Details " + response+" ]";
    }

    public String  fallbackMethod(int employeeid,Throwable th){
        return "Fallback response:: No employee details available temporarily";
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
