package net.gluu.erasmus.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.gluu.erasmus.R;
import net.gluu.erasmus.model.State;

public class StateAdapter extends BaseAdapter
{
	Context mContext;
	ArrayList<State> arAdvertisingSource;

	public StateAdapter(Context ctx, ArrayList<State> arAdvertisingSource)
	{
		// TODO Auto-generated constructor stub
		mContext = ctx;
		this.arAdvertisingSource = arAdvertisingSource;
	}

	@Override
	public int getCount()
	{
		return arAdvertisingSource.size();
	}

	@Override
	public Object getItem(int position)
	{
		return arAdvertisingSource.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		TextView name;
		if (v == null)
		{
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.spinner_item, null);
		}
		name = (TextView) v.findViewById(R.id.tvSpinnerItem);
		String salespersoningetview = arAdvertisingSource.get(position).getName();
		name.setText(salespersoningetview);
		return v;
	}
}
