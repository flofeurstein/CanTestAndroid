package at.fhv.uc.can;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import at.fhv.uc.can.customconnection.CustomInputStream;
import at.fhv.uc.can.customconnection.CustomOutputStream;
import at.fhv.uc.can.standardframe.CANStandardFrame;

public class CANBusSimulator {
	/**
	 * Delay between two Data bursts
	 */
	public static int SENDING_DELAY = 5000;
	private final ServerSocket m_server;
	private ArrayList<SensorBoard> m_sensors;
	private CustomOutputStream m_cos;
	private CustomInputStream m_cis;
	
	public CANBusSimulator(int port) throws IOException{
	    m_server = new ServerSocket(port);
	    m_sensors = new ArrayList<SensorBoard>();
	}
	
	/**
	 * startServing
	 * start the server job
	 */
	private void startServing(){
		System.out.println("Starting Server...");
	    while (true){
		    Socket client = null;
		    try{
		    	System.out.println("Waiting for connection...");
		        client = m_server.accept();
		        System.out.println("Connection with " + client.getInetAddress() + " established...");
		        
		        m_cos = new CustomOutputStream(client.getOutputStream());
		        m_cis = new CustomInputStream(client.getInputStream());
		        
		        byte[] data;
				data = m_cis.slowRead();
				CANStandardFrame helloCSF = new CANStandardFrame(data);
				
				if(helloCSF.getIdentifier() == 1 && helloCSF.getDataFrame().getType() == 0){
					while(client.isConnected()){
			        	
			        	for(SensorBoard sb:m_sensors){
			        		sb.communicate(m_cis, m_cos);
			        	}
			        	
			        	Thread.sleep(SENDING_DELAY);
			        }
				}
		        
		    }catch (IOException e){
		    	e.printStackTrace();
		    } catch (InterruptedException e) {
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
	 * addSensor
	 * add a sensor to the CANBus
	 * @param sensorID, ID of the sensor
	 */
	public void addSensor(int sensorID){
		m_sensors.add(new SensorBoard(sensorID));
	}
	
	public static void main(String[] args) throws IOException{
		CANBusSimulator cbsim = new CANBusSimulator(3141);
		cbsim.addSensor(1234);
		cbsim.addSensor(2345);
		cbsim.addSensor(3456);
		cbsim.addSensor(4567);
		//cbsim.addSensor(4711);
		cbsim.startServing();
	}
	
}
