import java.net.InetSocketAddress;
import java.util.Arrays;

import javax.swing.JOptionPane;

public class MainRoutingProtocol {

	static final int PORT_1 = 50000;
	static final int PORT_2 = 50001;
	static final int PORT_3 = 50002;
	static final int PORT_4 = 50003;
	static final int PORT_5 = 50004;
	static final int PORT_6 = 50005;
	static final int PORT_7 = 50006;
	static final int PORT_8 = 50007;

	/**
	 * creates the following typology:
	 * 
	 * 		fridge
	 * 		  |
	 * alice-jones--20--smith - bob
	 * 		5000		5002
	 * 		  |			 |
	 * 		  2			13
	 * 		  |			 |
	 * 		  r----3---murphy - tom
	 * 		5004		5006
	 */
	public static void main(String[] args) {

		InetSocketAddress a1 = new InetSocketAddress("localhost", PORT_1);
		InetSocketAddress a2 = new InetSocketAddress("localhost", PORT_2);
		InetSocketAddress a3 = new InetSocketAddress("localhost", PORT_3);
		InetSocketAddress a4 = new InetSocketAddress("localhost", PORT_4);
		InetSocketAddress a5 = new InetSocketAddress("localhost", PORT_5);
		InetSocketAddress a6 = new InetSocketAddress("localhost", PORT_6);
		InetSocketAddress a7 = new InetSocketAddress("localhost", PORT_7);
		InetSocketAddress a8 = new InetSocketAddress("localhost", PORT_8);

		String[] options = { "Distance Vector Routing", "Link State Routing", "Fixed" };
		Object selectedValue = JOptionPane.showInputDialog(null, "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE,
				null, options, options[0]);

		Router jones, smith, r, murphy;

		if (selectedValue.equals(options[1])) {
			jones = new LinkStateRouter("Jones", a1,4);
			smith = new LinkStateRouter("Smith", a3,4);
			r = new LinkStateRouter("Router", a5,4);
			murphy = new LinkStateRouter("Murphy", a7,4);
			
			jones.table.addEntry(smith.getAddress(), smith.getAddress(), 20);
			jones.table.addEntry(r.getAddress(), r.getAddress(), 2);
			jones.printTable();
			
			r.table.addEntry(jones.getAddress(), jones.getAddress(), 2);
			r.table.addEntry(murphy.getAddress(), murphy.getAddress(), 3);
			r.printTable();
			
			smith.table.addEntry(jones.getAddress(), jones.getAddress(), 20);
			smith.table.addEntry(murphy.getAddress(), murphy.getAddress(), 13);
			
			murphy.table.addEntry(r.getAddress(), r.getAddress(), 3);
			murphy.table.addEntry(smith.getAddress(), smith.getAddress(), 13);
			
			
			jones.addToNeighbours(smith.getAddress());
			jones.addToNeighbours(r.getAddress());
			jones.printTable();
			
			r.addToNeighbours(jones.getAddress());
			r.addToNeighbours(murphy.getAddress());
			
			smith.addToNeighbours(jones.getAddress());
			smith.addToNeighbours(murphy.getAddress());
			
			murphy.addToNeighbours(smith.getAddress());
			murphy.addToNeighbours(r.getAddress());
			
			murphy.printTable();
			smith.printTable();
			jones.printTable();
			r.printTable();
			
			smith.sendNeighbours();
			jones.sendNeighbours();
			r.sendNeighbours();
			murphy.sendNeighbours();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			murphy.printTable();
			smith.printTable();
			jones.printTable();
			r.printTable();
			
		} else if (selectedValue.equals(options[0])) {
			jones = new DistanceVectorRouter("Jones", a1);
			smith = new DistanceVectorRouter("Smith", a3);
			r = new DistanceVectorRouter("Router", a5);
			murphy = new DistanceVectorRouter("Murphy", a7);
			
			jones.addNeighbor(smith, 20);
			jones.addNeighbor(r, 2);
			r.addNeighbor(murphy, 3);
			murphy.addNeighbor(smith, 13);
			
		} else {
			jones = new LinkStateRouter("Jones", a1,4);
			smith = new LinkStateRouter("Smith", a3,4);
			r = new LinkStateRouter("Router", a5,4);
			murphy = new LinkStateRouter("Murphy", a7,4);

			RoutingTable t1 = new RoutingTable(4);
			RoutingTable t2 = new RoutingTable(4);
			t1.addEntry(a1, a1, 0);
			t1.addEntry(a2, a1, 1);
			t1.addEntry(a3, a3, 1);
			t1.addEntry(a4, a3, 2);

			jones.setTable(t1);

			t2.addEntry(a3, a3, 0);
			t2.addEntry(a4, a3, 1);
			t2.addEntry(a1, a1, 1);
			t2.addEntry(a2, a1, 2);

			smith.setTable(t2);
			
			jones.addNeighbor(smith, 20);
			jones.addNeighbor(r, 2);
			r.addNeighbor(murphy, 3);
			murphy.addNeighbor(smith, 13);
		}
		
		User alice = new User("Alice", a2, jones);
		User bob = new User("Bob", a4, smith);
		User fridge = new Fridge("Fridge J.", a6, jones, 1);
		User tom = new User("Tom", a8, murphy);
		
		jones.printTable();
		smith.printTable();
		r.printTable();
		murphy.printTable();
	}
	// String s = "Hello, Bob";
	// byte[] data = s.getBytes();
	// one.send(a4, data);
	//
	// s = "Hello, Alcie";
	// data = s.getBytes();
	// two.send(a2, data);

	// Just leaving this here so i don't have to re type it in case
	// jones.start();
	// alice.start();
	// smith.start();
	// bob.start();
	// r.start();
	// fridge.start();
	// murphy.start();
	// tom.start();
}
