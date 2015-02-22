package com.team.opendata;

import com.team.common.Constants;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

public class NFCActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_nfc);
		
		// see if app was started from a tag and show game console
        Intent intent = getIntent();
        if(intent.getType() != null && intent.getType().equals(com.team.common.Constants.MimeType.NFC_DEMO)) {
        	Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            NdefRecord cardRecord = msg.getRecords()[0];
            String locationId = new String(cardRecord.getPayload());
            
            Log.i("", "locationId" + locationId);
            
            Intent activityIntent = new Intent(NFCActivity.this, VendorDetailsHandler.class);
            activityIntent.putExtra(Constants.LOCATION_ID, Integer.valueOf(locationId));
            activityIntent.putExtra("isnfc", true);
            startActivity(activityIntent);
            
            finish();
            
        }
	}
}
