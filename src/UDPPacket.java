import java.net.DatagramPacket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UDPPacket {

	private short srcPort;
	private short destPort;
	private short length; // in bytes
	private short checksum; // header + data
	private byte[] data;

	public UDPPacket(short srcPort, short destPort, byte[] data) {
		this.srcPort = srcPort;
		this.destPort = destPort;
		this.data = data;
		this.length = (short) (8 + (data.length / 8));
		this.checksum = setChecksum();
	}

	public DatagramPacket toDatagramPacket() {
		DatagramPacket packet = null;

		try {
			ByteArrayOutputStream bout;
			ObjectOutputStream oout;
			byte[] content;

			bout = new ByteArrayOutputStream();
			oout = new ObjectOutputStream(bout);

			oout.writeShort(srcPort); // write type to stream
			oout.writeShort(destPort);
			oout.writeShort(length);
			oout.writeShort(checksum);
			oout.write(data);
			// toObjectOutputStream(oout); // write content to stream depending
			// on type

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

	public static UDPPacket fromDatagramPacket(DatagramPacket p) {
		UDPPacket udp = null;

		try {
			byte[] data;
			ByteArrayInputStream bin;
			ObjectInputStream oin;

			data = p.getData(); // use packet content as seed for stream
			bin = new ByteArrayInputStream(data);
			oin = new ObjectInputStream(bin);
			udp = new UDPPacket(oin);
			oin.close();
			bin.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return udp;
	}

	protected UDPPacket(ObjectInputStream oin) {
		try {
			srcPort = oin.readShort();
			destPort = oin.readShort();
			length = oin.readShort();
			checksum = oin.readShort();
			for (int i = 0; i < length - 8; i++) {
				data[i] = oin.readByte();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public short setChecksum() {
		return 0;
	}

	public int getDest(){
		return destPort;
	}
	
	public int getSrc(){
		return srcPort;
	}
	
	public int getLength(){
		return length;
	}
	public int getChecksum(){
		return checksum;
	}
	
	public byte[] getData(){
		return data;
	}
	
	
}