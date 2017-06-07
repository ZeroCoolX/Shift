package lucky8s.shift;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Christian on 4/5/2015.
 */
public class StoreDialog extends DialogFragment implements View.OnClickListener{

    Handler timerHandler = new Handler();
    DialogListener listener;

    Button toStore;
    Button cancel;

    TextView title;
    TextView message;
    TextView buttonMessage;
    TextView icon;

    String item;
    boolean timed;
    int seconds;
    double reduction;

    public final static String SPEED_BOOST = "Speed Boost";
    public final static String PORTAL_MASTER = "Portal Master";
    public final static String SPACE_HEATER = "Space Heater";
    public final static String PERFECTIONIST = "Perfectionist";
    public final static String NO_ADS_BUNDLE = "No Ads Bundle";
    public final static String ALL_PACKS_BUNDLE = "All Packs Bundle";
    public final static String PRO_BUNDLE = "Pro Bundle";
    public final static String COINS = "Coins";

    public StoreDialog() {
        // Empty constructor required for DialogFragment
    }
    @SuppressLint("ValidFragment")
    public StoreDialog(String item, boolean timed, double reduction, DialogListener listener) {
        this.item = item;
        this.timed = timed;
        this.reduction = reduction;
        this.listener = listener;
    }
    public void onCloseDialog() {}
    public void showCoinsStore(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.store_dialog, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        User.add("Show Dialog - "+(item != null ? item : ""));

        toStore = (Button) view.findViewById(R.id.to_store);
        cancel = (Button) view.findViewById(R.id.cancel);
        title = (TextView) view.findViewById(R.id.title);
        message = (TextView) view.findViewById(R.id.message);
        buttonMessage = (TextView) view.findViewById(R.id.button_message);
        icon = (TextView) view.findViewById(R.id.icon);

        title.setText(getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getString("Title", getString(R.string.store)));
        message.setText(getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getString("Message", ""));
        buttonMessage.setText(getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getString("Button", getString(R.string.store)));
        icon.setBackground(getIcon(item));


        toStore.setOnClickListener(this);
        cancel.setOnClickListener(this);

        if(timed){
            startTimer(20);
        }

        return view;
    }
    public Drawable getIcon(String item){
        switch (item){
            case SPEED_BOOST:
                return getResources().getDrawable(R.drawable.speed);
            case PORTAL_MASTER:
                return getResources().getDrawable(R.drawable.portal);
            case SPACE_HEATER:
                return getResources().getDrawable(R.drawable.flame);
            case PERFECTIONIST:
                return getResources().getDrawable(R.drawable.perfect_bonus_mod);
            case NO_ADS_BUNDLE:
                icon.setText(getString(R.string.ads));
                return getResources().getDrawable(R.drawable.no_ads);
            case ALL_PACKS_BUNDLE:
                return getResources().getDrawable(R.drawable.all_packs);
            case PRO_BUNDLE:
                return getResources().getDrawable(R.drawable.pro);
            case COINS:
                return getResources().getDrawable(R.drawable.gold_bonus);
            default:
                return getResources().getDrawable(R.drawable.pro);
        }
    }
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds--;
            if(seconds < 0) {
                endTimer();
            }else{
                refreshTime();
                timerHandler.postDelayed(this, 1000);
            }
        }
    };
    public void refreshTime(){
        if(seconds >= 0){
            toStore.setText(Integer.toString(seconds));
        }
    }
    public void endTimer(){
        timerHandler.removeCallbacks(timerRunnable);
        User.add("Let time run out.");
        getDialog().dismiss();
    }
    public void startTimer(int seconds){
        this.seconds = seconds;
        timerHandler.postDelayed(timerRunnable,0);
    }
    public void onResume(){
        super.onResume();
        Context context = getActivity().getBaseContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int height = size.y;
        int width = size.x;
        getDialog().getWindow().setLayout((width / 10) * 9, (height / 10) * 7);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_store:
                setTimedSale();
                timerHandler.removeCallbacks(timerRunnable);
                if(!item.equals(COINS)) {
                    User.add("Clicked to go to Store from StoreDialog");
                    User.add("Timed = "+timed);
                    Intent a = new Intent(getDialog().getOwnerActivity(), Store.class);
                    startActivity(a);
                    getDialog().dismiss();
                    getDialog().getOwnerActivity().finish();
                }else{
                    User.add("Clicked to go to CoinsDialog from StoreDialog");
                    getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putBoolean("double_coins", true).apply();
                    getDialog().dismiss();
                    listener.showCoinsStore();
                }
                break;
            case R.id.cancel:
                timerHandler.removeCallbacks(timerRunnable);
                User.add("Clicked cancel.");
                getDialog().dismiss();
                break;
            default:
                break;
        }
    }
    public void setTimedSale(){
        getDialog().getOwnerActivity().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Sale", item).apply();
        getDialog().getOwnerActivity().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Reduction", Double.toString(reduction)).apply();
    }
    public void onDestroy(){
        super.onDestroy();

        timerHandler.removeCallbacks(timerRunnable);

        System.gc();

    }
}
