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
import android.support.v4.widget.SwipeRefreshLayout;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.team.common.Constants;
import com.team.opendata.R;
import com.team.opendata.VendorDetailsHandler;
import com.team.actor.Comment;

public class CommentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

	private Button postRatingBtn;
	private TextView noRatingsTV;
	private ArrayList<Comment> vendorRatingList;
	private String locationId;
	
	private LinearLayout vendorListLayout;
	private View view;
	private Typeface typeface;
	
	private DisplayImageOptions options;
	
	private DisplayMetrics dm;
	private int screenWidth;
	
	int userId;
	protected ImageLoader imageLoader;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	     	 view = inflater
					.inflate(R.layout.list_screen_comments, container, false);
	     	 
	     	 Bundle extras = getArguments();
	    	 if (extras != null) {
	    		locationId = extras.getString("LOCATION_ID");
	    		userId = extras.getInt("USER_ID");
	    	 }
	     	 
	    	 vendorRatingList = new ArrayList<Comment>();
	     	 typeface = Typeface.createFromAsset(getActivity().getAssets(), Constants.HERO_BOLD); 
	     	 postRatingBtn = (Button) view.findViewById(R.id.rate_vendor_btn);
	     	 noRatingsTV = (TextView) view.findViewById(R.id.no_ratings_tv);
	     	 vendorListLayout = (LinearLayout) view.findViewById(R.id.vendor_list_linlayout);
	     	 postRatingBtn.setTypeface(typeface);
	     	 
	     	//get display metrics
		     dm = new DisplayMetrics(); 
		     getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm); 
		     screenWidth = dm.widthPixels;
	     	 
	     	 //image options
	         options = new DisplayImageOptions.Builder()
	 		 .cacheInMemory(false)
	 		 .cacheOnDisk(false)
	 		 .considerExifParams(true)
	 		 .showImageForEmptyUri(R.drawable.icon_noimage) // resource or drawable
	         .showImageOnFail(R.drawable.icon_noimage) 
	         .imageScaleType(ImageScaleType.NONE)
	 		 .build();
	         
	         imageLoader = ImageLoader.getInstance();
	         imageLoader.init(ImageLoaderConfiguration.createDefault(this.getActivity()));
	         
	     	 this.attachListeners();
	     	 return view;
	}
	
	private void attachListeners()
	{
		postRatingBtn.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    	//if user is logged in take them to the rating page
		    	((VendorDetailsHandler) getActivity()).promptFacebookLogin();
	    }});
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
			ArrayList<ImageView> imageList = new ArrayList<ImageView>();
			
			try {
				//put result into json array and parse
				JSONArray jsonArray = new JSONArray(result);
				JSONObject json_data = null;
				for (int i=0; i < jsonArray.length(); i++) {
					   json_data = jsonArray.getJSONObject(i);
					   vendorRatingList.add(new Comment(json_data.getInt("comment_id") ,json_data.getString("text_comment"), json_data.getInt("user_id"), json_data.getString("fb_username"), json_data.getString("fb_id"), json_data.getString("time_sent")));
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				if(vendorRatingList.size() == 0)
				{
					 noRatingsTV .setVisibility(View.VISIBLE);
				}
				else//there are some ratings
				{
					LinearLayout.LayoutParams outsideContainterParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);outsideContainterParams.setMargins(20, 10, 20, 10);
					LayoutParams titleContainterParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					
					TableLayout.LayoutParams profilePicParams = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					profilePicParams.gravity = Gravity.TOP;
					
					for(int i = 0; i < vendorRatingList.size(); i++)
					{
						//split the date between the time
						String[] timeSplit = vendorRatingList.get(i).getTimestamp().split(" ");
						String[] nameSplit = vendorRatingList.get(i).getUsername().split("[.]");
						String entireName = "Posted by ";
						
						LinearLayout outsideContainer = new LinearLayout(getActivity().getApplicationContext());
						outsideContainer.setLayoutParams(outsideContainterParams);
						outsideContainer.setOrientation(LinearLayout.VERTICAL);
						outsideContainer.setBackgroundColor(Color.WHITE);
						outsideContainer.setPadding(8, 8, 8, 4);
						
						LinearLayout ratingTitleContainer = new LinearLayout(getActivity().getApplicationContext());
						ratingTitleContainer.setLayoutParams(titleContainterParams);
						ratingTitleContainer.setOrientation(LinearLayout.HORIZONTAL);
						ratingTitleContainer.setPadding(0,0,0, 10);
						
						LinearLayout ratingTitleContentContainer = new LinearLayout(getActivity().getApplicationContext());
						ratingTitleContentContainer.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
						ratingTitleContentContainer.setOrientation(LinearLayout.VERTICAL);
						
						//get the profile pic
						ImageView profilePic = new ImageView(getActivity().getApplicationContext());
						profilePic.setLayoutParams(profilePicParams);
						profilePic.setPadding(0, 0, 10, 0);
						imageLoader.displayImage("http://graph.facebook.com/"+ vendorRatingList.get(i).getUserFbId() + "/picture?type=large", profilePic, options);
						
			            TextView ratingDate = new TextView(getActivity().getApplicationContext());
			            ratingDate.setText(timeSplit[0]);
			            ratingDate.setTextColor(Color.GRAY);
			            ratingDate.setPadding(0,0,0,5);
			            ratingDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
			            
			            // Create TextView for comment
			            TextView ratingComment = new TextView(getActivity().getApplicationContext());
			            ratingComment.setText(Constants.capitalizeFirstLeaveRest(vendorRatingList.get(i).getStringComment()));
			            ratingComment.setTextColor(Color.BLACK);
			            ratingComment.setPadding(0,0,0,5);
			            ratingComment.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
			            
			            // Create TextView for comment
			            TextView postedbyComment = new TextView(getActivity().getApplicationContext());
			            postedbyComment.setTextColor(Color.GRAY);
			            postedbyComment.setPadding(0,0,0,5);
			            ratingComment.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
			            
			            //done to add all the parts of a persons name
			            for(int j = 0; j < nameSplit.length; j++)
			            {
			            	entireName = entireName + nameSplit[j] + " ";
			            }
			            postedbyComment.setText(entireName);
			            
			             //add the title and date to the container
			            //ratingTitleContentContainer.addView(ratingTitle);
			           
			            //ratingTitleContentContainer.addView(vendorRating);
			            ratingTitleContentContainer.addView(ratingComment);
			            ratingTitleContentContainer.addView(postedbyComment);
			            ratingTitleContentContainer.addView(ratingDate);
			            ratingTitleContainer.addView(profilePic);
			            ratingTitleContainer.addView(ratingTitleContentContainer);
			            //Add the title container to the entire container
			            outsideContainer.addView(ratingTitleContainer);
			            
			          
			            
			            //add to the page
						vendorListLayout.addView(outsideContainer);
					}
					
					    noRatingsTV .setVisibility(View.GONE);
				}
			}
		}
	}
	
	public class DeleteRatingTask extends AsyncTask<String, Void, String> {
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
			//reload the ratings
			replaceRatings();
		}
	}
	
	//call this to reload the ratings
	public void replaceRatings()
	{
		vendorListLayout.removeAllViews();
		vendorRatingList.clear();
		//Get info from server and update ratings list
		DownloadJSON task = new DownloadJSON();
	    task.execute(new String[] { "http://pursefitness.com/opendata/get_comments.php?location_id=" + locationId });
	}
	
	@Override
	public void onResume(){
		super.onResume();
		replaceRatings();
		
		
    }
	
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onRefresh() {
		vendorListLayout.removeAllViews();
		vendorRatingList.clear();
		//Get info from server and update ratings list
		DownloadJSON task = new DownloadJSON();
	    task.execute(new String[] { "http://pursefitness.com/opendata/get_comments.php?location_id=" +  locationId });
	
		
	}
	
}
