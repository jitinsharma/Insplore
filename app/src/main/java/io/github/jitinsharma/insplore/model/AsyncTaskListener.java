package io.github.jitinsharma.insplore.model;

/**
 * Created by jitin on 28/06/16.
 */
public interface AsyncTaskListener<T>{
    void onTaskComplete(T result);
}
