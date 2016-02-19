/**
 * Classes to model bank accounts, transactions and customers
 * Solves EE422C Programming Assignment #2
 * @author Jett Anderson
 * eid: jra2995
 * @version 1.00 2016-02-13
 */

package Assignment2;

/**
 * Represents a Customer of a Bank, complete with name, address, and ID number fields
 * Purpose is to represent a person who may interact with a bank in real life
 * @author jra2995
 *
 */
public class Customer {
	
	//instance variables, shielded from being accessed directly outside the class
	/**
	 * The customer id for this customer
	 */
	private int id;
	
	/**
	 * name of the customer
	 */
	private String name;
	
	/**
	 * address of the customer
	 */
	private String address;
	
	//Constructors
	
	/**
	 * Creates a new customer with generic ID of 1, followed by blank name fields and address fields
	 */
	public Customer(){
		id = 1;
		name = "";
		address = "";
	}
	
	/**
	 * Creates a new customer with instance fields filled by the input parameters
	 * @param n the String name of this Customer
	 * @param i the int ID number of this Customer
	 * @param a the String address of this Customer
	 */
	public Customer(String n, int i, String a){
		id = i;
		name = n;
		address = a;
	}
	
	// Accessor Methods
	/**
	 * Gets the id number of this customer
	 * @return id the id number of this customer
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Gets the name of this customer
	 * @return name the name of this customer
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the address of this customer
	 * @return address the address of this customer
	 */
	public String getAddress(){
		return address;
	}
	
	/**
	 * changes the id number of this customer to the value of i
	 * @param i the new id number for this customer
	 */
	public void setID(int i){
		id = i;
	}
	
	/**
	 * changes the name of this customer to the value of n
	 * @param n the new name for this customer
	 */
	public void setName(String n){
		name = n;
	}
	
	/**
	 * changes the address of this customer to the value of a
	 * @param a the new address for this customer
	 */
	public void setAddress(String a){
		address = a;
	}
	
	/**
	 * returns a summary of this customer object, with name, id number, and address separated by spaces
	 * @return String object with name, ID number, and address instance fields
	 */
	public String toString(){
		return "Name: " + name + " ID #: " + id + " Address: " + address;
	}
}
