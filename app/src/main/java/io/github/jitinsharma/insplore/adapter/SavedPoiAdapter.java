package io.github.jitinsharma.insplore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.data.InContract;
import io.github.jitinsharma.insplore.model.OnItemClick;
import io.github.jitinsharma.insplore.model.PoiObject;
import io.github.jitinsharma.insplore.utilities.AnimationUtilities;
import io.github.jitinsharma.insplore.utilities.Utils;

/**
 * Created by jitin on 10/07/16.
 */
public class SavedPoiAdapter extends RecyclerView.Adapter<SavedPoiAdapter.SavedPoiVH>{
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<PoiObject> poiObjects;
    OnItemClick onItemClick;
    PoiObject cursorPoiObject;
    int duration = 500;

    public SavedPoiAdapter(Context context, ArrayList<PoiObject> poiObjects, OnItemClick onItemClick) {
        this.context = context;
        this.poiObjects = poiObjects;
        this.onItemClick = onItemClick;
    }

    @Override
    public SavedPoiVH onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_poi, parent, false);
        return new SavedPoiVH(view);
    }

    @Override
    public void onBindViewHolder(SavedPoiVH holder, int position) {
        PoiObject current = poiObjects.get(position);
        holder.poiTitle.setText(current.getTitle());
        holder.poiDescription.setVisibility(View.GONE);
        //holder.poiDescription.setText(current.getPoiDescription());
        holder.poiImage.setImageBitmap(Utils.convertBytesToBitmap(current.getImageArray()));
        holder.poiImage.setColorFilter(context.getResources().getColor(android.R.color.darker_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        holder.poiFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.state_favorite_accent));
        AnimationUtilities.setAdapterSlideAnimation(holder.itemView, context, position, duration);
        duration = duration + 100;
    }

    @Override
    public int getItemCount() {
        return poiObjects.size();
    }

    class SavedPoiVH extends RecyclerView.ViewHolder{
        TextView poiTitle;
        TextView poiDescription;
        ImageView poiImage;
        ImageView poiExplore;
        ImageView poiFavorite;
        ImageView poiWiki;
        ImageView poiShare;

        public SavedPoiVH(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            poiTitle = (TextView)itemView.findViewById(R.id.poi_title);
            poiDescription = (TextView)itemView.findViewById(R.id.poi_description);
            poiImage = (ImageView)itemView.findViewById(R.id.poi_image);
            poiExplore = (ImageView)itemView.findViewById(R.id.poi_explore);
            poiFavorite = (ImageView) itemView.findViewById(R.id.poi_favorite);
            poiWiki = (ImageView) itemView.findViewById(R.id.poi_wiki);
            poiShare = (ImageView) itemView.findViewById(R.id.poi_share);

            poiFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cursorPoiObject = poiObjects.get(getAdapterPosition());
                    context.getContentResolver().delete(
                            InContract.PoiEntry.CONTENT_URI,
                            InContract.PoiEntry.COLUMN_POI_LAT + " = ?",
                            new String[]{cursorPoiObject.getPoiLatitude()});
                    Toast.makeText(context, context.getString(R.string.remove_favorite), Toast.LENGTH_SHORT).show();
                    onItemClick.onFavoriteClicked(getAdapterPosition());
                }
            });

            poiExplore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onMapClicked(getAdapterPosition());
                }
            });

            poiWiki.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onWikiClick(getAdapterPosition());
                }
            });

            poiShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onShareIconClick(getAdapterPosition());
                }
            });

        }
    }
}
