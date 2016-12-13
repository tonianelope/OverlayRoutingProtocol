import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

public class User extends Thread{
	static final int PACKETSIZE = 65536;

	private String name;
	private int port;
	private InetSocketAddress routerAdr;
	private InetSocketAddress myAdr;
	private InetAddress address;

	DatagramSocket socket;

	public User(String name, InetSocketAddress adr, Router router) {
		try {
			this.name = name;
			this.myAdr = adr;
			routerAdr = router.getAddress();
			socket = new DatagramSocket(myAdr.getPort());
			//run();
			router.join(this);
			System.out.println("Creating: "+name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("Running: "+name);
		while (true) {
			DatagramPacket p = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
			try {
				socket.receive(p);
				receive(p);
			} catch (Exception e) {
				if (!(e instanceof SocketException))
					e.printStackTrace();
			}
		}
	}
	
	public void start(){
		System.out.println("Start: "+name);

	}

	public void send(InetSocketAddress dest, byte[] data) {
		try {
			System.out.println(name+" Sending: " + Arrays.toString(data) + " to " + dest);
			Packet p = new Packet(myAdr, dest, data);
			DatagramPacket packet = p.toDatagramPacket();
			System.out.println("Router "+routerAdr);
			packet.setSocketAddress(routerAdr);
			socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void receive(DatagramPacket p) throws Exception{
		Packet packet = Packet.fromDatagramPacket(p);
		byte[] data = packet.getData();
		System.out.println(name+" received "+new String(data, "UTF-8"));
		
	}

}
