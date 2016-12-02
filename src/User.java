import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class User implements Runnable{
	static final int PACKETSIZE = 65536;

	private String name;
	private int port;
	InetSocketAddress routerAdr;

	DatagramSocket socket;

	public User(String name, int port,String router, int routerPort) {
		try {
			this.name = name;
			this.port = port;
			routerAdr = new InetSocketAddress(router,routerPort);
			socket = new DatagramSocket(port);
			//run();
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

	public void send(int destPort, byte[] data) {
		try {
			System.out.println("Sending: " + data.toString() + " to " + destPort);
			UDPPacket udp = new UDPPacket((short) port, (short) destPort, data);
			DatagramPacket packet = udp.toDatagramPacket();
			packet.setSocketAddress(routerAdr);
			socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void receive(DatagramPacket p) throws Exception{
		UDPPacket udp = UDPPacket.fromDatagramPacket(p);
		byte[] data = udp.getData();
		System.out.println("received "+new String(data, "UTF-8"));
		
	}

}
