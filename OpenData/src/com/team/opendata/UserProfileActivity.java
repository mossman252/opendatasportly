package com.team.opendata;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import com.team.opendata.ShowEvents.CheckDatabase;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends Activity{
	
	int usrID = 1;
	List<String[]> uList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);

	      setContentView(R.layout.user_profile);
	      ImageView profilePic = (ImageView) findViewById(R.id.badgePic);
	      ImageView badgePic = (ImageView) findViewById(R.id.profilePic);
	      TextView name = (TextView) findViewById(R.id.nameText);
	      TextView score = (TextView) findViewById(R.id.scoreText);
	      
	      /*FragmentManager fragmentManager = getFragmentManager();
	      FragmentTransaction fragmentTransaction = 
	      fragmentManager.beginTransaction();

	         //CreateEvent create = new CreateEvent();
	         //fragmentTransaction.replace(android.R.id.content, create);
	      
	      	ShowEvents show = new ShowEvents();
	         fragmentTransaction.replace(android.R.id.content, show);
	         
	      fragmentTransaction.commit();*/
	      
	      new CheckDatabase().execute("http://pursefitness.com/opendata/get_user_profile.php?user_id=1"+usrID);
	    	
	        //wait for database pulling to finish
	    	try {
				Thread.sleep(1000);
				//name.setText(uList.get(0)[0]);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}
	
	class CheckDatabase extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				Log.d("[RESULT]",readUrl(arg0[0]));
				JSONArray arr = new JSONArray(readUrl(arg0[0]));
				String[] userInf = new String[3];
		    	for(int l =0; l<3; l++){
		    		userInf[0]=arr.getJSONObject(0).getString("full_name");
		    		userInf[1]=arr.getJSONObject(0).getString("score");
		    	}
		    	uList.add(userInf);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		public HttpResponse hitUrl(String url) {
			try {
			    HttpClient httpclient = new DefaultHttpClient();
			    HttpResponse response = httpclient.execute(new HttpGet(url));
			    Log.d("[RESULT]", response.toString());
			    return response;
			} catch (Exception e) {
			    Log.d("[GET REQUEST]", "Network exception", e);
			    return null;
			}
		}
		
		private String readUrl(String urlString) throws Exception {
		    BufferedReader reader = null;
		    try {
		        URL url = new URL(urlString);
		        reader = new BufferedReader(new InputStreamReader(url.openStream()));
		        StringBuffer buffer = new StringBuffer();
		        int read;
		        char[] chars = new char[1024];
		        while ((read = reader.read(chars)) != -1)
		            buffer.append(chars, 0, read); 

		        return buffer.toString();
		    } finally {
		        if (reader != null)
		            reader.close();
		    }
		}
		
	}
	
}

