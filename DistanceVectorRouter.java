import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * DistanceVectorRouter represents a router that uses RIP to forward packets
 */
public class DistanceVectorRouter extends Router{
	static final int PACKETSIZE = 65536;
	public static final int RTABLE_RESEND_INTERVAL = 10000;
	
	String name;
	int id,cost = 0; 
	DistanceVectorRouter[] neighbors;
	User[] users;
	DatagramSocket socket;
	RoutingTable table;
	private InetSocketAddress address;
	
	public DistanceVectorRouter(String name, int id , int totalUsers) {
		super(name, id);
		try {
			this.name = name;
			this.id = id;
			this.cost++;
			this.users = new User[0];
			this.neighbors = new DistanceVectorRouter[0];
			socket = new DatagramSocket(id);
			// run();
			System.out.println("Creating: " + name);
			createTable(totalUsers);
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
			if (hop == id) {
				hop = dest;
			}
			// check checksum?
			// p.setPort(dest);
			// Sets the SocketAddress (usually IP address + port number) of the
			// remote host to which this datagram is being sent.
			System.out.println("Hop: " + hop);
			p.setSocketAddress(new InetSocketAddress("localhost",hop));
			socket.send(p);
		} catch (Exception e) {
			System.err.println("ERROR can't forward packet");
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

	/**
	 * Creates Routing table of neighbors and users
	 */
	public void createTable(int length){
		this.table = new RoutingTable(length);
	}
	
	/**
	 *  Adds new router into routing table
	 */
	public void addNeighbor(DistanceVectorRouter n) {
		boolean added = false;
		int i = 0;
		DistanceVectorRouter[] temp = new DistanceVectorRouter[neighbors.length + 1];
		System.arraycopy(neighbors, 0, temp, 0, neighbors.length);
		while(!added && i < neighbors.length)
		{
			if(neighbors[i].id == n.id )
			{
				added = true;
			}
		}
		if(!added)
		{
			temp[neighbors.length] = n;
			neighbors = temp;	
			for(i = 0; i < n.neighbors.length; i++)
			{
				addUsers(n.users[i], n);
			}
			cost++;
		}
		else System.out.println(n.name + " already neighbour.");
	}
	
	/**
	 * Add new user to routing table
	 * @param user to be added
	 */
	public void addUsers(User user, DistanceVectorRouter r)
	{
		boolean added = false;
		int i = 0;
		User[] tempUsers = new User[users.length + 1];
		System.arraycopy(users, 0, tempUsers, 0, users.length);
		while(!added && i < users.length)
		{
			if(users[i] == user )
			{
				added = true;
			}
		}
		if(!added)
		{
			tempUsers[users.length] = user;
			users = tempUsers;	
			for(i = 0; i < users.length; i++)
			{
				table.addEntry(users[i].value, r.id, cost);
			}
		}
		else System.out.println(r.name + " already user.");

	}
	
	/**
	 * Updates routing table
	 */
	public void updateTable(){

	}
	/**
	 * Sends this router's current routing table to all neighbours
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

	public void setTable(RoutingTable t) {
		table = t;
	}

	public void join(User u) {
		User[] temp = new User[user.length + 1];
		System.arraycopy(user, 0, temp, 0, user.length);
		temp[user.length] = u;
		user = temp;
	}
	
	public int getPort(){
		return id;
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

