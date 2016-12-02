import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.DatagramPacket;

public class Router implements Runnable {
	static final int PACKETSIZE = 65536;

	String name;
	int id; // id static?
	Router[] neighbors;
	User[] user;
	DatagramSocket socket;
	RoutingTable table;

	/**
	 *
	 * @param name
	 * @param id?
	 * 
	 */
	public Router(String name, int id) {
		try {
			this.name = name;
			this.id = id;
			this.user = new User[0];
			this.neighbors = new Router[0];
			socket = new DatagramSocket(id);
			// run();
			System.out.println("Creating: " + name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		System.out.println("Start: " + name);

	}

	public void forwardMessage(DatagramPacket p) {
		try {
			UDPPacket udp = UDPPacket.fromDatagramPacket(p);
			int dest = udp.getDest();
			System.out.println("Received for: "+dest);

			int hop = table.getNextHop(dest);
			System.out.println("Hop: " + hop);
			if (hop == id) {
				hop = dest;
			}
			// check checksum?
			// p.setPort(dest);
			// Sets the SocketAddress (usually IP address + port number) of the
			// remote host to which this datagram is being sent.
			System.out.println("Hop: " + hop);
			p.setSocketAddress(new InetSocketAddress(hop));
			socket.send(p);
		} catch (Exception e) {
			System.out.println("ERROR can't forward packet");
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("Running: " + name);
		while (true) {
			DatagramPacket p = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
			try {
				socket.receive(p);
				System.out.println("r");
				forwardMessage(p);
			} catch (Exception e) {
				if (!(e instanceof SocketException))
					e.printStackTrace();
			}
		}
	}

	// public void forwarMessage(DatagramPacket p){
	// //get packet destination/address
	// IPv4Packet packet = IPv4Packet.fromDatagramPacket(p);
	// int dest = packet.getDest();
	// int hop = RoutingTable.getNextHop(dest);
	//
	// if(hop == id){
	// hop = dest;
	// }
	// //decrese time to live
	// //change checksum
	// //if(timeToLife>0);
	// p.setSocketAddress(dest); //???? whats the param
	// socket.send(p);
	// //lookup destination in routing table
	// //send to next hop
	// }

	public void addNeighbor(Router n) {
		Router[] temp = new Router[neighbors.length + 1];
		System.arraycopy(neighbors, 0, temp, 0, neighbors.length);
		temp[neighbors.length] = n;
		neighbors = temp;
	}

	public void setTable(RoutingTable t) {
		table = t;
	}

	public void join(User u) {
		User[] temp = new User[user.length + 1];
		System.arraycopy(user, 0, temp, 0, user.length);
		temp[user.length] = u;
		user = temp;
	}
}
