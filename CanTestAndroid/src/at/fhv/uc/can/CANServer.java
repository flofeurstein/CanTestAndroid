package at.fhv.uc.can;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import at.fhv.uc.can.customconnection.CustomInputStream;
import at.fhv.uc.can.customconnection.CustomOutputStream;
import at.fhv.uc.can.standardframe.CANFrameSimulator;
import at.fhv.uc.can.standardframe.CANStandardFrame;
import at.fhv.uc.can.standardframe.DataFrameType2;

public class CANServer {
	//public static int FRAME_SIZE = 11;
	/**
	 * Delay between two Data bursts
	 */
	public static int SENDING_DELAY = 0;
	private int m_sessionID = 0;
	private final ServerSocket m_server;
	private SensorBoard m_sts;
	private CANFrameSimulator m_cansim;
	
	public CANServer(int port) throws IOException{
	    m_server = new ServerSocket(port);
	    m_sts = new SensorBoard();
	    m_cansim = new CANFrameSimulator();
	}
	
	/**
	 * startServing
	 * start the server job
	 */
	private void startServing(){
		boolean success = false;
		System.out.println("Starting Server...");
	    while (true){
		    Socket client = null;
		    try{
		    	System.out.println("Waiting for connection...");
		        client = m_server.accept();
		        System.out.println("Connection with " + client.getInetAddress() + " established...");
		        
		        //success = handShake(client);
		        
		        while(!success){
		        	success = handShake(client);
		        }
		        
		        //handleConnection (client);
		        handleConnection(client);
		        
		    }catch (IOException e){
		    	e.printStackTrace();
		    }finally {
		    	if (client != null){
		    		try { 
		    			client.close(); 
		    		} catch (IOException e){ 
		    			e.printStackTrace();
		    		}
		    	}
		    }
	    }
	}

	/**
	 * handShake
	 * perform the tablet-sensor handshake
	 * @param client socket for the InputStream/OutputStream
	 * @return boolean if handshake was successful
	 * @throws IOException for InputStream/OutputStream
	 */
	private boolean handShake(Socket client) throws IOException{
		boolean success = true;
		CustomInputStream  in  = new CustomInputStream(client.getInputStream());
		byte[] data;// = new byte[FRAME_SIZE];
		data = in.slowRead();
		CANStandardFrame helloCSF = new CANStandardFrame(data);
		
		if(helloCSF.getIdentifier() == 1 && helloCSF.getDataFrame().getType() == 0){
			success = sendSensorID(client);
			
			if(success){
				byte[] sessionData;// = new byte[FRAME_SIZE];
				sessionData = in.slowRead();
				CANStandardFrame sessCSF = new CANStandardFrame(sessionData);
				
				if(sessCSF.getDataFrame().getType() == 2){
					DataFrameType2 dft2 = ((DataFrameType2)sessCSF.getDataFrame());
					
					if(dft2.getSensorID() == m_sts.getSensorID()){
						m_sessionID = dft2.getSessionID();
						return success;
					}
				}
			}
		}
		success = false;
		
		return success;
	}
	
	/**
	 * sendSensorID
	 * sends the SensorID to the tablet
	 * @param client socket for the InputStream/OutputStream
	 * @return boolean if the transmission was ok
	 * @throws IOException for InputStream/OutputStream
	 */
	private boolean sendSensorID(Socket client) throws IOException{
		CustomOutputStream out = new CustomOutputStream(client.getOutputStream());
		byte[] stdFrame = m_cansim.createStandardFrameType1(m_sts.getSensorID());
		
		out.slowWrite(stdFrame);
		out.flush();
		
		/**TODO
		 * check if successful
		 */
		return true;
	}
	
	/**
	 * handleConnection
	 * sends the sensordata to the tablet
	 * @param client socket for the InputStream/OutputStream
	 * @throws IOException for InputStream/OutputStream
	 */
	private void handleConnection(Socket client) throws IOException{
	    CustomOutputStream out = new CustomOutputStream(client.getOutputStream());
	    byte[] stdFrame;
	    while(true){
	    	
	    	if(m_sts.hasTemperatureSensor()){
		    	stdFrame = m_cansim.createStandardFrameType3(m_sessionID, m_sts.getTemperature());
				out.slowWrite(stdFrame);
				out.flush();
	    	}
			
	    	if(m_sts.hasHumiditySensor()){
				stdFrame = m_cansim.createStandardFrameType4(m_sessionID, m_sts.getHumidity());
				out.slowWrite(stdFrame);
				out.flush();
	    	}
	    	
			try {
				Thread.sleep(SENDING_DELAY);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

		/**TODO
		 * Abbruchbedingung!!
		 */
	}
	
	public static void main(String[] args) throws IOException{
		CANServer server = new CANServer(3141);
	    server.startServing();
	}
	
	/*private boolean handShakeTest(Socket client) throws IOException{
		boolean success = true;
		InputStream  in  = client.getInputStream();
		byte[] data = new byte[FRAME_SIZE];
		
		//receive hello message
		in.read(data);
		CANStandardFrame helloCSF = new CANStandardFrame(data);
		System.out.println();
		System.out.println("Hello Message:");
    	System.out.println("Identifier: " + helloCSF.getIdentifier());
    	System.out.println("DLC: " + helloCSF.getDLC());
		System.out.println("Data: " + helloCSF.getDataFrame().toString());
		System.out.println();
		
		//send sensorID
		sendSensorID(client);
		
		//receive sessionID
		byte[] sessionData = new byte[FRAME_SIZE+2];
		in.read(sessionData);

		CANStandardFrame sessCSF = new CANStandardFrame(sessionData);
		System.out.println("Receive SessionID:");
    	System.out.println("Identifier: " + sessCSF.getIdentifier());
    	System.out.println("DLC: " + sessCSF.getDLC());
		System.out.println("SessionCSF: " + sessCSF.getDataFrame().toString());
		
		return true;
	}*/
	
}
