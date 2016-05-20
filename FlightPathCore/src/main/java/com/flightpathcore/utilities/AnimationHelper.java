package com.flightpathcore.utilities;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

/**
 * Created by tomek on 2015-01-27.
 */
public class AnimationHelper {

    public static long getDurationForHeight(float heightToAnimate){
        return 500;
    }

    public static long getDurationForWidth(float widthToAnimate){
        return 500;
    }

    public static void expandAnimation(final View v, final float heightToAnimate){
        expandAnimation(v, heightToAnimate, getDurationForHeight(heightToAnimate));
    }

    public static void expandAnimation(final View v, final float heightToAnimate, long duration){
        if(v.getVisibility() == View.GONE){
            v.setVisibility(View.VISIBLE);
        }
        v.getLayoutParams().height = 0;
        v.requestLayout();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float h = (heightToAnimate * (interpolatedTime));
                ViewGroup.LayoutParams lp = v.getLayoutParams();
                lp.height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int)h;
                v.setLayoutParams(lp);
//                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        anim.setDuration(duration);
        v.startAnimation(anim);
    }

    public static void collapseAnimation(final View v, final float heightToAnimate){
        collapseAnimations(v, heightToAnimate, getDurationForHeight(heightToAnimate));
    }

    public static void collapseAnimations(final View v, final float heightToAnimate, long duration){
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float h = (heightToAnimate * (interpolatedTime));
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else {
                    ViewGroup.LayoutParams lp = v.getLayoutParams();
                    lp.height = (int) (heightToAnimate - h);//Utilities.dpToPx(getContext(), (int)h);
                    v.setLayoutParams(lp);
                }
//                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        anim.setDuration(duration);
        v.startAnimation(anim);
    }

    public static void showAnimation(final View v, final float heightToAnimate){
        showAnimation(v,heightToAnimate, getDurationForHeight(heightToAnimate));
    }

    public static void showAnimation(final View v, final float heightToAnimate, long duration){
        if(v.getVisibility() == View.GONE){
            v.setVisibility(View.VISIBLE);
        }
        v.setY(-heightToAnimate);
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float h = -heightToAnimate+(heightToAnimate * (interpolatedTime));
                if(interpolatedTime == 1){
                    v.setY(0);
                }else {
                    v.setY(h);
                }
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        anim.setDuration(duration);
        v.startAnimation(anim);
    }

    public static void hideAnimation(final View v, final float heightToAnimate){
        hideAnimation(v, heightToAnimate, getDurationForHeight(heightToAnimate));
    }

    public static void hideAnimation(final View v, final float heightToAnimate, long duration){
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float h = (heightToAnimate * (interpolatedTime));
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else {
                    v.setY(v.getY() - h);
                }
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        anim.setDuration(duration);
        v.startAnimation(anim);
    }

    public static void scaleUpAnimation(final View v){
        scaleUpAnimation(v, getDurationForHeight(v.getMeasuredHeight()), null);
    }

    public static void scaleUpAnimation(final View v, Animation.AnimationListener listener){
        scaleUpAnimation(v, getDurationForHeight(v.getMeasuredHeight()), listener);
    }

    public static void scaleUpAnimation(final View v, long duration, Animation.AnimationListener listener){
        Animation anim = new ScaleAnimation(0f,1f,0f,1f,0.5f,0.5f);
        anim.setDuration(duration);
        if(listener != null){
            anim.setAnimationListener(listener);
        }
        v.startAnimation(anim);
    }

    public static void scaleDownAnimation(final View v) {
        scaleDownAnimation(v, getDurationForHeight(v.getMeasuredHeight()), null);
    }

    public static void scaleDownAnimation(final View v, Animation.AnimationListener listener) {
        scaleDownAnimation(v, getDurationForHeight(v.getMeasuredHeight()), listener);
    }

    public static void scaleDownAnimation(final View v, long duration, Animation.AnimationListener listener){
        Animation anim = new ScaleAnimation(1f,0f,1f,0f,0.5f,0.5f);
        anim.setDuration(duration);
        if(listener != null){
            anim.setAnimationListener(listener);
        }
        v.startAnimation(anim);
    }

    public static void slideLeft(final View v1, final View v2, final float slideWidth){
        slideLeft(v1,v2,slideWidth, getDurationForWidth(slideWidth));
    }

    public static void slideLeft(final View v1, final View v2, final float slideWidth, long duration){
        final float startPosition1 = v1.getX();
        final float startPosition2 = v2.getX();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v1.setX(startPosition1+ slideWidth * interpolatedTime);
                v2.setX(startPosition2+ slideWidth * interpolatedTime);
            }
        };
        anim.setDuration(duration);
        v1.startAnimation(anim);
    }

    public static void slideRight(final View v1, final View v2, final float slideWidth){
        slideRight(v1, v2, slideWidth, getDurationForWidth(slideWidth));
    }

    public static void slideRight(final View v1, final View v2, final float slideWidth, long duration){
        final float startPosition1 = v1.getX();
        final float startPosition2 = v2.getX();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v1.setX(startPosition1 - slideWidth * interpolatedTime);
                v2.setX(startPosition2 - slideWidth * interpolatedTime);
            }
        };
        anim.setDuration(duration);
        v1.startAnimation(anim);
    }

    public static void slideDownAnimation(final View v, final float heightToAnimate){
        slideDownAnimation(v, heightToAnimate, getDurationForHeight(heightToAnimate));
    }

    public static void slideDownAnimation(final View v, final float heightToAnimate, long duration){
        final float startPos = v.getY();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float h = heightToAnimate * (interpolatedTime);
                v.setY(startPos + h);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        anim.setDuration(duration);
        v.startAnimation(anim);
    }

    public static void slideUpAnimation(final View v, final float heightToAnimate){
        slideUpAnimation(v,heightToAnimate, getDurationForHeight(heightToAnimate));
    }

    public static void slideUpAnimation(final View v, final float heightToAnimate, long duration){
        final float startPos = v.getY();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float h = (heightToAnimate * (interpolatedTime));
                v.setY(startPos - h);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        anim.setDuration(duration);
        v.startAnimation(anim);
    }
}
