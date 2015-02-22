package com.team.opendata;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventListAdapter extends ArrayAdapter<String[]> {

	// declaring our ArrayList of items
	private List<String[]> objects;


	public EventListAdapter(Context context, int textViewResourceId, List<String[]> objects) {
		super(context, textViewResourceId, objects);
		this.objects = objects;
	}

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	public View getView(int position, View convertView, ViewGroup parent){

		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.event_row, null);
		}


		String[] i = objects.get(position);

		if (i != null) {

			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			TextView description = (TextView) v.findViewById(R.id.rowDesc);
			TextView time = (TextView) v.findViewById(R.id.rowTime);
			TextView title = (TextView) v.findViewById(R.id.rowTitle);

			if (description != null){
				description.setText(i[1]);
			}
			if (title != null){
				title.setText(i[0]);
			}
			if (time != null){
				time.setText(i[2]);
			}
		}

		return v;
	}

}
