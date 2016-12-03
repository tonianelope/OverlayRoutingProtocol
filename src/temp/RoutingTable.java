package temp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;

/**
 * Class describes the functionality of a Routing Table.
 * default implementation is a table with 3 columns, |network id|next hop|cost|, and variable rows
 * @version 0.3 pre-Alpha
 */
public class RoutingTable {
	public static final int DEFAULT_COLUMNS = 2;
	
	private InetSocketAddress[][] table;
	private int[] tableWeights;
	private int length;
	/**
	 * Creates a new, empty routing table with the specified length
	 * @param length
	 */
	public RoutingTable(int length){
		this.length = length;
		table = new InetSocketAddress[length][DEFAULT_COLUMNS];
		tableWeights = new int [length];
	}
		
	/**
	 * Recreates an existing routing table from an ObjectInputStream
	 * @param in -the ObjectInputStream containing the table
	 * @throws IOException 
	 */
	public RoutingTable(ObjectInputStream in) throws IOException{
		this.length = in.readInt();
		try {
			this.table = (InetSocketAddress[][]) in.readObject();
			this.tableWeights = (int[]) in.readObject();
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
	public void addEntry(InetSocketAddress destID, InetSocketAddress nextHopID, int cost){
		int i = 0;
		boolean added = false;
		while(i < this.length && !added){
			if(table[i][0].equals(null)){
				table[i][0] = destID;
				table[i][1] = nextHopID;
				tableWeights[i] = cost;
				added = true;
			}
			i++;
		}
		if(!added){
			InetSocketAddress[][] temp = table;
			table = new InetSocketAddress[++length][DEFAULT_COLUMNS];
			System.arraycopy(temp, 0, table, 0, temp.length);
		}
	}
	
	/**
	 * Updates an existing entry in the routing table
	 * Does nothing if entry is not in table
	 */
	public void updateEntry(InetSocketAddress destID, InetSocketAddress newNextHop, int newCost){
		for(int i = 0; i<this.length; i++){
			if(table[i][0].equals(destID)){
				table[i][1] = newNextHop;
				tableWeights[i] = newCost;
			}
		}
	}
	
	/**
	 * Completely deletes an existing entry in the routing table
	 * Does nothing if entry is not in table
	 */
	public void deleteEntry(InetSocketAddress destID){
		for(int i = 0; i<this.length; i++){
			if(table[i][0].equals(destID)){
				table[i][0] = null;
				table[i][1] = null;
				tableWeights[i] = 0;
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
	public int costTo(InetSocketAddress netID){
		for(int i = 0; i<this.length; i++){
			if(table[i][0].equals(netID)){
				return tableWeights[i];
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
	public InetSocketAddress getNextHop(InetSocketAddress netID){
		for(int i = 0; i<this.length; i++){
			if(table[i][0].equals(netID)){
				return table[i][1];
			}
		}
		//else error
		return null;
	}
	
	/**
	 * returns the table array
	 */
	public InetSocketAddress[][] getTable(){
		return this.table;
	}
	
	/**
	 * returns the table costs
	 */
	public int[] getTableCosts(){
		return this.tableWeights;
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
			if(!table[i][0].equals(null)){
				result = result.concat(table[i][0] +"|"+ table[i][1] +"|"+ table[i][2] + ";");
			}
		}
		result = result.concat("]");
		return result;
	}
}
