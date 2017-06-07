package lucky8s.shift;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
/**
 * Created by Christian on 6/12/2015.
 */
public class Star {

    public Star() {
        super();
    }

    SoundDriver sd;

    View view;

    ObjectAnimator expandX;
    ObjectAnimator expandY;
    ObjectAnimator shrinkX;
    ObjectAnimator shrinkY;

    AnimatorSet expand = new AnimatorSet();
    AnimatorSet shrink = new AnimatorSet();
    AnimatorSet full = new AnimatorSet();

    boolean visible = false;

    public Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sd.star();
                    view.setVisibility(View.VISIBLE);

                }
            }, full.getStartDelay());
        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    public void setView(View view) {
        this.view = view;
        this.view.setVisibility(View.INVISIBLE);
        expandX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.1f, 1.25f);
        expandY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.1f, 1.25f);
        shrinkX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.25f, 1f);
        shrinkY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.25f, 1f);
        full.addListener(animatorListener);
        sd = MyApplication.getInstance().getSD();
    }

    public void setDuration(int duration) {
        expandX.setDuration(duration);
        expandY.setDuration(duration);
        shrinkX.setDuration(duration);
        shrinkY.setDuration(duration);
    }

    public void start() {
        expand.play(expandX).with(expandY);
        shrink.play(shrinkX).with(shrinkY);
        full.play(expand).before(shrink);
        full.start();
    }

    public void disable() {
        view.setVisibility(View.INVISIBLE);
        this.visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public AnimatorSet getAnimator() {
        expand.play(expandX).with(expandY);
        shrink.play(shrinkX).with(shrinkY);
        full.play(expand).before(shrink);
        return full;
    }
    public void delay(int ms){
        full.setStartDelay(ms);
    }
    public SoundDriver getSD(){
        return this.sd;
    }
}
