import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class Packet {

	private static UDPHeader udph;
	private static IPv4Header ipv4h;
	private byte[] data;
	private int type; 
	//Type to idetify routingTable or message packet? 1
	
	public Packet(InetSocketAddress src, InetSocketAddress dest, byte[] data){
		this.data = data;
		udph = new UDPHeader(src.getPort(), dest.getPort(), data.length);
		ipv4h = new IPv4Header(src.getAddress(), dest.getAddress(), data.length);
	}
	
	protected Packet(ObjectInputStream oin) {
		try {
			udph = new UDPHeader(oin);
			ipv4h = new IPv4Header(oin);
			int length = oin.readInt();
			for(int i = 0; i<length; i++){
				this.data[i] = oin.readByte();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DatagramPacket toDatagramPacket() {
		DatagramPacket packet = null;

		try {
			ByteArrayOutputStream bout;
			ObjectOutputStream oout;
			byte[] content;

			bout = new ByteArrayOutputStream();
			oout = new ObjectOutputStream(bout);

			udph.toObjectOutputStream(oout);
			ipv4h.toObjectOutputStream(oout);
			
			oout.write(data); // write type to stream
			
			oout.flush();
			content = bout.toByteArray(); // convert content to byte array
			packet = new DatagramPacket(content, content.length); 
			oout.close();
			bout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return packet;
	}

	public static Packet fromDatagramPacket(DatagramPacket p) {
		Packet packet = null;

		try {
			byte[] data;
			ByteArrayInputStream bin;
			ObjectInputStream oin;
			
			data = p.getData();
			bin = new ByteArrayInputStream(data);
			oin = new ObjectInputStream(bin);
			
			packet = new Packet(oin);
			oin.close();
			bin.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return packet;
	}
	
	public InetSocketAddress getDest(){
		return new InetSocketAddress(ipv4h.getDest(), udph.getDest());
	}
	
	public byte[] getData(){
		return data;
	}

}
