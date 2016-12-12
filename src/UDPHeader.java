import java.net.DatagramPacket;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UDPHeader {

	private int srcPort;
	private int destPort;
	private int length; // in bytes
	private int checksum; // header + data

	/**
	 * Creat UDP Packet (overlay over ipv4?)
	 * 
	 * 
	 * @param srcPort
	 * @param destPort
	 * @param data
	 */
	public UDPHeader(int srcPort, int destPort, int dataSize) {
		this.srcPort = srcPort;
		this.destPort = destPort;
		this.length = (8 + dataSize);
		this.checksum = setChecksum();
	}
	
	/**
	 * reads header info from ObjectInputStream
	 * 
	 * @param oin
	 */
	protected UDPHeader(ObjectInputStream oin) {
		try {
			srcPort = oin.readInt();
			destPort = oin.readInt();
			length = oin.readInt();
			checksum = oin.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * writes header info to output stream 1
	 * 
	 * @param oout
	 */
	protected void toObjectOutputStream(ObjectOutputStream oout) {
		try {
			oout.writeInt(srcPort); // write type to stream
			oout.writeInt(destPort);
			oout.writeInt(length);
			oout.writeInt(checksum);
		}
		catch(Exception e) {e.printStackTrace();}
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
	
}
