package at.fhv.uc.can.standardframe;

public class DataFrameType3 extends DataFrame{
	private int m_sessionID;
	private int m_temperature;
	
	public DataFrameType3(int sessionID, int temp){
		super(3);
		m_sessionID = sessionID;
		m_temperature = temp;
	}
	
	public int getSessionID() {
		return m_sessionID;
	}

	public void setSessionID(int sessionID) {
		this.m_sessionID = sessionID;
	}

	public int getTemperature() {
		return m_temperature;
	}

	public void setTemperature(int temperature) {
		this.m_temperature = temperature;
	}
	
	/**
	 * writes the data of the frame into a string
	 */
	public String toString(){
		return "Type: " + m_type + " , SessionID: " + m_sessionID + ", Temperature: " + m_temperature;
	}
	
}
