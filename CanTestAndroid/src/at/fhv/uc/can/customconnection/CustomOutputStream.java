package at.fhv.uc.can.customconnection;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CustomOutputStream extends FilterOutputStream{
	/**
	 * delay used between each byte
	 */
	private long m_delay = 100;
	
	public CustomOutputStream(OutputStream out) {
		super(out);
		// TODO Auto-generated constructor stub
	}

	/**
	 * slowWrite
	 * writes each byte of a byte array seperately to an OutputStream,
	 * has a custom delay between each byte 
	 * @param b byte array to send
	 */
	public void slowWrite(byte[] b){
		int len = b.length;
		try {
			out.write(len);
			
			for(int i=0; i<len; i++){
				out.write(b[i]);
				Thread.sleep(m_delay);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
