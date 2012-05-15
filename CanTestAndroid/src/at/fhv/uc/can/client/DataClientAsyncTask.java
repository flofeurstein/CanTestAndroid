package at.fhv.uc.can.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import at.fhv.uc.can.customconnection.CustomInputStream;
import at.fhv.uc.can.customconnection.CustomOutputStream;
import at.fhv.uc.can.standardframe.CANFrameSimulator;
import at.fhv.uc.can.standardframe.CANStandardFrame;
import at.fhv.uc.can.standardframe.DataFrameType1;


public class DataClientAsyncTask extends AsyncTask<Void, Void, Void>{
	private StringBuffer m_output;
	private TextView m_tv;
	private CANFrameSimulator m_cansim;
	private int m_tabletID;
	private static int m_currSessionID = 1;
	private CustomInputStream m_cis;
	private CustomOutputStream m_cos;
	
	/**
	 * m_sessionSensors
	 * HashMap<SensorID, SessionID>
	 */
	private HashMap<Integer, Integer> m_sessionSensors;
	
	public DataClientAsyncTask(TextView tv){
		m_cansim = new CANFrameSimulator();
		m_tabletID = 6492;
		m_sessionSensors = new HashMap<Integer, Integer>();
		m_output = new StringBuffer();
		m_tv = tv;
	}
	
	/**
	 * registerSensor
	 * generates a sessionID for a sensor, registers it and add it and its sessionID to the m_sessionSensors HashMap
	 * @param sensorID global ID of the sensor
	 * @return the sessionID
	 */
	private int registerSensor(int sensorID){
		/**
		 * test if sensorID is already registered
		 */
		
		//remove sensor entry if already registered
		m_sessionSensors.remove(sensorID);
		
		//if the unsigned byte is full, start with 1
		if(m_currSessionID == 255){
			m_currSessionID = 1;
		}
		
		m_sessionSensors.put(m_currSessionID, sensorID);
		
		return m_currSessionID++;
	}
	
	/**
	 * helloMessage
	 * writes the hello message (FrameType0) to the OutputStream
	 */
	private void helloMessage(){
		m_cos.slowWrite(m_cansim.createStandardFrameType0(m_tabletID));
	}
	
	/**
	 * handShake
	 * performs the handShake with the sensor (FrameType2)
	 * @param sensCSF holds the content of the received message
	 */
	private void handShake(CANStandardFrame sensCSF){
		int sensID = ((DataFrameType1)sensCSF.getDataFrame()).getSensorID();
		int sessionID = registerSensor(sensID);
		Log.v("handShake", "sensID: " + sensID + ", sessID: " + sessionID);
		m_cos.slowWrite(m_cansim.createStandardFrameType2(sensID, sessionID));
	}
	
	/**
	 * readData
	 * reads the data
	 * @param csf holds the content of the received message
	 */
	private void readData(CANStandardFrame csf){
		m_output.append("\n");
    	//System.out.println((new Date()).getTime());
		m_output.append("Receive Sensordata: " + "\n");
		m_output.append("Identifier: " + csf.getIdentifier() + "\n");
		m_output.append("DLC: " + csf.getDLC() + "\n");
		m_output.append("Data: " + csf.getDataFrame().toString() + "\n");
		Log.v("readData", "Data: " + csf.getDataFrame().toString());
	}
	
	/**
	 * communicate
	 * receives the messages from the InputStream and decides the action
	 * on the basis of the Identifier and the type of the message
	 */
	private void communicate(){
		byte[] canData;
		canData = m_cis.slowRead();
    	CANStandardFrame sensCSF = new CANStandardFrame(canData);
    	m_output.append("Receive SensorID:" + "\n");
    	m_output.append("Identifier: " + sensCSF.getIdentifier() + "\n");
    	m_output.append("DLC: " + sensCSF.getDLC() + "\n");
    	m_output.append("Data: " + sensCSF.getDataFrame().toString() + "\n");
    	m_output.append("\n");
    	Log.v("communicate", "Data: " + sensCSF.getDataFrame().toString());
    	
    	if(sensCSF.getIdentifier() == 3 && sensCSF.getDataFrame().getType() == 1){
    		handShake(sensCSF);
    	}else if(sensCSF.getIdentifier() == 3 && (sensCSF.getDataFrame().getType() == 3 || sensCSF.getDataFrame().getType() == 4)){
    		readData(sensCSF);
    	}
	}
	
	/**
	 * startConnection
	 * starts the Connection, creates the Socket and Input- and OutputStreams
	 * and calls the methods for the data processing
	 */
	public void startConnection(){
		Socket server = null;
		
		try {
			server = new Socket("192.168.1.140", 3141);
			m_cis = new CustomInputStream(server.getInputStream());
	    	m_cos = new CustomOutputStream(server.getOutputStream());
	    	
	    	helloMessage();
	    	
	    	while(server.isConnected()){
	    		communicate();
	    		publishProgress();
	    		m_output = new StringBuffer();
	    	}
	    	
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
	
	public static void main(String[] args) {
		DataClientAsyncTask dcl = new DataClientAsyncTask(null);
		dcl.startConnection();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		startConnection();
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {

		super.onProgressUpdate(values);
		m_tv.append(m_output);
	}
	
}
