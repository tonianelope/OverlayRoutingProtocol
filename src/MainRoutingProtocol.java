import java.net.InetSocketAddress;
import java.util.Arrays;

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
		Router rOne = new Router("Bruke", new InetSocketAddress("localhost",PORT_1));
		User one = new User("Alice", new InetSocketAddress("localhost",PORT_2), rOne);
		Router rTwo = new Router("Walsh", new InetSocketAddress("localhost",PORT_3));
		User two = new User("Bob",new InetSocketAddress("localhost",PORT_4), rTwo);
		
		InetSocketAddress a1 = new InetSocketAddress("localhost",PORT_1);
		InetSocketAddress a2 = new InetSocketAddress("localhost",PORT_2);
		InetSocketAddress a3 = new InetSocketAddress("localhost",PORT_3);
		InetSocketAddress a4 = new InetSocketAddress("localhost",PORT_4);

		
		RoutingTable t1 = new RoutingTable(4);
		RoutingTable t2 = new RoutingTable(4);
		
		t1.addEntry(a1, a1, 0);
		t1.addEntry(a2, a1, 1);
		t1.addEntry(a3, a3, 1);
		t1.addEntry(a4, a3, 2);
		
		rOne.setTable(t1);
		
		t2.addEntry(a3, a3, 0);
		t2.addEntry(a4, a3, 1);
		t2.addEntry(a1, a1, 1);
		t2.addEntry(a2, a1, 2);
		
		rTwo.setTable(t2);
		
		Thread tr1 = new Thread(rOne);
		Thread tr2 = new Thread(rTwo);
		Thread tr3 = new Thread(one);
		Thread tr4 = new Thread(two);
		
		tr1.start();
		tr2.start();
		tr3.start();
		tr4.start();
	
		String s = "Hello, Bob";
		byte[] data = s.getBytes();
		//System.out.println(Arrays.toString(data));
		one.send(a4, data);
		
		s = "Hello, Alcie";
		data = s.getBytes();
		//System.out.println(Arrays.toString(data));
		two.send(a2, data);
	}

}
