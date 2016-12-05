
public class Laptop extends User{
	public static final int LAPTOP = 0;
	private final int id;
	
	public Laptop(String name, int port, Router router, int id) {
		super(name, port, router);
		this.id = id;
	}
	
	public int getID(){
		return id;
	}
}
