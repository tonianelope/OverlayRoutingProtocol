import java.net.InetSocketAddress;

public class Laptop extends User{
	public static final int LAPTOP = 0;
	private final int id;
	
	public Laptop(String name, InetSocketAddress addr, Router router, int id) {
		super(name, addr, router);
		this.id = id;
	}
	
	public int getID(){
		return id;
	}
}
