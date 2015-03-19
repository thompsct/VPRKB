package vprExplorer;

public final class Settings {
	public static enum service { _REMOTE, _LOCAL_SERVER, _FILE};
	
	service servicetype;
	int port = 3030;
	
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
