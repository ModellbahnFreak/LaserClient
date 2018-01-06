package client;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Settings {
	private String _hostName = "localhost";
	private int _port = 8082;
	private String _status = "";
	private BufferedImage _screensh;
	private boolean newScreensh = false;
	private boolean ScreenshActive = false;
	private ArrayList<String> vidList = new ArrayList<String>();
	private ArrayList<String> imgList = new ArrayList<String>();
	private ArrayList<String> objList = new ArrayList<String>();
	
	public Settings() {

	}
	
	public ArrayList<String> getVidList() {
		return vidList;
	}
	
	public ArrayList<String> getImgList() {
		return imgList;
	}
	
	public ArrayList<String> getObjList() {
		return objList;
	}
	
	public void setHostName(String host) {
		_hostName = host;
	}
	
	public String getHostName() {
		return _hostName;
	}
	
	public void setPort(int PortNum) {
		if (PortNum >= 0 && PortNum < 65535) {
			_port = PortNum;
		}
	}
	
	public int getPort() {
		return _port;
	}
	
	public void setStatus(String status) {
		_status = status;
		synchronized (this) {
			this.notifyAll();
		}
	}
	
	public String getStatus() {
		return _status;
	}
	
	public BufferedImage getScreenshot() {
		//synchronized (_screensh) {
			if (newScreensh && ScreenshActive) {
				//System.out.println("Returned image");
				newScreensh = false;
				return _screensh;
			} else {
				//System.out.println("Returned null");
				return null;
			}
		//}
	}

	public void setScreenshot(BufferedImage bild) {
		//synchronized (_screensh) {
			_screensh = bild;
			newScreensh = true;
			//System.out.println("Got new screenshot");
		//}
	}
	
	public void setScreenshActive(boolean active) {
		ScreenshActive = active;
	}
	
	public boolean getScreenshActive() {
		return ScreenshActive;
	}
}
