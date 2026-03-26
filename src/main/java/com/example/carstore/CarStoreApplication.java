package com.example.carstore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.carstore.repository.AccountRepository;

@SpringBootApplication
public class CarStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarStoreApplication.class, args);
    }

    @Bean
    CommandLineRunner verifyDb(AccountRepository accountRepository) {
        return args -> {
            try {
                long count = accountRepository.count();
                System.out.println("[DB] Account count = " + count);
                accountRepository.findAll().stream()
                        .limit(5)
                        .forEach(a -> System.out.println("[DB] Account username=" + a.getUsername()
                                + ", role=" + a.getRole()
                                ));
            } catch (Exception e) {
                System.out.println("[DB] Cannot read Account table: " + e.getMessage());
            }
        };
    }
}
