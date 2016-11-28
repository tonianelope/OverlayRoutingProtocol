import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Class describes the functionality of a Routing Table.
 * @version 0.2 pre-Alpha
 */
public class RoutingTable {
	public static final int DEFAULT_COLUMNS = 3;
	
	private String[][] table;
	private int length;
	/**
	 * Creates a new, empty routing table with the specified length
	 * @param length
	 */
	public RoutingTable(int length){
		this.length = length;
		table = new String[length][DEFAULT_COLUMNS];
	}
		
	/**
	 * Recreates an existing routing table from an ObjectInputStream
	 * @param in -the ObjectInputStream containing the table
	 * @throws IOException 
	 */
	public RoutingTable(ObjectInputStream in) throws IOException{
		this.length = in.readInt();
		try {
			this.table = (String[][]) in.readObject();
		} 
		catch (ClassNotFoundException e) {
			System.err.println("Table not recognised from stream");
			e.printStackTrace();
		}
	}
	
	
	//TODO
	public void dijkstraAlgorithm(){
		
	}
	
	public String[][] getTable(){
		return this.table;
	}
	public int getLength(){
		return this.length;
	}
	
	public String toString(){
		String result = "[";
		
		for(int i=0; i<this.length; i++){
			if(table[i][0] != null){
				result = result.concat(table[i][0] +":"+ table[i][1] +":"+ table[i][2] + " ");
			}
		}
		result = result.concat("]");
		return result;
	}
}
