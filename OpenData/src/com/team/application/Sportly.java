package com.team.application;

import android.app.Application;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class Sportly extends Application{

	 @Override
	    public void onCreate() {
		 super.onCreate();

	        // Create global configuration and initialize ImageLoader with this configuration
	        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
	        	.threadPriority(1 - 5)
	        	.denyCacheImageMultipleSizesInMemory()
	        	.diskCacheFileNameGenerator(new Md5FileNameGenerator())
	        	.tasksProcessingOrder(QueueProcessingType.LIFO)
	        	.writeDebugLogs() // Remove for release app
	        	.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) 
	            .build();
	         ImageLoader.getInstance().init(config);
	    }
}
