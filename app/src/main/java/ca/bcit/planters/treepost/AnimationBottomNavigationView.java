package ca.bcit.planters.treepost;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;

public class AnimationBottomNavigationView {

    private static final int DURATION = 1000;

    public static void addAnimation(final View targetView, View starterView, final int startColor, final int endColor){

        // solo da Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // l'animazione parte dal centro del bottone cliccato
            int[] location = new int[2];
            starterView.getLocationOnScreen(location);
            final int x = location[0] + starterView.getWidth()/2;

            // quando cambia il layout...
            targetView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);

                    int radius = (int) Math.hypot(right, bottom);

                    Animator anim = ViewAnimationUtils.createCircularReveal(v, x, targetView.getHeight(), 0, radius);
                    anim.setInterpolator(new FastOutSlowInInterpolator());
                    anim.setDuration(DURATION);
                    anim.start();
                    startColorAnimation(v, startColor, endColor, DURATION);
                }

            });

        }

    }

    private static void startColorAnimation(final View view, final int startColor, final int endColor, int duration) {

        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startColor, endColor);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        anim.setDuration(duration);
        anim.start();

    }

}
