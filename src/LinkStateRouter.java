import java.net.InetSocketAddress;
import java.util.ArrayList;
/**
 * LinkStateRouter represents a router that uses OSPF to forward packets
 *
 */
public class LinkStateRouter extends Router{
	
	private ArrayList<Node> tentative;
	
	
	public LinkStateRouter(String name, int id) {
		super(name, id);
	}
	
	//Might be unnecessary
	/**
	 * Node represents the pair (IP address, distance) in the network graph
	 */
	private class Node{
		InetSocketAddress address;
		int distance;
		Node(InetSocketAddress addr, int dist){
			address = addr;
			distance = dist;
		}
	}
	
	/**
	 *	Sends Routing table only to immediate neighbours
	 *	
	 */
	public void sendTable(){
		
		for(int i = 0; i<this.neighbors.length; i++){
			
		}
	}
	
	/**
	 * Creates new rtable from received info
	 */
	public void createRTable(byte[] data) {
		tentative = new ArrayList<Node>();
		//TODO
		this.table = dijkstraAlgorithm();
	}
	
	//TODO
	/**
	 * Creates a new routing table giving the shortest path to each destination using Dijkstra's algorithm;
	 */
	public RoutingTable dijkstraAlgorithm(){
		RoutingTable newTable = new RoutingTable(table.getLength());
		//TODO add all to tentative
		while(!tentative.isEmpty()){
			//TODO
		}
		return newTable;
	}
}
