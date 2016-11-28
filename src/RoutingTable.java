
public class RoutingTable {
	import java.io.IOException;
	import java.io.ObjectInputStream;

	/**
	 * Class describes the functionality of a Routing Table.
	 * default implementation is a table with 2 columns, and variable rows
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
			table = new String[DEFAULT_COLUMNS][length];
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
}
