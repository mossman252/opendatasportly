package com.team.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

import com.team.fragment.LocationMapFragment.DownloadJSON;
import com.team.opendata.LocationActivity;
import com.team.opendata.R;
import com.team.opendata.R.id;
import com.team.opendata.R.layout;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LocationListFragment extends Fragment {

	private ListView list;
	private ListAdapter adapter;
	private LocationManager locationManager;
	private String provider;
	private Location myLocation;
		
	private String mFilter;
	private ArrayList<com.team.actor.Location> mLocList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.general_list_screen, container,
				false);

		// show progress
		getActivity().setProgressBarIndeterminateVisibility(true);
		((LocationActivity) getActivity()).setTitle("Recreation Areas");

		// find view by id for list
		list = (ListView) view.findViewById(R.id.recreation_list);

		// Get the location manager
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		// Define the criteria how to select the locatioin provider -> use
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);

		myLocation = locationManager.getLastKnownLocation(provider);
		
		mFilter = ((LocationActivity)getActivity()).getFilter();
		
		if(((LocationActivity)getActivity()).getLocList() != null) {
			mLocList = ((LocationActivity)getActivity()).getLocList();
			init();
		} else {
			DownloadJSON task = new DownloadJSON();
			String url;
			if(null != mFilter) {
				url = "http://pursefitness.com/opendata/get_locations.php?category=" + mFilter; 
			} else {
				url = "http://pursefitness.com/opendata/get_locations.php";
			}
			task.execute(new String[] { url });
		}

		return view;
	}
	
	public void createList()
	{
        adapter= new ListAdapter(getActivity());  
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new OnItemClickListener() {
    	    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) { 
//    	    	        Intent intent = new Intent(getActivity().getApplicationContext(), VendorDetailsHandler.class);
//    					intent.putExtra("VENDOR_ID",v.getTag().toString());
//    					startActivity(intent);
//    					getActivity().overridePendingTransition(R.anim.translate_left_offscreen, R.anim.translate_right_onscreen);
    			}
        });
	    //stop the progress bar
	    getActivity().setProgressBarIndeterminateVisibility(false);
        
	}
	
    public class ListAdapter extends BaseAdapter {
		
		private Activity activity;
		private LayoutInflater inflater = null;
		
		public ListAdapter(Activity a) {
			 activity = a;
			 inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public int getCount() {
			if(mLocList == null) {
				return 0;
			} else {
				return  mLocList.size();
			}
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view =convertView;
	        if(convertView==null) {
	        	view = inflater.inflate(R.layout.list_row_vendor, null);
	        }
			
			//Textview for workout name and body part
			TextView name = (TextView)view.findViewById(R.id.name); 
	        TextView timeTaken = (TextView)view.findViewById(R.id.distance);
			TextView count = (TextView)view.findViewById(R.id.count); 

	        // Setting all values in listview
	        name.setText("  " + mLocList.get(position).getName()); 
	        if(mLocList.get(position).getCount() > 0) {
	        	count.setText("  " + mLocList.get(position).getCount() + " Checked In"); 
	        } else {
	        	count.setText("  No one Checked In "); 
	        }
	        if(!(myLocation == null)) {
	        	timeTaken.setText((String.format("%.2f", (mLocList.get(position)).getDistance()/1000))  + "km");
	        }
	        
	        view.setTag(mLocList.get(position).getLocationId());
	        
			return view;
		}
		
	}
	
	public class DownloadJSON extends AsyncTask<String, Void, String> {
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
				//put result into json array and parse
				JSONArray jsonArray = new JSONArray(result);
				JSONObject json_data = null;

				mLocList = new ArrayList<com.team.actor.Location>();
				for (int i=0; i < jsonArray.length()-1; i++) {
					   json_data = jsonArray.getJSONObject(i);
					   mLocList.add(new com.team.actor.Location(json_data.getInt("location_id"), 
								json_data.getString("CATEGORY"), 
								json_data.getString("UNIT"),
								json_data.getString("STR_ADDR"),
								json_data.getString("MUN"), 
								json_data.getString("POSTAL"),
								json_data.getString("PHONE"), 
								json_data.getString("LM_NAME"), 
								json_data.getDouble("LATITUDE"), 
								json_data.getDouble("LONGITUDE"), 
								json_data.getString("WEBSITE"), 
								json_data.getString("LM_TYPE"),
								json_data.getInt("checkedin")));
				}
				
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				((LocationActivity)getActivity()).setLocList(mLocList);
				init();
			}
			
		}
	}
	
	public void init() {
		
		Location vendorLoc = new Location("");

		if(null != mLocList) {
		
			for (int i = 0; i < mLocList.size(); i++) {
				vendorLoc.setLatitude(mLocList.get(i).getLat());
				vendorLoc.setLatitude(mLocList.get(i).getLongi());
				if (!(myLocation == null)) {
					float[] results = new float[3];
					Location.distanceBetween(myLocation.getLatitude(),
							myLocation.getLongitude(),
							mLocList.get(i).getLat(), mLocList.get(i)
									.getLongi(), results);
					mLocList.get(i).setDistance(results[0]);
					// vendorList.get(i).setDistance(vendorLoc.distanceTo(myLocation));
				}
			}

			// only if the app knows where the user's location is
			if (!(myLocation == null)) {
				Comparator<com.team.actor.Location> comparator = new MyComparator<com.team.actor.Location>();
				Collections.sort(mLocList, comparator);
			}
		}
		
		createList();
	}
	
	public class MyComparator<Loc> implements Comparator<com.team.actor.Location> {

	    public int compare(com.team.actor.Location loc1, com.team.actor.Location loc2){
	       return (int) (((com.team.actor.Location) loc1).getDistance() - ((com.team.actor.Location) loc2).getDistance());
	    }
	}
}
