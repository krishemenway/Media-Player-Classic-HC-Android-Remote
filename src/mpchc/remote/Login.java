package mpchc.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Login extends Activity {
	private EditText servertxt;
	private EditText porttxt;
	private Button connectbtn;
	private ProgressDialog pd;
	private Spinner serverSpinner;
	public static final String PREFS_NAME = "MPCPrefs";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		PopulateSpinner();

		servertxt = (EditText) findViewById(R.id.server);
		porttxt = (EditText) findViewById(R.id.port);
		connectbtn = (Button) findViewById(R.id.connectbtn);
		connectbtn.setOnClickListener(loginListener);
	}
	
	public void PopulateSpinner(){
		serverSpinner = (Spinner) findViewById(R.id.serverSpinner);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		
		ArrayAdapter<String> adapter = 
			new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		
		adapter.add(this.getString(R.string.server_select));
		
		DBSettings dbsettings = new DBSettings(this);
		String[] servers = dbsettings.getSavedServers();
		
		for(int i = 10;i > 0;i--) {
			if()
				adapter.add(settings.getString("server" + i, ""));
		}
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		serverSpinner.setOnItemSelectedListener(serverSelected);
		serverSpinner.setAdapter(adapter);
	}
	
	OnItemSelectedListener serverSelected = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			servertxt = (EditText) findViewById(R.id.server);
			porttxt = (EditText) findViewById(R.id.port);
			String[] host = serverSpinner.getSelectedItem().toString().split(":");
			if(host.length == 2){
				servertxt.setText(host[0]);
				porttxt.setText(host[1]);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	};
	
	OnClickListener loginListener = new OnClickListener() {
		public void onClick(View v) {
			showProgressDialog();

			String server = servertxt.getText().toString();
			int port = (int) Integer.parseInt(porttxt.getText().toString());

			if (checkConnection(server, port)) {
				Intent i = new Intent(v.getContext(), MPCRemoteClient.class);
				i.putExtra("server", server);
				i.putExtra("port", port);

				SharedPreferences settings = getSharedPreferences(
						PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("lastserver", server);
				editor.putInt("lastport", port);
				
				for(int i = 0;i++;i < 10){
					if(settings.contains("server" + i)){
						
					}
				}
				
				editor.commit();

				startActivity(i);
			} else {
				showError("Could not connect to server");
			}
		}
	};
	
	public boolean checkConnection(String server, int port) {
		// TODO validate server info -- either has a tld or ip addr

		HttpURLConnection connection = null;
		BufferedReader rd = null;
		String line = null;
		URL serverAddress = null;
		boolean success = false;

		try {
			serverAddress = new URL("http://" + server + ":" + port);

			connection = null;
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setReadTimeout(10000);

			connection.connect();
			rd = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));

			for (int i = 0; i < 3; i++) {
				line = rd.readLine();
			}

			if (line.contains("MPC WebServer"))
				success = true;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connection.disconnect();
			pd.dismiss();
			rd = null;
			connection = null;
		}

		return success;
	}

	private void showError(String msg) {
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

	private void showProgressDialog() {
		pd = new ProgressDialog(this);
		pd.setTitle(R.string.please_wait);
		pd.setMessage(getString(R.string.connecting));
		pd.setCancelable(true);
		pd.show();
	}
}