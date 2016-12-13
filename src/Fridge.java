import java.net.InetSocketAddress;

public class Fridge extends User{
	public static final int FRIDGE = 1;
	private final int id;
	
	public Fridge(String name, InetSocketAddress addr, Router router, int idSet) {
		super(name, addr, router);
		id = idSet;
	}
	
	public int getID(){
		return id;
	}
}
