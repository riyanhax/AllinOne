package com.parasme.swopinfo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.model.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MultipleFilesRecyclerViewAdapter extends RecyclerView.Adapter<MultipleFilesViewHolders> {

    private List<Upload> itemList;
    private Context context;

    public MultipleFilesRecyclerViewAdapter(Context context, List<Upload> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public MultipleFilesViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recyler_mutiple_files, null);
        MultipleFilesViewHolders rcv = new MultipleFilesViewHolders(layoutView,context,itemList);
        return rcv;
    }

    @Override
    public void onBindViewHolder(MultipleFilesViewHolders holder, int position) {
        Picasso.with(context).load(itemList.get(position).getThumbURL()).placeholder(R.drawable.document_gray).error(R.drawable.document_gray).into(holder.countryPhoto);
        if (itemList.size() > 4 && position == 3)
        {
            holder.layoutMoreFiles.setVisibility(View.VISIBLE);
            holder.numOfRemainingFiles.setText("+"+(itemList.size()-4));
        }
//        holder.countryPhoto.setImageResource(itemList.get(position).getPhoto());
    }

    @Override
    public int getItemCount() {
        if(itemList.size()>4)
            return 4;
        else
            return this.itemList.size();
    }
}
