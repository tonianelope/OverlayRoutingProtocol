import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
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
	 * Node represents the triple (IP address, next hop IP, distance) in the network graph
	 */
	private class Node{
		InetSocketAddress address;
		InetSocketAddress nextHop;
		int distance;
		Node(InetSocketAddress addr, InetSocketAddress next, int dist){
			address = addr;
			nextHop = next;
			distance = dist;
		}
	}
	
	/**
	 *	Sends Routing table only to immediate neighbours
	 *	
	 */
	public void sendTable(){
		
		for(int i = 0; i<this.neighbors.length; i++){
			try {
				System.out.println(name+" Sending: " + Arrays.toString(table.toByteArray()) + " to " + neighbors[i].getAddress());
				//UDPPacket udp = new UDPPacket(port, destPort, data);
				//DatagramPacket packet = udp.toDatagramPacket();
				//packet.setSocketAddress(routerAddr);
				//socket.send(packet);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Creates new rtable from received info
	 */
	public void createRTable() {
		tentative = new ArrayList<Node>();
		//TODO
		this.table = dijkstraAlgorithm();
	}
	
	public void addToTentative(ObjectInputStream oin){
		try {
			temp.RoutingTable newIn = new temp.RoutingTable(oin);
			for(int i =0; i<newIn.getLength(); i++){
				tentative.add(new Node(newIn.getEntryAt(i), newIn.getHopAt(i), newIn.getCostAt(i)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
