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
 * Models a checking account, which is a specific type of bank account.
 * Includes overdraft protections on withdrawal if the owner of this bank account
 * owns a primary savings account as well.
 * @author jra2995
 *
 */
public class CheckingAccount extends BankAccount {
	
	/**
	 * transfer fee incurred when an overdraft occurs on a checking account
	 */
	protected static final double TRANSFER_FEE = 20.0;
	
	//Constructors
	/**
	 * creates a new checking account with a 0 balance
	 */
	public CheckingAccount(){
		super(0);
	}
	
	/**
	 * creates a new checking account with an initial balance based on the input parameter
	 * @param initialBalance the starting balance for this new checking account
	 */
	public CheckingAccount(double initialBalance){
		super(initialBalance);
	}
	
	/**
	 * creates a new checking account with an initial balance, an owner, and an account number based on input parameters
	 * @param acct the account number for this checking account
	 * @param owner the owner customer object of this checking account
	 * @param initialBalance the starting balance for this new checking account
	 */
	public CheckingAccount(int acct, Customer owner, double initialBalance){
		super(acct, owner, initialBalance);
	}
	
	// Mutators
	/**
	 * withdraws the given amount from the current balance of this checking account
	 * @param amount the amount to be withdrawn from the current balance of this checking account
	 * @throws OverdraftException if there is not enough money in the current balance to support withdrawing the amount specified
	 * @throws WithdrawException if there is an error in the super class version of withdraw (which there shouldn't be since 
	 * an overdraftexception is a withdraw exception
	 */
	public void withdraw(double amount) throws OverdraftException, WithdrawException {
		if(balance - amount < 0){
			throw new OverdraftException();
		}
		else{
			super.withdraw(amount);
		}
	}
	
	/**
	 * withdraws a given amount from the current balance, using a primary savings account as the source of covering a potential 
	 * overdraft
	 * @param amount the amount to be withdrawn from this bank account
	 * @param primary the savings account to be used to cover overdraft if a withdrawal fails
	 * @throws OverdraftException in the event that there is not enough in the savings account to cover the overdraft
	 * @throws WithdrawException if there is a problem in the super class version of withdraw, even though it shouldn't happen
	 */
	public void withdraw(double amount, SavingsAccount primary) throws OverdraftException, WithdrawException {
		if(balance - amount < 0){
			double overdraft = amount - balance + TRANSFER_FEE;
			if(primary.getBalance() >= overdraft){
				this.setBalance(0);
				primary.withdraw(overdraft);
			}
			else{
				throw new OverdraftException();
			}
		}
		else{
			super.withdraw(amount);
		}
	}
	
	/**
	 * transfers the amount of money specified to the given bank account specified in the input parameters
	 * @param toTransfer the bank account to transfer money into
	 * @param amount is the amount of money to be transferred
	 * @returns -1 if the transfer fails (withdraw cannot take place from this checking account), or 1 if the transfer succeeds
	 */
	public int transfer(BankAccount toTransfer, double amount){
		try{
			this.withdraw(amount);
		}
		catch(OverdraftException oe){
			return -1;
		}
		catch(WithdrawException we){
			return -1;
		}
		toTransfer.deposit(amount);
		return 1;
	}
	
	/**
	 * transfers the amount of money specified to the other bank account specified, using the primary savings account as a 
	 * source to cover any overdraft that may occur from transferring money out of this checking account
	 * @param toTransfer the bank account to transfer money into
	 * @param amount the amount of money to be transferred
	 * @param primary savings account that will be used as a source of covering any overdraft that may occur
	 * @return -1 if the transfer fails, noting a withdrawal failure, a 0 if an overdraft occurred but it was cleared by the 
	 * provided savings account, or a 1 if the transfer was a success without overdraft, or a 2 if balance of the other account
	 * has overflowed.
	 */
	public int transfer(BankAccount toTransfer, double amount, SavingsAccount primary){
		try{
			this.withdraw(amount);
		}
		catch(OverdraftException oe){
			try{
				this.withdraw(amount, primary);
				toTransfer.deposit(amount);
				return 0;
			}
			catch(OverdraftException oe2){
				return -1;
			}
			catch(WithdrawException we2){
				return -1;
			}
		}
		catch(WithdrawException we){
			return -1;
		}
		int res = toTransfer.deposit(amount);
		if(res == 0){
			toTransfer.balance = Double.MAX_VALUE;
			return 2;
		}
		return 1;
	}
}
