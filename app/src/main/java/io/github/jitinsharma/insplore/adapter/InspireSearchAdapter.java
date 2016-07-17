package io.github.jitinsharma.insplore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.utilities.AnimationUtilities;
import io.github.jitinsharma.insplore.model.InspireSearchObject;

/**
 * Created by jitin on 16/07/16.
 */
public class InspireSearchAdapter extends RecyclerView.Adapter<InspireSearchAdapter.InspireSearchVH>{
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<InspireSearchObject> inspireSearchObjects;

    public InspireSearchAdapter(Context context, ArrayList<InspireSearchObject> inspireSearchObjects) {
        this.context = context;
        this.inspireSearchObjects = inspireSearchObjects;
    }

    @Override
    public InspireSearchVH onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_inspire_me, parent, false);
        return new InspireSearchVH(view);
    }

    @Override
    public void onBindViewHolder(InspireSearchVH holder, int position) {
        InspireSearchObject current = inspireSearchObjects.get(position);
        holder.depCode.setText(current.getDepCode());
        holder.arrCode.setText(current.getDestinationCode());
        holder.depDate.setText(current.getDepDate());
        holder.arrDate.setText(current.getArrDate());
        holder.price.setText(current.getPrice());
        holder.destinationCityName.setText(current.getDestinationCity());
        AnimationUtilities.setAnimation(holder.itemView, context, position, 500);
    }

    @Override
    public int getItemCount() {
        return inspireSearchObjects.size();
    }

    class InspireSearchVH extends RecyclerView.ViewHolder{
        TextView destinationCityName;
        TextView depCode;
        TextView arrCode;
        TextView depDate;
        TextView arrDate;
        TextView price;

        public InspireSearchVH(View itemView) {
            super(itemView);
            destinationCityName = (TextView)itemView.findViewById(R.id.im_city_name);
            depCode = (TextView)itemView.findViewById(R.id.im_dep_code);
            arrCode = (TextView)itemView.findViewById(R.id.im_arr_code);
            depDate = (TextView)itemView.findViewById(R.id.im_dep_date);
            arrDate = (TextView)itemView.findViewById(R.id.im_arr_date);
            price = (TextView)itemView.findViewById(R.id.im_price);

        }
    }
}
