package com.team.opendata;

import java.util.ArrayList;

import com.team.common.Constants;
import com.team.fragment.CommentsFragment;
import com.team.fragment.VendorInfoFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Bundle;


public class VendorDetailsHandler extends FragmentActivity{
	
	private LinearLayout dotsLayout;
	private int dotsCount;
	private TextView[] dots;
	private ArrayList<Integer> listOfItems;
	
	private SharedPreferences userInfoPref;
	private FragmentPagerAdapter mFragPagerAdapter;
	String locationId;
	int userId;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout_handler);
        
        //get bundle from previous class
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	locationId = extras.getString("LOCATION_ID");
        }
        
        //get shared pref
        userInfoPref = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        userId = userInfoPref.getInt(Constants.USER_ID, 0);
        
        listOfItems = new ArrayList<Integer>();
		listOfItems.add(1);
		listOfItems.add(2);
		listOfItems.add(3);
		listOfItems.add(4);
		listOfItems.add(5);
        
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        
        mFragPagerAdapter = new MyPagerAdapter(getFragmentManager());
		pager.setAdapter(mFragPagerAdapter);
		pager.setOnPageChangeListener(viewPagerPageChangeListener);
		
		setUiPageViewController();
    }

    
    private class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			
			Bundle bundle_args = new Bundle();
		    bundle_args.putString("LOCATION_ID", locationId);
		    bundle_args.putInt("USER_ID", userId);
		    
			switch(pos) {
			case 0:  VendorInfoFragment f = new VendorInfoFragment();
			         f.setArguments(bundle_args);
				     return f;
			case 1:  CommentsFragment g = new CommentsFragment();
				     g.setArguments(bundle_args);
			         return g;
			default: VendorInfoFragment h = new VendorInfoFragment();
	        		 h.setArguments(bundle_args);
	        		 return h;
			}
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
	}
    
    //	page change listener
	OnPageChangeListener viewPagerPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < dotsCount; i++) {
				dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
			}
			dots[position].setTextColor(getResources().getColor(android.R.color.black));
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};
	
	private void setUiPageViewController() {
		dotsLayout = (LinearLayout)findViewById(R.id.viewPagerCountDots);
		dotsCount = mFragPagerAdapter.getCount();
		dots = new TextView[dotsCount];
		
		for (int i = 0; i < dotsCount; i++) {
			dots[i] = new TextView(this);
			dots[i].setText(Html.fromHtml("&#8226;"));
			dots[i].setTextSize(30);
			dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
			dotsLayout.addView(dots[i]);			
		}
		
		dots[0].setTextColor(getResources().getColor(android.R.color.black));
	}
	
	
	public void promptFacebookLogin()
 	{
 		//CustomDialog.createSignInDialog(getString(R.string.signin_message), "Sign in" , super.getSupportFragmentManager());
 		//if(userLoggedIn)
    	//{
    		Intent intent = new Intent(getApplicationContext(), PostCommentActivity.class);	
    		intent.putExtra("LOCATION_ID", locationId);
    		intent.putExtra("USER_ID", userId);
    		startActivity(intent);
			overridePendingTransition(R.anim.translate_left_offscreen, R.anim.translate_right_onscreen);
    	/*}
 		else{
 			authButton.performClick();
 		}*/
 	}
}
