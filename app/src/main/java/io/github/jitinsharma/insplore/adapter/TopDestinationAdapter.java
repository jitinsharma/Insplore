package io.github.jitinsharma.insplore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.model.TopDestinationObject;

/**
 * Created by jitin on 28/06/16.
 */
public class TopDestinationAdapter extends RecyclerView.Adapter<TopDestinationAdapter.TopDestinationVH> {
    Context context;
    ArrayList<TopDestinationObject> topDestinationObjects;
    LayoutInflater layoutInflater;
    private int lastPosition = -1;
    int duration = 700;

    public TopDestinationAdapter(Context context, ArrayList<TopDestinationObject> topDestinationObjects) {
        this.context = context;
        this.topDestinationObjects = topDestinationObjects;
    }

    @Override
    public TopDestinationVH onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_top_destination, parent, false);
        return new TopDestinationVH(view);
    }

    @Override
    public void onBindViewHolder(TopDestinationVH holder, int position) {
        TopDestinationObject current = topDestinationObjects.get(position);
        holder.cityName.setText(current.getCityName());
        holder.noOfFlights.setText(current.getNoOfFlights());
        holder.noOfPax.setText(current.getNoOfPax());
        setAnimation(holder.itemView, position);
    }

    /*@Override
    public int getItemCount() {
        return topDestinationObjects.size();
    }*/

    @Override
    public int getItemCount() {
        return topDestinationObjects.size();
    }

    class TopDestinationVH extends RecyclerView.ViewHolder {
        TextView cityName;
        TextView noOfFlights;
        TextView noOfPax;

        public TopDestinationVH(View itemView) {
            super(itemView);
            cityName = (TextView)itemView.findViewById(R.id.top_dest_city);
            noOfFlights = (TextView) itemView.findViewById(R.id.top_dest_flights);
            noOfPax = (TextView)itemView.findViewById(R.id.top_dest_passengers);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        duration = duration + 100;
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.enter_from_right);
            animation.setDuration(duration);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
