package com.twozo.controller.impl;

import com.twozo.controller.BankController;
import com.twozo.service.BankService;
import com.twozo.service.impl.TenkasiBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TenkasiBankController extends BankController {

	private final BankService bankService;

	@Autowired
	public TenkasiBankController(final TenkasiBankService tenkasiBankService){
		this.bankService = tenkasiBankService;
	}

	@Override
	public final BankService getBankService() {return bankService;}
}