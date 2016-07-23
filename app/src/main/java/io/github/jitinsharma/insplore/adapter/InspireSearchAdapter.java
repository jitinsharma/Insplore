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
import io.github.jitinsharma.insplore.utilities.AnimationUtilities;
import io.github.jitinsharma.insplore.model.InspireSearchObject;

/**
 * Created by jitin on 16/07/16.
 */
public class InspireSearchAdapter extends RecyclerView.Adapter<InspireSearchAdapter.InspireSearchVH>{
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<InspireSearchObject> inspireSearchObjects;
    OnPlacesClick onPlacesClick;
    int duration = 500;

    public InspireSearchAdapter(Context context, ArrayList<InspireSearchObject> inspireSearchObjects, OnPlacesClick onPlacesClick) {
        this.context = context;
        this.inspireSearchObjects = inspireSearchObjects;
        this.onPlacesClick = onPlacesClick;
    }

    @Override
    public InspireSearchVH onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_inspire_me, parent, false);
        return new InspireSearchVH(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(InspireSearchVH holder, int position) {
        InspireSearchObject current = inspireSearchObjects.get(position);
        holder.depCode.setText(current.getDepCode());
        holder.arrCode.setText(current.getDestinationCode());
        holder.depDate.setText(current.getDepDate());
        if(current.getArrDate()!=null && !current.getArrDate().isEmpty()) {
            holder.arrDate.setText(current.getArrDate());
            holder.dateHyphen.setVisibility(View.VISIBLE);
        }
        if (current.isPlaceEnabled()){
            holder.places.setVisibility(View.VISIBLE);
        }
        else{
            holder.places.setVisibility(View.GONE);
        }
        holder.currencyCode.setText(current.getCurrencyCode());
        holder.price.setText(current.getPrice());
        holder.destinationCityName.setText(current.getDestinationCity());
        AnimationUtilities.setAdapterSlideAnimation(holder.itemView, context, position, duration);
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
        TextView dateHyphen;
        TextView places;
        TextView currencyCode;

        public InspireSearchVH(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            destinationCityName = (TextView)itemView.findViewById(R.id.im_city_name);
            depCode = (TextView)itemView.findViewById(R.id.im_dep_code);
            arrCode = (TextView)itemView.findViewById(R.id.im_arr_code);
            depDate = (TextView)itemView.findViewById(R.id.im_dep_date);
            arrDate = (TextView)itemView.findViewById(R.id.im_arr_date);
            price = (TextView)itemView.findViewById(R.id.im_price);
            dateHyphen = (TextView)itemView.findViewById(R.id.im_date_hyphen);
            places = (TextView)itemView.findViewById(R.id.im_places);
            currencyCode = (TextView)itemView.findViewById(R.id.im_currency_code);

            places.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlacesClick.onClick(getAdapterPosition());
                }
            });
        }
    }
}
