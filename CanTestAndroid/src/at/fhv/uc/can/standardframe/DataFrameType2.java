package at.fhv.uc.can.standardframe;

public class DataFrameType2 extends DataFrame{
	private int m_sensorID;
	private int m_sessionID;

	public DataFrameType2(int sensorID, int sessionID){
		super(2);
		m_sensorID = sensorID;
		m_sessionID = sessionID;
	}
	
	public int getSensorID() {
		return m_sensorID;
	}

	public void setSensorID(int sensorID) {
		this.m_sensorID = sensorID;
	}

	public int getSessionID() {
		return m_sessionID;
	}

	public void setSessionID(int sessionID) {
		this.m_sessionID = sessionID;
	}
	
	/**
	 * writes the data of the frame into a string
	 */
	public String toString(){
		return "Type: " + m_type + " , SensorID: " + m_sensorID + ", SessionID: " + m_sessionID;
	}
}
