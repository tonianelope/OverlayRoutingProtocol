import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Class describes the functionality of a Routing Table.
 * default implementation is a table with 3 columns, |network id|next hop|cost|, and variable rows
 * @version 0.2 pre-Alpha
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
		
		return -1;
	}
	
	/**
	 * Returns the ID of the next hop to the destination given by netID
	 * Returns -1 if given netID is not found in routing table
	 * @param netID 
	 */
	public int nextHop(int netID){
		for(int i = 0; i<this.length; i++){
			if(table[i][0] == netID){
				return table[i][1];
			}
		}
		
		return -1;
	}
	
	public int[][] getTable(){
		return this.table;
	}
	public int getLength(){
		return this.length;
	}
	
	public int toString(){
		String result = "[";
		
		for(int i=0; i<this.length; i++){
			if(table[i][0] != null){
				result = result.concat(table[i][0] +"|"+ table[i][1] +"|"+ table[i][2] + ";");
			}
		}
		result = result.concat("]");
		return result;
	}
}
