/**
 * This class contains all of the game data for Clue. That involves the cards, the game card, location, and other things involved in the setup of the game.
 * @author Maddie Moyer
 */
public class GameData {

	private static Person[] people;
	private static Weapon[] weapons;
	private static Room[] rooms;
	private String[][] playerGameCard;
	private String[][] computerGameCard;
	
	private static Person murderPerson;
	private static Weapon murderWeapon;
	private static Room murderRoom;
	
	private static String[] startingCards; // Cards to be passed out to the players (all cards except for the 3 murder info cards)
	private static String[] playerCards; 
	private static String[] computerCards;
	
	private Room playerLocation;
	private Room computerLocation;
	
	private boolean computerGuessedCorrectly;
	private boolean didComputerAccuse;
	
	
	/**
	 * Constructor
	 */
	public GameData() {
		people = initPeople(); //An array of all of the people
		weapons = initWeapons(); //An array of all of the weapons
		rooms = initRooms(); //An array of all of the rooms
		playerGameCard = new String[people.length + weapons.length + rooms.length][2];
		computerGameCard = new String[people.length + weapons.length + rooms.length][2];
		
		buildGameCard(playerGameCard); //Set up the player's game card
		buildGameCard(computerGameCard); //Set up the computer's game card
		
		chooseMurderDetails(); //Pick random murder person, weapon, and room at setup
		
		startingCards = new String[(people.length - 1) + (weapons.length - 1) + (rooms.length - 1)]; // 18 cards
		startingCardsArr();
		
		playerCards = new String[startingCards.length / 2]; // 9 cards (combination of people, weapons, and rooms)
		computerCards = new String[startingCards.length / 2]; // 9 Cards
		chooseStartingCards();
		
		crossOffStartingCardElements(playerCards, playerGameCard);
		crossOffStartingCardElements(computerCards, computerGameCard);
		
		playerLocation = getRooms()[0]; //The courtyard is the starting location of the player
		computerLocation = getRooms()[0]; //The courtyard is also the starting location of the computer
		
		this.computerGuessedCorrectly = false; //A variable to set in case the computer somehow makes a completely correct guess
		this.didComputerAccuse = false; //Change to true after the computer accuses to signal the end of the game
		
	}

	
	/**
	 * Initialize all of the people (suspects) in Clue using the Person class.
	 * @return an array of the Person class containing all of the people (suspects) in the game.
	 */
	private Person[] initPeople() {
		Person green = new Person("Green");
		Person mustard = new Person("Mustard");
		Person peacock = new Person("Peacock");
		Person plum = new Person("Plum");
		Person scarlet = new Person("Scarlet");
		Person white = new Person("White");
		
		Person[] peopleArr = new Person[] {green, mustard, peacock, plum, scarlet, white};
		return peopleArr;
	}
	
	
	/**
	 * Initialize all of the weapons in Clue using the Weapon class.
	 * @return an array of the Weapon class containing all of the weapons in the game.
	 */
	private Weapon[] initWeapons() {
		Weapon candlestick = new Weapon("Candlestick");
		Weapon knife = new Weapon("Knife");
		Weapon leadPipe = new Weapon("Lead Pipe");
		Weapon pistol = new Weapon("Pistol");
		Weapon rope = new Weapon("Rope");
		Weapon wrench = new Weapon("Wrench");
		
		Weapon[] weaponsArr = new Weapon[] {candlestick, knife, leadPipe, pistol, rope, wrench};
		return weaponsArr;
	}
	
	
	/**
	 * Initialize all of the rooms on the Clue board using the Room class.
	 * @return an array of the Room class containing all of the rooms on the Clue board
	 */
	private Room[] initRooms() {
		Room courtyard = new Room("Courtyard");
		Room garage = new Room("Garage");
		Room gameRoom = new Room("Game Room");
		Room bedroom = new Room("Bedroom");
		Room bathroom = new Room("Bathroom");
		Room office = new Room("Office");
		Room kitchen = new Room("Kitchen");
		Room diningRoom = new Room("Dining Room");
		Room livingRoom = new Room("Living Room");
		
		Room[] roomsArr = new Room[] {courtyard, garage, gameRoom, bedroom, bathroom, office, kitchen, diningRoom, livingRoom};
		return roomsArr;
	}
	
	
	/**
	 * Build the game card, which is a double String array where the first column contains the people, weapons, and rooms, and the second column contains spaces to mark off the elements.
	 * @param gameCard The player/computer's game card. A double String array.
	 */
	private void buildGameCard(String[][] gameCard) {
		 int index = 0;
	     for (Person person : people) {
	    	 gameCard[index][0] = person.toString();
	         gameCard[index][1] = "";
	         index++;
	     }
	     for (Weapon weapon : weapons) {
	    	 gameCard[index][0] = weapon.toString();
	         gameCard[index][1] = "";
	         index++;
	     }
	     for (Room room : rooms) {
	    	 gameCard[index][0] = room.toString();
	         gameCard[index][1] = "";
	         index++;
	     }
	}
	
	
	/**
	 * Cross off the cards delt out to the player/computer at the beginning of the game on their game card
	 * @param userCards The cards delt to the player/computer at the beginning of the game (an assortment of 9 people, weapons, and rooms)
	 * @param gameCard The player/computer's game card
	 */
	public void crossOffStartingCardElements(String[] userCards, String[][] gameCard) {
		//Mark off each element in the userCards array on the game card
		for(String card : userCards) {
			markCard(gameCard, card);
		}
	}
	
	
	/**
	 * Uses the array of numbers 0 to 17 in a random order created in the startingCardRandomNumGenerator() method to set the player's and the computer's cards.
	 * The player's cards are determined by the indices in the first half of the random num array and the computer's the second half. Index the startingCards array.
	 */
	public static void chooseStartingCards() {
		int[] newIdxArr = startingCardRandomNumGenerator();
		
		for(int i = 0; i < startingCards.length; i++) {
			int shuffledIdx = newIdxArr[i];
			if(i < playerCards.length) { // The first half of the random card number array is for the player's cards
				playerCards[i] = startingCards[shuffledIdx];
			}else { // The second half of the random card number array is for the computer's cards
				computerCards[i-playerCards.length] = startingCards[shuffledIdx];
			}
		}
	}
	
	
	/**
	 * Fills up an array called startingCards which contains all of the elements except for the person, weapon, and room of the murder.
	 */
	public static void startingCardsArr() {
		int idx = 0;
	
		for(Person person : people) {
			if(!person.equals(murderPerson)) { //Add the person to the array if it is not the murder person
				startingCards[idx] = person.toString();
				idx++;
			}
		}
		for(Weapon weapon : weapons) {
			if(!weapon.equals(murderWeapon)) { //Add the weapon to the array if it is not the murder weapon
				startingCards[idx] = weapon.toString();
				idx++;
			}
		}
		for(Room room : rooms) {
			if(!room.equals(murderRoom)) { //Add the room to the array if it is not the murder room
				startingCards[idx] = room.toString();
				idx++;
			}
		}
	}
	
	
	/**
	 * Randomly choose one person, one weapon, and one room to be the details of the murder.
	 */
	public static void chooseMurderDetails() {
		// Randomly choose the person, weapon, and room of the murder at the beginning of the game
		int randomPersonIdx = (int)(Math.random() * people.length); //Generates a random number from 0 to 5
		int randomWeaponIdx = (int)(Math.random() * weapons.length); //Generates a random number from 0 to 5
		int randomRoomIdx = (int)(Math.random() * rooms.length); //Generates a random number from 0 to 8
	
		murderPerson = people[randomPersonIdx];
		murderWeapon = weapons[randomWeaponIdx];
		murderRoom = rooms[randomRoomIdx];	
	}
	
	
	
	//Getters and Setters
	
	/**
	 * @return the people
	 */
	public Person[] getPeople() {
		return people;
	}

	/**
	 * @return the weapons
	 */
	public Weapon[] getWeapons() {
		return weapons;
	}

	/**
	 * @return the rooms
	 */
	public Room[] getRooms() {
		return rooms;
	}
	
	/**
	 * @return the playerGameCard
	 */
	public String[][] getPlayerGameCard() {
		return playerGameCard;
	}	

	/**
	 * @return the computerGameCard
	 */
	public String[][] getComputerGameCard() {
		return computerGameCard;
	}
	
	/**
	 * @return the murderPerson
	 */
	public Person getMurderPerson() {
		return murderPerson;
	}

	/**
	 * @return the murderWeapon
	 */
	public Weapon getMurderWeapon() {
		return murderWeapon;
	}

	/**
	 * @return the murderRoom
	 */
	public Room getMurderRoom() {
		return murderRoom;
	}
	
	/**
	 * @return the playerCards
	 */
	public String[] getPlayerCards() {
		return playerCards;
	}

	/**
	 * @return the computerCards
	 */
	public String[] getComputerCards() {
		return computerCards;
	}
	
	/**
	 * @return the playerLocation
	 */
	public Room getPlayerLocation() {
		return playerLocation;
	}
	
	/**
	 * @param playerLocation the playerLocation to set
	 */
	public void setPlayerLocation(Room playerLocation) {
		this.playerLocation = playerLocation;
	}
	
	/**	
	 * @return the computerLocation
	 */
	public Room getComputerLocation() {
		return computerLocation;
	}
	
	/**
	 * @param computerLocation the computerLocation to set
	 */
	public void setComputerLocation(Room newRoom) {
        computerLocation = newRoom;
    }

	/**
	 * @return the boolean computerGuessedCorrectly
	 */
	public boolean isComputerGuessedCorrectly() {
		return computerGuessedCorrectly;
	}

	/**
	 * @param computerGuessedCorrectly the boolean computerGuessedCorrectly to set
	 */
	public void setComputerGuessedCorrectly(boolean computerGuessedCorrectly) {
		this.computerGuessedCorrectly = computerGuessedCorrectly;
	}
	
	/**
	 * @return the boolean didComputerAccuse
	 */
	public boolean getDidComputerAccuse() {
		return didComputerAccuse;
	}

	/**
	 * @param didComputerAccuse the boolean didComputerAccuse to set
	 */
	public void setDidComputerAccuse(boolean didComputerAccuse) {
		this.didComputerAccuse = didComputerAccuse;
	}
	
	
	//Methods

	/**
	 * Print out the game card, which contains all of the people, weapons, and rooms, as well as spaces to mark each of them off throughout the game.
	 * @param gameCard A double array containing the people, weapons, and rooms in the first column and the marks/blank spaces in the second column.
	 */
	public void printGameCard(String[][] gameCard) {
        System.out.println("GAME CARD:");
        System.out.println("-----------------");
        System.out.println("People:");
        System.out.println("-----------------");
        for (Person person : people) {
            System.out.printf("%-12s | %s%n", person, findMark(gameCard, person.toString()));
        }
        System.out.println("-----------------");
        System.out.println("Weapons:");
        System.out.println("-----------------");
        for (Weapon weapon : weapons) {
            System.out.printf("%-12s | %s%n", weapon, findMark(gameCard, weapon.toString()));
        }
        System.out.println("-----------------");
        System.out.println("Rooms:");
        System.out.println("-----------------");
        for (Room room : rooms) {
            System.out.printf("%-12s | %s%n", room, findMark(gameCard, room.toString()));
        }
        System.out.println("-----------------");
    }
	
	
	/**
	 * Find the mark for a specific element on the game card. The mark is an X if the element is crossed off or a blank space if not.
	 * @param gameCard the game card containing all of the people, weapons, and rooms (could be the player's card or the computer's)
	 * @param elementName the element to find the mark for
	 * @return the mark on the gameCard for the specific element. Could be an X if the element is crossed off or a blank space if not.
	 */
	public String findMark(String[][] gameCard, String elementName) {
        for (String[] row : gameCard) {
            if (row[0] != null && row[0].equals(elementName)) {
                return row[1]; //Either an X or a blank space
            }
        }
        return ""; //If the game card has not been built yet (all row[0]s are null), return nothing
    }

	
	/**
	 * Cross off an element (person, weapon, room) on the game card (the player's or the computer's)
	 * @param gameCard the game card containing all of the people, weapons, and rooms (could be the player's card or the computer's)
	 * @param elementName the name of the element to be crossed off
	 */
    public void markCard(String[][] gameCard, String elementName) {
        for (String[] row : gameCard) {
            if (row[0] != null && row[0].equals(elementName)) {
                row[1] = "X";
                break; //break so the loop stops after the element has been marked (no need to check the rest)
            }
        }
    }

    /**
     * Print out the details of the murder (person, weapon, room) that were randomly decided before the game started.
     */
	public void printMurderDetails() {
		// Print out the details of the murder at the end of the game
        System.out.println("Murder Details:");
        System.out.println("Person: " + murderPerson);
        System.out.println("Weapon: " + murderWeapon);
        System.out.println("Room: " + murderRoom);
        System.out.println();
	}
	
	/**
	 * Print out the player's cards passed out to them at the beginning of the game (an assortment of people, weapons, and rooms).
	 */
	public void printPlayerCards() {
		System.out.print("Player Cards: ");
		printArray(playerCards);
    }

	
	/**
	 * Create an array containing the numbers 0 through 18 in a random order. Used to determine which cards go to the computer and which to the player.
	 * @return an integer array containing the numbers 0 through 17 in a random order.
	 */
	public static int[] startingCardRandomNumGenerator() {
		
		int[] cardNums = new int[startingCards.length]; //18 total cards
		
		for (int i = 0; i < cardNums.length; i++) {
		    int randomNum; //Initialize the random number integer variable
		    boolean duplicate; //Initialize a boolean variable which is used to check if a number is duplicated in the array

		    do {
		        randomNum = (int)(Math.random() * cardNums.length); // Randomly generate a number from 0 to 17
		        duplicate = false;

		        for (int j = 0; j < i; j++) {
		            if (cardNums[j] == randomNum) { //If the number is already in the array
		                duplicate = true; //There is a duplicate
		                break; //Circle back through the do-while loop and try a new number at the same index
		            }
		        }
		    } while (duplicate); //Continue looping through all of the previous numbers until there are no duplicates

		    cardNums[i] = randomNum; //The number is not a duplicate, so add it to the array and move to the next index in the array
		}
		
		return cardNums;
	}
	
	/**
	 * Print out the elements in an Object array
	 * @param arr An array to be printed out
	 */
	public static void printArray(Object[] arr) {
		for (Object val : arr) {
			System.out.print(val + ", ");
		}
		System.out.println();
	}
	
	/**
	 * Print out the elements in an Object double array
	 * @param arr A double array to be printed out
	 */
	public static void printArray(Object[][] arr) {
		for (Object[] row : arr) {
			printArray(row);
		}
	}
	
		
}
