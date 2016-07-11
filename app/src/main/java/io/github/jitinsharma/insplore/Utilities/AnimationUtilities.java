package io.github.jitinsharma.insplore.Utilities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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
        // If the bound view wasn't previously displayed on screen, it's animated
        duration = duration + 100;
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            animation.setDuration(duration);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
