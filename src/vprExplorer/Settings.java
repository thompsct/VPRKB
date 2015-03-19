package vprExplorer;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public final class Settings {
	public static enum service { _REMOTE, _LOCAL_SERVER, _FILE};
	private GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

	private Dimension screensize;
	
	service servicetype;
	int port = 3030;
	
	public Settings() {
		int screenwidth = gd.getDisplayMode().getWidth();
		int screenheight = gd.getDisplayMode().getHeight();
		
		screensize = new Dimension(screenwidth, screenheight);
	}
	
	public Dimension getScreenDimensions() {
		return screensize;
	}
	
	public int getScreenWidth() {
		return (int) screensize.getWidth();
	}
	
	public int getScreenHeight() {
		return (int) screensize.getHeight();
	}
	
	public void setService(service ser) {
		servicetype = ser;
	}
	
	public service getService() {
		return servicetype;
	}
	
	public void setPort(int portnum) {
		port = portnum;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPorttoLocalDefault() {
		port = 3030;
	}
	
}
