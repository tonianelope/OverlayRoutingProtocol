public class IPv4Packet{

  final byte version = 4;
  final byte hLength = 20; (in bytes)
  
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
  }
  
  public int getDest(){
    return destIPAdr;
  }
  
  public byte[] getData(){
    return data;
  }
  
  public int getDataLength(){
    return data.length;
  }
  
}
