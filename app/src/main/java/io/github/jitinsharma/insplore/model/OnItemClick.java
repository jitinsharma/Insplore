package io.github.jitinsharma.insplore.model;

/**
 * Created by jitin on 06/07/16.
 */
public interface OnItemClick {
    void onViewClicked(int position);
    void onFavoriteClicked(int position);
    void onMapClicked(int position);
}
