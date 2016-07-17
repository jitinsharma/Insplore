package io.github.jitinsharma.insplore.utilities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import io.github.jitinsharma.insplore.R;

/**
 * Created by jitin on 30/06/16.
 */
public class AnimationUtilities {
    static int lastPosition = -1;

    public static void animatedBackgroundColor(int colorFrom, int colorTo, final View view){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    public static void setAnimation(View viewToAnimate, Context context, int position, int duration) {
        duration = duration + 100;
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            animation.setDuration(duration);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static void animateImage(ImageView imageView, Drawable from, Drawable to){
        Drawable[] layers = new Drawable[2];
        layers[0] = from;
        layers[1] = to;
        TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(1000);
    }

    public static void animateSlideIn(ImageView view, Context context, int duration, Drawable drawable){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animation.setDuration(duration);
        view.setImageDrawable(drawable);
        view.startAnimation(animation);
    }
}
