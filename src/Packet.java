import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class Packet {

	private UDPHeader udph;
	private IPv4Header ipv4h;
	private byte[] data;
	private int type; 
	public final static int MESSAGE = 1;
	public final static int ROUTER_TABLE = 2;
	//Type to idetify routingTable or message packet? 1
	
	public Packet(InetSocketAddress src, InetSocketAddress dest, byte[] data, int type){
		this.type = type;
		this.data = data;
		this.udph = new UDPHeader(src.getPort(), dest.getPort(), data.length);
		ipv4h = new IPv4Header(src.getAddress(), dest.getAddress(), data.length);
	}
	
	protected Packet(ObjectInputStream oin) {
		try {
			this.type = oin.readInt();
			this.udph = new UDPHeader(oin);
			ipv4h = new IPv4Header(oin);
			int length = oin.readInt();
			data = new byte[length];
			for(int i = 0; i<length; i++){
				this.data[i] = oin.readByte();
			}
			System.out.println(this+" read: "+new InetSocketAddress(ipv4h.destIPAdr, udph.getDest())+
					" "+new String(data));
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
			System.out.println(this+" write: "+new InetSocketAddress(ipv4h.destIPAdr, udph.getDest())+
					" "+new String(data));
			bout = new ByteArrayOutputStream();
			oout = new ObjectOutputStream(bout);

			oout.writeInt(type);
			udph.toObjectOutputStream(oout);
			ipv4h.toObjectOutputStream(oout);
			
			oout.writeInt(data.length);
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
	
	public int getType(){
		return type;
	}

}
