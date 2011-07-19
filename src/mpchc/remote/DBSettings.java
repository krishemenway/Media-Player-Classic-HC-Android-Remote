package mpchc.remote;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSettings {
	private Context context;
	private static final String DATABASE_NAME = "settings.db";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase db;
	
	public DBSettings(Context context) {
		this.context = context;
		SQLHelper openHelper = new SQLHelper(this.context);
		this.db = openHelper.getWritableDatabase();
	}
	
	public String[] getSavedServers(){
		List<String> hosts = new ArrayList<String>();
		Cursor cursor = db.query("servers", new String[] {"host"}, null, null, null, null, "createdate DESC");
		if(cursor.moveToFirst()){
			do {
				hosts.add(cursor.getString(0));
			} while(cursor.moveToNext());
		}
		cursor.close();
		
		return (String[]) hosts.toArray();
	}
	
	private static class SQLHelper extends SQLiteOpenHelper {

		public SQLHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// CREATE Tables
			db.execSQL("CREATE TABLE IF NOT EXISTS servers ('server_id' INTEGER UNSIGNED PRIMARY KEY AUTOINCREMENT"
					+ ",'host' VARCHAR(255) NOT NULL,'createdate' TIMESTAMP DEFAULT NOW())");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
