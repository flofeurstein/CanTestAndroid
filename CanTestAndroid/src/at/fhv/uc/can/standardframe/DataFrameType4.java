package at.fhv.uc.can.standardframe;

public class DataFrameType4 extends DataFrame{
	private int m_sessionID;
	private int m_humidity;
	
	public DataFrameType4(int sessionID,int hum){
		super(4);
		m_sessionID = sessionID;
		m_humidity = hum;
	}
	
	public int getSessionID() {
		return m_sessionID;
	}

	public void setSessionID(int sessionID) {
		this.m_sessionID = sessionID;
	}

	public int getHumidity() {
		return m_humidity;
	}

	public void setHumidity(int humidity) {
		this.m_humidity = humidity;
	}
	
	/**
	 * writes the data of the frame into a string
	 */
	public String toString(){
		return "Type: " + m_type + " , SessionID: " + m_sessionID + ", Humidity: " + m_humidity;
	}
	
}
