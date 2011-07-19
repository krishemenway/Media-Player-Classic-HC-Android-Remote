package mpchc.remote;

public class Host {
	public String Hostname;
	public int Port;
	
	public Host(String hostname,int port){
		Hostname = hostname;
		Port = port;
	}

	@Override
	public String toString() {		
		return Hostname + ":" + Port;
	}
}
