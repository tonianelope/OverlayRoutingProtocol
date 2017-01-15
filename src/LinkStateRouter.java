import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * LinkStateRouter represents a router that uses OSPF to forward packets
 *
 */
public class LinkStateRouter extends Router{
	
	private int[] prevSeqNums;
	private int totalNodes;
	private ArrayList<Node> temporaryList;
	private int seqNum;
	private int infoReceived;
	private static int ids = 0;
	private int id;
	
	public LinkStateRouter(String name, InetSocketAddress id, int totalNodes) {
		super(name, id);
		temporaryList = new ArrayList<Node>();
		seqNum = 1;
		infoReceived = 0;
		this.totalNodes = totalNodes;
		prevSeqNums = new int[totalNodes];
		this.id = LinkStateRouter.ids++;
	}
	
	@Override
	public void run() {
		System.out.println("Running: " + name);
		while (true) {
			DatagramPacket p = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
			try {
				socket.receive(p);
				Packet packet = Packet.fromDatagramPacket(p);
				if(packet.getType() == Packet.MESSAGE){
					forwardMessage(p);
				}
				else if (packet.getType() == Packet.ROUTER_TABLE){
					receiveTable(p);					
				}
			} catch (Exception e) {
				if (!(e instanceof SocketException))
					e.printStackTrace();
			}
		}
	}
	
	/**
	 * Creates new rtable from received info
	 */
	public void createRTable() {
		this.table = dijkstraAlgorithm();
	}
	
	public void addToTemporaryList(ObjectInputStream oin){
		try {
			RoutingTable newIn = new RoutingTable(oin);
			for(int i =0; i<newIn.getLength(); i++){
				temporaryList.add(new Node(newIn.getEntryAt(i), newIn.getHopAt(i), newIn.getCostAt(i)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Sends neighbours 
	 */
	@Override
	public void sendNeighbours(){
		System.out.println("sending neighbours");
		int numOfNodes = this.neighbors.length;
		
		Node[] neighbourNodes = new Node[numOfNodes];
		
		for(int i = 0; i<this.neighbors.length; i++){
			int cost = table.costTo(this.neighbors[i].getAddress());
			Node neighbourNode = new Node(this.getAddress(), this.neighbors[i].getAddress(), cost);
			neighbourNodes[i] = neighbourNode;
		}
		
		ObjectOutputStream oout;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try{
			oout = new ObjectOutputStream(byteOut);
			oout.writeInt(seqNum++);						//seqNum prevents infintite loops
			oout.writeInt(id);
			oout.writeInt(numOfNodes);
			oout.writeObject(neighbourNodes);
			oout.flush();
			
			byte[] data = byteOut.toByteArray();
			
			for(int i = 0; i<table.getLength(); i++){
				
				if(table.getEntryAt(i) != this.getAddress()){
					Packet p = new Packet(this.getAddress(), table.getEntryAt(i), data, Packet.ROUTER_TABLE);
					DatagramPacket packet = p.toDatagramPacket();
					System.out.println("Router "+this.getAddress()+" sending neighbours in OSPF to " + table.getEntryAt(i));
					packet.setSocketAddress(table.getHopAt(i));
					socket.send(packet);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	//TODO needs testing
	/**
	 * Creates a new routing table giving the shortest path to each destination using Dijkstra's algorithm;
	 */
	public RoutingTable dijkstraAlgorithm(){
		RoutingTable newTable = new RoutingTable(totalNodes);
		newTable.addEntry(this.getAddress(), this.getAddress(), 0);
		ArrayList<Node> tentative = new ArrayList<Node>();
		int count = 1;
		
		while(count < newTable.getLength()){
			for(int i = 0; i<temporaryList.size(); i++){
				if(temporaryList.get(i).address.equals(newTable.getEntryAt(count-1)) || 
								temporaryList.get(i).nextHop.equals(newTable.getEntryAt(count-1))){
					Node newNode;
					Node oldNode= (temporaryList.get(i));
					
					//correct node so final destination is in right field
					if(oldNode.address.equals(newTable.getEntryAt(count-1))){
						newNode = new Node(oldNode.nextHop, oldNode.address, 0);
					}
					else{
						newNode = new Node(oldNode.address, oldNode.nextHop, 0);
					}
					
					newNode.distance = oldNode.distance + newTable.getCostAt(count-1);
					tentative.add(newNode);
				}
			}
			Node smallest = tentative.get(0);
			int indexOfSmallest = 0;
			for(int i = 0; i<tentative.size(); i++){
				if (tentative.get(i).distance < smallest.distance){
					smallest = tentative.get(i);
					indexOfSmallest = i;
				}
			}
			if(smallest != null){
				newTable.addEntry(smallest.address, smallest.nextHop, smallest.distance);
				tentative.remove(indexOfSmallest);
				count++;
			}
		}
		
		return newTable;
	}

	@Override
	void receiveTable(DatagramPacket p) {

		Packet packet = Packet.fromDatagramPacket(p);
		byte[] data = packet.getData();
		
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		try {
			ObjectInputStream oin = new ObjectInputStream(bin);
			int sNum = oin.readInt();
			int readID = oin.readInt();
			
			if(sNum > prevSeqNums[readID]){									//will add
				prevSeqNums[readID] = sNum;
				++infoReceived;
				int numOfNodes = oin.readInt();
				Node[] neighbourNodes = (Node[]) oin.readObject();
				
				for(int i = 0; i<numOfNodes; i++){
					temporaryList.add(neighbourNodes[i]);
				}
				if(infoReceived >= this.totalNodes-1){
					infoReceived = 0;
					this.createRTable();
				}
			
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
	}
}
