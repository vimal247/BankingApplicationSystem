package com.twozo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface InquiryRepository extends CrudRepository<Object, Long> {

    @Query("SELECT a.balance FROM Account a WHERE a.accountNumber = :accountNumber")
    Double findBalanceByAccountNumber(String accountNumber);
}
