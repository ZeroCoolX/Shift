package lucky8s.shift;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Christian on 12/27/2015.
 */
public class ButtonAnimator {
    View view;
    ObjectAnimator pulseX;
    ObjectAnimator pulseY;
    AnimatorSet pulse;

    public ButtonAnimator(View view){
        this.view = view;
        init();
    }
    public void init(){
        pulseX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.8f, 1.0f);
        pulseY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.8f, 1.0f);
        pulseX.setDuration(750);
        pulseY.setDuration(750);
        pulseX.setRepeatMode(ObjectAnimator.REVERSE);
        pulseY.setRepeatMode(ObjectAnimator.REVERSE);
        pulseX.setRepeatCount(ObjectAnimator.INFINITE);
        pulseY.setRepeatCount(ObjectAnimator.INFINITE);
        pulseX.setInterpolator(new AccelerateInterpolator());
        pulseY.setInterpolator(new AccelerateInterpolator());
        pulse = new AnimatorSet();
        pulse.play(pulseX).with(pulseY);
    }
    public void setDuration(int duration){
        pulseX.setDuration(duration);
        pulseY.setDuration(duration);
    }
    public void setScale(float scaleLow, float scaleHigh){
        pulseX.setFloatValues(scaleLow, scaleHigh);
        pulseY.setFloatValues(scaleLow, scaleHigh);
    }
    public void start(){
        pulse.start();
    }
    public void stop(){
        pulse.cancel();
    }
    public boolean isRunning(){
        if(pulse.isRunning()){
            return true;
        }else return false;
    }
}
