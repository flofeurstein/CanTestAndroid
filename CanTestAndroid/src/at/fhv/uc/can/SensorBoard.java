package at.fhv.uc.can;

import at.fhv.uc.can.customconnection.CustomInputStream;
import at.fhv.uc.can.customconnection.CustomOutputStream;
import at.fhv.uc.can.standardframe.CANFrameSimulator;
import at.fhv.uc.can.standardframe.CANStandardFrame;
import at.fhv.uc.can.standardframe.DataFrameType2;

public class SensorBoard {
	private int m_sensorID;
	private int m_sessionID = -1;
	private int m_currTemp;
	private int m_currHum;
	private int m_counter = 0;
	private int m_rand = 0;
	private boolean m_hasTemperature = true;
	private boolean m_hasHumidity = true;
	private CANFrameSimulator m_cansim;

	public SensorBoard(){
		m_sensorID = 4711;
		m_currTemp = 1512;
		m_currHum = 65;
		m_cansim = new CANFrameSimulator();
	}
	
	public SensorBoard(int sensorID){
		m_sensorID = sensorID;
		m_currTemp = 1512;
		m_currHum = 65;
		m_cansim = new CANFrameSimulator();
	}
	
	public SensorBoard(int sensorID, int initTemperature, int initHumidity){
		m_sensorID = sensorID;
		m_currTemp = initTemperature;
		m_currHum = initHumidity;
		m_cansim = new CANFrameSimulator();
	}
	
	/**
	 * getter and setter
	 */
	public int getTemperature(){
		m_counter++;
		m_rand = (int)Math.round(Math.random()*10);
		if(m_counter % 4 == 0){
			m_currTemp +=m_rand;
		}else if(m_counter % 4 == 1){
			m_currTemp -=m_rand;
		}else if(m_counter % 4 == 2){
			m_currTemp -=m_rand;
		}else{
			m_currTemp +=m_rand;
		}
		return m_currTemp;
	}
	
	public int getHumidity(){
		if(m_counter % 4 == 0){
			m_currHum +=1;
		}else if(m_counter % 4 == 1){
			m_currHum -=1;
		}else if(m_counter % 4 == 2){
			m_currHum -=1;
		}else{
			m_currHum +=1;
		}
		return m_currHum;
	}
	
	public int getSensorID(){
		return m_sensorID;
	}
	
	/**
	 * hasTemperatureSensor
	 * @return true if it has a temperature sensor else false
	 */
	public boolean hasTemperatureSensor(){
		return m_hasTemperature;
	}
	
	/**
	 * hasHumiditySensor
	 * @return true if it has a temperature sensor else false
	 */
	public boolean hasHumiditySensor(){
		return m_hasHumidity;
	}
	
	/**
	 * communicate
	 * decides the action on the basis of the sessionID (if sensor has a 
	 * sessionID it sends data else it performs a handShake to get a sessionID)
	 * @param cis InputStream to read data from
	 * @param cos OutputStream to write data to
	 */
	public void communicate(CustomInputStream cis, CustomOutputStream cos){
		if(m_sessionID == -1){
			handShake(cis, cos);
		}else{
			sendData(cis, cos);
		}
	}
	
	/**
	 * handShake
	 * perform the tablet-sensor handshake
	 * @param cis, cos CustomInputStream and CustomOutputStream to send and receive data
	 * @return boolean if handshake was successful
	 */
	private boolean handShake(CustomInputStream cis, CustomOutputStream cos){
		boolean success = true;		
		success = sendSensorID(cis,cos);
			
		if(success){
			byte[] sessionData;
			sessionData = cis.slowRead();
			CANStandardFrame sessCSF = new CANStandardFrame(sessionData);
			
			if(sessCSF.getDataFrame().getType() == 2){
				DataFrameType2 dft2 = ((DataFrameType2)sessCSF.getDataFrame());
				
				if(dft2.getSensorID() == getSensorID()){
					m_sessionID = dft2.getSessionID();
					return success;
				}
			}
		}

		success = false;
		
		return success;
	}
	
	/**
	 * sendSensorID
	 * sends the SensorID to the tablet
	 * @param cis, cos CustomInputStream and CustomOutputStream to send and receive data
	 * @return boolean if the transmission was ok
	 */
	private boolean sendSensorID(CustomInputStream cis, CustomOutputStream cos){
		//CustomOutputStream out = new CustomOutputStream(client.getOutputStream());
		byte[] stdFrame = m_cansim.createStandardFrameType1(getSensorID());
		
		cos.slowWrite(stdFrame);
		//cos.flush();
		
		/**TODO
		 * check if successful
		 */
		return true;
	}
	
	/**
	 * sendData
	 * sends the sensordata to the tablet
	 * @param cis, cos CustomInputStream and CustomOutputStream to send and receive data
	 */
	private void sendData(CustomInputStream cis, CustomOutputStream cos){
	    //CustomOutputStream out = new CustomOutputStream(client.getOutputStream());
	    byte[] stdFrame;

    	if(hasTemperatureSensor()){
	    	stdFrame = m_cansim.createStandardFrameType3(m_sessionID, getTemperature());
			cos.slowWrite(stdFrame);
			//cos.flush();
    	}
		
    	if(hasHumiditySensor()){
			stdFrame = m_cansim.createStandardFrameType4(m_sessionID, getHumidity());
			cos.slowWrite(stdFrame);
			//cos.flush();
    	}

	}
}
