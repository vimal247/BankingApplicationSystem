package com.twozo.repository;

import com.twozo.enums.AccountType;
import com.twozo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Account findByAccountNumber(final String accountNumber);

    boolean existsByAccountNumber(final String accountNumber);

    @Query("SELECT COUNT(a) > 0 FROM Accounts a JOIN a.customers c WHERE " +
            "(:columnName = 'pan_card_number' AND c.panCardNumber = :value) OR " +
            "(:columnName = 'aadhar_number' AND c.aadharNumber = :value) OR " +
            "(:columnName = 'mobile_number' AND c.mobileNumber = :value) AND " +
            "a.accountType = :accountType")
    boolean existsByColumnAndAccountType(@Param("columnName") String columnName,
                                         @Param("value") String value,
                                         @Param("accountType") AccountType accountType);
}
