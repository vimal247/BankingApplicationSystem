package com.control;


import com.service.AccountCreation;
import com.service.Loan;
import com.service.UserAccount;
import com.service.UserDatabase;
//import com.view.DisplayAccDetails;
public class Main { 

	  public static void main(String[] args) {
		  
		  // User Account Creation Using With the Constructor.
		  AccountCreation person1 = new AccountCreation("vimal","IOB123", 63824658,7465784,"Keela Surandai", "surandai", "IOB");
		//DisplayAccDetails person1Details= new DisplayAccDetails(person1);
		  UserDatabase.addData(person1); 
		  
		  // User Accessing The BankAccount.
		  UserAccount User1=new UserAccount("IOB123", 1000);
		  User1.deposit(1000);
		  UserDatabase.addUserData(User1);  
		 
		// User Creating, Another User Account.Using Constructor. 
		  AccountCreation person2 = new AccountCreation("vimal","IOB321", 63824658,7465784,"Keela Surandai", "surandai", "IOB");
		//DisplayAccDetails person2Details= new DisplayAccDetails(person2);
		  UserDatabase.addData(person2);
		  
		  UserAccount User2=new UserAccount("IOB321",1000);
		  UserDatabase.addUserData(User2);
		   
		  
		  // User Money Transfer with Using Constructor.
		 User1.transferMoneyTo(User2, 500);
		 User1.accountBalance();
		 User2.accountBalance();
		 System.out.println(User1);
		 System.out.println(User2);
		 
		 /* Loan Register And Sanction */
		 Loan loanUser1 = new Loan("IOB123", 200000, 1, "Home Loan");
		 loanUser1.LoanRegiter(loanUser1);
		 loanUser1.LoanSanction();
		 
	  } 
}
