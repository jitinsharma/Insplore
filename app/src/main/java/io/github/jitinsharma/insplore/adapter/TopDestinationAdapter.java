package io.github.jitinsharma.insplore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.model.TopDestinationObject;

/**
 * Created by jitin on 28/06/16.
 */
public class TopDestinationAdapter extends RecyclerView.Adapter<TopDestinationAdapter.TopDestinationVH>{
    Context context;
    ArrayList<TopDestinationObject> topDestinationObjects;
    LayoutInflater layoutInflater;

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
        holder.toAirport.setText(current.getDestination());
        holder.noOfFlights.setText(current.getNoOfFlights());
    }

    @Override
    public int getItemCount() {
        return topDestinationObjects.size();
    }

    class TopDestinationVH extends RecyclerView.ViewHolder{
        TextView toAirport;
        TextView noOfFlights;
        TextView noOfPax;

        public TopDestinationVH(View itemView) {
            super(itemView);
            toAirport = (TextView)itemView.findViewById(R.id.to);
            noOfFlights = (TextView)itemView.findViewById(R.id.no_of_flights);
        }
    }
}
