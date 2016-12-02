import java.net.InetSocketAddress;

public class MainRoutingProtocol {

	static final int PORT_1 = 50000;
	static final int PORT_2 = 50001;
	static final int PORT_3 = 50002;
	static final int PORT_4 = 50003;
	static final int PORT_5 = 50004;
	static final int PORT_6 = 50005;
	static final int PORT_7 = 50006;
	static final int PORT_8 = 50007;
		
	public static void main(String[] args) {
		System.out.println("Start:");
		Router rOne = new Router("localhost", PORT_1);
		User one = new User("Alice", PORT_2,"localhost", PORT_1);
		Router rTwo = new Router("localhost", PORT_3);
		User two = new User("Bob", PORT_4,"localhost", PORT_3);
		
		System.out.println("tables");
		RoutingTable t1 = new RoutingTable(4);
		RoutingTable t2 = new RoutingTable(4);
		
		t1.addEntry(PORT_1, PORT_1, 0);
		t1.addEntry(PORT_2, PORT_1, 1);
		t1.addEntry(PORT_3, PORT_3, 1);
		t1.addEntry(PORT_4, PORT_3, 2);
		
		rOne.setTable(t1);
		
		t2.addEntry(PORT_3, PORT_3, 0);
		t2.addEntry(PORT_4, PORT_3, 1);
		t2.addEntry(PORT_1, PORT_1, 1);
		t2.addEntry(PORT_2, PORT_1, 2);
		
		rTwo.setTable(t2);
		
		rOne.start();
		rTwo.start();
		one.start();
		two.start();
		
		System.out.println("Start");
		String s = "Hello, Bob";
		byte[] data = s.getBytes();
		one.send(PORT_4, data);
		
		
	}

}
