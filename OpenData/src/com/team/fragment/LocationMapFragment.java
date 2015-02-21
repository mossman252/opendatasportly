package com.team.fragment;

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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.team.actor.Location;

public class LocationMapFragment extends Fragment implements OnMapLongClickListener, OnInfoWindowClickListener{
	private MapFragment fragment;
	private GoogleMap map;
	
	static final LatLng Toronto = new LatLng(43.65, -79.38);
    private ArrayList<Location> locList;
    
    private String mFilter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    return inflater.inflate(R.layout.map_layout, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    FragmentManager fm = getChildFragmentManager();
	    fragment = (MapFragment) fm.findFragmentById(R.id.map);
	    if (fragment == null) {
	        fragment = MapFragment.newInstance();
	        fm.beginTransaction().replace(R.id.map, fragment).commit();
	    }
	    
	    //show progress
		getActivity().setProgressBarIndeterminateVisibility(true);
		((LocationActivity) getActivity()).setTitle("Map");
		
	    //initialize vendorList
        locList = new ArrayList<Location>();
        
        mFilter = ((LocationActivity)getActivity()).getFilter();
        
        DownloadJSON task = new DownloadJSON();
        String url;
        if(null != mFilter) {
        	url = "http://pursefitness.com/opendata/get_locations.php?category=" + mFilter; 
        } else {
        	url = "http://pursefitness.com/opendata/get_locations.php";
        }
        		
	    task.execute(new String[] { url });
	    
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
				for (int i=0; i < jsonArray.length()-1; i++) {
					   json_data = jsonArray.getJSONObject(i);
						locList.add(new Location(json_data.getInt("location_id"), 
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
								json_data.getString("LM_TYPE")));
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				addMarker();
			}
			
		}
	}
	
	public void addMarker()
    {
    	while(map == null){}
    	configureMap(map);
    	
    	for(int i = 0; i < locList.size(); i++)
		{
			 Marker marker = map.addMarker(new MarkerOptions()
      			.position(new LatLng(locList.get(i).getLat(), locList.get(i).getLongi())));
      			
      				//marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_dog_stand));
      				marker.setTitle(" " + locList.get(i).getName());
      				marker.setSnippet("Address: " + locList.get(i).getAddress());
      				marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		}
    	
    	//stop the progress bar
	    getActivity().setProgressBarIndeterminateVisibility(false);
	    map.setMyLocationEnabled(true);
         
    }
	
	@Override
	public void onInfoWindowClick(Marker marker) {
//			Intent intent = new Intent(getActivity().getApplicationContext(), VendorDetailsHandler.class);
//			intent.putExtra("VENDOR_ID", marker.getTitle().substring(12, marker.getTitle().length()));
//			startActivity(intent);
//			getActivity().overridePendingTransition(R.anim.translate_left_offscreen, R.anim.translate_right_onscreen);
	}
	
	@Override
	public void onMapLongClick(LatLng point) {
			
			map.setMyLocationEnabled(true);
	}
	    
	
	@Override
	public void onResume() {
	    super.onResume();
    	
	    if (map == null) {
	        map = fragment.getMap();
	    }
	    //Move the camera position into toronto
	  	CameraPosition cameraPosition = new CameraPosition.Builder().target(Toronto).zoom(13).build();
	  	map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	  	map.setOnMapLongClickListener(this);
	  	map.setOnInfoWindowClickListener(this);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
	}
	
	private void configureMap(GoogleMap map)
	{
	    if (map == null){
	        return; // Google Maps not available
	    }
	    
	    try {
	        MapsInitializer.initialize(getActivity());
	    }
	    catch (Exception e) {
	        System.out.println(e);
	    	return;
	    }
	    
	}
}
