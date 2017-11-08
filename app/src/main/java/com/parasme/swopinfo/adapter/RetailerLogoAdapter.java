package com.parasme.swopinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.model.Retailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SoNu on 6/6/2016.
 */

public class RetailerLogoAdapter extends ArrayAdapter<Retailer> {

    private int resourceId;
    private ArrayList<Retailer> retailerArrayList;
    private Context context;
    private ViewHolder viewHolder;
    private LayoutInflater vi;

    public RetailerLogoAdapter(Context context, int resourceId, ArrayList<Retailer> retailerArrayList) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.retailerArrayList = retailerArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return retailerArrayList.size();
        //return 5;
    }


    class ViewHolder
    {
        ImageView imageRetailerLogo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.imageRetailerLogo = (ImageView) view.findViewById(R.id.img_retailer_logo);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

       Picasso.with(context).load(retailerArrayList.get(position).getRetailerLogo()).placeholder(R.drawable.app_icon).error(R.drawable.document_gray).into(viewHolder.imageRetailerLogo);

/*
        switch (position){
            case 0:
                viewHolder.imageRetailerLogo.setImageResource(R.drawable.logo_game);
                break;
            case 1:
                viewHolder.imageRetailerLogo.setImageResource(R.drawable.logo_hifi);
                break;
            case 2:
                viewHolder.imageRetailerLogo.setImageResource(R.drawable.logo_pick);
                break;
            case 3:
                viewHolder.imageRetailerLogo.setImageResource(R.drawable.logo_spar);
                break;
            case 4:
                viewHolder.imageRetailerLogo.setImageResource(R.drawable.logo_wool);
                break;
        }
*/

        return view;
    }


}
