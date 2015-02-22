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

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentManager;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.team.common.Constants;
import com.team.opendata.R;
import com.team.actor.Location;
import com.team.actor.User;

public class VendorInfoFragment extends Fragment{

	Location loc;
	String vendorID;
	private MapFragment fragment;
	private GoogleMap map;
	
	private DisplayImageOptions options;
	ArrayList<User> users;
	
	LinearLayout layoutPeople;
	
	//TextViews
	private TextView businessNameTV; //owner name
	private TextView businessNameTitle;
	private TextView websiteTitle;
	private TextView websiteTV;
	private TextView bNameTitle;
	private TextView categoryTV; //owner name
	private TextView categoryTitle;
	private TextView mapAddress;
	
	private ImageView vendorImage;
	private Typeface typeface_reg;
	
	private int userId;
	private ImageLoader imageLoader;
	
	@SuppressLint("NewApi") @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		//set the view for the fragment
        View view = inflater.inflate(R.layout.vendor_details_layout, container, false);
        getActivity().setProgressBarIndeterminateVisibility(true);
        
        //intantiate items
        businessNameTV = (TextView) view.findViewById(R.id.business_name_tv);
        businessNameTitle = (TextView) view.findViewById(R.id.business_name_title);
        websiteTV = (TextView) view.findViewById(R.id.website_tv);
        websiteTitle = (TextView) view.findViewById(R.id.website_title);
        categoryTitle = (TextView) view.findViewById(R.id.category_title);
        categoryTV = (TextView) view.findViewById(R.id.category_tv);
        mapAddress = (TextView) view.findViewById(R.id.map_address_tv);
        layoutPeople =(LinearLayout) view.findViewById(R.id.layout_people);
        typeface_reg = Typeface.createFromAsset(getActivity().getAssets(), Constants.HERO_BOLD); 
        users = new ArrayList<User>();
        
        //font declaration
        businessNameTitle.setTypeface(typeface_reg);
        categoryTitle.setTypeface(typeface_reg);
        websiteTitle.setTypeface(typeface_reg);
        
        //image options
        options = new DisplayImageOptions.Builder()
		.cacheInMemory(false)
		.cacheOnDisk(false)
		.considerExifParams(true)
		.showImageForEmptyUri(R.drawable.icon_noimage) // resource or drawable
        .showImageOnFail(R.drawable.icon_noimage) 
		.build();
        
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this.getActivity()));
        
        //produce map
        FragmentManager fm = getChildFragmentManager();
        fragment = (MapFragment) fm.findFragmentById(R.id.topmap);
        if (fragment == null) {
            fragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.topmap, fragment).commit();
        }
        
        Bundle extras = getArguments();
    	if (extras != null) {
    		vendorID = extras.getString("LOCATION_ID");
    		userId = extras.getInt("USER_ID");
    	}
        
    	//Get info from server
        DownloadJSON task = new DownloadJSON();
	    task.execute(new String[] { "http://pursefitness.com/opendata/get_location_details.php?location_id=" + vendorID  });

	    DownloadUsersForLocation userTask = new DownloadUsersForLocation();
	    userTask.execute(new String[] { "http://pursefitness.com/opendata/get_user_bylocation.php?location_id=1" + vendorID  });
	    
	    
        return view;
	}
	
	//this is done for location
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
				JSONArray jsonArray = new JSONArray(result);
				
				JSONObject json_data = null;
				for (int i=jsonArray.length()-1; i >= 0; i--) {
					    json_data = jsonArray.getJSONObject(i);
					    System.out.println(json_data);
						loc = new Location(json_data.getInt("location_id"), 
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
								json_data.getString("LM_TYPE")); 
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				//update vendor info
				updateVendorFields();
			}
			
		}
		
	}
	
	
	
	//this is done for location
		public class DownloadUsersForLocation extends AsyncTask<String, Void, String> {
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

					for(int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject json_data = null;
						json_data = jsonArray.getJSONObject(i);
						users.add(new User(json_data.getString("user_id"), json_data.getString("fb_id")));
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					//update vendor info
					addUserImagesToUI();
				}
				
			}
			
		}
		
	public void addUserImagesToUI()
	{
		
		TableLayout.LayoutParams profilePicParams = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		profilePicParams.gravity = Gravity.TOP;
		
		for(int i = 0; i<users.size(); i++)
		{
			
			ImageView profilePic = new ImageView(getActivity().getApplicationContext());
			profilePic.setLayoutParams(profilePicParams);
			profilePic.setPadding(10, 10, 10, 10);
			ImageLoader.getInstance().displayImage("http://graph.facebook.com/"+ users.get(i).getFbId() + "/picture?type=large", profilePic, options);
		    
			/*profilePic.setOnClickListener(new Button.OnClickListener() {
				Intent in = new Intent(getActivity().class. ProfilePic.class);
				in.setArguments("", users.get(i).getUserId);
				startActivity(in);
			});*/
			layoutPeople.addView(profilePic);
		}
		
	}
	
	//update all the fields once the vendor returns from a poo
	public void updateVendorFields()
	{
		
		if(loc != null) {
			//set the address of the vendor
			mapAddress.setText(loc.getAddress() + ", " + loc.getPostalCode());
		
			businessNameTV.setText(Constants.capitalizeFirst(loc.getName()));
			
			//if the vendor doesnt specify the name dont show it
			if(loc.getCategory().length() > 3)
			{
			    categoryTV.setText(loc.getCategory());  
			}
		
			//if the vendor doesnt specify the name dont show it
			if(loc.getWebsite().length() > 3)
			{
				websiteTV.setText(loc.getWebsite());
			}
			
			
			LatLng vendorLoc = new LatLng(loc.getLat(), loc.getLongi());
			CameraPosition cameraPosition = new CameraPosition.Builder().target(vendorLoc).zoom(15).build();
		  	map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		  	map.addMarker(new MarkerOptions()
               .position(vendorLoc));
		  	map.setMyLocationEnabled(true);
		  	
		  	getActivity().setProgressBarIndeterminateVisibility(false);
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    if (map == null) {
	        map = fragment.getMap();
	    }
	    //set the zoom invisible
	    map.getUiSettings().setZoomControlsEnabled(false);
	}
}
