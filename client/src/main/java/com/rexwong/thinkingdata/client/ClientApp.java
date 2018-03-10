package com.rexwong.thinkingdata.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ClientApp  {
    public static void main(String[] args){
        SpringApplication.run(ClientApp.class, args);
    }
}