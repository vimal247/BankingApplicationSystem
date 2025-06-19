package com.twozo.controller.impl;

import com.twozo.controller.BankController;
import com.twozo.service.BankService;
import com.twozo.service.impl.TenkasiBankService;

public class TenkasiBankController extends BankController {
	
	private static BankService bankService;
	private static BankController instance; 
 
	// Singleton Method For TenkasiBankController, For Only One Instance.
	public static final BankController getInstance() {
		if (instance == null) {
			instance = new TenkasiBankController();
		}
		return instance; 
	}

	// Private Constructor To Restrict Instantiation Outside The Class.
	private TenkasiBankController() {

	} 

	@Override
	public final BankService getBankService() { 
		if (bankService == null) {
			bankService = TenkasiBankService.getInstance();
		}
		return bankService;
	}
}
