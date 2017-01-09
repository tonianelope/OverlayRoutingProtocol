import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import tcdIO.*;


public class User extends Thread{
	static final int PACKETSIZE = 65536;

	private String name;
	private int port;
	private InetSocketAddress routerAdr;
	private InetSocketAddress myAdr;
	private InetAddress address;
	static String[] users = new String[20];
	static int numOfUsers;
	static InetSocketAddress[] addresses = new InetSocketAddress[20];
	boolean send;
	
	Terminal terminal;
	
	DatagramSocket socket;

	public User(String name, InetSocketAddress adr, Router router) {
		try {
			
			this.name = name;
			this.terminal = new Terminal(name);
			this.myAdr = adr;
			routerAdr = router.getAddress();
			socket = new DatagramSocket(myAdr.getPort());
			router.join(this);
			System.out.println("Creating: "+name);
			users[numOfUsers] = this.name;
			addresses[numOfUsers] = this.myAdr;
			numOfUsers++;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	public void run() {
		String message, dest;
		System.out.println("Running: "+name);
		Timer t = new Timer( );
		t.scheduleAtFixedRate(new TimerTask() {
		    @Override
		    public void run() {
		    	String message,dest;
				if(terminal.readString().equals("send")) {
					message = terminal.readString("Enter message: ");
					dest = terminal.readString("Enter recepient name: ");
					send(getDest(dest), message.getBytes());
				}	
		    }
		}, 0,1000);
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
		terminal.println("Received "+new String(data, "UTF-8"));		
	}

	public void main(String[] args){
		InetSocketAddress id =  new InetSocketAddress(args[1], Integer.parseInt(args[2]));
		User u = new User(args[0], id, null);
		boolean close = false;
		Scanner sc = new Scanner(System.in);
		while(!close){
			if(sc.hasNext("c")) close = true;
			else if(sc.hasNext("send")){
				sc.next();
				send(new InetSocketAddress(sc.next(), sc.nextInt()), sc.next().getBytes());
			}
		}
		sc.close();
	}
	
	/*
	 * @return the address of the user with the name @param name
	 */
	private InetSocketAddress getDest(String name){
		int i = 0;
		System.out.println(name);
		while(i < users.length && !name.equals(users[i])){ 
			System.out.print(users[i]+"  ");
			System.out.println(!name.equals(users[i]));

			i++;
		}
		return (i<addresses.length && name.equals(users[i]))? addresses[i]:null;
	}
	
	public void setRouter(Router r){
		this.routerAdr = r.getAddress();
	}
}
