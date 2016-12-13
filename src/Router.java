import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.DatagramPacket;

public class Router extends Thread {
	static final int PACKETSIZE = 65536;
	public static final int RTABLE_RESEND_INTERVAL = 10000; //10 s, 10000 ms

	String name;
	int id;
	Router[] neighbors;
	User[] user;
	DatagramSocket socket;
	RoutingTable table;
	private InetSocketAddress address;
	
	/**
	 *
	 * @param name
	 * @param id
	 * 		- ip and port 1
	 * 
	 */
	public Router(String name, InetSocketAddress id) {
		try {
			this.name = name;
			this.address = id;
			this.user = new User[0];
			this.neighbors = new Router[0];
			socket = new DatagramSocket(address.getPort());
			// run();
			System.out.println("Creating: " + name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		System.out.println("Start: " + name);

	}

	/**
	 * 
	 * @param p
	 */
	public void forwardMessage(DatagramPacket p) {
		try {
			Packet packet = Packet.fromDatagramPacket(p);
			InetSocketAddress dest = packet.getDest();
			System.out.println(name+" Received for: "+dest);

			InetSocketAddress hop = table.getNextHop(dest);
			if (hop.equals(address)) {
				hop = dest;
			}
			// check checksum?
			// p.setPort(dest);
			// Sets the SocketAddress (usually IP address + port number) of the
			// remote host to which this datagram is being sent.
			System.out.println("Hop: " + hop);
			p.setSocketAddress(hop);
			socket.send(p);
		} catch (Exception e) {
			System.err.println(name+" ERROR can't forward packet");
			e.printStackTrace();
		}
	}

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
	
	public void addNeighbor(Router n) {
		Router[] temp = new Router[neighbors.length + 1];
		System.arraycopy(neighbors, 0, temp, 0, neighbors.length);
		temp[neighbors.length] = n;
		neighbors = temp;
		//table.addEntry(destID, nextHopID, cost); needs change to InetAddresss
		
	}
	
	/**
	 * Sends this router's current routing table to all neighbours
	 */
	public void sendTable(){
		
	}

	public void setTable(RoutingTable t) {
		table = t;
	}

	public void join(User u) {
		User[] temp = new User[user.length + 1];
		System.arraycopy(user, 0, temp, 0, user.length);
		temp[user.length] = u;
		user = temp;
		//table.addEntry(destID, nextHopID, cost); needs change to InetAddresss

	}
	
	public int getPort(){
		return address.getPort();
	}
	
	public String getRouterName(){
		return name;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
}
