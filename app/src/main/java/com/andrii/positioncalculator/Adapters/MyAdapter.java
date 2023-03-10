package com.andrii.positioncalculator.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andrii.positioncalculator.Activities.LogsActivity;
import com.andrii.positioncalculator.Activities.MainActivity;
import com.andrii.positioncalculator.Helpers.Direction;
import com.andrii.positioncalculator.Helpers.Order;
import com.example.positioncalculator.R;

import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final List<Order> orderList;
    private final Context mContext;

    public MyAdapter(Context context, List<Order> orders) {
        this.orderList = orders;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order item = orderList.get(position);
        holder.text.setText(item.toString());
        Bitmap direction_bitmap = BitmapFactory.decodeResource(mContext.getResources(),(item.getDirection() == Direction.BUY)? R.drawable.buy_header : R.drawable.sell_header);
        holder.direction_image.setImageBitmap(direction_bitmap);
        holder.remove_button.setOnClickListener(v -> {
            notifyItemRemoved(position);
            notifyDataSetChanged();
            orderList.remove(item);
            MainActivity.position.removeOrder(item);
            MainActivity.initPosition();
            LogsActivity.reloadStats();
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView remove_button;
        public ImageView direction_image;
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            remove_button = itemView.findViewById(R.id.delete_btn);
            direction_image = itemView.findViewById(R.id.direction_img);
            text = itemView.findViewById(R.id.order_info);
        }
    }
}
