package at.fhv.uc.can.customconnection;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomInputStream extends FilterInputStream{

	public CustomInputStream(InputStream in) {
		super(in);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * slowRead
	 * counterpart of the slowWrite function, receives an integer (nr of bytes to receive) and then the bytes
	 * @return an byte array with all the received bytes
	 */
	public byte[] slowRead(){
		int len;
		byte[] b = null;
		try {
			len = in.read();
			b = new byte[len];
			
			for(int i=0; i<len; i++){
				b[i] = (byte)in.read();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}

}
