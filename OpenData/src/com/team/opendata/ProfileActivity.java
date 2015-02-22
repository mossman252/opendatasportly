package com.team.opendata;


import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team.actor.Location;
import com.team.common.Constants;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {
	
	private int userId;
	
	private TextView mCheckInStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		mCheckInStatus.setVisibility(View.INVISIBLE);
		if(getIntent().getIntExtra(com.team.common.Constants.LOCATION_ID, 0) > 0) {
			 //get shared pref
	        SharedPreferences userInfoPref = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
	        userId = userInfoPref.getInt(Constants.USER_ID, 0);
	        
	        int locationId = getIntent().getIntExtra(com.team.common.Constants.LOCATION_ID, 0);
	        String checkInUrl = "http://pursefitness.com/opendata/user_check.php?user_id=" 
	        		+ userId + "&location_id=" + locationId;
	        
	        new DownloadJson().execute(checkInUrl);
	       
		} else {
			
		}
		
	}
	

	public class DownloadJson extends AsyncTask<String, Void, String> {
    	//download the vendor list from the server
		String json = "";
		@Override
		protected String doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(urls[0]);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
		     
				if (entity != null) {
					json = EntityUtils.toString(entity);
				}	
			} catch (ClientProtocolException e) {
				Log.e("Error", e.getMessage());
			} catch (IOException e) {
				Log.e("Error", e.getMessage());
			}
			return json;
		}
	
		//run when the data is pulled online
		@Override
		protected void onPostExecute(String result) {
			
			try {
				JSONArray jsonArray = new JSONArray(result);
				JSONObject json_data = jsonArray.getJSONObject(0);
				String status = json_data.getString("status");
				
				if(status.equals("1")) {
					Toast.makeText(ProfileActivity.this,"You Just Checked In !!", 
							Toast.LENGTH_LONG).show();
					mCheckInStatus.setText("Status : Checked In");
				} else {
					Toast.makeText(ProfileActivity.this,"You Just Checked Out !!", 
							Toast.LENGTH_LONG).show();
					mCheckInStatus.setText("Status : Checked Out");
				}
				mCheckInStatus.setVisibility(View.VISIBLE);
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	

}
