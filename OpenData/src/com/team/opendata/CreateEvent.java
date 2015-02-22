package com.team.opendata;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

public class CreateEvent extends Activity{

	public static EditText title_txt;
	public static EditText desc_txt;
	public static TimePicker time;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {

		 super.onCreate(savedInstanceState);
	      setContentView(R.layout.create_event);
	      
	      final Button done = (Button)findViewById(R.id.createBtn);
	      title_txt = (EditText) findViewById(R.id.titleText);
	      desc_txt = (EditText) findViewById(R.id.descriptionText);
	      time = (TimePicker) findViewById(R.id.timePicker);
	      
	      
	      done.setOnClickListener (new View.OnClickListener(){
	        	public void onClick (View v){
	        		String url = "http://pursefitness.com/opendata/activity_add.php";
	        		String title = title_txt.getText().toString().replace(" ", "_");
	        		String description = desc_txt.getText().toString().replace(" ", "_");
	        		String userID = "10323";
	        		String locID = "1";
	        		String time_dat = time.getCurrentHour()+","+time.getCurrentMinute();
	        		String data = (url+"?aname="+title+"&adescription="+description+"&user_id="
	        				+userID+"&locid="+locID+"&atime="+time_dat);
	        		//update database class
	                new UpdateDatabase().execute(data);
	                
	                title_txt.setText("");
	                desc_txt.setText("");
	                finish();
	        	}
	      });
	  }
}

class UpdateDatabase extends AsyncTask<String, String, String> {

	@Override
	protected String doInBackground(String... arg0) {
		hitUrl(arg0[0]);
		return null;
	}
	
	public static HttpResponse hitUrl(String url) {
		try {
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response = httpclient.execute(new HttpGet(url));
		    Log.d("[RESULT]", "SUCCESS");
		    return response;
		} catch (Exception e) {
		    Log.d("[GET REQUEST]", "Network exception", e);
		    return null;
		}
	}
}


