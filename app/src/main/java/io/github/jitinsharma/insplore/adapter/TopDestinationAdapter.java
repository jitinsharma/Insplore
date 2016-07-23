package io.github.jitinsharma.insplore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.model.OnPlacesClick;
import io.github.jitinsharma.insplore.model.TopDestinationObject;
import io.github.jitinsharma.insplore.utilities.AnimationUtilities;

/**
 * Created by jitin on 28/06/16.
 */
public class TopDestinationAdapter extends RecyclerView.Adapter<TopDestinationAdapter.TopDestinationVH> {
    Context context;
    ArrayList<TopDestinationObject> topDestinationObjects;
    LayoutInflater layoutInflater;
    int duration = 700;
    OnPlacesClick onPlacesClick;

    public TopDestinationAdapter(Context context, ArrayList<TopDestinationObject> topDestinationObjects, OnPlacesClick onPlacesClick) {
        this.context = context;
        this.topDestinationObjects = topDestinationObjects;
        this.onPlacesClick = onPlacesClick;
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
        holder.details.setVisibility(View.GONE);
        if (current.isPlaceEnabled()){
            holder.details.setVisibility(View.VISIBLE);
        }
        else{
            holder.details.setVisibility(View.GONE);
        }
        holder.cityName.setText(current.getCityName());
        holder.noOfFlights.setText(current.getNoOfFlights());
        holder.noOfPax.setText(current.getNoOfPax());
        AnimationUtilities.setAdapterSlideAnimation(holder.itemView, context, position, duration);
        duration = duration + 100;
    }

    @Override
    public int getItemCount() {
        return topDestinationObjects.size();
    }

    class TopDestinationVH extends RecyclerView.ViewHolder {
        TextView cityName;
        TextView noOfFlights;
        TextView noOfPax;
        TextView details;

        public TopDestinationVH(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            cityName = (TextView)itemView.findViewById(R.id.top_dest_city);
            noOfFlights = (TextView) itemView.findViewById(R.id.top_dest_flights);
            noOfPax = (TextView)itemView.findViewById(R.id.top_dest_passengers);
            details = (TextView)itemView.findViewById(R.id.top_dest_detail);

            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlacesClick.onClick(getAdapterPosition());
                }
            });
        }
    }
}
