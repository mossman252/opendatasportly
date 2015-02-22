package com.team.opendata;

import java.io.IOException;
import java.nio.charset.Charset;

import com.team.common.Constants;
import com.team.common.Constants.MimeType;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class NFCListenerActivity extends Activity {
	
	private NfcAdapter mAdapter;
	private boolean mInWriteMode;
	
	private int mLocationId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfclistener);
		getActionBar().hide();
		
		// grab our NFC Adapter
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        
        Bundle extras = getIntent().getExtras();
		if (null != extras) {
			
			String locationId = extras.getString(Constants.LOCATION_ID, "");
			
			if(TextUtils.isEmpty(locationId)) {
				mLocationId = -1;
			} else {
				mLocationId = Integer.parseInt(locationId);
			}
		}
        
	}
	
	@Override
	protected void onResume() {
        enableWriteMode();
		super.onResume();
	}



	/**
	 * Called when our blank tag is scanned executing the PendingIntent
	 */
	@Override
    public void onNewIntent(Intent intent) {
		if(mInWriteMode) {
			mInWriteMode = false;
			
			// write to newly scanned tag
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			writeTag(tag);
			finish();
		}
    }
	
	/**
	 * Format a tag and write our NDEF message
	 */
	private boolean writeTag(Tag tag) {
		// record to launch Play Store if app is not installed
		NdefRecord appRecord = NdefRecord.createApplicationRecord("com.rm.lambtonconnect");
		
		// record that contains our custom "retro console" game data, using custom MIME_TYPE
		byte[] payload = String.valueOf(mLocationId).getBytes();
		byte[] mimeBytes = MimeType.NFC_DEMO.getBytes(Charset.forName("US-ASCII"));
        NdefRecord cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, 
        										new byte[0], payload);
		NdefMessage message = new NdefMessage(new NdefRecord[] { cardRecord, appRecord});
        
		try {
			// see if tag is already NDEF formatted
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				if (!ndef.isWritable()) {
					//displayMessage("Read-only tag.");
					return false;
				}
				
				// work out how much space we need for the data
				int size = message.toByteArray().length;
				if (ndef.getMaxSize() < size) {
					//displayMessage("Tag doesn't have enough free space.");
					return false;
				}

				ndef.writeNdefMessage(message);
				Toast.makeText(this,"Tag written Successfully ! ", 
						Toast.LENGTH_LONG).show();
				return true;
			} else {
				// attempt to format tag
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						Toast.makeText(this,"Tag written Successfully ! ", 
								Toast.LENGTH_LONG).show();
						return true;
					} catch (IOException e) {
						//displayMessage("Unable to format tag to NDEF.");
						return false;
					}
				} else {
					//displayMessage("Tag doesn't appear to support NDEF format.");
					return false;
				}
			}
		} catch (Exception e) {
			//displayMessage("Failed to write tag");
		}

        return false;
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		disableWriteMode();
	}

	/**
	 * Force this Activity to get NFC events first
	 */
	private void enableWriteMode() {
		mInWriteMode = true;
		// set up a PendingIntent to open the app when a tag is scanned
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { tagDetected };
        
		mAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
	}
	
	private void disableWriteMode() {
		mAdapter.disableForegroundDispatch(this);
	}

}
