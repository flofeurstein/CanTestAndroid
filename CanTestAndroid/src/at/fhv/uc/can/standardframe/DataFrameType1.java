package at.fhv.uc.can.standardframe;

public class DataFrameType1 extends DataFrame{
	private int m_sensorID;

	public DataFrameType1(int sensorID){
		super(1);
		m_sensorID = sensorID;
	}
	
	public int getSensorID() {
		return m_sensorID;
	}

	public void setSensorID(int sensorID) {
		this.m_sensorID = sensorID;
	}
	
	/**
	 * writes the data of the frame into a string
	 */
	public String toString(){
		return "Type: " + m_type + " , SensorID: " + m_sensorID;
	}
}
