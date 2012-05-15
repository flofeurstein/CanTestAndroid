package at.fhv.uc.can.standardframe;

public class CANFrameSimulator {
	private String sof = "0";//1 Bit - SOF
	//private String ident = "00000000001";//11 Bit - Identifier
	private String rtr = "0";//1 Bit - RTR(Remote Transmission Request)
	private String ide = "0";//1 Bit - IDE(Identifier extension - Standard or Extended Frame)
	private String r0 = "0";//1 Bit - r0(reserved bit)
	private String crc = "101010101010101";//15 Bit - CRC
	private String ack = "00";//2 Bit - Ack
	private String eof = "1111111";//7 Bit - EOF
	private String ifs = "111";//3 Bit - IFS(Time to send)
	
	/**
	 * createStandardFrameType0
	 * creates a byte array with the data of a CAN-Standard-Frame and the self defined DataFrameType0 in its Data segment
	 * @param tabID is the TabletID for the DataFrameType0
	 * @return byte array, CAN-Standard-Frame
	 */
	public byte[] createStandardFrameType0(int tabID){
		return createStandardFrameType0or1(1, 0, tabID);//Identifier = 1, Type = 0
	}
	
	/**
	 * createStandardFrameType1
	 * creates a byte array with the data of a CAN-Standard-Frame and the self defined DataFrameType1 in its Data segment
	 * @param sensID is the ID of the sensor
	 * @return byte array, CAN-Standard-Frame
	 */
	public byte[] createStandardFrameType1(int sensID){
		return createStandardFrameType0or1(3, 1, sensID);//Identifier = 3, Type = 1
	}
	
	/**
	 * createStandardFrameType0or1
	 * supporting function to create DataFrameType0 and DataFrameType1
	 * @param ident identifier for the CAN-Standard-Frame
	 * @param messagetype the type of the DataFrame
	 * @param HWID HardwareID, either TabletID or SensorID
	 * @return byte array, CAN-Standard-Frame
	 */
	public byte[] createStandardFrameType0or1(int ident, int messagetype, int HWID){
		String identifier = stuffBits(11, Integer.toBinaryString(ident));
		String preData = sof+identifier+rtr+ide+r0;
		String postData = crc+ack+eof+ifs;
		String type = stuffBits(5, Integer.toBinaryString(messagetype));//Type = 0 or 1
		String hardwareID = stuffBits(32, Integer.toBinaryString(HWID));
		
		String dlc = stuffBits(4, Integer.toBinaryString(((type.length() + hardwareID.length())/8))+1);
		String data = preData + dlc + type + hardwareID + postData;
		
		return createByteArray(data);
	}
	
	/**
	 * createStandardFrameType2
	 * creates a byte array with the data of a CAN-Standard-Frame and the self defined DataFrameType2 in its Data segment
	 * @param sensID SensorID of the current sensor
	 * @param sessID SessionID for the current session
	 * @return byte array, CAN-Standard-Frame
	 */
	public byte[] createStandardFrameType2(int sensID, int sessID){
		String identifier = stuffBits(11, Integer.toBinaryString(2));//Identifier = 2
		String preData = sof+identifier+rtr+ide+r0;
		String postData = crc+ack+eof+ifs;
		String type = stuffBits(5, Integer.toBinaryString(2));//Type = 2
		String hardwareID = stuffBits(32, Integer.toBinaryString(sensID));
		String sessionID = stuffBits(8, Integer.toBinaryString(sessID));
		
		String dlc = stuffBits(4, Integer.toBinaryString(((type.length() + hardwareID.length() + sessionID.length())/8))+1);
		String data = preData + dlc + type + hardwareID  + sessionID + postData;
		
		return createByteArray(data);
	}
	
	/**
	 * createStandardFrameType3
	 * creates a byte array with the data of a CAN-Standard-Frame and the self defined DataFrameType3 in its Data segment
	 * @param sessID SessionID for the current session
	 * @param temp value of the temperature sensor
	 * @param humid value of the humidity sensor
	 * @return byte array, CAN-Standard-Frame
	 */
	public byte[] createStandardFrameType3(int sessID, int temp){
		String identifier = stuffBits(11, Integer.toBinaryString(4));//Identifier = 4
		String preData = sof+identifier+rtr+ide+r0;
		String postData = crc+ack+eof+ifs;
		String type = stuffBits(5, Integer.toBinaryString(3));//Type = 3
		String sessionID = stuffBits(8, Integer.toBinaryString(sessID));
		String temperature = stuffBits(16, Integer.toBinaryString(temp));
		
		String dlc = stuffBits(4, Integer.toBinaryString(((type.length() + sessionID.length() + temperature.length())/8))+1);
		String data = preData + dlc + type + sessionID + temperature + postData;
		
		return createByteArray(data);
	}
	
	/**
	 * createStandardFrameType4
	 * creates a byte array with the data of a CAN-Standard-Frame and the self defined DataFrameType4 in its Data segment
	 * @param sessID SessionID for the current session
	 * @param humid value of the humidity sensor
	 * @return byte array, CAN-Standard-Frame
	 */
	public byte[] createStandardFrameType4(int sessID, int humid){
		String identifier = stuffBits(11, Integer.toBinaryString(4));//Identifier = 4
		String preData = sof+identifier+rtr+ide+r0;
		String postData = crc+ack+eof+ifs;
		String type = stuffBits(5, Integer.toBinaryString(4));//Type = 4
		String sessionID = stuffBits(8, Integer.toBinaryString(sessID));
		String humidity = stuffBits(8, Integer.toBinaryString(humid));
		
		String dlc = stuffBits(4, Integer.toBinaryString(((type.length() + sessionID.length() + humidity.length())/8))+1);
		String data = preData + dlc + type + sessionID + humidity + postData;
		
		return createByteArray(data);
	}
	
	/**
	 * createByteArray
	 * creates a byte array from a "01" string
	 * @param data String with 0 and 1
	 * @return byte array
	 */
	private byte[] createByteArray(String data){
		byte[] stdFrame = new byte[(data.length()/8)+1];
		
		//fill byte array
		for(int k=0; k<data.length();k++){
			if(data.charAt(k) == '1'){
				stdFrame[k/8] = setBit(stdFrame[k/8], 7-(k%8));//7-(k%8) because we read the string from the front but we fill the byte array from the back
			}
		}
		return stdFrame;
	}
	
	/**
	 * stuffBits
	 * enlarges or shortens a binary string to a preferred length (unsigned)
	 * @param preferredLenght preferred length of the new binary string
	 * @param binStr binary string data
	 * @return binary string
	 */
	public String stuffBits(int preferredLenght, String binStr){//preferredLength in bits
		int diff = binStr.length() - preferredLenght;
		String newBinStr = binStr;
		if(diff > 0){
			newBinStr = binStr.substring(0 + diff, binStr.length());
		}else if(diff<0){
			for(int i=0;i>diff;i--){
				newBinStr = "0" + newBinStr;
			}
		}
		return newBinStr;
	}
	
	/**
	 * printByteBin
	 * supporting function to print a byte bitwise
	 * @param b byte to print bitwise
	 */
	public static void printByteBin(byte b){
		for(int j=7;j>=0;j--){
			if(testBit(b, j)){
				System.out.print(1);
			}else{
				System.out.print(0);
			}
		}
	}
	
	/**
	 * setBit
	 * sets a certain bit in a byte
	 * @param n byte to mutate
	 * @param pos position of the bit to be set
	 * @return mutated byte array
	 */
	private byte setBit( byte n, int pos ){
		return n |= (1 << pos);
	}

	/**
	 * testBit
	 * tests if a bit is set in a byte
	 * @param n byte to test
	 * @param pos position of the bit to test
	 * @return true if bit at pos is set, false if not
	 */
	private static boolean testBit( byte n, int pos ){
		int mask = 1 << pos;
		return (n & mask) == mask;
		// alternativ: return (n & 1<<pos) != 0;
	}
	

	/*private byte clearBit( byte n, int pos ){
		return n &= ~(1 << pos);
	}

	private byte flipBit( byte n, int pos ){
		return n ^= (1 << pos);
	}*/
}
