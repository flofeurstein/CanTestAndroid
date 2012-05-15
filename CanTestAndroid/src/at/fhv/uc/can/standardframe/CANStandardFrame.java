package at.fhv.uc.can.standardframe;

public class CANStandardFrame {
	private int m_identifier;
	private int m_dlc;
	private DataFrame m_dataFrame;
	
	public CANStandardFrame(){
		
	}
	
	public CANStandardFrame(byte[] canData){
		parseBytes(canData);
	}
	
	/**
	 * parseBytes
	 * parses the byte array in the format of a CAN-Standard-Frame, defines the type of the dataframe
	 * and writes the data of the DATA-Segment from the CAN-Standard-Frame into the DataFrame objects
	 * @param canData byte array in a CAN-Standard-Frame format
	 */
	public void parseBytes(byte[] canData){
		short ident = (short)((canData[0] << 8) | canData[1] & 0xff);//0xff to make the byte unsigned
		m_identifier = (ident>>4);

		short dlc = (short)(((canData[1] & 0x1) << 8) | canData[2] & 0xff);//0xff to make the byte unsigned
		m_dlc = (dlc>>5);

		short messageType = (short)(canData[2] & 0x1f);//0xff to make the byte unsigned
		
		if(messageType == 0){
			int tabID = (canData[3]) << 24 | (canData[4] & 0xff) << 16 | (canData[5] & 0xff) << 8 | (canData[6] & 0xff);
			m_dataFrame = new DataFrameType0(tabID);
		}else if(messageType == 1){
			int sensorID = (canData[3]) << 24 | (canData[4] & 0xff) << 16 | (canData[5] & 0xff) << 8 | (canData[6] & 0xff);
			m_dataFrame = new DataFrameType1(sensorID);
		}else if(messageType == 2){
			int sensorID = (canData[3]) << 24 | (canData[4] & 0xff) << 16 | (canData[5] & 0xff) << 8 | (canData[6] & 0xff);
			int sessionID = (canData[7] & 0xff);//0xff to make the byte unsigned
			m_dataFrame = new DataFrameType2(sensorID, sessionID);
		}else if(messageType == 3){
			int sessionID = (canData[3] & 0xff);
			int temp = (canData[4]) << 8 | (canData[5] & 0xff);
			m_dataFrame = new DataFrameType3(sessionID, temp);
		}else if(messageType == 4){
			int sessionID = (canData[3] & 0xff);
			int hum = (canData[4] & 0xff);
			m_dataFrame = new DataFrameType4(sessionID,hum);
		}else{
			m_dataFrame = new DataFrame();
		}
	}
	
	public int getIdentifier(){
		return m_identifier;
	}
	
	public int getDLC(){
		return m_dlc;
	}
	
	public DataFrame getDataFrame(){
		return m_dataFrame;
	}
}
