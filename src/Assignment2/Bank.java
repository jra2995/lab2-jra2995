/**
 * Classes to model bank accounts, transactions and customers
 * Solves EE422C Programming Assignment #2
 * @author Jett Anderson
 * eid: jra2995
 * @version 1.00 2016-02-13
 */

package Assignment2;

import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Models a bank by holding information on customers, checking accounts, primary savings accounts,
 * student loan savings accounts, and auto loan savings accounts, as well as providing access to said
 * information. Also is used to prompt and process new customer info so their accounts can be opened
 * @author jra2995
 *
 */
public class Bank {
	//Instance fields
	/**
	 * List of customers at this bank with their information
	 */
	private ArrayList<Customer> customers;
	
	/**
	 * this bank's checking accounts for each customer
	 */
	private ArrayList<CheckingAccount> cAccounts;
	
	/**
	 * this bank's primary savings accounts for each customer
	 */
	private ArrayList<SavingsAccount> primaryAccounts;
	
	/**
	 * this bank's student loan savings accounts for each customer
	 */
	private ArrayList<SavingsAccount> studentLoanAccounts;
	
	/**
	 * this bank's auto loan savings accounts for each customer
	 */
	private ArrayList<SavingsAccount> autoLoanAccounts;
	
	//Constructors
	/**
	 * initializes a new bank with empty ArrayLists of customers, checking accounts, primary savings accounts,
	 * student loan accounts, and auto loan savings accounts
	 */
	public Bank(){
		customers = new ArrayList<Customer>();
		cAccounts = new ArrayList<CheckingAccount>();
		primaryAccounts = new ArrayList<SavingsAccount>();
		studentLoanAccounts = new ArrayList<SavingsAccount>();
		autoLoanAccounts = new ArrayList<SavingsAccount>();
	}
	
	/**
	 * initializes a new bank with information based on the input parameters provided
	 * @param c the list of customers that this bank will start out with
	 * @param ca the list of checking accounts this bank will start out with
	 * @param p the list of primary savings accounts this bank will start out with
	 * @param s the list of student loan savings accounts this bank will start out with
	 * @param a the list of auto loan savings accounts that this bank will start out with
	 */
	public Bank(ArrayList<Customer> c, ArrayList<CheckingAccount> ca, ArrayList<SavingsAccount> p, ArrayList<SavingsAccount> s, 
				ArrayList<SavingsAccount> a){
		customers = c;
		cAccounts = ca;
		primaryAccounts = p;
		studentLoanAccounts = s;
		autoLoanAccounts = a;
	}
	
	//Mutator methods
	/**
	 * Displays a welcome message to welcome any customers using this bank for transactions
	 */
	public void displayWelcome(){
		JOptionPane.showMessageDialog(null, "Welcome to this bank!", "Welcome", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * prompts user for their ID number and checks to make sure it is valid, i.e. positive
	 * @return -1 if the id is nonvalid, returns an int representing the user's positive ID number otherwise 
	 */
	public int promptForID(){
		int id = -1;
		String message = "";
		message = JOptionPane.showInputDialog(null, "Customer, what is your ID number?", "ID?", 
				JOptionPane.QUESTION_MESSAGE);
		
		// Checks to make sure either the id input is a valid positive number, or that it actually is a number and not
		// non-integer strings
 		try{
			id = Integer.parseInt(message);
			if(id < 1){
				JOptionPane.showMessageDialog(null, "We\'re sorry, this bank can only handle positive ID numbers.\nPlease try again.", 
						"Error - Not This Bank's Customer", JOptionPane.ERROR_MESSAGE);
				return -1;
			}
			return id;
		}
		catch(NumberFormatException nfe){
			if(message == null){
				return id;
			}
			JOptionPane.showMessageDialog(null, "We\'re sorry, the ID you have entered is invalid.\nPlease try again.", 
					"Error - Invalid ID", JOptionPane.ERROR_MESSAGE);
			return id;
		}
	}
	
	/**
	 * creates a customer object if possible and then creates appropriate accounts that will be linked to the current customer
	 * @param id the id number of the current customer that needs to be created
	 * @return false if the customer was not created due to input information errors, true if the customer was created or 
	 * already exists
	 */
	public boolean createCustomer(int id){
		// If this bank has no customers yet, create a new customer if possible, otherwise search if there already is a
		// customer with the id sent to this method
		if(customers.size() != 0){
			boolean customerExists = false;
			for(int i = 0; i < customers.size(); i++){
				if(customers.get(i).getID() == id){
					customerExists = true;
				}
			}
			
			// If the customer exists, do nothing, otherwise create a new customer with the correct id number and accounts
			// that correspond to the new customer
			if(!customerExists){
				Customer c = new Customer("", id, "");
				boolean custExists = promptCustInfo(id, c);
				if(!custExists){
					return false;
				}
				customers.add(c);
				CheckingAccount checking = new CheckingAccount(0);
				SavingsAccount primary = new SavingsAccount(0);
				SavingsAccount studentLoan = new SavingsAccount(0);
				SavingsAccount autoLoan = new SavingsAccount(0);
				checking.setOwner(c);
				primary.setOwner(c);
				studentLoan.setOwner(c);
				autoLoan.setOwner(c);
				cAccounts.add(checking);
				primaryAccounts.add(primary);
				studentLoanAccounts.add(studentLoan);
				autoLoanAccounts.add(autoLoan);
				return true;
			}
			return true;
		}
		else{
			Customer c = new Customer("", id, "");
			boolean custExists = promptCustInfo(id, c);
			if(!custExists){
				return false;
			}
			customers.add(c);
			CheckingAccount checking = new CheckingAccount(0);
			SavingsAccount primary = new SavingsAccount(0);
			SavingsAccount studentLoan = new SavingsAccount(0);
			SavingsAccount autoLoan = new SavingsAccount(0);
			checking.setOwner(c);
			primary.setOwner(c);
			studentLoan.setOwner(c);
			autoLoan.setOwner(c);
			cAccounts.add(checking);
			primaryAccounts.add(primary);
			studentLoanAccounts.add(studentLoan);
			autoLoanAccounts.add(autoLoan);
			return true;
		}
	}
	
	/**
	 * Prompts the current customer for the type of transaction to be made, and checks to see if it is a valid transaction
	 * that the bank's accounts can support
	 * @param id the id of the customer currently making a transaction
	 * @return a string with the transaction type, or an empty string if there was invalid input
	 */
	public String promptForTransaction(int id){
		String transactionMessage = "";
		transactionMessage = JOptionPane.showInputDialog(null, "Customer " + id + ", what transaction would you " + 
				"like to perform?" , "Transaction Type?", JOptionPane.QUESTION_MESSAGE);
		if(transactionMessage == null){
			return "";
		}
		
		// Checks for valid input, which is D deposit, T transfer, W withdraw, G get, or I interest
		if(!(transactionMessage.equals("D") || transactionMessage.equals("T") || transactionMessage.equals("W") || 
			transactionMessage.equals("G") || transactionMessage.equals("I"))){
			JOptionPane.showMessageDialog(null,  "We\'re sorry, the transaction type you have entered is invalid.\n" + 
					"Please try again.", "Error - Invalid Transaction Type", JOptionPane.ERROR_MESSAGE);
			return "";
		}
		
		return transactionMessage;
	}
	
	/**
	 * Prompts the current customer for the amount if necessary as part of the transaction to be made, and
	 * checks to see if the amount input is valid, ie non-negative and an actual number
	 * @param id the id number of the current customer making a transaction
	 * @return -1 if the amount entered is not valid or negative, or the double value amount that is entered
	 * by the customer
	 */
	public double promptForAmount(int id){
		String amountMessage = "";
		amountMessage = JOptionPane.showInputDialog(null, "Customer " + id + ", by how much?", 
				"Amount?", JOptionPane.QUESTION_MESSAGE);
		if(amountMessage == null){
			return -1;
		}
		
		double amount = 0.0;
		
		// Checks to see if the amount entered is a double first, then if the amount is non-negative
		try{
			amount = Double.parseDouble(amountMessage);
			if(amount < 0){
				JOptionPane.showMessageDialog(null, "We\'re sorry, the amount you have entered is negative.\nPlease " + 
						"try again.", "Error - Negative Amount", JOptionPane.ERROR_MESSAGE);
				return -1;
			}
			return amount;
		}
		catch(NumberFormatException nfe){
			JOptionPane.showMessageDialog(null, "We\'re sorry, the amount you have entered is invalid.\nPlease try again.", 
					"Error - Invalid Amount", JOptionPane.ERROR_MESSAGE);
			return -1;
		}
	}
	
	/**
	 * Prompts the customer for the account type, it can either be a checking account, primary savings account, 
	 * student loan savings account, and auto loan savings account. Also checks to see if the account type 
	 * is valid
	 * @param id the id number of the current customer making a transaction
	 * @return a blank string if there is invalid input, or the account type that the customer selects as appropriate
	 */
	public String promptForAcct1Type(int id){
		String acctType1 = "";
		acctType1 = JOptionPane.showInputDialog(null, "Customer " + id + ", what type is your account?", 
			"Account Type?", JOptionPane.QUESTION_MESSAGE);
		if(acctType1 == null){
			return "";
		}
		
		// Checks to see if the account type entered by the user is one of the supported account types of the bank
		if(!(acctType1.equals("C") || acctType1.equals("L") || acctType1.equals("S") || acctType1.equals("A"))){
			JOptionPane.showMessageDialog(null, "We\'re sorry, the account type you have entered is invalid.\n" + 
					"Please try again.", "Error - Invalid Account Type", JOptionPane.ERROR_MESSAGE);
			return "";
		}
		
		return acctType1;
	}
	
	/**
	 * Prompts the customer for the second account type if necessary, it can either be a checking account, primary savings account, 
	 * student loan savings account, and auto loan savings account. Also checks to see if the account type 
	 * is valid
	 * @param id the id number of the current customer making a transaction
	 * @return a blank string if there is invalid input, or the account type that the customer selects as appropriate
	 */
	public String promptForAcct2Type(int id, String transactionMessage){
		String acctType2 = "";
		if(transactionMessage.equals("T")){
			acctType2 = JOptionPane.showInputDialog(null, "Customer " + id + ", what is the second account type?", 
					"Second Account Type?", JOptionPane.QUESTION_MESSAGE);
			if(acctType2 == null){
				return "";
			}
			
			// Checks to see if the account type entered by the user is one of the supported account types of the bank
			if(!(acctType2.equals("C") || acctType2.equals("L") || acctType2.equals("S") || acctType2.equals("A"))){
				JOptionPane.showMessageDialog(null, "We\'re sorry, the account type you have entered is invalid.\n" + 
						"Please try again.", "Error - Invalid Account Type", JOptionPane.ERROR_MESSAGE);
				return "";
			}
		}
		
		return acctType2;
	}
	
	/**
	 * Prompts the current customer for their information, namely their name and address
	 * @param id the id number of the current customer
	 * @param customer the Customer object that will hold this current customer's information entered
	 * @return false if there was an error with the information input process, true if information was entered at all
	 * as opposed to having a cancel button on the JOptionPane window being pushed
	 */
	public boolean promptCustInfo(int id, Customer customer){
		String custName1 = "";
		custName1 = JOptionPane.showInputDialog(null, "What is your name, Customer " + id + "?", 
				"Name?", JOptionPane.QUESTION_MESSAGE);
		if(custName1 == null){
			return false;
		}
		customer.setName(custName1);
		String address1 = "";
		address1 = JOptionPane.showInputDialog(null, "What is your address?", 
				"Address?", JOptionPane.QUESTION_MESSAGE);
		if(address1 == null){
			return false;
		}
		customer.setAddress(address1);
		return true;
	}
	
	/**
	 * Displays a summary for all customers of this bank, in which a window with all of their account balances are
	 * brought up.
	 */
	public void processSummaries(){
		if(customers == null){
			return;
		}
		
		// Cycles through all customers of the bank and displays every account's balance of each customer and all
		// customer information related to that customer
		for(int i = 0; i < customers.size(); i++){
			String name = customers.get(i).getName();
			int id = customers.get(i).getID();
			String address = customers.get(i).getName();
			if(name == ""){
				name = "Not Specified";
			}
			if(address == ""){
				address = "Not Specified";
			}
			CheckingAccount check = cAccounts.get(i);
			SavingsAccount primary = primaryAccounts.get(i);
			SavingsAccount autoLoanRepay = autoLoanAccounts.get(i);
			SavingsAccount studentLoanRepay = studentLoanAccounts.get(i);
			JOptionPane.showMessageDialog(null,  "Name: " + name + "\nCustomer ID: " + id + "\nAddress: " + 
					address + "\nHere is your summary:\nThe " + 
					"final balance of the checking account is $" + String.format("%.2f", check.getBalance()) + 
					".\nThe final balance of the primary savings account is $" + String.format("%.2f", primary.getBalance()) + 
					".\nThe final balance of the auto loan repayment savings account is $" + 
					String.format("%.2f", autoLoanRepay.getBalance()) + ".\nThe final balance of the student loan " + 
					"repayment savings account is $" + String.format("%.2f", studentLoanRepay.getBalance()) + 
					".", "Final Summary of Accounts", JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
	
	/**
	 * Asks the customer if there are any more transactions that need to be made
	 * @return an int representing a JOptionPane confirm dialog option, either YES_OPTION or NO_OPTION or CLOSED_OPTION
	 */
	public int checkForEndOfTransactions(){
		int closeOption = JOptionPane.showConfirmDialog(null,  "Do you want to continue making transactions?", 
				"Continue Transactions?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		return closeOption;
	}
	
	/**
	 * Closes resources related to JOptionPane windows that are used for the bank
	 */
	public void closeBank(){
		System.exit(0);
	}
	
	//Accessor methods
	/**
	 * Gets the checking account related to a specified customer ID
	 * @param id the id of the customer that owns the checking account to be found
	 * @return the checking account of the owner specified
	 */
	public CheckingAccount getCheckingAccount(int id){
		if(cAccounts == null){
			return null;
		}
		
		// Cycles through the checking accounts of this bank to find the one that the specified customer owns
		for(int i = 0; i < cAccounts.size(); i++){
			if(cAccounts.get(i).getOwner().getID() == id){
				return cAccounts.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Gets the primary savings account related to a specified customer ID
	 * @param id the id of the customer that owns the primary savings account to be found
	 * @return the primary savings account of the owner specified
	 */
	public SavingsAccount getPrimaryAccount(int id){
		if(primaryAccounts == null){
			return null;
		}
		
		// Cycles through the primary savings accounts of this bank to find the one that the specified customer owns
		for(int i = 0; i < primaryAccounts.size(); i++){
			if(primaryAccounts.get(i).getOwner().getID() == id){
				return primaryAccounts.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Gets the student loan savings account related to a specified customer ID
	 * @param id the id of the customer that owns the student loan savings account to be found
	 * @return the student loan savings account of the owner specified
	 */
	public SavingsAccount getStudentLoanAccount(int id){
		if(studentLoanAccounts == null){
			return null;
		}
		
		// Cycles through the student loan savings accounts of this bank to find the one that the specified customer owns
		for(int i = 0; i < studentLoanAccounts.size(); i++){
			if(studentLoanAccounts.get(i).getOwner().getID() == id){
				return studentLoanAccounts.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Gets the auto loan savings account related to a specified customer ID
	 * @param id the id of the customer that owns the auto loan savings account to be found
	 * @return the auto loan savings account of the owner specified
	 */
	public SavingsAccount getAutoLoanAccount(int id){
		if(autoLoanAccounts == null){
			return null;
		}
		
		// Cycles through the auto loan savings accounts of this bank to find the one that the specified customer owns
		for(int i = 0; i < autoLoanAccounts.size(); i++){
			if(autoLoanAccounts.get(i).getOwner().getID() == id){
				return autoLoanAccounts.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Gets the list of customers that this bank has as clients
	 * @return the list of customers that this bank has
	 */
	public ArrayList<Customer> getCustomers(){
		return customers;
	}
	
	/**
	 * Gets the list of checking accounts that this bank has
	 * @return the list of checking accounts that this bank has
	 */
	public ArrayList<CheckingAccount> getCheckingAccounts(){
		return cAccounts;
	}
	
	/**
	 * Gets the list of the primary accounts that this bank has
	 * @return the list of primary savings accounts that this bank has
	 */
	public ArrayList<SavingsAccount> getPrimaryAccounts(){
		return primaryAccounts;
	}
	
	/**
	 * Gets the list of the student loan savings accounts that this bank has
	 * @return the list of the student loan savings accounts that this bank has
	 */
	public ArrayList<SavingsAccount> getStudentLoanAccounts(){
		return studentLoanAccounts;
	}
	
	/**
	 * Gets the list of the auto loan savings accounts that this bank has
	 * @return the list of the auto loan savings accounts that this bank has
	 */
	public ArrayList<SavingsAccount> getAutoLoanAccounts(){
		return autoLoanAccounts;
	}
	
	/**
	 * Sets the list of customers that this bank has to the specified input
	 * @param c the list of customers that this bank will now have
	 */
	public void setCustomers(ArrayList<Customer> c){
		customers = c;
	}
	
	/**
	 * Sets the list of checking accounts that this bank has to the specified input
	 * @param ca the list of checking accounts that this bank will now have
	 */
	public void setCheckingAccounts(ArrayList<CheckingAccount> ca){
		cAccounts = ca;
	}
	
	/**
	 * Sets the list of primary accounts that this bank has to the specified input
	 * @param p the list of primary savings accounts that this bank will now have
	 */
	public void setPrimaryAccounts(ArrayList<SavingsAccount> p){
		primaryAccounts = p;
	}
	
	/**
	 * Sets the list of auto loan accounts that this bank has to specified input
	 * @param a the list of auto loan savings accounts that this bank will now have
	 */
	public void setAutoLoanAccounts(ArrayList<SavingsAccount> a){
		autoLoanAccounts = a;
	}
	
	/**
	 * Sets the list of student loan accounts that this bank has to specified input
	 * @param s the list of student loan savings accounts that this bank will now have
	 */
	public void setStudentLoanAccounts(ArrayList<SavingsAccount> s){
		studentLoanAccounts = s;
	}
}
