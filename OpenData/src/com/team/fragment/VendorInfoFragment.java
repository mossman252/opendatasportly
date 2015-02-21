package com.team.fragment;

import java.io.IOException;

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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.team.common.Constants;
import com.team.actor.Location;

public class VendorInfoFragment extends Fragment{

	Location location;
	String vendorID;
	private MapFragment fragment;
	private GoogleMap map;
	
	private DisplayImageOptions options;
	
	//TextViews
	private TextView oNameTV; //owner name
	private TextView oNameTitle;
	private TextView websiteTitle;
	private TextView websiteTV;
	private TextView websiteTtitle; 
	private TextView bNameTitle;
	private TextView categoryTV; //owner name
	private TextView categoryTitle;
	private TextView mapAddress;
	private TextView vendorRatingTxt;
	private ImageView vendorImage;
	private Typeface typeface_reg;
	
	private RatingBar vendorRating;
	private int userId;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		//set the view for the fragment
        View view = inflater.inflate(R.layout.vendor_details_layout, container, false);
        getActivity().setProgressBarIndeterminateVisibility(true);
        
        //intantiate items
        bNameTV = (TextView) view.findViewById(R.id.business_name_tv);
        bNameTitle = (TextView) view.findViewById(R.id.business_name_title);
        oNameTV = (TextView) view.findViewById(R.id.owner_name_tv);
        oNameTitle = (TextView) view.findViewById(R.id.owner_title);
        websiteTV = (TextView) view.findViewById(R.id.website_tv);
        websiteTitle = (TextView) view.findViewById(R.id.website_title);
        categoryTitle = (TextView) view.findViewById(R.id.category_title);
        categoryTV = (TextView) view.findViewById(R.id.license_num_tv);
        mapAddress = (TextView) view.findViewById(R.id.map_address_tv);
        vendorRatingTxt = (TextView) view.findViewById(R.id.vendor_rating_text);
        typeface_reg = Typeface.createFromAsset(getActivity().getAssets(), Constants.HERO_BOLD); 

        //font declaration
        bNameTitle.setTypeface(typeface_reg);
        oNameTitle.setTypeface(typeface_reg);
        websiteTitle.setTypeface(typeface_reg);
        licenseTitle.setTypeface(typeface_reg);
        
        //image options
        options = new DisplayImageOptions.Builder()
		.cacheInMemory(false)
		.cacheOnDisk(false)
		.considerExifParams(true)
		.showImageForEmptyUri(R.drawable.icon_noimage) // resource or drawable
        .showImageOnFail(R.drawable.icon_noimage) 
		.build();
        
        //produce map
        FragmentManager fm = getChildFragmentManager();
        fragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.topmap, fragment).commit();
        }
        
        Bundle extras = getArguments();
    	if (extras != null) {
    		vendorID = extras.getString("VENDOR_ID");
    		userId = extras.getInt("USER_ID");
    	}
        
    	//Get info from server
        DownloadJSON task = new DownloadJSON();
	    task.execute(new String[] { "http://pursefitness.com/opendata/get_location_details.php?location_id=" + vendorID  });

        return view;
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
	
	//update all the fields once the vendor returns from a poo
	public void updateVendorFields()
	{
			//set the address of the vendor
			mapAddress.setText(vendor.getAddress() + ", " + vendor.getPostalCode());
			
			if(vendor.getHotdogScore() == 0) //no one has rated the vendor yet
			{
				vendorRatingTxt.setVisibility(View.VISIBLE);
				vendorRating.setRating(0);
			}
			else
			{
				vendorRatingTxt.setVisibility(View.VISIBLE);
				vendorRatingTxt.setText("Rated: " + (int) vendor.getHotdogScore() + " / 5" );
				vendorRating.setRating((int) vendor.getHotdogScore());
			}
			
			//if the user favoireted this vendor populate the star
			if(vendor.getFavorited() > 0)
			{
			   	((VendorDetailsHandler) getActivity()).activateFavoriteStar();
			}
			
			//show business views if the business name is specified
			if(vendor.getBusinessName().length() > 3)
			{
				bNameTV.setText(Constants.capitalizeFirst(vendor.getBusinessName()));
				bNameTV.setVisibility(View.VISIBLE);
				bNameTitle.setVisibility(View.VISIBLE);
			}
			
			//if the vendor doesnt specify the name dont show it
			if(vendor.getVendorName().length() > 3 && vendor.getVendorName().contains("."))
			{
				String[] ownerSplit = vendor.getVendorName().split("[.]");
			    oNameTV.setText(ownerSplit[1].substring(1, 2) + ". " + Constants.capitalizeFirst(ownerSplit[0]));  
				oNameTV.setVisibility(View.VISIBLE);
				oNameTitle.setVisibility(View.VISIBLE);
			}
		
			//if the vendor doesnt specify the name dont show it
			if(vendor.getOffer().length() > 3)
			{
				offerTV.setText(Constants.capitalizeFirst(vendor.getOffer().substring(0, vendor.getOffer().length()-1).replace(";", ", ")));
				offerTV.setVisibility(View.VISIBLE);
				offerTitle.setVisibility(View.VISIBLE);
			}
			
			//update license of vendor if it is set
			if(vendor.getLicence().length() > 3)
			{
				licenseNumTV.setText(vendor.getLicence());
				licenseNumTV.setVisibility(View.VISIBLE);
				licenseTitle.setVisibility(View.VISIBLE);
			}
			
			vendorImage.getLayoutParams().height = 200;
			vendorImage.getLayoutParams().width = 200;
			ImageLoader.getInstance().displayImage("http://www.pursefitness.com/hotdog_app/images/"+ vendor.getVendorId() + "/logo.png", vendorImage, options);
			
			LatLng vendorLoc = new LatLng(vendor.getLat(), vendor.getLongi());
			CameraPosition cameraPosition = new CameraPosition.Builder().target(vendorLoc).zoom(15).build();
		  	map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		  	map.addMarker(new MarkerOptions()
               .position(vendorLoc));
		  	map.setMyLocationEnabled(true);
		  	
		  	getActivity().setProgressBarIndeterminateVisibility(false);
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
