package com.twozo.repository;

import com.twozo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query(value = "SELECT * FROM Customers WHERE id = (SELECT customer_id FROM Accounts WHERE account_number = ?1)", nativeQuery = true)
    Customer findCustomerByAccountNumber(final String accountNumber);
}
