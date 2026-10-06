package org.example.adminsky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdminSkyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminSkyApplication.class, args);
    }

}
