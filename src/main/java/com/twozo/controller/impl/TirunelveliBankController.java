package com.twozo.controller.impl;

import com.twozo.controller.BankController;
import com.twozo.service.BankService;
import com.twozo.service.impl.TirunelveliBankService;

public class TirunelveliBankController extends BankController {
	
	private static BankService bankService;
	private static BankController instance;

	// Singleton Method For TirunelveliBankController, For Only One Instance.
	public static final BankController getInstance() {
		if (instance == null) {
			instance = new TirunelveliBankController();
		}
		return instance;
	}

	// Private Constructor To Restrict Instantiation Outside The Class.
	private TirunelveliBankController() {
		
	}

	@Override
	public final BankService getBankService() {
		if (bankService == null) {
			bankService = TirunelveliBankService.getInstance();
		}
		return bankService;
	}
}
