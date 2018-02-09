package com.parasme.swopinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.model.Upload;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<Object> {
	private Context context;
	private List<Upload> values;
	String [] a = {"https://dummyimage.com/320x180/ff00/fff",
//			"https://dummyimage.com/600x400/ff00/fff",
			"https://dummyimage.com/120x480/000/fff",
//			"https://dummyimage.com/120x480/000/fff",
			"https://dummyimage.com/1024x480/000/fff",
			"https://dummyimage.com/600x600/000/fff",
//			"https://dummyimage.com/320x180/ff00/fff",
//			"https://dummyimage.com/600x400/ff00/fff",
//			"https://dummyimage.com/320x480/000/fff",
//			"https://dummyimage.com/1024x480/000/fff"

	};

	public MyAdapter(Context context, ArrayList<Upload> values) {
		super(context, R.layout.row_item);
		this.context = context;
		this.values = values;
	}


	@Override
	public int getCount() {
		return values.size()>4 ? 4 : values.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.row_item, parent, false);
		ImageView image = (ImageView) rowView.findViewById(R.id.image);
		if(values.get(position).getThumbURL().contains("presentation.png") && values.get(position).getFileType().contains("image"))
			Picasso.with(context).load(values.get(position).getFileURL()).resize(200,200).onlyScaleDown().placeholder(R.drawable.app_icon).error(R.drawable.document_gray).into(image);
		else
			Picasso.with(context).load(values.get(position).getThumbURL()).placeholder(R.drawable.app_icon).error(R.drawable.document_gray).into(image);

//		Picasso.with(context).load(values.get(position).getThumbURL()).into(image);
		return rowView;
	}
}
