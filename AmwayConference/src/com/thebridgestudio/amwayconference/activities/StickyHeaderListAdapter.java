package com.thebridgestudio.amwayconference.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.thebridgestudio.amwayconference.R;


public class StickyHeaderListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

	private String[] countries;
	private LayoutInflater inflater;

	public StickyHeaderListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		countries = context.getResources().getStringArray(R.array.countries);
	}

	@Override
	public int getCount() {
		return countries.length;
	}

	@Override
	public Object getItem(int position) {
		return countries[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.sticky_list_item_layout, parent, false);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.text.setText(countries[position]);

		return convertView;
	}

	@Override public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = inflater.inflate(R.layout.sticky_header, parent, false);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}

		//set header text as first char in name
		char headerChar = countries[position].subSequence(0, 1).charAt(0);
		String headerText;
		if(headerChar%2 == 0){
			headerText = headerChar + "\n" + headerChar + "\n" + headerChar;
		}else{
			headerText = headerChar + "\n" + headerChar;
		}
		holder.text.setText(headerText);

		return convertView;
	}

	//remember that these have to be static, postion=1 should walys return the same Id that is.
	@Override
	public long getHeaderId(int position) {
		//return the first character of the country as ID because this is what headers are based upon
		return countries[position].subSequence(0, 1).charAt(0);
	}

	class HeaderViewHolder {
		TextView text;
	}

	class ViewHolder {
		TextView text;
	}
}
