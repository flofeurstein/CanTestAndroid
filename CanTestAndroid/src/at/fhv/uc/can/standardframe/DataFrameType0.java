package at.fhv.uc.can.standardframe;

public class DataFrameType0 extends DataFrame{
	private int m_tabletID;

	public DataFrameType0(int tabID){
		super(0);
		m_tabletID = tabID;
	}
	
	public int getTabletID() {
		return m_tabletID;
	}

	public void setTabletID(int tabletID) {
		this.m_tabletID = tabletID;
	}	
	
	/**
	 * writes the data of the frame into a string
	 */
	public String toString(){
		return "Type: " + m_type + " , TabletID: " + m_tabletID;
	}
}
