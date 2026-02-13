
import java.util.Scanner;

/**
 * This class goes through an entire game of Clue, where the user (player) is playing against the computer.
 * @author Maddie Moyer
 */
public class Clue {

	public static GameData gameData = new GameData();
	public static Board board = new Board(gameData.getRooms());

	/**
	 * Run a game of Clue
	 * @param args the variables needed to run a game of Clue
	 */
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		
		beginGameSpiel(sc);
		gameLoop(sc);
		
	}
	
	
	/**
	 * A game of Clue. Loops through the player's and the computer's turns and stops when one accuses.
	 * @param sc
	 */
	public static void gameLoop(Scanner sc) {
		boolean gameEnded = false;
		int choice = beginPlayerTurnSpiel(sc);
		while(choice != 2) { // 2 is the sentinel value - continue in the loop until the player wants to accuse
			playerTurn(sc);
			computerTurn(sc);
			if(gameData.getDidComputerAccuse()) { //End the game if the computer accused
				gameEnded = true;
				break;
			}
			choice = beginPlayerTurnSpiel(sc); //choice variable is set and returned in this method
		}
		if(!gameEnded) { //The player can accuse if the computer did not already accuse and end the game
			gameData.printGameCard(gameData.getPlayerGameCard());
			playerAccuse(sc);
		}	
	}
	
	
	/**
	 * The sequence of events occuring during the player's turn.
	 * @param sc a Scanner
	 */
	public static void playerTurn(Scanner sc) {
		int diceRoll = diceRoll();
		
		System.out.println("You are in the " + gameData.getPlayerLocation());
			
		System.out.println("Press ENTER to roll the dice");
		sc.nextLine();  //Scanner waits for player to press ENTER to move on
		System.out.println("You rolled a: " + diceRoll);
		board.printDistancesFrom(gameData.getPlayerLocation().toString());
		System.out.print("Would you like to move to a new room or stay in the " + gameData.getPlayerLocation() + "? Enter \"Move\" or \"Stay\": ");
		String moveOrStay = sc.nextLine();
			
		if(moveOrStay.equalsIgnoreCase("Move")) {
			movePlayerRoom(sc, diceRoll);
		}else if(moveOrStay.equalsIgnoreCase("Stay")) {
			System.out.println("\nYou are staying in the " + gameData.getPlayerLocation());
		}else {
			System.out.println("That is not one of the options. You will stay in your current room.");	
		}
			
		String[] playerGuess = playerMakeAGuess(sc);
		computerShowPlayerACard(sc, playerGuess);		
	}
	
	
	/**
	 * The sequence of events occuring during the computer's turn.
	 * @param sc a Scanner
	 */
	public static void computerTurn(Scanner sc) {
		int diceRoll = diceRoll();
		
		System.out.println("\nIt is the computer's turn.");
		System.out.print("Press ENTER as you continue through the computer's turn.");
		sc.nextLine();
		System.out.println("The computer is in the " + gameData.getComputerLocation());
		sc.nextLine();
		computerAccuse(sc);
		if(gameData.getDidComputerAccuse()) { //If the computer accused, the game is over
			//Do nothing. Game is over and code has ended (in other methods)
		}else { //If the computer did not accuse, continue on with the turn
			System.out.println("The computer rolled a: " + diceRoll);
			board.printDistancesFrom(gameData.getComputerLocation().toString());
			sc.nextLine();
			moveComputerRoom(diceRoll);
			sc.nextLine();
			String[] computerGuess = computerMakeAGuess();
			System.out.println("The computer is guessing: \"" + computerGuess[0] + "\" in the \"" + computerGuess[2] + "\" with the \"" + computerGuess[1] + "\""); //Person in the room with the weapon
			sc.nextLine();
			playerShowComputerACard(sc, computerGuess);
		}
	}
	
	
	/**
	 * The process of the player moving to a new room (or staying in the same room) on the board after rolling the dice.
	 * @param sc a Scanner
	 * @param diceRoll A random number from 2 to 12 representing the roll of two six-sided dice. The number of spaces the player can move.
	 */
	public static void movePlayerRoom(Scanner sc, int diceRoll) {
		System.out.print("You chose \"Move\". Which room would you like to move to (remember, you rolled a " + diceRoll + "): ");
		String newRoom = sc.nextLine();
		System.out.println();
		Room newLocation = new Room(newRoom); //Convert the new room from a String to a Room variable
		int distBetweenRooms = board.getDistance(gameData.getPlayerLocation().toString(), newRoom);
		if(distBetweenRooms <= diceRoll) {
			if(newRoom.equals(gameData.getPlayerLocation().toString())) { //If you chose "MOVE" but are actually staying in the same place
				System.out.println("You are staying in the " + newLocation);
			}else {
				System.out.println("You have moved to the " + newLocation);
				gameData.setPlayerLocation(newLocation);
			}
		}else {
			System.out.println("The " + newLocation + " is further than " + diceRoll + " spaces away from the " + gameData.getPlayerLocation() + ". You cannot move there.");
			System.out.println("Here are the moves you are allowed to make: ");
			playerPossibleRoomsToMoveTo(diceRoll);
			System.out.println("\nPlease choose one of these options and try again. ");
			movePlayerRoom(sc, diceRoll);
		}
	}
	
	
	/**
	 * The process of the computer moving to a new room on the board (or staying in the same room) after rolling the dice.
	 * @param diceRoll A random number from 2 to 12 representing the roll of two six-sided dice. The number of spaces the computer can move.
	 */
	public static void moveComputerRoom(int diceRoll) {
		//The computer will move to a random room (that is within the dice roll distance) that it does not already have marked off on its game card
		Room[] possibleRoomsArr = computerPossibleRoomsToMoveTo(diceRoll); //These are the rooms that the computer is allowed to move to (determined by the dice roll)
		if(possibleRoomsArr.length == 1) { //If there is only 1 possible room in the array, the room must be the room the computer is already in
			System.out.println("The computer is staying in the " + gameData.getComputerLocation()); //The computer has to stay in that room
		}else { //If the computer has multiple room options
			Room[] moveToArr = new Room[possibleRoomsArr.length]; //A new array that will have the possible rooms that are unmarked on the computer's game card
			int idx = 0; //Index to add rooms to moveToArr
			for(Room room : possibleRoomsArr) {
				if(!(gameData.findMark(gameData.getComputerGameCard(), room.toString()).equals("X"))) {
					//If the room is not marked off on the computer's game card, add it to a new array
					moveToArr[idx] = room;
				}
			}
			moveToArr = removeNullFromArray(moveToArr); //Remove any null entries from the array
			int randomNum = (int)(Math.random() * moveToArr.length); //Randomly generate an index from the moveToArr
			Room computerNewLocation = moveToArr[randomNum];
			gameData.setComputerLocation(computerNewLocation); //The computer moves to that room
			System.out.println("The computer has moved to the " + computerNewLocation);
		}	
	}
	
	
	/**
	 * The process of the player making a guess at the end of their turn.
	 * @param sc a Scanner
	 * @return a String array containing the details of the player's guess (the person, weapon, and room)
	 */
	public static String[] playerMakeAGuess(Scanner sc) {
		System.out.println("Now, type in your guess. Don't forget to consult your game card above!: ");
		System.out.println("Room : " + gameData.getPlayerLocation());
		System.out.print("Person: ");
		String guessedPerson = sc.nextLine();
		System.out.print("Weapon: ");
		String guessedWeapon = sc.nextLine();
		String[] guessArray = new String[] {guessedPerson, guessedWeapon, gameData.getPlayerLocation().toString()};
		return guessArray; // {Person, Weapon, Room}
	}
	
	
	/**
	 * The process of the computer making a guess at the end of its turn.
	 * @return a String array containing the details of the computer's guess (the person, weapon, and room)
	 */
	public static String[] computerMakeAGuess() {
		Room roomGuess = gameData.getComputerLocation();
		Person[] personGuessesArr = new Person[gameData.getPeople().length];
		Weapon[] weaponGuessesArr = new Weapon[gameData.getWeapons().length];
		int idx = 0;
		//Add the people that the computer has unmarked to an array
		for(Person person : gameData.getPeople()) {
			if(!(gameData.findMark(gameData.getComputerGameCard(), person.toString()).equals("X"))) {
				personGuessesArr[idx] = person;
			}
		}
		//Add the weapons that the computer has unmarked to an array
		idx = 0; //reset index
		for(Weapon weapon : gameData.getWeapons()) {
			if(!(gameData.findMark(gameData.getComputerGameCard(), weapon.toString()).equals("X"))) {
				weaponGuessesArr[idx] = weapon;
			}
		}
		personGuessesArr = removeNullFromArray(personGuessesArr);
		weaponGuessesArr = removeNullFromArray(weaponGuessesArr);
		int randomPersonIdx = (int)(Math.random() * personGuessesArr.length); //Generate a random index in personGuessesArr
		int randomWeaponIdx = (int)(Math.random() * weaponGuessesArr.length); //Generate a random index in weaponGuessesArr
		Person personGuess = personGuessesArr[randomPersonIdx];
		Weapon weaponGuess = weaponGuessesArr[randomWeaponIdx];
		
		String[] guessesArray = {personGuess.toString(), weaponGuess.toString(), roomGuess.toString()};
		return guessesArray;
	}
	
	
	/**
	 * The process of the player showing the computer a card after the computer makes a guess
	 * @param sc a Scanner
	 * @param computerGuess a String array containing the details of the computer's guess (person, weapon, and room)
	 */
	public static void playerShowComputerACard(Scanner sc, String[] computerGuess) {
		gameData.printPlayerCards();
		String[] cardsPlayerHasArr = new String[computerGuess.length];
		int idx = 0;
		for(String card : gameData.getPlayerCards()) { //Check if the player has any of the cards that the computer guessed
			for(String guess : computerGuess) {
				if(guess.equals(card)) {
					cardsPlayerHasArr[idx] = guess;
					idx++;
				}
			}
		}
		cardsPlayerHasArr = removeNullFromArray(cardsPlayerHasArr);
		if(cardsPlayerHasArr.length == 0) {
			gameData.setComputerGuessedCorrectly(true); //If the player has none of the cards, then the computer guessed everything correctly
			System.out.println("You do not have any of those cards. You show the computer nothing.");
		}else if(cardsPlayerHasArr.length == 1) {
			System.out.println("You have: " + cardsPlayerHasArr[0]);
			System.out.println("You show the computer: " + cardsPlayerHasArr[0]);
		}else {
			System.out.print("You have: ");
			for(String card : cardsPlayerHasArr) {
				System.out.print(card + ", ");
			}
			System.out.print("\nWhich card would you like to show to the computer? Please ENTER card name: ");
			String cardShown = sc.nextLine();
			gameData.markCard(gameData.getComputerGameCard(), cardShown);
			System.out.println("You have shown the computer: " + cardShown + "\n");
		}
	}
	
	
	/**
	 * The process of the computer showing the player a card after the player makes a guess.
	 * @param sc a Scanner
	 * @param playerGuess a String array containing the details of the player's guess (person, weapon, and room)
	 */
	public static void computerShowPlayerACard(Scanner sc, String[] playerGuess) {
		String[] computerHasThese = new String[3];
		int idx = 0;
		
		for(String card : gameData.getComputerCards()) {
			for(String guess : playerGuess) {
				if(guess.equals(card)) {
					computerHasThese[idx] = guess;
					idx++;
				}
			}
		}
		computerHasThese = removeNullFromArray(computerHasThese);
		if(computerHasThese.length == 0) {
			System.out.println("The computer does not have any of those cards. \n");
		}else {
			int randomNum = (int)(Math.random() * computerHasThese.length); //Generate a random number between 0 and the length of the array minus 1
			String compCard = computerHasThese[randomNum];
			System.out.println("The computer is showing you: " + compCard);
			System.out.println("Press ENTER to cross off the card on your Game Card.");
			sc.nextLine();
			gameData.markCard(gameData.getPlayerGameCard(), compCard);
			gameData.printGameCard(gameData.getPlayerGameCard());
		}
	}
	
	
	/**
	 * Prints out the rooms that the player is allowed to move to (the rooms that are less than or equal to the dice roll number of spaces away)
	 * @param diceRoll A random number from 2 to 12 representing the roll of two six-sided dice. The number of spaces the player can move.
	 */
	public static void playerPossibleRoomsToMoveTo(int diceRoll) {
		//Prints out the rooms that the player is allowed to move to (determined by the dice roll)
		Room currentLocation = gameData.getPlayerLocation();		
		System.out.println("1. You can stay in the " + currentLocation);
		
		int num = 2; //Numbered list for the possible rooms the player can move to. 1 is stay in current room, so start num = 2.
		for(Room room : gameData.getRooms()) {
			int dist = board.getDistance(currentLocation.getName(), room.getName()); //need to use .getName() because the rooms need to be Strings in this method
			if((dist <= diceRoll) && !room.equals(currentLocation)) { 
				System.out.println(num + ". You can move to the " + room.getName());
				num++;
			}
		}
	}
	
	
	/**
	 * Uses the computer's dice roll to determine which rooms the computer can move to (compares the dice roll to the distances between the current room and the other rooms)
	 * @param diceRoll A random number from 2 to 12 representing the roll of two six-sided dice. The number of spaces the computer can move.
	 * @return a Room array containing all of the possible rooms that the computer can move to (determined by the dice roll)
	 */
	public static Room[] computerPossibleRoomsToMoveTo(int diceRoll) {
		//Creates an array that contains the rooms that the computer is allowed to move to (determined by the dice roll)
		Room currentLocation = gameData.getComputerLocation();
		Room[] possibleRoomsArr = new Room[gameData.getRooms().length];
		possibleRoomsArr[0] = currentLocation;
		
		int idx = 1; //Index to add possible rooms to the possibleRoomsArr. Already added the current location so start idx at 1
		for(Room room : gameData.getRooms()) {
			int dist = board.getDistance(currentLocation.getName(), room.getName()); //need to use .getName() because the rooms need to be Strings in this method
			if((dist <= diceRoll) && !room.equals(currentLocation)) { 
				possibleRoomsArr[idx] = room;
				idx++;
			}
		}
		possibleRoomsArr = removeNullFromArray(possibleRoomsArr); //Get rid of any null entries at the end of the array (spots that were not filled)
		return possibleRoomsArr;
	}

	
	/**
	 * The spiel each time at the beginning of the player's (user's) turn
	 * @param sc a Scanner
	 * @return an integer. 1 means that the player wants to continue the game and 2 means that the player wants to make an accusation.
	 */
	public static int beginPlayerTurnSpiel(Scanner sc) {
		//Stuff to print out at the beginning of the player's turn every time
		System.out.println("It is your turn! \n");
		System.out.println("Here are your cards: ");
		gameData.printPlayerCards();
		System.out.println();
		gameData.printGameCard(gameData.getPlayerGameCard());
		System.out.println("\nWould you like to: ");
		System.out.println(" 1. Roll the dice and continue the game OR");
		System.out.println(" 2. Make an accusation");
		System.out.print("Type \"1\" to continue or \"2\" to make your accusation: ");
		int choice = sc.nextInt();
		sc.nextLine(); //clear leftover newline that comes from scanning ints or doubles
		System.out.println();
		return choice;
	}
	
	
	/**
	 * The spiel given to introduce the game before it starts.
	 * @param sc a Scanner
	 */
	public static void beginGameSpiel(Scanner sc) {
		System.out.println("Welcome to Clue!");
		System.out.println("The goal of the game is to solve a murder. You need to figure out the person, weapon, and room.");
		System.out.println("This game is case-sensitive, so when you type in words, please always capitalize the first letter!");
		System.out.println("Also, always double check your spelling.");
		System.out.println("Thank you! \n");
		System.out.println("Press ENTER to begin the game.");
		sc.nextLine();
	}
	
	
	/**
	 * The sequence of events that plays out when the player (user) decides to make an accusation
	 * @param sc a Scanner
	 */
	public static void playerAccuse(Scanner sc) {
		System.out.println("You have chosen to make an accusation.");
		System.out.println("Remember, once you accuse, the game is over.");
		System.out.print("Are you ready to make your accusation? (Yes/No): ");
		String answer = sc.nextLine();
		if(answer.equalsIgnoreCase("Yes")) { //Player does want to make an accusation
			System.out.println("\nPlease type in your accusation.");
			System.out.print("Person: ");
			String person = sc.nextLine();
			System.out.print("Weapon: ");
			String weapon = sc.nextLine();
			System.out.print("Room: ");
			String room = sc.nextLine();
			System.out.println("\nPress ENTER to reveal the details of the murder...");
			sc.nextLine();
			checkPlayerAccusation(person, weapon, room);
		}else { //Player does not want to make an accusation
			System.out.println("\nYou have chosen not to make an accusation. Continue on with the game. \n");
			gameLoop(sc); //Go back through the game loop until the player want to actually make an accusation (or the computer)
		}
	}
	
	
	/**
	 * The sequence of events that plays out when the computer wants to make an accusation.
	 * @param sc a Scanner
	 */
	public static void computerAccuse(Scanner sc) {
		//First, check if the computer's last guess was fully correct. If it was, computer will accuse
		if(gameData.isComputerGuessedCorrectly()) {
			computerIsMakingCorrectAccusation(sc);
			
		}else if(doesArrayContainNull(isComputerReadyToAccuse())){ //If the computer is not ready to accuse (does not have all but 1 element from each category unmarked)
			//Do nothing. Continue on with the game.
			
		}else { //If the computer is ready to accuse (the array from isComputerReadyToAccuse() does not contain any nulls)
			if(isComputerAccusationCorrect(isComputerReadyToAccuse())) { 
				computerIsMakingCorrectAccusation(sc);	
			}else { //If the computer's accusation was incorrect. This should not happen but I have it as a precaution.
				String person = isComputerReadyToAccuse()[0];
				String weapon = isComputerReadyToAccuse()[1];
				String room = isComputerReadyToAccuse()[2];
				System.out.println("The computer is accusing: " + person + " in the " + room + " with the " + weapon + "\n");
				System.out.println("Press ENTER to see if the computer is correct.");
				sc.nextLine();
				System.out.println("The computer's guess was INCORRECT.");
				System.out.println("The computer has lost, so you have won!");
				System.out.print("Would you like to 1. Reveal the murder details and end the game, or 2. Make an accusation as well? Type 1 or 2:");
				int choice = sc.nextInt();
				sc.nextLine();
				if(choice == 1) {
					gameData.printMurderDetails();
				}else {
					gameData.printGameCard(gameData.getPlayerGameCard());
					playerAccuse(sc);
				}
				gameData.setDidComputerAccuse(true);
			}
		}
	}
	
	
	/**
	 * Checks if the player's accusation matches the murder details and prints out messages telling the player is they won/lost
	 * @param person a String of the person the player is accusing for the murder
	 * @param weapon a String of the weapon the player is accusing for the murder
	 * @param room a String of the room the player is accusing for the murder location
	 */
	public static void checkPlayerAccusation(String person, String weapon, String room) {
		Person accusedPerson = new Person(person);
		Weapon accusedWeapon = new Weapon(weapon);
		Room accusedRoom = new Room(room);
		
		Person murderPerson = gameData.getMurderPerson();
		Weapon murderWeapon = gameData.getMurderWeapon();
		Room murderRoom = gameData.getMurderRoom();
		
		gameData.printMurderDetails();
		
		if(accusedPerson.equals(murderPerson) && accusedWeapon.equals(murderWeapon) && accusedRoom.equals(murderRoom)) {
			System.out.println("Congratulations! You correctly solved the murder! You win!!!!");
			System.out.println("GAME OVER");
		}else {
			System.out.println("Your accusation was incorrect. You did not solve the murder.");
			System.out.println("You lose  :-( ");
			System.out.println("GAME OVER");
		}
	}
	
	
	/**
	 * The computer makes a correct accusation and wins the game. Prints off a series of messages showing this sequence.
	 * @param sc a Scanner
	 */
	public static void computerIsMakingCorrectAccusation(Scanner sc) {
		//Prints out the computer's accusation (which is correct in this method) and says that the computer has won.
		System.out.println("The computer has decided to make an accusation.");
		sc.nextLine(); //Player must press enter to continue on
		System.out.println("The computer is accusing: " + gameData.getMurderPerson() + " in the " + gameData.getMurderRoom() + " with the " + gameData.getMurderWeapon() + "\n");
		System.out.println("Press ENTER to see if the computer is correct.");
		sc.nextLine();
		gameData.printMurderDetails();
		System.out.println("The computer guessed correctly!");
		System.out.println("The computer has won. \n");
		System.out.println("GAME OVER");
		gameData.setDidComputerAccuse(true);
	}
	
	
	/**
	 * Check if the computer's 1 unmarked person, 1 unmarked weapon, and 1 unmarked room match the murder details.
	 * @param accusationArray an array containing 1 person, 1 weapon, and 1 room.
	 * @return true if the accusation array matches the murder details, false if not.
	 */
	public static boolean isComputerAccusationCorrect(String[] accusationArray) {
		String person = accusationArray[0]; //I formatted accusationArray so that the person is in index 0,
		String weapon = accusationArray[1]; //the weapon is in index 1, 
		String room = accusationArray[2]; //and the room is in index 2
		
		Person accusedPerson = new Person(person);
		Weapon accusedWeapon = new Weapon(weapon);
		Room accusedRoom = new Room(room);
		
		Person murderPerson = gameData.getMurderPerson();
		Weapon murderWeapon = gameData.getMurderWeapon();
		Room murderRoom = gameData.getMurderRoom();
		
		if(accusedPerson.equals(murderPerson) && accusedWeapon.equals(murderWeapon) && accusedRoom.equals(murderRoom) && !doesArrayContainNull(accusationArray)) {
			//The computer's accusation is the same as the murder details determined at the beginning of the game (computer is correct)
			return true;
		}
		return false; //Computer's guess was incorrect
	}
	
	
	/**
	 * If the computer has everything on its game card marked off besides 1 person, 1 weapon, and 1 room, create an array containing those three elements. If not, create a null array.
	 * @return an array containing the computer's 1 unmarked person, 1 unmarked weapon, and 1 unmarked room, or an array full of nulls if that criteria was not met.
	 */
	public static String[] isComputerReadyToAccuse() {
		//If the computer has everything marked off except for 1 person, 1 weapon, and 1 room, it is ready to accuse
		String[] accusationArray = new String[3]; //An array to contain the computer's unmarked person, weapon, and room
		int personIdx = -1; //Used to index the unmarked person (changed to a valid number later)
		int weaponIdx = -1; //Used to index the unmarked weapon (changed to a valid number later)
		int roomIdx = -1; //Used to index the unmarked room (changed to a valid number later)
		int pCount = 0; //Used to count the number of unmarked people
		int wCount = 0; //Used to count the number of unmarked weapons
		int rCount = 0; //Used to count the number of unmarked rooms
		
		for(int i = 0; i < gameData.getPeople().length; i++) {
			String person = gameData.getPeople()[i].toString();
			if(gameData.findMark(gameData.getComputerGameCard(), person).equals("") ){ //If the person is unmarked
				personIdx = i; 
				pCount++; 
			}
		}
		for(int j = 0; j < gameData.getWeapons().length; j++) {
			String weapon = gameData.getWeapons()[j].toString();
			if(gameData.findMark(gameData.getComputerGameCard(), weapon).equals("") ){ //If the weapon is unmarked
				weaponIdx = j;
				wCount++; 
			}
		}
		for(int k = 0; k < gameData.getRooms().length; k++) {
			String room = gameData.getRooms()[k].toString();
			if(gameData.findMark(gameData.getComputerGameCard(), room).equals("") ){ //If the room is unmarked
				roomIdx = k;
				rCount++;
			}
		}
		if( (pCount == 1) && (wCount == 1) && (rCount == 1) ) { //If the computer has everything marked except for 1 thing from each category
			accusationArray[0] = gameData.getPeople()[personIdx].toString(); //Add unmarked person to array
			accusationArray[1] = gameData.getWeapons()[weaponIdx].toString(); //Add unmarked weapon to array
			accusationArray[2] = gameData.getRooms()[roomIdx].toString(); //Add unmarked room to array
		}
		return accusationArray; //If array is full of nulls, then the computer is not ready to accuse
	}
	
	
	/**
	 * Roll two six-sided dice.
	 * @return a random integer between 2 and 12 (as if you rolled two six-sided dice)
	 */
	public static int diceRoll() {
		// Generates a random number between 2 and 12 (rolling 2 six-sided dice)
		int min = 2;
		int max = 12;
		
		int diceRoll = (int)(Math.random() * (max - min + 1)) + min; //Generates a random number from 2 to 12
		return diceRoll;
	}
	
	
	/**
	 * Remove any null values from a String array and resize the array
	 * @param arr a String array
	 * @return arr, but without the nulls and resized
	 */
	public static String[] removeNullFromArray(String[] arr) {
		int length = 0;
		for(String element : arr) {
			if(element != null) {
				length++;
			}
		}
		String[] newArray = new String[length];
		int idx = 0;
		for(String element : arr) {
			if(element != null) {
				newArray[idx] = element;
				idx++;
			}
		}
		return newArray;
	}
	
	
	/**
	 * Remove any null values from a Person array and resize the array
	 * @param arr a Person array
	 * @return arr, but without the nulls and resized
	 */
	public static Person[] removeNullFromArray(Person[] arr) {
		int length = 0;
		for(Person element : arr) {
			if(element != null) {
				length++;
			}
		}
		Person[] newArray = new Person[length];
		int idx = 0;
		for(Person element : arr) {
			if(element != null) {
				newArray[idx] = element;
				idx++;
			}
		}
		return newArray;
	}
	
	
	/**
	 * Remove any null values from a Weapon array and resize the array
	 * @param arr a Weapon array
	 * @return arr, but without the nulls and resized
	 */
	public static Weapon[] removeNullFromArray(Weapon[] arr) {
		int length = 0;
		for(Weapon element : arr) {
			if(element != null) {
				length++;
			}
		}
		Weapon[] newArray = new Weapon[length];
		int idx = 0;
		for(Weapon element : arr) {
			if(element != null) {
				newArray[idx] = element;
				idx++;
			}
		}
		return newArray;
	}
	
	
	/**
	 * Remove any null values from a Room array and resize the array
	 * @param arr a Room array
	 * @return arr, but without the nulls and resized
	 */
	public static Room[] removeNullFromArray(Room[] arr) {
		int length = 0;
		for(Room element : arr) {
			if(element != null) {
				length++;
			}
		}
		Room[] newArray = new Room[length];
		int idx = 0;
		for(Room element : arr) {
			if(element != null) {
				newArray[idx] = element;
				idx++;
			}
		}
		return newArray;
	}
	
	
	/**
	 * Check if there are any nulls in a String array.
	 * @param arr a String array
	 * @return true if the array contains any nulls, false if there are no nulls in the array
	 */
	public static boolean doesArrayContainNull(String[] arr) {
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] == null) {
				return true;
			}
		}
		return false;
	}
	
	
}

