/**
 * This class is used to set up each person in Clue.
 * @author Maddie Moyer
 */
public class Person {

	private String name;

	/**
	 * Constructor
	 * @param name the name to construct
	 */
	public Person(String name) {
		this.name = name;
	}

	
	//Getter and Setter
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	//Methods
	
	/**
	 * Overrides the toString() method (so if you print the name, it actually prints the name and not the place in memory).
	 */
	public String toString() {
		return this.name;
	}

}
