package io.github.jitinsharma.insplore.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.Utilities.Utils;
import io.github.jitinsharma.insplore.data.InContract;
import io.github.jitinsharma.insplore.model.OnItemClick;
import io.github.jitinsharma.insplore.model.PoiObject;

/**
 * Created by jitin on 10/07/16.
 */
public class SavedPoiAdapter extends RecyclerView.Adapter<SavedPoiAdapter.SavedPoiVH>{
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<PoiObject> poiObjects;
    OnItemClick onItemClick;
    String[] selectionArgs;
    Bitmap bitmap;
    byte[] imageArray;
    PoiObject current;
    Cursor cursor;
    public static final String[] POI_COLUMNS = {
            InContract.PoiEntry._ID,
            InContract.PoiEntry.COLUMN_POI_TITLE,
            InContract.PoiEntry.COLUMN_POI_DESC,
            InContract.PoiEntry.COLUMN_POI_IMAGE,
            InContract.PoiEntry.COLUMN_POI_LAT,
            InContract.PoiEntry.COLUMN_POI_LONG,
            InContract.PoiEntry.COLUMN_GEO_ID,
            InContract.PoiEntry.COLUMN_POI_WIKI_LINK
    };

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
        current = poiObjects.get(position);
        holder.poiTitle.setText(current.getTitle());
        holder.poiDescription.setText(current.getPoiDescription());
        holder.poiImage.setImageBitmap(Utils.convertBytesToBitmap(current.getImageArray()));
        holder.poiFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
    }

    @Override
    public int getItemCount() {
        return poiObjects.size();
    }

    class SavedPoiVH extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView poiTitle;
        TextView poiDescription;
        ImageView poiImage;
        ImageView poiExplore;
        ImageView poiFavorite;

        public SavedPoiVH(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            poiTitle = (TextView)itemView.findViewById(R.id.poi_title);
            poiDescription = (TextView)itemView.findViewById(R.id.poi_description);
            poiImage = (ImageView)itemView.findViewById(R.id.poi_image);
            poiExplore = (ImageView)itemView.findViewById(R.id.poi_explore);
            poiFavorite = (ImageView) itemView.findViewById(R.id.poi_favorite);

            poiFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.getContentResolver().delete(
                            InContract.PoiEntry.CONTENT_URI,
                            InContract.PoiEntry.COLUMN_GEO_ID + " = ?",
                            new String[]{current.getGeoNameId()});
                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    onItemClick.onFavoriteClicked(getAdapterPosition());
                }
            });

            poiExplore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onMapClicked(getAdapterPosition());
                }
            });

        }

        @Override
        public void onClick(View view) {
            if (view == poiExplore){
                onItemClick.onMapClicked(getAdapterPosition());
            }
        }
    }
}
