package com.twozo.controller.impl;

import com.twozo.controller.BankController;
import com.twozo.service.BankService;
import com.twozo.service.impl.TirunelveliBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TirunelveliBankController extends BankController {

	private final BankService bankService;

	@Autowired
	public TirunelveliBankController(final TirunelveliBankService tirunelveliBankService) {
		this.bankService = tirunelveliBankService;
	}

	@Override
	public final BankService getBankService() {
		return bankService;
	}
}
