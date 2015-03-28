package com.example.john.smartlist;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

/**
 * Created by mshehab on 2/9/15.
 */
public class ListAdapterHolder extends RecyclerView.Adapter<ListAdapterHolder.ViewHolder>{

    OnItemClickListener mItemClickListener;
    Context mContext;
    List<ParseObject> mData;
    Random rand = new Random();

    public ListAdapterHolder(Context mContext, List<ParseObject> data) {
        this.mContext = mContext;
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(mContext);
        final View view = mInflater.inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParseObject object = mData.get(position);
        holder.textViewName.setText(object.getString("name"));


        if(object.getBoolean("recommendation")){
            //holder.itemView.setBackgroundColor(R.color.);
        } else{

        }

        if(object.getBoolean("discount")){
            int cnt = 2 + rand.nextInt(4);
            double price = cnt * object.getNumber("price").doubleValue();
            DecimalFormat df = new DecimalFormat("#.##");
            holder.textViewPrice.setText(cnt + " for " + df.format(price) + "");
            holder.textViewPrice.setTextColor(Color.RED);
        } else{
            holder.textViewPrice.setText(object.getNumber("price").toString());
            //holder.textViewPrice.setTextColor(Color.BLACK);
        }

        ParseFile photo = (ParseFile) object.get("photo");
        Picasso.with(mContext)
                .load(photo.getUrl())
                .placeholder(R.drawable.event_photo_loading)
                .error(R.drawable.event_photo_error)
                .resize(100, 100)
                .centerCrop()
                .into(holder.imageViewItem);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView imageViewItem, imageViewCheck;
        TextView textViewName, textViewPrice;
        View mainView;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            imageViewItem = (ImageView) itemView.findViewById(R.id.imageViewItem);
            imageViewCheck = (ImageView) itemView.findViewById(R.id.imageViewCheck);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
            imageViewCheck.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null){
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(View view, int position);
    }
}
