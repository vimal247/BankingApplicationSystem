package com.twozo.repository;

import com.twozo.model.Account;
import com.twozo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Account findByAccountNumber(final String accountNumber);
}
