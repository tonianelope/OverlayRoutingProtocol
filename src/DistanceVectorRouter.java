import java.net.InetSocketAddress;

/**
 * DistanceVectorRouter represents a router that uses RIP to forward packets
 */
public class DistanceVectorRouter extends Router{

	public DistanceVectorRouter(String name, InetSocketAddress id) {
		super(name, id);
	}
	
	
}
