import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Class describes the functionality of a Routing Table.
 * default implementation is a table with 3 columns, |network id|next hop|cost|, and variable rows
 * @version 0.3 pre-Alpha
 */
public class RoutingTable {
	public static final int DEFAULT_COLUMNS = 3;
	
	private int[][] table;
	private int length;
	/**
	 * Creates a new, empty routing table with the specified length
	 * @param length
	 */
	public RoutingTable(int length){
		this.length = length;
		table = new int[length][DEFAULT_COLUMNS];
	}
		
	/**
	 * Recreates an existing routing table from an ObjectInputStream
	 * @param in -the ObjectInputStream containing the table
	 * @throws IOException 
	 */
	public RoutingTable(ObjectInputStream in) throws IOException{
		this.length = in.readInt();
		try {
			this.table = (int[][]) in.readObject();
		} 
		catch (ClassNotFoundException e) {
			System.err.println("Table not recognised from stream");
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a new entry to the routing table
	 * If there is no space, the table is extended
	 * Warning: untested
	 */
	public void addEntry(int destID, int nextHopID, int cost){
		int i = 0;
		boolean added = false;
		while(i < this.length && !added){
			if(table[i][0] == 0){
				table[i][0] = destID;
				table[i][1] = nextHopID;
				table[i][2] = cost;
				added = true;
			}
			i++;
		}
		if(!added){
			int[][] temp = table;
			table = new int[++length][2];
			System.arraycopy(temp, 0, table, 0, temp.length);
		}
	}
	
	/**
	 * Updates an existing entry in the routing table
	 * Does nothing if entry is not in table
	 */
	public void updateEntry(int destID, int newNextHop, int newCost){
		for(int i = 0; i<this.length; i++){
			if(table[i][0] == destID){
				table[i][1] = newNextHop;
				table[i][2] = newCost;
			}
		}
	}
	
	/**
	 * Completely deletes an existing entry in the routing table
	 * Does nothing if entry is not in table
	 */
	public void deleteEntry(int destID){
		for(int i = 0; i<this.length; i++){
			if(table[i][0] == destID){
				table[i][0] = 0;
				table[i][1] = 0;
				table[i][2] = 0;
			}
		}
	}
	
	//TODO
	public void dijkstraAlgorithm(){
		
	}
	
	/**
	 * Returns the cost to the destination given by netID
	 * Returns -1 if ID is not found in routing table
	 * @param netID 
	 */
	public int costTo(int netID){
		for(int i = 0; i<this.length; i++){
			if(table[i][0] == netID){
				return table[i][2];
			}
		}
		//else error
		return -1;
	}
	
	/**
	 * Returns the ID of the next hop to the destination given by netID
	 * Returns -1 if given netID is not found in routing table
	 * @param netID 
	 */
	public int getNextHop(int netID){
		for(int i = 0; i<this.length; i++){
			if(table[i][0] == netID){
				return table[i][1];
			}
		}
		//else error
		return -1;
	}
	
	/**
	 * returns the table array
	 */
	public int[][] getTable(){
		return this.table;
	}
	/**
	 * returns the length of the table
	 */
	public int getLength(){
		return this.length;
	}
	
	
	public String toString(){
		String result = "[";
		
		for(int i=0; i<this.length; i++){
			if(table[i][0] != 0){
				result = result.concat(table[i][0] +"|"+ table[i][1] +"|"+ table[i][2] + ";");
			}
		}
		result = result.concat("]");
		return result;
	}
}
