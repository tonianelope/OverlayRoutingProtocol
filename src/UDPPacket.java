import java.net.DatagramPacket;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UDPPacket {

	private int srcPort;
	private int destPort;
	private int length; // in bytes
	private int checksum; // header + data
	private byte[] data;

	/**
	 * Creat UDP Packet (overlay over ipv4?)
	 * 
	 * 
	 * @param srcPort
	 * @param destPort
	 * @param data
	 */
	public UDPPacket(int srcPort, int destPort, byte[] data) {
		this.srcPort = srcPort;
		this.destPort = destPort;
		this.data = data;
		this.length = (8 + data.length);
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

			oout.writeInt(srcPort); // write type to stream
			oout.writeInt(destPort);
			oout.writeInt(length);
			oout.writeInt(checksum);
			oout.write(data);
			// toObjectOutputStream(oout); // write content to stream depending
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
			srcPort = oin.readInt();
			destPort = oin.readInt();
			length = oin.readInt();
			checksum = oin.readInt();
			int dataLength = (length-8);
			data = new byte[dataLength];
			for (int i = 0; i < dataLength; i++) {
				data[i] = oin.readByte();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int setChecksum() {
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