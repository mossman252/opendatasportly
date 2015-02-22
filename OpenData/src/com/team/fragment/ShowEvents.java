package com.team.fragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import com.team.opendata.CreateEvent;
import com.team.opendata.EventListAdapter;
import com.team.opendata.R;
import com.team.opendata.R.id;
import com.team.opendata.R.layout;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class ShowEvents extends Fragment{
	
	List<String[]> uList;
	EventListAdapter listAdapter;
	int locID = 1;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		
		View view = inflater.inflate(R.layout.show_events, container, false);
		Button create = (Button) view.findViewById(R.id.createBtn);
		  
		ListView lv = (ListView) view.findViewById(R.id.listViewEvents);
		uList = new ArrayList<String[]>();
	    listAdapter = new EventListAdapter(getActivity().getApplicationContext(), R.layout.event_row, uList);
        lv.setAdapter(listAdapter);
        
        create.setOnClickListener (new View.OnClickListener(){
        	public void onClick (View v){
        		startActivity(new Intent(getActivity().getApplicationContext(), CreateEvent.class));
        	}
        });
        
        new CheckDatabase().execute("http://pursefitness.com/opendata/get_activity.php?locid="+locID);
    	
        //wait for database pulling to finish
    	try {
			Thread.sleep(1000);
			listAdapter.notifyDataSetChanged();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
	    return view;
	    
	}
	

	public void refresh(){
		
		uList.clear();
		new CheckDatabase().execute("http://pursefitness.com/opendata/get_activity.php?locid="+locID);
    	
        //wait for database pulling to finish
    	try {
			Thread.sleep(1500);
			listAdapter.notifyDataSetChanged();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
	class CheckDatabase extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			//hitUrl(arg0[0]);
			try {
				Log.d("[RESULT]",readUrl(arg0[0]));
				//JSONObject obj = new JSONObject(readUrl(arg0[0]));
				//JSONArray arr = obj.getJSONArray("");
				JSONArray arr = new JSONArray(readUrl(arg0[0]));
				for (int i = arr.length()-1; i >= 0; i--)
				{
				    String[] userInf = new String[3];
			    	for(int l =0; l<3; l++){
			    		userInf[0]=arr.getJSONObject(i).getString("activity_name").replace("_", " ");
			    		userInf[1]=arr.getJSONObject(i).getString("activity_description").replace("_", " ");
			    		userInf[2]=arr.getJSONObject(i).getString("event_time").replace(",", ":");
			    	}
			    	uList.add(userInf);
				}
				
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
