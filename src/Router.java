import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class Router {

	String name;
	Int id; //id static?
	Router[] neighbors;
	User[] users;
	/**
	 *
	 * @param name
	 * @param id?
	 * 
	 */
	public Router(String name, int id){
		this.name = name;
		this.id = id;
		this.user = new User[0];
		this.neighbors = new Router[0];
		
	}
	
	public void forwarMessage(DatagramPacket p){
		//get packet destination/address
		IPv4Packet packet = IPv4Packet.fromDatagramPacket(p);
		int dest = packet.getDest();
		int hop = RoutingTable.getNextHop(dest);
		
		if(hop == id){
			hop = dest;
		}
		//decrese time to live
		//change checksum
		p.setSocketAddress(dest); //???? whats the param
		socket.send(p);
		//lookup destination in routing table
		//send to next hop
	}
	
	public void addNeighbor(Router n){
		Router[] temp = new Router[neighbors.length +1];
		System.arraycopy(neighbors, 0, temp, neighbors.length);
		temp[neighbors.length] = n;
		neighbors = temp;
	}
	
	public void join(User u){
		User[] temp = new User[user.length +1];
		System.arraycopy(user, 0, temp, user.length);
		temp[user.length] = u;
		user = temp;	
	}
}
