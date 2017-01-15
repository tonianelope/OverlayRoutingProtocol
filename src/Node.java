import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * Node represents the triple (IP address, next hop IP, distance) in the network graph
 */
public class Node implements Serializable{
	private static final long serialVersionUID = 1L;
	InetSocketAddress address;
	InetSocketAddress nextHop;
	int distance;
	Node(InetSocketAddress addr, InetSocketAddress next, int dist){
		address = addr;
		nextHop = next;
		distance = dist;
	}
	
	public String toString(){
		return ("(" + address + "," + nextHop + "," + distance +")");
	}
}