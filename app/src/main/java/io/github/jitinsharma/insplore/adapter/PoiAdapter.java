package io.github.jitinsharma.insplore.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.data.InContract.PoiEntry;
import io.github.jitinsharma.insplore.model.OnItemClick;
import io.github.jitinsharma.insplore.model.PoiObject;
import io.github.jitinsharma.insplore.utilities.AnimationUtilities;
import io.github.jitinsharma.insplore.utilities.Utils;

/**
 * Created by jitin on 04/07/16.
 */
public class PoiAdapter extends RecyclerView.Adapter<PoiAdapter.PoiVH>{
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<PoiObject> poiObjects;
    OnItemClick onItemClick;
    Cursor cursor;
    String[] selectionArgs;
    Bitmap bitmap;
    byte[] imageArray;
    PoiObject cursorPoiObject;
    public static final String[] POI_COLUMNS = {
            PoiEntry._ID,
            PoiEntry.COLUMN_POI_TITLE,
            PoiEntry.COLUMN_POI_DESC,
            PoiEntry.COLUMN_POI_IMAGE,
            PoiEntry.COLUMN_POI_LAT,
            PoiEntry.COLUMN_POI_LONG,
            PoiEntry.COLUMN_GEO_ID,
            PoiEntry.COLUMN_POI_WIKI_LINK
    };

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public PoiAdapter(Context context, ArrayList<PoiObject> poiObjects) {
        this.context = context;
        this.poiObjects = poiObjects;
    }

    public PoiAdapter(Context context, ArrayList<PoiObject> poiObjects, OnItemClick onItemClick) {
        this.context = context;
        this.poiObjects = poiObjects;
        this.onItemClick = onItemClick;
    }

    @Override
    public PoiVH onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_poi, parent, false);
        return new PoiVH(view);
    }

    @Override
    public void onBindViewHolder(PoiVH holder, int position) {
        PoiObject current = poiObjects.get(position);
        if (current.getPoiLatitude()!=null){
            selectionArgs = new String[]{current.getPoiLatitude()};
        }
        cursor = context.getContentResolver().query(
                PoiEntry.CONTENT_URI,
                POI_COLUMNS,
                PoiEntry.COLUMN_POI_LAT + " = ?",
                selectionArgs,
                null
        );
        if (cursor!=null && cursor.moveToFirst()){
            holder.poiFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_accent_24dp));
        }
        else{
            holder.poiFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        }
        holder.poiTitle.setText(current.getTitle());
        holder.poiDescription.setVisibility(View.GONE);
        //holder.poiDescription.setText(current.getPoiDescription());
        Glide.with(context)
                .load(current.getMainImageUrl())
                .into(holder.poiImage);
        holder.poiImage.setColorFilter(context.getResources().getColor(android.R.color.darker_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        AnimationUtilities.setAnimation(holder.itemView, context, position, 400);
    }

    @Override
    public int getItemCount() {
        return poiObjects.size();
    }

    class PoiVH extends RecyclerView.ViewHolder{
        TextView poiTitle;
        TextView poiDescription;
        ImageView poiImage;
        ImageView poiExplore;
        ImageView poiFavorite;
        ImageView poiWiki;
        ImageView poiShare;

        public PoiVH(View itemView) {
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
                    cursor = context.getContentResolver().query(
                            PoiEntry.CONTENT_URI,
                            POI_COLUMNS,
                            PoiEntry.COLUMN_POI_LAT + " = ?",
                            new String[]{cursorPoiObject.getPoiLatitude()},
                            null
                    );
                    if (cursor!=null && !cursor.moveToFirst()){
                        ContentValues poiValues = new ContentValues();
                        //bitmap = ((BitmapDrawable)poiImage.getDrawable()).getBitmap();
                        bitmap = ((GlideBitmapDrawable)poiImage.getDrawable().getCurrent()).getBitmap();
                        imageArray = Utils.convertBitmapToBytes(bitmap);

                        poiValues.put(PoiEntry.COLUMN_GEO_ID, cursorPoiObject.getGeoNameId());
                        poiValues.put(PoiEntry.COLUMN_POI_TITLE, cursorPoiObject.getTitle());
                        poiValues.put(PoiEntry.COLUMN_POI_DESC, cursorPoiObject.getPoiDescription());
                        poiValues.put(PoiEntry.COLUMN_POI_IMAGE, imageArray);
                        poiValues.put(PoiEntry.COLUMN_POI_LAT, cursorPoiObject.getPoiLatitude());
                        poiValues.put(PoiEntry.COLUMN_POI_LONG, cursorPoiObject.getPoiLongitude());
                        poiValues.put(PoiEntry.COLUMN_POI_WIKI_LINK, cursorPoiObject.getWikipediaLink());
                        Uri uri = context.getContentResolver().insert(PoiEntry.CONTENT_URI, poiValues);
                        Animation in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                        Animation out = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                        poiFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_accent_24dp));
                        poiFavorite.setAnimation(out);
                        in.start();
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        context.getContentResolver().delete(
                                PoiEntry.CONTENT_URI,
                                PoiEntry.COLUMN_POI_LAT + " = ?",
                                new String[]{cursorPoiObject.getPoiLatitude()}
                        );
                        Animation out = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                        poiFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                        poiFavorite.setAnimation(out);
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                    //notifyDataSetChanged();
                    cursor.close();
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
