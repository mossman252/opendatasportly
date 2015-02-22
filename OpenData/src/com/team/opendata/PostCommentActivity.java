package com.team.opendata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.team.common.Constants;

public class PostCommentActivity extends Activity{

	private Typeface titleFace;
	private String locationID;
	private Boolean numRatingChanged = false;
	
	private Button postCommentBtn;
	
	private EditText commentEditText;
	private SharedPreferences userInfoPref;
	private int userId;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.raiting_page_layout);
      
	 	Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	locationID = extras.getString("LOCATION_ID");
        	userId = extras.getInt("USER_ID");
        }
	 	
	 	//get the user id
        this.userInfoPref = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
     	this.userId = userInfoPref.getInt(Constants.SETTING_VENDORID_INFO, 0);
	 	
	 	postCommentBtn = (Button) findViewById(R.id.post_rating_btn);
	 	commentEditText = (EditText) findViewById(R.id.edit_text_comment);
	 	
	 	titleFace = Typeface.createFromAsset(getAssets(), Constants.HERO_BOLD); 
	 	
	 	try{
	 		int titleId = getResources().getIdentifier("action_bar_title", "id",
	 				"android");
	 		TextView yourTextView = (TextView) findViewById(titleId);
	 		yourTextView.setTextColor(getResources().getColor(R.color.white));
	 		yourTextView.setText("Rate Vendor");
	 		yourTextView.setTypeface(titleFace);
	 	}catch(Exception e){}
	 		
	    postCommentBtn.setTypeface(titleFace);
	    attachListeners();
	}
	
	private void attachListeners()
	{
		postCommentBtn.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    	//Test to see if everything is filled in
		    	if(commentEditText.getText().toString().equalsIgnoreCase("")){Toast.makeText(getApplicationContext(), "Please enter a Comment", Toast.LENGTH_SHORT).show();}
				else
		    	{
		    		 Toast.makeText(getApplicationContext(), R.string.posting_comment, Toast.LENGTH_LONG).show();
		    		 PostCommentTask task = new PostCommentTask();
		    		 task.execute(new String[] { "http://pursefitness.com/opendata/post_comment.php" });
		    		 postCommentBtn.setEnabled(false);
		    		 setProgressBarIndeterminateVisibility(true);
		    	}
	        }
	    });
		
	}
	
	
	public class PostCommentTask extends AsyncTask<String, Void, String> {
    	//download the vendor list from the server
		String json = "";
		@Override
		protected String doInBackground(String... urls) {
			try {
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	    	    nameValuePairs.add(new BasicNameValuePair("userid", Integer.toString(userId)));
	    	    nameValuePairs.add(new BasicNameValuePair("locationid", "1")); //locationID
	    	    nameValuePairs.add(new BasicNameValuePair("comment", commentEditText.getText().toString()));
				
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(urls[0]);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
			finish();
		}
	}
	
}
