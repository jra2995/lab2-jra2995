/**
 * Classes to model bank accounts, transactions and customers
 * Solves EE422C Programming Assignment #2
 * @author Jett Anderson
 * eid: jra2995
 * @version 1.00 2016-02-13
 */

package Assignment2;

/**
 * Models a savings account, which is a specific type of bank account. Includes the ability
 * to add interest based on a statically defined interest rate, if a minimum balance is met
 * @author jra2995
 *
 */
public class SavingsAccount extends BankAccount {
	
	// Static constants
	/**
	 * default interest rate for savings accounts
	 */
	
	/**
	 * minimum balance for interest to be applied to a savings account
	 */
	protected static final double INTEREST_RATE = 0.04;
	protected static final double MIN_BALANCE_FOR_INTEREST = 1000.0;
	
	//Constructors
	
	/**
	 * creates a new savings account with a 0 initial balance
	 */
	public SavingsAccount(){
		super(0);
	}
	
	/**
	 * creates a new savings account with an initial balance as specified by the input
	 * @param initialBalance the initial balance for this new savings account
	 */
	public SavingsAccount(double initialBalance){
		super(initialBalance);
	}
	
	/**
	 * creates a new savings account with owner, account number, and initial balance specified by input parameters
	 * @param acct the account number for this new savings account
	 * @param owner the customer object that owns this particular bank account
	 * @param initialBalance the initial balance for this new savings account
	 */
	public SavingsAccount(int acct, Customer owner, double initialBalance){
		super(acct, owner, initialBalance);
	}
	
	//Mutators
	/**
	 * adds interest to the current balance by the specified INTEREST_RATE for savings accounts
	 */
	public void addInterest(){
		if(balance >= MIN_BALANCE_FOR_INTEREST){
			balance += (balance * INTEREST_RATE);
		}
	}
}
