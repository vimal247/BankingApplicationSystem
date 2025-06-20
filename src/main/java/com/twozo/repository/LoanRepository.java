package com.twozo.repository;

import com.twozo.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, String> {

    Loan findByLoanNumber(String loanNumber);
}
