package lucky8s.shift;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Christian on 4/5/2015.
 */
public class TutorialDialog extends DialogFragment implements View.OnClickListener{
    Button next;

    View dude;
    View reset;
    View hints;
    View time;
    View moves;
    View finish;
    View obstacle;
    View outline;
    View breakable;
    View molten;
    View hint;
    View portal;
    View bubble;
    View frozen;

    TextView description;

    ObjectAnimator moveOutlineX;
    ObjectAnimator moveOutlineY;

    AnimatorSet moveOutline;

    int step;
    Generator generator;

    ArrayList<View> obstacles;

    @SuppressLint("ValidFragment")
    public TutorialDialog(Generator generator) {
        this.generator = generator;
        // Empty constructor required for DialogFragment
    }
    public TutorialDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorial_dialog, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        User.add("In TutorialDialog");

        outline = view.findViewById(R.id.outline);
        next =(Button) view.findViewById(R.id.next);
        description = (TextView) view.findViewById(R.id.description);
        next.setOnClickListener(this);
        dude = getActivity().findViewById(R.id.dude);
        reset = getActivity().findViewById(R.id.restart);
        hints = getActivity().findViewById(R.id.hint);
        time = getActivity().findViewById(R.id.timer);
        moves = getActivity().findViewById(R.id.numMoves);

        obstacles = generator.getObstacles();


        finish = generator.getFinish();
        dude = generator.getDude();
        molten = getMolten();
        breakable = getBreakable();
        obstacle = getStandard();
        bubble = getBubble();
        portal = getPortal();
        frozen = getFrozen();

        if(step == 0){
            if(getDialog().getContext().getSharedPreferences("Tutorial", Context.MODE_PRIVATE).getInt("step", 0) == 11){
                step = 10;
                breakable = getBreakable();
            }else if(getDialog().getContext().getSharedPreferences("Tutorial", Context.MODE_PRIVATE).getInt("step", 0) == 16){
                step = 15;
                molten = getMolten();
            }else if(getDialog().getContext().getSharedPreferences("Tutorial", Context.MODE_PRIVATE).getInt("step", 0) == 18){
                step = 17;
                molten = getMolten();
            }else if(getDialog().getContext().getSharedPreferences("Tutorial", Context.MODE_PRIVATE).getInt("step", 0) == 21){
                step = 20;
                bubble = getBubble();
            }else if(getDialog().getContext().getSharedPreferences("Tutorial", Context.MODE_PRIVATE).getInt("step", 0) == 23){
                step = 22;
                frozen = getFrozen();
            }

            step++;
            showTutorial(step);
        }

        return view;
    }
    public View getStandard(){
        for(int i = 0; i < obstacles.size(); i++){
            if(obstacles.get(i) instanceof Standard){
                return obstacles.get(i);
            }
        }
        return null;
    }
    public View getBreakable(){
        for(int i = 0; i < obstacles.size(); i++){
            if(obstacles.get(i) instanceof Breakable){
                return obstacles.get(i);
            }
        }
        return null;
    }
    public View getBubble(){
        for(int i = 0; i < obstacles.size(); i++){
            if(obstacles.get(i) instanceof Bubble){
                return obstacles.get(i);
            }
        }
        return null;
    }
    public View getMolten(){
        for(int i = 0; i < obstacles.size(); i++){
            if(obstacles.get(i) instanceof Molten){
                return obstacles.get(i);
            }
        }
        return null;
    }
    public View getPortal(){
        for(int i = 0; i < obstacles.size(); i++){
            if(obstacles.get(i) instanceof Portal){
                return obstacles.get(i);
            }
        }
        return null;
    }
    public View getFrozen(){
        for(int i = 0; i < obstacles.size(); i++){
            if(obstacles.get(i) instanceof Frozen){
                return obstacles.get(i);
            }
        }
        return null;
    }
    public void showTutorial(int step){
        User.add("Showing Tutorial Step "+Integer.toString(step));
        switch(step){
            case 1:
                description.setText(getResources().getString(R.string.welcome_to_shift));
                break;
            case 2:
                outline(dude);
                description.setText(getResources().getString(R.string.dude_description));
                break;
            case 3:
                outline(finish);
                description.setText(getResources().getString(R.string.finish_description));
                break;
            case 4:
                outline(finish);
                description.setText(getString(R.string.finish_end_up));
                break;
            case 5:
                outline(obstacle);
                description.setText(getResources().getString(R.string.obstacle_description));
                break;
            case 6:
                outline.setVisibility(View.INVISIBLE);
                description.setText(getResources().getString(R.string.swipe_right));
                next.setText(getResources().getString(R.string.okay));
                break;
            case 7:
                outline(moves);
                description.setText(getResources().getString(R.string.moves_description));
                break;
            case 8:
                outline.setVisibility(View.INVISIBLE);
                description.setText(getResources().getString(R.string.swipe_down));
                next.setText(getResources().getString(R.string.okay));
                break;
            case 9:
                outline(time);
                description.setText(getResources().getString(R.string.timer_description));
                next.setText(getResources().getString(R.string.okay));
                break;
            case 10:
                outline.setVisibility(View.INVISIBLE);
                description.setText(getResources().getString(R.string.swipe_left));
                next.setText(getResources().getString(R.string.okay));
                break;
            case 11:
                outline(reset);
                description.setText(getResources().getString(R.string.restart_description));
                break;
            case 12:
                outline(hints);
                description.setText(getResources().getString(R.string.hints_description));
                break;
            case 13:
                outline(breakable);
                description.setText(getResources().getString(R.string.breakables_description));
                break;
            case 14:
                description.setText(getResources().getString(R.string.breakables_action));
                break;
            case 15:
                outline(hints);
                description.setText(getResources().getString(R.string.tap_hints));
                next.setText(getResources().getString(R.string.okay));
                break;
            case 16:
                outline(molten);
                description.setText(getResources().getString(R.string.molten_description));
                break;
            case 17:
                description.setText(getResources().getString(R.string.molten_action));
                next.setText(getResources().getString(R.string.okay));
                break;
            case 18:
                outline(portal);
                description.setText(getResources().getString(R.string.portal_description));
                break;
            case 19:
                description.setText(getResources().getString(R.string.portal_action));
                break;
            case 20:
                description.setText(getResources().getString(R.string.follow_hints));
                next.setText(getResources().getString(R.string.okay));
                break;
            case 21:
                outline(bubble);
                description.setText(getResources().getString(R.string.bubble_description));
                break;
            case 22:
                description.setText(getResources().getString(R.string.bubble_action));
                next.setText(getResources().getString(R.string.okay));
                break;
            case 23:
                outline(frozen);
                description.setText(getResources().getString(R.string.frozen_description));
                break;
            case 24:
                description.setText(getResources().getString(R.string.frozen_action));
                next.setText(getResources().getString(R.string.okay));
                break;

        }
    }
    public void outline(View view){
        if(view != null) {
            int outlineWidth = (int) (view.getWidth() * 2.0);
            centerOutline(outline, view, outlineWidth, view.getWidth());
        }
    }
    public void centerOutline(final View outline,View view, int outlineWidth, int viewWidth) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(outlineWidth, outlineWidth);
        outline.setLayoutParams(layoutParams);
        moveOutline = new AnimatorSet();
        int [] outlineCoords = new int[2];
        outline.getLocationOnScreen(outlineCoords);
        int [] viewCoords = new int[2];
        view.getLocationOnScreen(viewCoords);
        int x = viewCoords[0]-(int)(((double)outlineWidth-(double)viewWidth)/2.0);
        int y = viewCoords[1]-(int)(((double)outlineWidth-(double)viewWidth)/2.0);
        moveOutlineX = ObjectAnimator.ofFloat(outline, "X", outlineCoords[0], x);
        moveOutlineY = ObjectAnimator.ofFloat(outline, "Y", outlineCoords[1], y);
        moveOutline.play(moveOutlineX).with(moveOutlineY);
        moveOutline.start();
        moveOutline.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                outline.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    public void onResume(){
        super.onResume();
        Context context = getDialog().getOwnerActivity().getBaseContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int height = size.y;
        int width = size.x;
        getDialog().getWindow().setLayout(width, height);
        getDialog().getWindow().setGravity(Gravity.CENTER);

        outline.setVisibility(View.INVISIBLE);
        Log.i("tutorial", "showing step = " + step);
        showTutorial(step);

    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.next:
                User.add("Clicked next in Tutorial");
                if(next.getText().toString().equals(getString(R.string.okay))){
                    getDialog().getContext().getSharedPreferences("Tutorial", Context.MODE_PRIVATE).edit().putInt("step", step).apply();
                    getDialog().dismiss();
                    step++;
                }else {
                    step++;
                    showTutorial(step);
                }
                break;
        }
    }
    public void onDestroy(){
        super.onDestroy();

        System.gc();

    }
}