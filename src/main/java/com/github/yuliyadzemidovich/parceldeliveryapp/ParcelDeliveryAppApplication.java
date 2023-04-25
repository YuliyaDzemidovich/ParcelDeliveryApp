package com.github.yuliyadzemidovich.parceldeliveryapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * {@link SpringBootServletInitializer} is an opinionated WebApplicationInitializer to run a SpringApplication
 * from a traditional WAR deployment. Binds Servlet, Filter and ServletContextInitializer beans
 * from the application context to the server.
 */
@SpringBootApplication
@EntityScan("com.github.yuliyadzemidovich.parceldeliveryapp")
public class ParcelDeliveryAppApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ParcelDeliveryAppApplication.class, args);
    }

}
