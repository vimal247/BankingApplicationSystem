package com.twozo.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingApplicationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingApplicationSystemApplication.class, args);
        System.out.println("\n Application Started.");
    }
}