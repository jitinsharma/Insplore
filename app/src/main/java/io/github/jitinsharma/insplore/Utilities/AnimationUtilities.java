package io.github.jitinsharma.insplore.utilities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

    public static void setAdapterSlideAnimation(View viewToAnimate, Context context, int position, int duration) {
        duration = duration + 100;
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.enter_from_right);
            animation.setDuration(duration);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static void animateImageFadeIn(ImageView view, Context context, int duration, Drawable drawable){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animation.setDuration(duration);
        view.setImageDrawable(drawable);
        view.startAnimation(animation);
    }

    public static void animateViewUp(View view, Context context, int duration){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }
}
