package com.parasme.swopinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.model.Category;
import com.parasme.swopinfo.model.Store;

import java.util.ArrayList;

/**
 * Created by SoNu on 6/6/2016.
 */

public class PromotionsAdapter extends ArrayAdapter<Store.Promotion> {

    private int resourceId;
    private ArrayList<Store.Promotion> promotionArrayList;
    private Context context;
    private ViewHolder viewHolder;
    private LayoutInflater vi;

    public PromotionsAdapter(Context context, int resourceId, ArrayList<Store.Promotion> promotionArrayList) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.promotionArrayList = promotionArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return promotionArrayList.size();
    }


    class ViewHolder
    {
        ImageView imagePromotion;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.imagePromotion = (ImageView) view.findViewById(R.id.img_promotion);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.imagePromotion.setImageResource(promotionArrayList.get(position).image);

        return view;
    }


}
