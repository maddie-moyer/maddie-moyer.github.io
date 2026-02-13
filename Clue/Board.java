/**
 * This class sets up the board for the game Clue. It sets the distances between each room on the Clue board.
 * @author Maddie Moyer
 */
public class Board {

	private Room[] rooms;
    private int[][] distances;

    
    /**
     * Constructor
     * @param rooms An array containing the rooms on the Clue board
     */
    public Board(Room[] rooms) {
        this.rooms = rooms;
        this.distances = new int[rooms.length][rooms.length];
        setupDistancesCourtyard();
        setupDistancesGarageAndGameRoom();
        setupDistancesRemaining();
    }

    
    /**
     * Sets the distances between the Courtyard and all of the other rooms on the Clue board.
     */
    private void setupDistancesCourtyard() {
        // How far each room is from each other (how many spaces)
        setDistance("Courtyard", "Garage", 5); //The courtyard is 5 spaces from the garage
        setDistance("Courtyard", "Game Room", 6);
        setDistance("Courtyard", "Bedroom", 10);
        setDistance("Courtyard", "Bathroom", 9);
        setDistance("Courtyard", "Office", 8);
        setDistance("Courtyard", "Kitchen", 8);
        setDistance("Courtyard", "Dining Room", 6);
        setDistance("Courtyard", "Living Room", 5);
    }
    
    
    /**
     * Sets the distances between the Garage and all other rooms as well as the Game Room and all other rooms.
     */
    private void setupDistancesGarageAndGameRoom() {
    	setDistance("Garage", "Game Room", 4);
        setDistance("Garage", "Bedroom", 9);
        setDistance("Garage", "Bathroom", 9);
        setDistance("Garage", "Office", 9);
        setDistance("Garage", "Kitchen", 0);
        setDistance("Garage", "Dining Room", 9);
        setDistance("Garage", "Living Room", 9);
        
        setDistance("Game Room", "Bedroom", 7);
        setDistance("Game Room", "Bathroom", 7);
        setDistance("Game Room", "Office", 7);
        setDistance("Game Room", "Kitchen", 10);
        setDistance("Game Room", "Dining Room", 6);
        setDistance("Game Room", "Living Room", 9);       
    }
    
    
    /**
     * Sets the distances between the rest of the rooms on the Clue board.
     */
    private void setupDistancesRemaining() {
    	setDistance("Bedroom", "Bathroom", 4);
        setDistance("Bedroom", "Office", 5);
        setDistance("Bedroom", "Kitchen", 9);
        setDistance("Bedroom", "Dining Room", 8);
        setDistance("Bedroom", "Living Room", 0);
        
        setDistance("Bathroom", "Office", 3);
        setDistance("Bathroom", "Kitchen", 8);
        setDistance("Bathroom", "Dining Room", 7);
        setDistance("Bathroom", "Living Room", 10);
        
        setDistance("Office", "Kitchen", 7);
        setDistance("Office", "Dining Room", 6);
        setDistance("Office", "Living Room", 9);
        
        setDistance("Kitchen", "Dining Room", 4);
        setDistance("Kitchen", "Living Room", 7);
        
        setDistance("Dining Room", "Living Room", 5);
    }

    
    /**
     * Takes in two rooms and the distance between them and adds that information to an integer array (distances).
     * @param roomA A String representing a room
     * @param roomB A String representing another room
     * @param dist The distance (number of spaces) between roomA and roomB
     */
    private void setDistance(String roomA, String roomB, int dist) {
    	//Set the distance between two rooms
    	try {
    		int i = getIndex(roomA); //The index of roomA in the rooms array (created in GameData.java)
    		int j = getIndex(roomB); //The index of roomB in the rooms array
    		distances[i][j] = dist; //Put the distance between two rooms at the row of roomA's index and the column of roomB's index. Easier to access later this way.
    		distances[j][i] = dist;
    	}catch(Exception e) {
    		System.out.println("At least of your two inputted rooms is not in the rooms array. Try again with a room that is in the array. ");
    	}
        
    }

    
    /**
     * This method finds the index in the rooms array (created in GameData.java) of a specific room. It is used to set distances in the distances array.
     * @param roomName The name of a room on the Clue game board.
     * @return the index of the room in the rooms array
     */
    private int getIndex(String roomName) {
    	for(int i = 0; i < rooms.length; i++) { //Cycle through the rooms in the rooms array
            if(rooms[i].getName().equals(roomName)) {
            	return i;
            }
        }
        return -1; //If the inputted room is not one of the rooms, it doesn't have an index, so return -1
    }
    
    
    /**
     * This method gives you the distance (set in the setDistance method) between two rooms on the Clue board.
     * @param roomA A String representing a room
     * @param roomB A String representing another room
     * @return the distance (number of spaces on the board) between roomA and roomB.
     */
    public int getDistance(String roomA, String roomB) {
    	//Get the distance between two rooms
        return distances[getIndex(roomA)][getIndex(roomB)];
    }

    
    /**
     * Takes in a room (as a String) and prints out the distances between that room and every other room on the board.
     * @param room A String representing a room name. You want to find the distances between this room and all other rooms.
     */
    public void printDistancesFrom(String room) {
    	//Input a room and print out how far every other room is from that room
        int index = getIndex(room);
        System.out.println("Room distances from " + room + ":");
        for (int j = 0; j < rooms.length; j++) {
            if (j != index) {
                System.out.println("- " + rooms[j] + ": " + distances[index][j]);
            }
        }
    }
    
}

