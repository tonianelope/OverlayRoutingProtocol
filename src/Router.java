import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.net.DatagramPacket;
import tcdIO.*;

public abstract class Router extends Thread {
	static final int PACKETSIZE = 65536;
	public static final int RTABLE_RESEND_INTERVAL = 10000; // 10 s, 10000 ms

	String name;
	int id;
	Router[] neighbors;
	User[] users;
	DatagramSocket socket;
	RoutingTable table;
	InetSocketAddress address;
	Terminal terminal;

	/**
	 *
	 * @param name
	 * @param id
	 *            - ip and port 1
	 * 
	 */
	public Router(String name, InetSocketAddress adr) {
		try {
			this.name = name;
			this.address = adr;
			this.users = new User[0];
			this.neighbors = new Router[0];
			socket = new DatagramSocket(address.getPort());
			System.out.println("Creating: " + name);
			terminal = new Terminal(this.name);
			new Thread(this).start();
			table = new RoutingTable(1);
			table.addEntry(adr, adr, 0);
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (terminal.readString().equals("print")) {
						printTable();
					}
				}
			}, 0, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param p
	 */
	public void forwardMessage(DatagramPacket p) {
		try {
			Packet packet = Packet.fromDatagramPacket(p);
			InetSocketAddress dest = packet.getDest();
			System.out.println(name + " Received for: " + dest);
			terminal.println("Received for: " + dest);
			InetSocketAddress hop = table.getNextHop(dest);
			if (hop.equals(address)) {
				hop = dest;
			}
			// check checksum?
			// p.setPort(dest);
			// Sets the SocketAddress (usually IP address + port number) of the
			// remote host to which this datagram is being sent.
			System.out.println("Hop: " + hop);
			terminal.println("Forwarding to: " + hop);
			p.setSocketAddress(hop);
			socket.send(p);
		} catch (Exception e) {
			System.err.println(name + " ERROR can't forward packet");
			e.printStackTrace();
		}
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (terminal.readString().equals("print")) {
					terminal.println("PRINT");
					printTable();
				}
			}
		}, 0, 1000);
	}

	public void run() {
		System.out.println("Running: " + name);
		
		while (true) {
			DatagramPacket p = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
			try {
				socket.receive(p);
				if (Packet.MESSAGE == Packet.fromDatagramPacket(p).getType()) {
					forwardMessage(p);
				} else {
					receiveTable(p);
				}
			} catch (Exception e) {
				if (!(e instanceof SocketException))
					e.printStackTrace();
			}
		}
	}

	abstract void receiveTable(DatagramPacket p);

	public void addNeighbor(Router n, int cost) {
		Router[] temp = new Router[neighbors.length + 1];
		System.arraycopy(neighbors, 0, temp, 0, neighbors.length);
		temp[neighbors.length] = n;
		neighbors = temp;
		table.addEntry(n.getAddress(), address, cost);
		temp = new Router[n.neighbors.length + 1];
		System.arraycopy(n.neighbors, 0, temp, 0, n.neighbors.length);
		temp[n.neighbors.length] = this;
		n.neighbors = temp;
		n.table.addEntry(address, n.getAddress(), cost);
		printN();
		n.printN();
	}

	/**
	 * Sends this router's current routing table to all neighbours
	 */
	public void sendTable() {

	}

	public void setTable(RoutingTable t) {
		table = t;
	}

	public void join(User u) {
		User[] temp = new User[users.length + 1];
		System.arraycopy(users, 0, temp, 0, users.length);
		temp[users.length] = u;
		users = temp;
		u.setRouter(this);
		table.addEntry(u.getAdr(), address, 1);
	}

	public int getPort() {
		return address.getPort();
	}

	public String getRouterName() {
		return name;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

	public void printTable() {
		System.out.println(table);
		try {
			terminal.println("\n" + this.getName() + "'s Routing table:");
			terminal.println(table.toString());
		} catch (Exception e) {
			terminal.println("No table");
		}
	}

	/*
	 * creates router in a (currently) fixed topology
	 */
	public void main(String[] args) {
		InetSocketAddress id = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
		// Router r = new Router(args[0], id);
		boolean close = false;
		Scanner sc = new Scanner(System.in);
		while (!close) {
			if (sc.hasNext("c"))
				close = true;
			else if (sc.hasNext("add")) {
				sc.next();
				// r.addNeighbor(new Router(sc.next(), new
				// InetSocketAddress(sc.next(), sc.nextInt())));
			}
		}
		sc.close();
	}

	private void printN() {
		for (int i = 0; i < this.neighbors.length; i++) {
			System.out.println("N "+name +" "+i+" "+neighbors.length);
			System.out.println(neighbors[i].getRouterName()+" "+neighbors[i].getAddress());
			terminal.println(neighbors[i].getRouterName()+" "+neighbors[i].getAddress());
		}
	}
	
	/**
	 * implemented by LSRouter
	 */
	public void sendNeighbours() {

	}
	public void addToNeighbours(InetSocketAddress nAddr){
		
	}
}
