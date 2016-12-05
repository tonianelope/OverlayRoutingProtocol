
public class Fridge extends User{
	public static final int FRIDGE = 1;
	private final int id;
	
	public Fridge(String name, int port, Router router, int idSet) {
		super(name, port, router);
		id = idSet;
	}
	
	public int getID(){
		return id;
	}
}
