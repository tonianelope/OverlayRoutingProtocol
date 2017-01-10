import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * LinkStateRouter represents a router that uses OSPF to forward packets
 *
 */
public class LinkStateRouter extends Router{
	
	private ArrayList<Node> temporaryList;
	
	public LinkStateRouter(String name, InetSocketAddress id) {
		super(name, id);
		temporaryList = new ArrayList<Node>();
	}
	
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
	
	@Override
	public void run() {
		System.out.println("Running: " + name);
		while (true) {
			DatagramPacket p = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
			try {
				socket.receive(p);
				forwardMessage(p);
			} catch (Exception e) {
				if (!(e instanceof SocketException))
					e.printStackTrace();
			}
		}
	}
	
	/**
	 *	Sends distance to immediate neighbours
	 *	@deprecated Only included in case its needed in future. currently does nothing
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
		//TODO
		this.table = dijkstraAlgorithm();
	}
	
	public void addToTemporaryList(ObjectInputStream oin){
		try {
			RoutingTable newIn = new RoutingTable(oin);
			for(int i =0; i<newIn.getLength(); i++){
				temporaryList.add(new Node(newIn.getEntryAt(i), newIn.getHopAt(i), newIn.getCostAt(i)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Sends neighbours 
	 */
	public void sendNeighbours(){
		//TODO
	}
	
	
	//TODO needs testing
	/**
	 * Creates a new routing table giving the shortest path to each destination using Dijkstra's algorithm;
	 */
	public RoutingTable dijkstraAlgorithm(){
		RoutingTable newTable = new RoutingTable(table.getLength());
		newTable.addEntry(this.getAddress(), this.getAddress(), 0);
		ArrayList<Node> tentative = new ArrayList<Node>();
		int count = 1;
		
		while(count != newTable.getLength()){
			for(int i = 0; i<temporaryList.size(); i++){
				if(temporaryList.get(i).address.equals(newTable.getEntryAt(count)) || temporaryList.get(i).nextHop.equals(newTable.getEntryAt(count))){
					Node newNode = (temporaryList.get(i));
					newNode.distance = newNode.distance + newTable.getCostAt(i);
					tentative.add(newNode);
				}
			}
			Node smallest = tentative.get(0);
			int indexOfSmallest = 0;
			for(int i = 0; i<tentative.size(); i++){
				if (tentative.get(i).distance < smallest.distance){
					smallest = tentative.get(i);
					indexOfSmallest = i;
				}
			}
			if(smallest != null){
				newTable.addEntry(smallest.address, smallest.nextHop, smallest.distance);
				tentative.remove(indexOfSmallest);
				count++;
			}
		}
		
		return newTable;
	}
}
