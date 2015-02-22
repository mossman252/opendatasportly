package com.team.opendata;

import android.app.Activity;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.team.common.Constants;

public class LoginActivity extends Activity{

	private LoginButton authButton;
	private TextView skipLogin;
	private TextView userNameTV;
	private TextView appText;
	
	private Typeface montReg;
	private Typeface montBlack;
	
	private SharedPreferences userInfoPref;
	Editor userInfoEditor;
	private UiLifecycleHelper uiHelper;
	private Boolean isFirstTime;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.splash_screen);
     	
		//tie the image to the layout id
     	skipLogin = (TextView) findViewById(R.id.skip_login_tv);
     	userNameTV = (TextView) findViewById(R.id.user_name);
     	appText = (TextView) findViewById(R.id.app_text);
     	authButton = (LoginButton) findViewById(R.id.authButton);
     	authButton.setReadPermissions(Arrays.asList("public_profile","email", "user_friends"));
     	montReg = Typeface.createFromAsset(getAssets(), Constants.MONT_REG); 
     	montBlack = Typeface.createFromAsset(getAssets(), Constants.MONT_BLACK); 
     	
     	skipLogin.setTypeface(montReg);
     	appText.setTypeface(montBlack);
     	
     	userInfoPref = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
     	//isFirstTime = userInfoPref.getBoolean(Constants.FIRST_TIME_USAGE, Constants.IS_FIRST_TIME);
     	
     	
		Session session = Session.getActiveSession();
        if (session == null) {
        	System.out.println("Session is null");
             if (savedInstanceState != null) {
                 session = Session.restoreSession(this, null, callback, savedInstanceState);
             }
             if (session == null) {
                 session = new Session(this);
             }
             Session.setActiveSession(session);
             if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	 System.out.println("Get read token");
                 session.openForRead(new Session.OpenRequest(this).setCallback(callback));
             }
         }
        
        System.out.println(session.isOpened());
        session.addCallback(callback);
       
        skipLogin.setOnClickListener(new OnClickListener() {
  			public void onClick(View v) {
  				//System.out.println("User id " + userInfoPref.getInt(Constants.SETTING_VENDORID_INFO, 0));

  					startActivity(new Intent(LoginActivity.this, ItemListActivity.class));
  	  				overridePendingTransition(R.anim.translate_left_offscreen, R.anim.translate_right_onscreen);
  	  				finish();
  				
  				
  			}
          });
	}
	
	 private Session.StatusCallback callback = new Session.StatusCallback() {
	        @SuppressWarnings("deprecation")
			@Override
	        public void call(Session session, SessionState state, Exception exception) {
	        	if (state.equals(SessionState.OPENED)) {
	        		System.out.println("State is opening");
	        		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
	   					@Override
	   					public void onCompleted(GraphUser user, Response response) {
	   						System.out.println("completed");
	   						if (user != null) {
	   							userNameTV.setText("Welcome " + user.getFirstName());
	   	                    	CreateUserTask task = new CreateUserTask();
	   	      			    	task.execute(new String[] { "http://pursefitness.com/opendata/user_create.php?name=" + user.getFirstName() + "%20" + user.getLastName() 
	   	      			    			                     + "&fb_id=" + user.getId() 
	   	      			    								 + "&uname=" + user.getUsername() 
	   	      			    			                     + "&email=" + user.asMap().get("email") 
	   	      			    			                    });
	   						}
	   					}
	   	            });
	           } else if (state.isClosed()) {
	        	 System.out.println("State is closing");
	           	 userNameTV.setVisibility(View.GONE);
	           	 skipLogin.setText("Skip Login");
	           	 final Editor userInfoEditor = userInfoPref.edit();
	           	 userInfoEditor.putInt(Constants.SETTING_VENDORID_INFO, 0);
	   			 userInfoEditor.commit();
	           }   
	        }
    };
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
 
	private class CreateUserTask extends AsyncTask<String, Void, String> {
		//This async task downloads the Tips from the Net
		String json = "";
		@Override
	    protected String doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
			    HttpGet httppost = new HttpGet(urls[0]);
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
		@SuppressLint("NewApi") @Override
	    protected void onPostExecute(String result) {
			System.out.println("result");
			try {
					//put result into json array and parse
					JSONArray jsonArray = new JSONArray(result);
					JSONObject json_data = null;
					json_data = jsonArray.getJSONObject(0);
				    
					final Editor editor = userInfoPref.edit();
				    editor.putInt(Constants.USER_ID, json_data.getInt("user_id"));
				    editor.commit();
			
			} catch (JSONException e) {
					e.printStackTrace();
			}
	    }
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
	
}
