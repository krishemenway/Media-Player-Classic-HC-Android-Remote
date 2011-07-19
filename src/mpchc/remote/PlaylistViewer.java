package mpchc.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaylistViewer {

	private static final String playlistfile = "browser.html";
	private String server;
	private String port;
	
	public PlaylistViewer(String server,String port){
		this.server = server;
		this.port = port;
	}
	
	public void LoadPlaylist(){
    	HttpURLConnection connection = null;
    	BufferedReader rd  = null;
    	String line = null;
    	URL serverAddress = null;

    	try {
	    	serverAddress = new URL("http://" + server + ":" + port + "/" + playlistfile);

	    	connection = null;
	    	connection = (HttpURLConnection) serverAddress.openConnection();
	    	connection.setRequestMethod("GET");
	    	connection.setRequestProperty("Accept-Charset", "UTF-8");
	    	connection.setReadTimeout(10000);
	
	    	connection.connect();

	    	rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    	
			while(!rd.readLine().contains("<tr class=\"dir\">")) {} // SPIN LOCK WAITING FOR filenames to start
			    
			while ((line = rd.readLine()) != null)
			{
				if(line.contains("<td class=\"filename\">"))
					ParseAndAddVideo(line);
			}
	        
    	}
    	catch (MalformedURLException e){
    		e.printStackTrace();
    	}
    	catch (IOException e){
    		e.printStackTrace();
    	}
    	finally
    	{
	    	connection.disconnect();
	    	rd = null;
	    	connection = null;
    	}
	}
	
	private void ParseAndAddVideo(String incomingLine){
		String link = "";
		String filename = "";
		
		Pattern p = Pattern.compile("<a href=\"([^\"])\">([^<]+)</a>");
		Matcher matcher;
	}

}
