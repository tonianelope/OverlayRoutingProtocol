import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

public class IPv4Header{

  private final byte version = 4;
  private final byte hLength = 20; //(in bytes)
  
  //service not used
  
  int tLength; //totallength 16 bits
  
  int identification;
  //falg 3 bits?
  int fragOffset;
  
  byte timeToLife;  //set default?
  byte protocol = 4;
  
  int hChecksum;
  
  InetAddress srcIPAdr;
  InetAddress destIPAdr;
    
  
  public IPv4Header(InetAddress srcIPAdr, InetAddress destIPAdr, int dataSize){
    this.srcIPAdr = srcIPAdr;
    this.destIPAdr = destIPAdr;
    tLength =(short) (hLength + dataSize);
    setChecksum();
  }
  
  /**
   * reads header info from ObjectInputStream
   * 
   * @param oin
   */
  protected IPv4Header(ObjectInputStream oin) {
		try {
//			srcPort = oin.readInt();
//			destPort = oin.readInt();
			srcIPAdr = (InetAddress) oin.readObject();
			destIPAdr = (InetAddress) oin.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  
  /**
   * read data
   * 
   * @param oout
   */
  protected void toObjectOutputStream(ObjectOutputStream oout) {
		try {
			//oout.writeInt(srcPort); // write type to stream
			//oout.writeInt(destPort);
			oout.writeObject(srcIPAdr);
			oout.writeObject(destIPAdr);
		}
		catch(Exception e) {e.printStackTrace();}
	}

  
  public void setChecksum(){
     //add all 16 bit values (exluding checksum)
    //convert to binary. first 4 bits (carry) get added to rest
    //invert = checksum
  } 
    
  public InetAddress getDest(){
    return this.destIPAdr;
  }
 
  
  public int getHChecksum(){
    return this.hChecksum;
  }
  
}
