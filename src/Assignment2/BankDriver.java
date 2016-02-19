/**
 * Classes to model bank accounts, transactions and customers
 * Solves EE422C Programming Assignment #2
 * @author Jett Anderson
 * eid: jra2995
 * @version 1.00 2016-02-13
 */

package Assignment2;

import javax.swing.JOptionPane;

/**
 * Purpose is to simulate a bank with multiple customers, types of accounts, and input/output responses
 * for various types of transactions like depositing, transferring, and withdrawing. Mostly handles I/O in 
 * JOptionPane for the transactions.
 * @author jra2995
 *
 */
public class BankDriver {

	public static void main(String[] args) {
		Bank bank = new Bank();
		
		bank.displayWelcome();
		
		// Based on whether or not a user is done with the transaction window, or chooses to say there are no more 
		// transactions to be made, the program will execute so long as the user doesn't say No to more data.
		// This loop controls the prompts and transaction IO
		do{
			// Gets ID from the current customer and checks if it's valid
			int id = bank.promptForID();
			if(id == -1){
				continue;
			}
			
			// Gets Customer Info and checks to see if it was actually created or not
			boolean customerCreated = bank.createCustomer(id);
			if(!customerCreated){
				continue;
			}
			
			// Gets type of transaction and safeguards against invalid input
			String transactionMessage = bank.promptForTransaction(id);
			if(transactionMessage == ""){
				continue;
			}
			
			// Checks the amount for the transaction if applicable (so not interest or get operations)
			// and to see if it's valid input
			double amount = 0.0;
			if(!transactionMessage.equals("I") && !transactionMessage.equals("G")){
				amount = bank.promptForAmount(id);
				if(amount == -1){
					continue;
				}
			}
			
			// Gets the account type for the transaction and checks for invalid type
			String acctType1 = bank.promptForAcct1Type(id);
			if(acctType1 == null){
				continue;
			}
			
			// If a second account is needed (only for transfer transactions), checks for type of second account
			// and for invalid input
			String acctType2 = bank.promptForAcct2Type(id, transactionMessage);
			if(acctType2 == null){
				continue;
			}
			
			// Main logic for this driver happens here
			// Checks the transaction type, followed by account types and begins to complete transactions and display IO
			if(transactionMessage.equals("D")){
				// Based on type of account, deposits the amount specified by the customer into their appropriate account
				if(acctType1.equals("C")){
					CheckingAccount check = bank.getCheckingAccount(id);
					double oldBalance = check.getBalance();
					int res = check.deposit(amount);
					if(res == 0){
						JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
								"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
					}
					String name = check.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldBalance) + " in your checking " + 
							"account.\nYou now have $" + String.format("%.2f", check.getBalance()) + ".", "Deposit Confirmed for Checking Account", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else if(acctType1.equals("S")){
					SavingsAccount primary = bank.getPrimaryAccount(id);
					double oldBalance = primary.getBalance();
					int res = primary.deposit(amount);
					if(res == 0){
						JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
								"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
					}
					String name = primary.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldBalance) + " in your primary savings " + 
							"account.\nYou now have $" + String.format("%.2f", primary.getBalance()) + ".", "Deposit Confirmed for Primary Savings Account", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else if(acctType1.equals("A")){
					SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
					double oldBalance = autoLoanRepay.getBalance();
					int res = autoLoanRepay.deposit(amount);
					if(res == 0){
						JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
								"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
					}
					String name = autoLoanRepay.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldBalance) + " in your auto loan " + 
							"savings account.\nYou now have $" + String.format("%.2f", autoLoanRepay.getBalance()) + ".", 
							"Deposit Confirmed for Auto Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
				}
				else if(acctType1.equals("L")){
					SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
					double oldBalance = studentLoanRepay.getBalance();
					int res = studentLoanRepay.deposit(amount);
					if(res == 0){
						JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
								"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
					}
					String name = studentLoanRepay.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldBalance) + " in your student loan " + 
							"savings account.\nYou now have $" + String.format("%.2f", studentLoanRepay.getBalance()) + ".", 
							"Deposit Confirmed for Student Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else if(transactionMessage.equals("W")){
				// Based on the type of account, withdraws money from account if balance can withstand it, otherwise
				// displays a message saying the account doesn't have enough money to support the withdrawal
				if(acctType1.equals("C")){
					// For withdrawals from a checking account, we check to see if any possible overdraft can be cleared 
					// with money from the corresponding primary savings account, otherwise the withdrawal will not go through.
					// Though it if goes through naturally, then the amount is just withdrawn from the checking account
					try{
						CheckingAccount check = bank.getCheckingAccount(id);
						SavingsAccount primary = bank.getPrimaryAccount(id);
						double overdraft = amount - check.getBalance() + CheckingAccount.TRANSFER_FEE;
						double oldSavingsBalance = primary.getBalance();
						double oldBalance = check.getBalance();
						check.withdraw(amount, primary);
						String name = check.getOwner().getName();
						
						// Checks to see if the balance of the savings account changed, in which case an overdraft was cleared
						if(oldSavingsBalance != primary.getBalance()){
							JOptionPane.showMessageDialog(null, "You attempted to withdraw $" + String.format("%.2f", amount) + " from your checking " + 
									"account, but you only had $" + String.format("%.2f", oldBalance) + ".\nWe have automatically transferred $" + 
									String.format("%.2f", overdraft) + " from your primary savings account, including a $20 overdraft fee.", 
									"Overdraft Cleared", JOptionPane.INFORMATION_MESSAGE);
							JOptionPane.showMessageDialog(null,  name + ", you had $" + String.format("%.2f", oldBalance) + " in your checking " + 
									"account.\nYou now have $" + String.format("%.2f", check.getBalance()) + ".\nYou had $" + 
									String.format("%.2f", oldSavingsBalance) + " in your primary savings account.\nYou now have $" + 
									String.format("%.2f",  primary.getBalance()) + ".", "Overdraft Cleared", 
									JOptionPane.INFORMATION_MESSAGE);
						}
						else{
							JOptionPane.showMessageDialog(null,  name + ", you had $" + String.format("%.2f", oldBalance) + " in your checking " + 
									"account.\nYou now have $" + String.format("%.2f", check.getBalance()) + ".", "Withdrawal Confirmed for Checking Account", 
									JOptionPane.INFORMATION_MESSAGE);
						}
						
					}
					catch(OverdraftException oe){
						JOptionPane.showMessageDialog(null, "We\'re sorry, you attempted to withdraw too much from your " + 
								"checking account.\nYour primary savings account didn\'t have enough to cover the overdraft." + 
								"\nPlease try again.", "Error - Insufficient Overdraft Funds", JOptionPane.ERROR_MESSAGE);
						
					}
					catch(WithdrawException we){
						JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the withdrawal." + 
								"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(acctType1.equals("S")){
					try{
						SavingsAccount primary = bank.getPrimaryAccount(id);
						double oldBalance = primary.getBalance();
						primary.withdraw(amount);
						String name = primary.getOwner().getName();
						JOptionPane.showMessageDialog(null,  name + ", you had $" + String.format("%.2f", oldBalance) + " in your primary " + 
								"savings account.\nYou now have $" + String.format("%.2f", primary.getBalance()) + ".", "Withdrawal Confirmed for " + 
								"Primary Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					catch(WithdrawException we){
						JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the withdrawal." + 
								"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(acctType1.equals("L")){
					try{
						SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
						double oldBalance = studentLoanRepay.getBalance();
						studentLoanRepay.withdraw(amount);
						String name = studentLoanRepay.getOwner().getName();
						JOptionPane.showMessageDialog(null,  name + ", you had $" + String.format("%.2f", oldBalance) + " in your student " + 
								"loan savings account.\nYou now have $" + String.format("%.2f", studentLoanRepay.getBalance()) + ".", 
								"Withdrawal Confirmed for Student Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					catch(WithdrawException we){
						JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the withdrawal." + 
								"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(acctType1.equals("A")){
					try{
						SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
						double oldBalance = autoLoanRepay.getBalance();
						autoLoanRepay.withdraw(amount);
						String name = autoLoanRepay.getOwner().getName();
						JOptionPane.showMessageDialog(null,  name + ", you had $" + String.format("%.2f", oldBalance) + " in your auto " + 
								"loan savings account.\nYou now have $" + String.format("%.2f", autoLoanRepay.getBalance()) + ".", 
								"Withdrawal Confirmed for Auto Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					catch(WithdrawException we){
						JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the withdrawal." + 
								"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else if(transactionMessage.equals("I")){
				// If account type is a savings account of some sort, interest can be added. So we perform the interest operation
				// on these accounts and display the result, whether interest was added or not to the balance.
				if(acctType1.equals("S")){
					SavingsAccount primary = bank.getPrimaryAccount(id);
					double oldBalance = primary.getBalance();
					primary.addInterest();
					String name = primary.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldBalance) + 
							" in your primary savings account.\nYou now have $" + String.format("%.2f", primary.getBalance()) + ".", 
							"Interest for Primary Savings Account", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else if(acctType1.equals("L")){
					SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
					double oldBalance = studentLoanRepay.getBalance();
					studentLoanRepay.addInterest();
					String name = studentLoanRepay.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldBalance) + 
							" in your student loan savings account.\nYou now have $" + String.format("%.2f", studentLoanRepay.getBalance()) + ".", 
							"Interest for Student Loan Savings Account", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else if(acctType1.equals("A")){
					SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
					double oldBalance = autoLoanRepay.getBalance();
					autoLoanRepay.addInterest();
					String name = autoLoanRepay.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldBalance) + 
							" in your auto loan savings account.\nYou now have $" + String.format("%.2f", autoLoanRepay.getBalance()) + ".", 
							"Interest for Auto Loan Savings Account", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else if(acctType1.equals("C")){
					JOptionPane.showMessageDialog(null, "We\'re sorry, checking accounts do not generate interest.\n" + 
							"Please try again.", "Error - Non-Savings Account Interest", JOptionPane.ERROR_MESSAGE);
				}
			}
			else if(transactionMessage.equals("G")){
				// Based on the account type, gets current balance from the appropriate account and displays it for the customer
				if(acctType1.equals("S")){
					SavingsAccount primary = bank.getPrimaryAccount(id);
					String name = primary.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you have $" + String.format("%.2f", primary.getBalance()) + 
							" in your primary savings account.", "Balance for Primary Savings Account", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else if(acctType1.equals("L")){
					SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
					String name = studentLoanRepay.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you have $" + String.format("%.2f", studentLoanRepay.getBalance()) + 
							" in your student loan savings account.", "Balance for Student Loan Savings Account", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else if(acctType1.equals("A")){
					SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
					String name = autoLoanRepay.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you have $" + String.format("%.2f", autoLoanRepay.getBalance()) + 
							" in your auto loan savings account.", "Balance for Auto Loan Savings Account", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else if(acctType1.equals("C")){
					CheckingAccount check = bank.getCheckingAccount(id);
					String name = check.getOwner().getName();
					JOptionPane.showMessageDialog(null, name + ", you have $" + String.format("%.2f", check.getBalance()) + 
							" in your checking account.", "Balance for Checking Account", 
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else if(transactionMessage.equals("T")){
				// Based on the account types, a transfer occurs from the first account to the second account of the customer.
				// If a withdrawal can be made from the first account without error or an overdraft failure in the case of 
				// withdrawal from a checking account, then the deposit will go through to the second account and the transaction
				// will be complete.
				if(acctType1.equals("C")){
					// Based on the second account, this determines where the withdrawn money goes, if the withdrawal 
					// can happen
					if(acctType2.equals("C")){
						CheckingAccount check = bank.getCheckingAccount(id);
						double oldBalance = check.getBalance();
						double overdraft = amount - check.getBalance() - CheckingAccount.TRANSFER_FEE;
						int tran = check.transfer(check, amount);
						String name = check.getOwner().getName();
						// If the transaction can't go through, display transfer can't happen, otherwise display that an
						// overdraft was cleared, or that the withdrawal went clear through.
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "You attempted to withdraw $" + String.format("%.2f", amount) + " from your checking " + 
									"account, but you only had $" + String.format("%.2f", oldBalance) + ".\nWe have automatically transferred $" + 
									String.format("%.2f", overdraft) + " from your primary savings account, including a $20 overdraft fee.", 
									"Overdraft Cleared", JOptionPane.INFORMATION_MESSAGE);
							JOptionPane.showMessageDialog(null,  name + ", you had $" + String.format("%.2f", oldBalance) + " in your checking " + 
									"account.\nYou now have $" + String.format("%.2f", check.getBalance()) + ".\nYou had $" + 
									String.format("%.2f", oldBalance) + " in your checking account.\nYou now have $" + 
									String.format("%.2f",  check.getBalance()) + ".", "Overdraft Cleared", 
									JOptionPane.INFORMATION_MESSAGE);
						}
						else if(tran == 2){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						else{
							JOptionPane.showMessageDialog(null,  name + ", you had $" + String.format("%.2f", oldBalance) + " in your checking " + 
									"account.\nYou now have $" + String.format("%.2f", check.getBalance()) + ".", "Transfer Confirmed for Checking Account", 
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
					else if(acctType2.equals("S")){
						CheckingAccount check = bank.getCheckingAccount(id);
						SavingsAccount primary = bank.getPrimaryAccount(id);
						double oldPBalance = primary.getBalance();
						double oldCBalance = check.getBalance();
						double overdraft = amount - check.getBalance() + CheckingAccount.TRANSFER_FEE;
						int tran = check.transfer(primary, amount, primary);
						
						// If withdrawal can't go through, transfer doesn't happen. Otherwise, transfer happens with an overdraft, 
						// or without one and the results are displayed.
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "You attempted to withdraw $" + String.format("%.2f", amount) + " from your checking " + 
									"account, but you only had $" + String.format("%.2f", oldCBalance) + ".\nWe have automatically transferred $" + 
									String.format("%.2f", overdraft) + " from your primary savings account, including a $20 overdraft fee.\n" + 
									"Your primary savings account now has $" + String.format("%.2f", primary.getBalance()) + ".", 
									"Overdraft Cleared", JOptionPane.INFORMATION_MESSAGE);
						}
						else if(tran == 2){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = check.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldCBalance) + 
								" in your checking account. You now have $" + String.format("%.2f", check.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldPBalance) + " in your primary savings " + 
								"account.\nYou now have $" + String.format("%.2f", primary.getBalance()) + ".", 
								"Transfer Confirmed for Primary Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("A")){
						CheckingAccount check = bank.getCheckingAccount(id);
						SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
						SavingsAccount primary = bank.getPrimaryAccount(id);
						double oldABalance = autoLoanRepay.getBalance();
						double oldCBalance = check.getBalance();
						double overdraft = amount - check.getBalance() - CheckingAccount.TRANSFER_FEE;
						int tran = check.transfer(autoLoanRepay, amount, primary);
						
						// If withdrawal can't go through, transfer doesn't happen. Otherwise, transfer happens with an overdraft, 
						// or without one and the results are displayed.
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "You attempted to withdraw $" + String.format("%.2f", amount) + " from your checking " + 
									"account, but you only had $" + String.format("%.2f", oldCBalance) + ".\nWe have automatically transferred $" + 
									String.format("%.2f", overdraft) + " from your primary savings account, including a $20 overdraft fee.\n" + 
									"Your primary savings account now has $" + String.format("%.2f", primary.getBalance()) + ".", 
									"Overdraft Cleared", JOptionPane.INFORMATION_MESSAGE);
						}
						else if(tran == 2){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = check.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldCBalance) + 
								" in your checking account. You now have $" + String.format("%.2f", check.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldABalance) + " in your auto loan savings " + 
								"account.\nYou now have $" + String.format("%.2f", autoLoanRepay.getBalance()) + ".", 
								"Transfer Confirmed for Auto Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("L")){
						CheckingAccount check = bank.getCheckingAccount(id);
						SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
						SavingsAccount primary = bank.getPrimaryAccount(id);
						double oldSBalance = studentLoanRepay.getBalance();
						double oldCBalance = check.getBalance();
						double overdraft = amount - check.getBalance() - CheckingAccount.TRANSFER_FEE;
						int tran = check.transfer(studentLoanRepay, amount, primary);
						
						// If withdrawal can't go through, transfer doesn't happen. Otherwise, transfer happens with an overdraft, 
						// or without one and the results are displayed.
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "You attempted to withdraw $" + String.format("%.2f", amount) + " from your checking " + 
									"account, but you only had $" + String.format("%.2f", oldCBalance) + ".\nWe have automatically transferred $" + 
									String.format("%.2f", overdraft) + " from your primary savings account, including a $20 overdraft fee.\n" + 
									"Your primary savings account now has $" + String.format("%.2f", primary.getBalance()) + ".", 
									"Overdraft Cleared", JOptionPane.INFORMATION_MESSAGE);
						}
						else if(tran == 2){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = check.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldCBalance) + 
								" in your checking account. You now have $" + String.format("%.2f", check.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldSBalance) + " in your student loan savings " + 
								"account.\nYou now have $" + String.format("%.2f", studentLoanRepay.getBalance()) + ".", 
								"Transfer Confirmed for Student Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				else if(acctType1.equals("S")){
					// Transfer from a Savings Account type of Bank Account doesn't involve overdrafts on withdrawals
					// so either a transfer happens because a withdrawal can occur, or it doesn't happen because a withdrawal
					// can't occur
					if(acctType2.equals("C")){
						CheckingAccount check = bank.getCheckingAccount(id);
						SavingsAccount primary = bank.getPrimaryAccount(id);
						double oldCBalance = check.getBalance();
						double oldPBalance = primary.getBalance();
						int tran = primary.transfer(check, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = primary.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldPBalance) + 
								" in your primary savings account. You now have $" + String.format("%.2f", primary.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldCBalance) + " in your checking " + 
								"account.\nYou now have $" + String.format("%.2f", check.getBalance()) + ".", 
								"Transfer Confirmed for Checking Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("S")){
						SavingsAccount primary = bank.getPrimaryAccount(id);
						double oldPBalance = primary.getBalance();
						int tran = primary.transfer(primary, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = primary.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldPBalance) + 
								" in your primary savings account. You now have $" + String.format("%.2f", primary.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldPBalance) + " in your primary savings " + 
								"account.\nYou now have $" + String.format("%.2f", primary.getBalance()) + ".", 
								"Transfer Confirmed for Primary Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("A")){
						SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
						SavingsAccount primary = bank.getPrimaryAccount(id);
						double oldABalance = autoLoanRepay.getBalance();
						double oldPBalance = primary.getBalance();
						int tran = primary.transfer(autoLoanRepay, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = primary.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldPBalance) + 
								" in your primary savings account. You now have $" + String.format("%.2f", primary.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldABalance) + " in your auto loan savings " + 
								"account.\nYou now have $" + String.format("%.2f", autoLoanRepay.getBalance()) + ".", 
								"Transfer Confirmed for Auto Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("L")){
						SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
						SavingsAccount primary = bank.getPrimaryAccount(id);
						double oldSBalance = studentLoanRepay.getBalance();
						double oldPBalance = primary.getBalance();
						int tran = primary.transfer(studentLoanRepay, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = primary.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldPBalance) + 
								" in your primary savings account. You now have $" + String.format("%.2f", primary.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldSBalance) + " in your student loan savings " + 
								"account.\nYou now have $" + String.format("%.2f", studentLoanRepay.getBalance()) + ".", 
								"Transfer Confirmed for Student Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				else if(acctType1.equals("L")){
					// Transfer from a Savings Account type of Bank Account doesn't involve overdrafts on withdrawals
					// so either a transfer happens because a withdrawal can occur, or it doesn't happen because a withdrawal
					// can't occur
					if(acctType2.equals("C")){
						CheckingAccount check = bank.getCheckingAccount(id);
						SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
						double oldCBalance = check.getBalance();
						double oldSBalance = studentLoanRepay.getBalance();
						int tran = studentLoanRepay.transfer(check, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = studentLoanRepay.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldSBalance) + 
								" in your student loan savings account. You now have $" + 
								String.format("%.2f", studentLoanRepay.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldCBalance) + " in your checking " + 
								"account.\nYou now have $" + String.format("%.2f", check.getBalance()) + ".", 
								"Transfer Confirmed for Checking Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("S")){
						SavingsAccount primary = bank.getPrimaryAccount(id);
						SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
						double oldPBalance = primary.getBalance();
						double oldSBalance = studentLoanRepay.getBalance();
						int tran = studentLoanRepay.transfer(primary, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = studentLoanRepay.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldSBalance) + 
								" in your student loan savings account. You now have $" + 
								String.format("%.2f", studentLoanRepay.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldPBalance) + " in your primary savings " + 
								"account.\nYou now have $" + String.format("%.2f", primary.getBalance()) + ".", 
								"Transfer Confirmed for Primary Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("A")){
						SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
						SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
						double oldABalance = autoLoanRepay.getBalance();
						double oldSBalance = studentLoanRepay.getBalance();
						int tran = studentLoanRepay.transfer(autoLoanRepay, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = studentLoanRepay.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldSBalance) + 
								" in your student loan savings account. You now have $" + 
								String.format("%.2f", studentLoanRepay.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldABalance) + " in your auto loan savings " + 
								"account.\nYou now have $" + String.format("%.2f", autoLoanRepay.getBalance()) + ".", 
								"Transfer Confirmed for Auto Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("L")){
						SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
						double oldSBalance = studentLoanRepay.getBalance();
						int tran = studentLoanRepay.transfer(studentLoanRepay, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = studentLoanRepay.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldSBalance) + 
								" in your student loan savings account. You now have $" + 
								String.format("%.2f", studentLoanRepay.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldSBalance) + " in your student loan savings " + 
								"account.\nYou now have $" + String.format("%.2f", studentLoanRepay.getBalance()) + ".", 
								"Transfer Confirmed for Student Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				else if(acctType1.equals("A")){
					// Transfer from a Savings Account type of Bank Account doesn't involve overdrafts on withdrawals
					// so either a transfer happens because a withdrawal can occur, or it doesn't happen because a withdrawal
					// can't occur
					if(acctType2.equals("C")){
						SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
						CheckingAccount check = bank.getCheckingAccount(id);
						double oldABalance = autoLoanRepay.getBalance();
						double oldCBalance = check.getBalance();
						int tran = autoLoanRepay.transfer(check, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = autoLoanRepay.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldABalance) + 
								" in your auto loan savings account. You now have $" + 
								String.format("%.2f", autoLoanRepay.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldCBalance) + " in your checking " + 
								"account.\nYou now have $" + String.format("%.2f", check.getBalance()) + ".", 
								"Transfer Confirmed for Checking Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("S")){
						SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
						SavingsAccount primary = bank.getPrimaryAccount(id);
						double oldABalance = autoLoanRepay.getBalance();
						double oldPBalance = primary.getBalance();
						int tran = autoLoanRepay.transfer(primary, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = autoLoanRepay.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldABalance) + 
								" in your auto loan savings account. You now have $" + 
								String.format("%.2f", autoLoanRepay.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldPBalance) + " in your primary savings " + 
								"account.\nYou now have $" + String.format("%.2f", primary.getBalance()) + ".", 
								"Transfer Confirmed for Primary Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("A")){
						SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
						double oldABalance = autoLoanRepay.getBalance();
						int tran = autoLoanRepay.transfer(autoLoanRepay, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = autoLoanRepay.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldABalance) + 
								" in your auto loan savings account. You now have $" + 
								String.format("%.2f", autoLoanRepay.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldABalance) + " in your auto loan savings " + 
								"account.\nYou now have $" + String.format("%.2f", autoLoanRepay.getBalance()) + ".", 
								"Transfer Confirmed for Auto Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
					else if(acctType2.equals("L")){
						SavingsAccount autoLoanRepay = bank.getAutoLoanAccount(id);
						SavingsAccount studentLoanRepay = bank.getStudentLoanAccount(id);
						double oldABalance = autoLoanRepay.getBalance();
						double oldSBalance = studentLoanRepay.getBalance();
						int tran = autoLoanRepay.transfer(studentLoanRepay, amount);
						
						// If withdrawal can't occur, display that transfer fails, otherwise display the results of the transfer
						// from the two accounts' changing balances
						if(tran == -1){
							JOptionPane.showMessageDialog(null, "We\'re sorry, you do not have enough to cover the transfer." + 
									"\nPlease try again", "Error - Overdraft", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						else if(tran == 0){
							JOptionPane.showMessageDialog(null, "We\'re sorry, your bank account has reached maximum " + 
									"capacity.\nPlease try again.", "Error - Balance Overflowed", JOptionPane.ERROR_MESSAGE);
						}
						String name = autoLoanRepay.getOwner().getName();
						JOptionPane.showMessageDialog(null, name + ", you had $" + String.format("%.2f", oldABalance) + 
								" in your auto loan savings account. You now have $" + 
								String.format("%.2f", autoLoanRepay.getBalance()) + 
								".\nYou used to have $" + String.format("%.2f", oldSBalance) + " in your student loan savings " + 
								"account.\nYou now have $" + String.format("%.2f", studentLoanRepay.getBalance()) + ".", 
								"Transfer Confirmed for Student Loan Savings Account", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}
		while(bank.checkForEndOfTransactions() == JOptionPane.YES_OPTION);
		
		// Display the account summaries for each customer of the bank, and then close
		// the bank by exiting the program (cleaning up JOptionPane windows)
		bank.processSummaries();
		bank.closeBank();
	}
}
