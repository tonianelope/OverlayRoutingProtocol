public class IPv4Packet{

  private final byte version = 4;
  ptfinal byte hLength = 20; (in bytes)
  
  //service not used
  
  short tLength; //totallength 16 bits
  
  short identification;
  //falg 3 bits?
  short fragOffset;
  
  byte timeToLife;  //set default?
  byte protocol = 4;
  
  short hChecksum;
  
  int srcIPAdr;
  int destIPAdr;
  
  byte[] data;
  
  public IPv4Packet(int srcIPAdr, int destIPAdr, byte[] data){
    this.srcIPAdr = srcIPAdr;
    this.destIPAdr = destIPAdr;
    this.data = data;
    tLength = hLength + data.length;
    hChecksum = setChecksum(this);
  }
  
  public setChecksum(IPv4Packet p){
     //add all 16 bit values (exluding checksum)
    //convert to binary. first 4 bits (carry) get added to rest
    //invert = checksum
  } 
  
  public int getDest(){
    return this.destIPAdr;
  }
  
  public byte[] getData(){
    return this.data;
  }
  
  public short getHChecksum(){
    return this.hChecksum;
  }
  
  public int getDataLength(){
    return data.length;
  }
  
}
