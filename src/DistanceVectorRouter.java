import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * DistanceVectorRouter represents a router that uses RIP to forward packets
 */
public class DistanceVectorRouter extends Router {

	int id, cost, time = 0;

	public DistanceVectorRouter(String name, InetSocketAddress adr) {
		super(name, adr);
	}

	@Override
	public void run() {
		System.out.println("Running: " + name);
		Timer timer = new Timer();
		/**
		 * repeatadly sends out table (4 times) then cancels timer
		 */
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (time <= 3) {
					sendTable();
					time++;
				} else {
					timer.cancel();
				}
			}
		}, 0, 1000);
		while (true) {
			DatagramPacket p = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
			try {
				socket.receive(p);
				System.out.println(name + " Received for: " + Packet.fromDatagramPacket(p).getDest());
				if (Packet.MESSAGE == Packet.fromDatagramPacket(p).getType()) {
					forwardMessage(p);
				} else {
					System.out.println("REC");
					receiveTable(p);
				}
			} catch (Exception e) {
				if (!(e instanceof SocketException))
					e.printStackTrace();
			}
		}
	}

	/**
	 * Creates Routing table of neighbors and users
	 */
	public void createTable(int length) {
		this.table = new RoutingTable(length);
	}

	/**
	 * Adds new router into routing table
	 */
	public void addNeighbor(DistanceVectorRouter n) {
		boolean added = false;
		int i = 0;
		DistanceVectorRouter[] temp = new DistanceVectorRouter[neighbors.length + 1];
		System.arraycopy(neighbors, 0, temp, 0, neighbors.length);
		while (!added && i < neighbors.length) {
			if (neighbors[i].address.equals(n.address)) {
				added = true;
			}
		}
		if (!added) {
			temp[neighbors.length] = n;
			neighbors = temp;
			for (i = 0; i < n.neighbors.length; i++) {
				addUsers(n.users[i], n);
			}
			cost++;
		} else
			System.out.println(n.name + " already neighbour.");
	}

	/**
	 * Add new user to routing table
	 * 
	 * @param user
	 *            to be added
	 */
	public void addUsers(User user, DistanceVectorRouter r) {
		boolean added = false;
		int i = 0;
		User[] tempUsers = new User[users.length + 1];
		System.arraycopy(users, 0, tempUsers, 0, users.length);
		while (!added && i < users.length) {
			if (users[i] == user) {
				added = true;
			}
		}
		if (!added) {
			tempUsers[users.length] = user;
			users = tempUsers;
			for (i = 0; i < users.length; i++) {
				table.addEntry(users[i].getAdr(), r.getAddress(), cost);
			}
		} else
			System.out.println(r.name + " already user.");

	}

	/**
	 * Updates routing table
	 */
	public void updateTable() {

	}

	/**
	 * Sends this router's current routing table to all neighbors
	 */
	public void sendTable() {
		for (int i = 0; i < this.neighbors.length; i++) {
			try {
				Packet p = new Packet(address, neighbors[i].getAddress(), table.toByteArray(), Packet.ROUTER_TABLE);
				DatagramPacket packet = p.toDatagramPacket();
				packet.setSocketAddress(neighbors[i].getAddress());
				socket.send(packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * receives a routing table from its neighbors updates the routing table
	 * accordingly
	 */
	@Override
	void receiveTable(DatagramPacket p) {
		RoutingTable received = RoutingTable.fromDatagramPacket(p);
		System.out.println(name+" Received table\n"+received.toString());
		// loop trough entries in received table
		for(int i = 0; i<received.getLength(); i++){
			InetSocketAddress nextDest = received.getEntryAt(i);
			int cost = table.costTo(nextDest);
			System.out.println(name+" "+nextDest+" "+cost);
			// if entry not in this.table add to table, calculates the cost
			// based on the hops
			if (cost == -1){
				table.addEntry(nextDest, received.getHopAt(i), calcTotalCost(received, i));
			// if cost to the entry is change hop
			}
			else if (table.costTo(nextDest) > calcTotalCost(received, i)){
				table.updateEntry(nextDest, received.getHopAt(i), calcTotalCost(received, i));
			}
		}
		System.out.println(table);

	}

	/**
	 * calculates the cost from this router to the dest at position i in the
	 * received table adds the cost from the received table and the cost to get
	 * to the nextHop from this router
	 * 
	 * @param received
	 *            (received table)
	 * @param i
	 *            (entry to update)
	 * @return
	 */
	private int calcTotalCost(RoutingTable received, int i) {
		int cost = received.getCostAt(i);
		InetSocketAddress nextHop = received.getHopAt(i);
		cost += table.costTo(nextHop);
		return cost;
	}

}
