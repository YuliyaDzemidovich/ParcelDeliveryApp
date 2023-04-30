package com.github.yuliyadzemidovich.parceldeliveryapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

/**
 * {@link SpringBootServletInitializer} is an opinionated WebApplicationInitializer to run a SpringApplication
 * from a traditional WAR deployment. Binds Servlet, Filter and ServletContextInitializer beans
 * from the application context to the server.
 */
@SpringBootApplication
@EntityScan("com.github.yuliyadzemidovich.parceldeliveryapp") // enable @Entity classes auto scan
@PropertySource("classpath:secrets-${spring.profiles.active}.properties") // load additional property file with secrets
public class ParcelDeliveryAppApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ParcelDeliveryAppApplication.class, args);
    }

}
