package in.co.eko.fundu.constants;



import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Random;


/**
 * Created by zartha on 7/25/17.
 */
public class Animate {
    private Context context;
    private static String TAG = "Animate";
    private final int COLOR_RED = 0, COLOR_BLUE = 1, COLOR_YELLOW = 2;
    private int COLOR, delayMillis = 175;
    private Bitmap red, blue, yellow, blueYellow, blueRed, redYellow, blueRedYellow;
    private Random rand;
    private static Boolean stopLoader = false;

    public Animate(Context context) {
        this.context = context;

    }

    public static void fadeIn(final View view, final int animCode){
        view.setAlpha(0.0f);
        view.animate().alpha(1.0f).setStartDelay(100).setDuration(250);

    }
    public static void bubbleOutFadeIn(final View view,final View fadeInview, final int animCode){
        view.setVisibility(View.VISIBLE);
        view.setScaleX(1);
        view.setScaleY(1);
        view.animate().scaleX(1.5f).scaleY(1.5f).setDuration(250).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.animate().scaleX(1f).scaleY(1f).setDuration(250);
                view.animate().alpha(0.0f).setDuration(250);
                fadeInview.animate().alpha(1.0f).setDuration(260);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public static void bubble(final View view, final int animCode) {
        view.setVisibility(View.VISIBLE);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(250).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.animate().scaleX(1f).scaleY(1f).setDuration(50);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    public static void reverseBubble(final View view, final int animCode) {
        view.setVisibility(View.VISIBLE);
        view.setScaleX(1);
        view.setScaleY(1);
        view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(50).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                view.animate().scaleX(0f).scaleY(0f).setDuration(250).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void swoopIn(final View view, final int animCode) {
//        view.animate().scaleX(0.7f).scaleY(0.7f).setDuration(200).withEndAction(new Runnable() {
//
//            @Override
//            public void run() {
//                view.animate().scaleX(0f).scaleY(0f).setDuration(100).withEndAction(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        listener.onAnimationEnd(animCode);
//                    }
//                });
//            }
//        });
    }

    public void showLeftToRight(final int code, final View view) {
        view.setScaleX(1f);
        view.setScaleY(1f);
        final float width = view.getMeasuredWidth(), height = view.getMeasuredHeight();
        view.getLayoutParams().height = 0;
        view.getLayoutParams().width = 0;
        view.requestLayout();


        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                view.getLayoutParams().height = (int) (interpolatedTime * height);
                view.getLayoutParams().width = (int) (interpolatedTime * width);
                view.requestLayout();
            }
        };
        anim.setDuration(100);
        view.startAnimation(anim);

    }

    public void showRightToLeft(final int code, final View view) {
        view.setScaleX(1f);
        view.setScaleY(1f);
        final float width = view.getMeasuredWidth(), height = view.getMeasuredHeight(), x = view.getX();
        view.getLayoutParams().height = 0;
        view.getLayoutParams().width = 0;
        view.setX(view.getX() + width);
        view.requestLayout();


        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                view.getLayoutParams().height = (int) (interpolatedTime * height);
                view.getLayoutParams().width = (int) (interpolatedTime * width);
                view.requestLayout();
                view.setX(x + (1 - interpolatedTime) * width);
            }
        };

        anim.setDuration(100);
        view.startAnimation(anim);
    }

    public void largeToSmall(int large, View view, final int code) {
        view.setScaleX(large);
        view.setScaleY(large);
        view.animate().scaleX(1).scaleY(1).setDuration(200);

    }

    public void throwDown(final View view, final int code) {
//        view.animate().translationYBy(-10).setDuration(10).withEndAction(new Runnable() {
//
//            @Override
//            public void run() {
//                view.animate().translationYBy(500).setDuration(100).withEndAction(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        if (listener != null)
//                            listener.onAnimationEnd(code);
//                    }
//                });
//            }
//        });
    }

    public void loader(final View one, final View two, final View three, final int code) {
        final int dur = 300;
        one.setScaleX(0.7f);
        two.setScaleX(0.7f);
        three.setScaleX(0.7f);
        one.setScaleY(0.7f);
        two.setScaleY(0.7f);
        three.setScaleY(0.7f);

        final float large = 1f;

        one.animate().scaleX(large).scaleY(large).setDuration(dur);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                two.animate().scaleX(large).scaleY(large).setDuration(dur);
            }
        }, dur / 2);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                three.animate().scaleX(large).scaleY(large).setDuration(dur);
            }
        }, dur);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                one.animate().scaleX(0.7f).scaleY(0.7f).setDuration(dur);
            }
        }, 3 * dur);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                two.animate().scaleX(0.7f).scaleY(0.7f).setDuration(dur);
            }
        }, (3 * dur) + (dur / 2));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                three.animate().scaleX(0.7f).scaleY(0.7f).setDuration(dur);
            }
        }, 4 * dur);


    }

    public void changeHeight(final View view, float times, int duration, int respCode) {
        int newHeight = (int) (view.getMeasuredHeight() * times);
        final int diff = newHeight - view.getMeasuredHeight();
        final int oldHeight = view.getMeasuredHeight();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                view.getLayoutParams().height = (int) (oldHeight + (interpolatedTime * diff));
                view.requestLayout();
            }
        };

        anim.setDuration(duration);

        view.startAnimation(anim);
    }

    public void changeWidth(final View view, float times, int duration, int respCode) {
        int newHeight = (int) (view.getMeasuredHeight() * times);
        final int diff = newHeight - view.getMeasuredHeight();
        final int oldHeight = view.getMeasuredHeight();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                view.getLayoutParams().width = (int) (oldHeight + (interpolatedTime * diff));
                view.requestLayout();
            }
        };

        anim.setDuration(duration);

        view.startAnimation(anim);
    }



    public void alphaOne(final View view, final int respCode) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(200);
    }

    public void alphaZero(final View view, final int respCode) {
        view.animate().alpha(0f).setDuration(200);
    }


    private static HashMap<View, Boolean> keepSpinning;

    public static void stopSpinning(View view) {
        if (keepSpinning != null)
            keepSpinning.put(view, false);
        view.setVisibility(View.GONE);
    }

    private void showOne(int color1, int color2, final ImageView fLoaderImage) {
        int randNum = rand.nextInt(2);
        final int color;
        if (randNum == 0)
            color = color1;
        else
            color = color2;

        switch (color) {
            case COLOR_RED:
                fLoaderImage.setImageBitmap(red);
                break;
            case COLOR_BLUE:
                fLoaderImage.setImageBitmap(blue);
                break;
            case COLOR_YELLOW:
                fLoaderImage.setImageBitmap(yellow);
                break;
        }
        if (!stopLoader)
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    showTwo(color, fLoaderImage);
                }
            }, delayMillis);
    }

    private void showTwo(final int color, final ImageView fLoaderImage) {
        final int randNum = rand.nextInt(2);
        switch (color) {
            case COLOR_RED:
                if (randNum == 0) COLOR = COLOR_BLUE;
                else COLOR = COLOR_YELLOW;
                break;
            case COLOR_BLUE:
                if (randNum == 0) COLOR = COLOR_RED;
                else COLOR = COLOR_YELLOW;
                break;
            case COLOR_YELLOW:
                if (randNum == 0) COLOR = COLOR_BLUE;
                else COLOR = COLOR_RED;
                break;
        }

        Bitmap bitmap = getBitmap(color, COLOR);

        fLoaderImage.setImageBitmap(bitmap);
        if (!stopLoader)
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    //				if(randNum==0)
                    //					showOne(color, COLOR);
                    //				else

                    showThree(fLoaderImage);
                }
            }, delayMillis);
    }

    private Bitmap getBitmap(int color1, int color2) {
        if ((color1 == COLOR_RED || color1 == COLOR_BLUE) && (color2 == COLOR_BLUE || color2 == COLOR_RED))
            return blueRed;
        else if ((color1 == COLOR_RED || color2 == COLOR_RED) && (color2 == COLOR_YELLOW || color1 == COLOR_YELLOW))
            return redYellow;
        else
            return blueYellow;
    }

    private void showTwo(final ImageView fLoaderImage) {
        final int randNum = rand.nextInt(3);
        final int rand2 = rand.nextInt(3);
        if (randNum == rand2) {
            showTwo(fLoaderImage);
            return;
        } else {
            Bitmap bitmap = getBitmap(randNum, rand2);
            fLoaderImage.setImageBitmap(bitmap);
            if (!stopLoader)
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //					int ran = rand.nextInt(2);
                        //					if(ran==0)
                        //						showThree();
                        //					else

                        showOne(randNum, rand2, fLoaderImage);
                    }
                }, delayMillis);
        }
    }

    private void showThree(final ImageView fLoaderImage) {
        fLoaderImage.setImageBitmap(blueRedYellow);
        if (!stopLoader)
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    showTwo(fLoaderImage);
                }
            }, delayMillis);
    }

    public static void bubble(View view) {
        bubble(view, 0);
    }
}
