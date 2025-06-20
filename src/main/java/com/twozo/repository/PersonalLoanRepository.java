package com.twozo.repository;

import com.twozo.model.PersonalLoan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalLoanRepository extends JpaRepository<PersonalLoan, Long> {
}
