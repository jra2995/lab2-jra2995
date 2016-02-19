/**
 * Classes to model bank accounts, transactions and customers
 * Solves EE422C Programming Assignment #2
 * @author Jett Anderson
 * eid: jra2995
 * @version 1.00 2016-02-13
 */

package Assignment2;
/**  
 * Model for general bank account object.  The purpose is to record money,  
 * and allow for various financial transactions to be performed over the  
 * life of a specific bank account.  
 *  
 * @author ee422c teaching team  
 */ 
 public class BankAccount {   
	 // instance variables (protected to allow inheriting them)
	 /**  
	  * A unique number that identifies the account  
	  */ 
	 protected int accountNumber;
	 /**  
	  * The Customer that this account belongs to  
	  */
     protected Customer owner;         
     /**      
      * the current value (in dollars) of the money in this account      
      */     
     protected double balance;
     
     /**
      * Number of bank accounts currently in existence. Only used to generate bank account numbers.
      */
     private static int NUM_BANK_ACCOUNTS = 0;
    
     //constructors     
     /**      
      * Create an account with an initial balance.      
      * @param initialBalance The initial balance of the account      
      */     
     public BankAccount(double initialBalance)     
     {         
    	 balance = initialBalance;
    	 if(NUM_BANK_ACCOUNTS == 0){
    		 accountNumber = 10000;
    		 NUM_BANK_ACCOUNTS = accountNumber;
    	 }
    	 else{
    		 accountNumber = NUM_BANK_ACCOUNTS;
    		 NUM_BANK_ACCOUNTS++;
    	 }
     }         
     
     /**      
      * Create an account with initial parameters.      
      * @param acct The account number      
      * @param owner The owner of the account      
      * @param initBalance The initial balance of the account      
      */     
     public BankAccount(int acct, Customer own, double initBalance)     
     {         
    	 accountNumber = acct;         
    	 owner = own;         
    	 balance = initBalance;     
     }     
     
     // balance changing methods     
     /**      
      * Updates the current balance by adding in a given amount.      
      * Post condition: the new balance is increased by the amount.      
      * @param amount The amount to add  
      * @return 0 if deposit fails (overflows balance), 1 if succeeds    
      */     
     public int deposit(double amount)     
     {
    	double oldBalance = balance;
    	balance = balance + amount;
    	if(oldBalance > balance){
    		balance = Double.MAX_VALUE;
    		return 0;
    	}
    	return 1;
     }

     /**      
      * Update the current balance by subtracting the given amount.      
      * Precondition: the current balance must have at least the amount in it.      
      * Postcondition: the new balance is decreased by the given amount.      
      * @param amount  The amount to subtract      
      */     
     public void withdraw(double amount) throws OverdraftException, WithdrawException
     {         
    	 if (balance >=  amount)             
    		 balance = balance - amount;
    	 else
    		 throw new WithdrawException();
     }
     
     /**
 	 * transfers the amount of money specified to the given bank account specified in the input parameters
 	 * @param toTransfer the bank account to transfer money into
 	 * @param amount is the amount of money to be transferred
 	 * @returns -1 if the transfer fails (withdraw cannot take place from this checking account), or 1 if the transfer succeeds, or
 	 * 0 if deposit has overflowed balance
 	 */
     public int transfer(BankAccount toTransfer, double amount){
    	 try{
    		 this.withdraw(amount);
    	 }
    	 catch(WithdrawException we){
    		 return -1;
    	 }
    	 int res = toTransfer.deposit(amount);
    	 if(res == 0){
    		 toTransfer.balance = Double.MAX_VALUE;
    		 return 0;
    	 }
    	 return 1;
     }
     
     // get and set methods     
     /**      
      * @return The available balance.      
      */     
     public double getBalance( )     
     {         
    	 return balance;     
     }         
     
     /**      
      * @return The account number.      
      */     
     public int getAccountNumber( )     
     {         
    	 return accountNumber;     
     }         
     
     /**      
      * @return The owner's name.      
      */     
     public Customer getOwner( )     
     {         
    	 return owner;     
     }
     
     // set: postconditions- these all are used to set new values for the instance variables     
     /**      
      * Set the balance.      
      * @param newBalance  The new balance.      
      */     
     public void setBalance(double newBalance )     
     {         
    	 balance = newBalance;     
     }         
     
     /**      
      * Set the acount number.      
      * @param newAcctNumber The new account number.      
      */     
     public void setAccountNumber(int newAcctNumber )     
     {         
    	 accountNumber = newAcctNumber;     
     }
     
     /**      
      * Set the new owner of the account.
      * @param newOwner      
      */     
     public void setOwner(Customer newOwner )     
     {         
    	 owner = newOwner;     
     } 
     
     /**
      * Displays bank account information in a succinct manner, with all instance fields listed out followed by spaces
      * @return description of the bank account, with account number, owner, and current balance
      */
     public String toString(){
    	 return "Account #: " + accountNumber + " Owner: " + owner + " Balance: $" + balance;
     }
}