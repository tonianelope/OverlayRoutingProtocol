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
	private ArrayList<InetSocketAddress> neighbours;
	
	public LinkStateRouter(String name, InetSocketAddress id, int totalNodes) {
		super(name, id);
		temporaryList = new ArrayList<Node>();
		seqNum = 1;
		infoReceived = 0;
		this.totalNodes = totalNodes;
		prevSeqNums = new int[totalNodes];
		neighbours = new ArrayList<InetSocketAddress>();
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
		terminal.println("Building Routing Table from received info using Dijkstra");
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
	 * add a neighbour of this router's address
	 * @param nAddr
	 */
	@Override
	public void addToNeighbours(InetSocketAddress nAddr){
		neighbours.add(nAddr);
	}
	
	public void sendNodeArray(Node[] nodes, int numOfNodes, int sNum, int senderID){
		ObjectOutputStream oout;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try{
			oout = new ObjectOutputStream(byteOut);
			oout.writeInt(sNum);						//seqNum prevents infintite loops
			oout.writeInt(senderID);
			oout.writeInt(numOfNodes);
			oout.writeObject(nodes);
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
	
	/**
	 * Sends neighbours 
	 */
	@Override
	public void sendNeighbours(){
		System.out.println("sending neighbours");
		int numOfNodes = this.neighbours.size();
		
		Node[] neighbourNodes = new Node[numOfNodes];
		
		for(int i = 0; i<this.neighbours.size(); i++){
			int cost = table.costTo(this.neighbours.get(i));
			Node neighbourNode = new Node(this.getAddress(), this.neighbours.get(i), cost);
			neighbourNodes[i] = neighbourNode;
		}
		sendNodeArray(neighbourNodes, numOfNodes, this.seqNum++, this.id);
		
	}
	
	
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
				if(( temporaryList.get(i).address.equals(newTable.getEntryAt(count-1)) || 
								temporaryList.get(i).nextHop.equals(newTable.getEntryAt(count-1))) && !(newTable.contains(temporaryList.get(i).address))){
					
					Node newNode;
					Node oldNode= (temporaryList.get(i));
					
					//correct node so final destination is in right field
					
					if(oldNode.address.equals(newTable.getEntryAt(count-1))){
						newNode = new Node(oldNode.nextHop, oldNode.address, 0);
					}
					else{
						newNode = new Node(oldNode.address, oldNode.nextHop, 0);
					}
					
					if(newNode.nextHop.equals(this.address)){
						newNode.nextHop = newNode.address;
					}
					
					
					newNode.distance = oldNode.distance + newTable.getCostAt(count-1);
					if(newNode.address.equals(this.address)){
						newNode.distance = Integer.MAX_VALUE;
					}
					boolean add = true;
					for(int index = 0; index<tentative.size(); index++){
						if(tentative.get(index).address.equals(newNode.address)){
							
							if(tentative.get(index).distance < newNode.distance){
								add = false;
							}
							else{
								tentative.remove(index);
							}
							
						}
					}
					if(add==true){
					tentative.add(newNode);}
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
				if(!(newTable.contains(smallest.address))){
					newTable.addEntry(smallest.address, smallest.nextHop, smallest.distance);
				}
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
			
			if(sNum > prevSeqNums[readID] && readID != this.id){									//will add
				
				
				prevSeqNums[readID] = sNum;
				++infoReceived;
				int numOfNodes = oin.readInt();
				Node[] neighbourNodes = (Node[]) oin.readObject();
				
				this.sendNodeArray(neighbourNodes, numOfNodes, sNum, readID);
				
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
