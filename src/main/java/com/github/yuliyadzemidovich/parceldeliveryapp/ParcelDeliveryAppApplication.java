package com.github.yuliyadzemidovich.parceldeliveryapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.github.yuliyadzemidovich.parceldeliveryapp")
public class ParcelDeliveryAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParcelDeliveryAppApplication.class, args);
    }

}
