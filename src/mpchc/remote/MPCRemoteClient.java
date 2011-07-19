package mpchc.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MPCRemoteClient extends Activity {
	
	private String server;
	private int port;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent i = getIntent();
        server = i.getStringExtra("server");
        port = i.getIntExtra("port", 0);

        setButtons();
    }
    
    public void setButtons(){
    	RelativeLayout rl = (RelativeLayout)findViewById(R.id.RelativeLayout01);
        
        for(int k = 0; k < rl.getChildCount();k++){
        	final Button btn = (Button) rl.getChildAt(k);
        	
        	btn.setOnClickListener( new OnClickListener() {
    			public void onClick(View v) {
    				if(sendCommand(Integer.parseInt(btn.getTag().toString())) == false){
    					showError("Could not send command");
    					finish(); // TODO error & die
    				}
    			}
    		});
        }
    }
   
    public boolean sendCommand(int cmdNum) {
    	HttpURLConnection conn;
    	BufferedReader rd;
    	OutputStreamWriter wr;
    	URL url;
    	
        try {
			url = new URL("http://" + server + ":" + port + "/command.html");
	        conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Accept-Charset", "UTF-8");
	        conn.setReadTimeout(10000);
	        conn.setConnectTimeout(1000);
	        conn.setUseCaches(false);
        	
            String data = URLEncoder.encode("wm_command", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(cmdNum), "UTF-8");
			data += "&" + URLEncoder.encode("submit", "UTF-8") + "=" + URLEncoder.encode("Go!", "UTF-8");
			
	        // Send data
			wr = new OutputStreamWriter(conn.getOutputStream());
	        wr.write(data);
	        wr.flush();

	        // Read data
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    	rd.readLine();
	    	
	        wr.close();
	        rd.close();
	    	
	    	if(conn.getResponseCode() == 200);
    			return true;


		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		return false;
    }
    
    
    
	private void showError(String msg){
		AlertDialog.Builder build = new AlertDialog.Builder(this);
		AlertDialog diag = build.create();
        diag.setMessage(msg);
        diag.setButton("Okay", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   dialog.dismiss();
	           }
	       });
		diag.show();
	}
}
