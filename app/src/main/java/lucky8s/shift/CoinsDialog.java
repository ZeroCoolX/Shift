package lucky8s.shift;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.parse.ParseObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Christian on 4/5/2015.
 */
public class CoinsDialog extends DialogFragment implements View.OnClickListener, AdInterface{

    ConnectivityManager cm;
    NetworkInfo netInfo;
    BillingProcessor bp;
    DialogListener listener;
    SQL sql;

    EditText promoCode;
    ScrollView scrollViewSilver;
    ScrollView scrollViewGold;

    Button buy500Silver;
    Button buy1000Silver;
    Button buy2000Silver;
    Button buy5000Silver;
    Button buy10000Silver;
    Button buy20000Silver;
    Button buy50000Silver;
    Button buy10Gold;
    Button buy25Gold;
    Button buy50Gold;
    Button buy100Gold;
    Button buy200Gold;
    Button buy500Gold;
    Button buy1000Gold;
    Button buy3000Gold;
    Button freeSilver;
    Button freeGold;
    Button apply;
    Button silverButton;
    Button goldButton;
    Button back;

    TextView message500Silver;
    TextView message1000Silver;
    TextView message2000Silver;
    TextView message5000Silver;
    TextView message10000Silver;
    TextView message20000Silver;
    TextView message50000Silver;

    TextView message10Gold;
    TextView message25Gold;
    TextView message50Gold;
    TextView message100Gold;
    TextView message200Gold;
    TextView message500Gold;
    TextView message1000Gold;
    TextView message3000Gold;

    TextView freeSilverMessage;
    TextView freeGoldMessage;


    View item1;
    View item2;
    View item3;
    View item4;
    View item5;
    View item6;
    View item7;
    View item8;
    View item9;
    View item10;
    View item11;
    View item12;
    View item13;
    View item14;
    View item15;
    View item17;
    View item18;

    TextView gold1;
    TextView gold2;
    TextView gold3;
    TextView gold4;
    TextView gold5;
    TextView gold6;
    TextView gold7;
    TextView gold8;

    String PURCHASE_CODE = "";
    String pack = "";
    String usedCode = "";
    String currency = "";

    int origGold1;
    int origGold2;
    int origGold3;
    int origGold4;
    int origGold5;
    int origGold6;
    int origGold7;
    int origGold8;

    boolean doubleGold;


    HashMap<String, Double> priceList = new HashMap<>();

    @SuppressLint("ValidFragment")
    public CoinsDialog(DialogListener activityDialogListener) {
        this.listener = activityDialogListener;
    }
    public CoinsDialog(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buy_coins, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        cm = (ConnectivityManager) getDialog().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        sql = new SQL(getDialog().getContext());
        Ad.setAct(getDialog().getOwnerActivity());

        setBillingProcessor();

        Ad.setAdInterface(this);

        scrollViewSilver = (ScrollView) view.findViewById(R.id.scrollview_silver);
        scrollViewGold = (ScrollView) view.findViewById(R.id.scrollview_gold);
        promoCode = (EditText) view.findViewById(R.id.promo_code);

        buy500Silver = (Button) view.findViewById(R.id.buy_500_silver);
        buy500Silver.setOnClickListener(this);
        buy1000Silver = (Button) view.findViewById(R.id.buy_1000_silver);
        buy1000Silver.setOnClickListener(this);
        buy2000Silver = (Button) view.findViewById(R.id.buy_2000_silver);
        buy2000Silver.setOnClickListener(this);
        buy5000Silver = (Button) view.findViewById(R.id.buy_5000_silver);
        buy5000Silver.setOnClickListener(this);
        buy10000Silver = (Button) view.findViewById(R.id.buy_10000_silver);
        buy10000Silver.setOnClickListener(this);
        buy20000Silver = (Button) view.findViewById(R.id.buy_20000_silver);
        buy20000Silver.setOnClickListener(this);
        buy50000Silver = (Button) view.findViewById(R.id.buy_50000_silver);
        buy50000Silver.setOnClickListener(this);
        freeSilver = (Button) view.findViewById(R.id.free_silver);
        freeSilver.setOnClickListener(this);
        buy10Gold = (Button) view.findViewById(R.id.buy_10_gold);
        buy10Gold.setOnClickListener(this);
        buy25Gold = (Button) view.findViewById(R.id.buy_25_gold);
        buy25Gold.setOnClickListener(this);
        buy50Gold = (Button) view.findViewById(R.id.buy_50_gold);
        buy50Gold.setOnClickListener(this);
        buy100Gold = (Button) view.findViewById(R.id.buy_100_gold);
        buy100Gold.setOnClickListener(this);
        buy200Gold = (Button) view.findViewById(R.id.buy_200_gold);
        buy200Gold.setOnClickListener(this);
        buy500Gold = (Button) view.findViewById(R.id.buy_500_gold);
        buy500Gold.setOnClickListener(this);
        buy1000Gold = (Button) view.findViewById(R.id.buy_1000_gold);
        buy1000Gold.setOnClickListener(this);
        buy3000Gold = (Button) view.findViewById(R.id.buy_3000_gold);
        buy3000Gold.setOnClickListener(this);
        freeGold = (Button) view.findViewById(R.id.free_gold);
        freeGold.setOnClickListener(this);
        back = (Button) view.findViewById(R.id.back);
        back.setOnClickListener(this);

        apply = (Button) view.findViewById(R.id.apply);
        apply.setOnClickListener(this);
        silverButton = (Button) view.findViewById(R.id.silver_button);
        silverButton.setOnClickListener(this);
        goldButton = (Button) view.findViewById(R.id.gold_button);
        goldButton.setOnClickListener(this);

        message500Silver = (TextView) view.findViewById(R.id.message_1);
        message1000Silver = (TextView) view.findViewById(R.id.message_2);
        message2000Silver = (TextView) view.findViewById(R.id.message_3);
        message5000Silver = (TextView) view.findViewById(R.id.message_4);
        message10000Silver = (TextView) view.findViewById(R.id.message_5);
        message20000Silver = (TextView) view.findViewById(R.id.message_6);
        message50000Silver = (TextView) view.findViewById(R.id.message_7);

        message10Gold = (TextView) view.findViewById(R.id.message_8);
        message25Gold = (TextView) view.findViewById(R.id.message_9);
        message50Gold = (TextView) view.findViewById(R.id.message_10);
        message100Gold = (TextView) view.findViewById(R.id.message_11);
        message200Gold = (TextView) view.findViewById(R.id.message_12);
        message500Gold = (TextView) view.findViewById(R.id.message_13);
        message1000Gold = (TextView) view.findViewById(R.id.message_14);
        message3000Gold = (TextView) view.findViewById(R.id.message_15);

        freeSilverMessage = (TextView) view.findViewById(R.id.message_18);
        freeGoldMessage = (TextView) view.findViewById(R.id.message_17);

        item1 = view.findViewById(R.id.item_1);
        item2 = view.findViewById(R.id.item_2);
        item3 = view.findViewById(R.id.item_3);
        item4 = view.findViewById(R.id.item_4);
        item5 = view.findViewById(R.id.item_5);
        item6 = view.findViewById(R.id.item_6);
        item7 = view.findViewById(R.id.item_7);
        item8 = view.findViewById(R.id.item_8);
        item9 = view.findViewById(R.id.item_9);
        item10 = view.findViewById(R.id.item_10);
        item11 = view.findViewById(R.id.item_11);
        item12 = view.findViewById(R.id.item_12);
        item13 = view.findViewById(R.id.item_13);
        item14 = view.findViewById(R.id.item_14);
        item15 = view.findViewById(R.id.item_15);
        item17 = view.findViewById(R.id.item_17);
        item18 = view.findViewById(R.id.item_18);

        gold1 = (TextView) view.findViewById(R.id.gold_1);
        gold2 = (TextView) view.findViewById(R.id.gold_2);
        gold3 = (TextView) view.findViewById(R.id.gold_3);
        gold4 = (TextView) view.findViewById(R.id.gold_4);
        gold5 = (TextView) view.findViewById(R.id.gold_5);
        gold6 = (TextView) view.findViewById(R.id.gold_6);
        gold7 = (TextView) view.findViewById(R.id.gold_7);
        gold8 = (TextView) view.findViewById(R.id.gold_8);

        doubleGold = getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getBoolean("double_coins", false);
        if(doubleGold){
            showDoubleGold();
        }
        resetMessages();
        return view;
    }
    public void showDoubleGold(){
        origGold1 = Integer.valueOf(gold1.getText().toString());
        origGold2 = Integer.valueOf(gold2.getText().toString());
        origGold3 = Integer.valueOf(gold3.getText().toString());
        origGold4 = Integer.valueOf(gold4.getText().toString());
        origGold5 = Integer.valueOf(gold5.getText().toString());
        origGold6 = Integer.valueOf(gold6.getText().toString());
        origGold7 = Integer.valueOf(gold7.getText().toString());
        origGold8 = Integer.valueOf(gold8.getText().toString());

        gold1.setText(Integer.toString(origGold1*2));
        gold2.setText(Integer.toString(origGold2*2));
        gold3.setText(Integer.toString(origGold3*2));
        gold4.setText(Integer.toString(origGold4*2));
        gold5.setText(Integer.toString(origGold5*2));
        gold6.setText(Integer.toString(origGold6*2));
        gold7.setText(Integer.toString(origGold7*2));
        gold8.setText(Integer.toString(origGold8*2));

        gold1.setTextColor(Color.GREEN);
        gold2.setTextColor(Color.GREEN);
        gold3.setTextColor(Color.GREEN);
        gold4.setTextColor(Color.GREEN);
        gold5.setTextColor(Color.GREEN);
        gold6.setTextColor(Color.GREEN);
        gold7.setTextColor(Color.GREEN);
        gold8.setTextColor(Color.GREEN);

        populateScrollView("gold");
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
        getDialog().getWindow().setLayout((width / 10) * 9, (height / 10) * 9);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
    public void buttonClick(View button) {
        final View view = button;
        new Thread() {
            @Override
            public void run() {
                if(getDialog() != null && getDialog().getOwnerActivity() != null) {
                    getDialog().getOwnerActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(getActivity() != null) {
                                view.getBackground().setColorFilter(getResources().getColor(R.color.gold_tint), PorterDuff.Mode.SRC_ATOP);
                            }
                        }
                    });
                }
            }
        }.start();
    }
    @Override
    public void onClick(View v){
        String free = getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).getString("FREE", "");
        if(v instanceof Button){
            buttonClick(v);
        }
        switch (v.getId()) {
            case R.id.buy_500_silver:
                PURCHASE_CODE="silver_500";
                if(!PURCHASE_CODE.equals(free)) {
                    convert(Integer.valueOf(buy500Silver.getText().toString()), 500);
                }
                break;
            case R.id.buy_1000_silver:
                PURCHASE_CODE="silver_1000";
                if(!PURCHASE_CODE.equals(free)) {
                    convert(Integer.valueOf(buy1000Silver.getText().toString()), 1000);
                }
                break;
            case R.id.buy_2000_silver:
                PURCHASE_CODE="silver_2000";
                if(!PURCHASE_CODE.equals(free)) {
                    convert(Integer.valueOf(buy2000Silver.getText().toString()), 2000);
                }
                break;
            case R.id.buy_5000_silver:
                PURCHASE_CODE="silver_5000";
                if(!PURCHASE_CODE.equals(free)) {
                    convert(Integer.valueOf(buy5000Silver.getText().toString()), 5000);
                }
                break;
            case R.id.buy_10000_silver:
                PURCHASE_CODE="silver_10000";
                if(!PURCHASE_CODE.equals(free)) {
                    convert(Integer.valueOf(buy10000Silver.getText().toString()), 10000);
                }
                break;
            case R.id.buy_20000_silver:
                PURCHASE_CODE="silver_20000";
                if(!PURCHASE_CODE.equals(free)) {
                    convert(Integer.valueOf(buy20000Silver.getText().toString()), 20000);
                }
                break;
            case R.id.buy_50000_silver:
                PURCHASE_CODE="silver_50000";
                if(!PURCHASE_CODE.equals(free)) {
                    convert(Integer.valueOf(buy50000Silver.getText().toString()), 50000);
                }
                break;
            case R.id.buy_10_gold:
                PURCHASE_CODE = "gold_10";
                break;
            case R.id.buy_25_gold:
                PURCHASE_CODE = "gold_25";
                break;
            case R.id.buy_50_gold:
                PURCHASE_CODE = "gold_50";
                break;
            case R.id.buy_100_gold:
                PURCHASE_CODE = "gold_100";
                break;
            case R.id.buy_200_gold:
                PURCHASE_CODE = "gold_200";
                break;
            case R.id.buy_500_gold:
                PURCHASE_CODE = "gold_500";
                break;
            case R.id.buy_1000_gold:
                PURCHASE_CODE = "gold_1000";
                break;
            case R.id.buy_3000_gold:
                PURCHASE_CODE = "gold_3000";
                break;
            case R.id.silver_button:
                PURCHASE_CODE="";
                populateScrollView("silver");
                break;
            case R.id.gold_button:
                cm = (ConnectivityManager) getDialog().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = cm.getActiveNetworkInfo();
                if(netInfo != null) {
                    if(!bp.isInitialized()){
                        setBillingProcessor();
                    }
                    populateScrollView("gold");
                }else{
                    Toast.makeText(getDialog().getContext(), getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
                }
                PURCHASE_CODE="";
                break;
            case R.id.apply:
                InputMethodManager imm = (InputMethodManager)getDialog().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(promoCode.getWindowToken(), 0);
                netInfo = cm.getActiveNetworkInfo();
                if(promoCode.getText().equals("")){
                    Toast.makeText(getDialog().getContext(), getResources().getString(R.string.promo_code_blank), Toast.LENGTH_SHORT).show();
                }else if(netInfo == null){
                    Toast.makeText(getDialog().getContext(), getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
                }else{
                    applyPromo(promoCode.getText().toString());
                }
                PURCHASE_CODE="";
                break;
            case R.id.free_gold:
                playAd("gold");
                PURCHASE_CODE="";
                break;
            case R.id.free_silver:
                playAd("silver");
                PURCHASE_CODE="";
                break;
            case R.id.back:
                if(listener != null )listener.onCloseDialog();
                dismiss();
                PURCHASE_CODE="";
                break;
            default:
                PURCHASE_CODE = "";
                break;
        }
        if(!PURCHASE_CODE.equals("")){
            User.add("Clicked on "+ PURCHASE_CODE);
        }
        v.getBackground().clearColorFilter();
        if(!PURCHASE_CODE.equals("") && netInfo != null){
            if(PURCHASE_CODE.equals(getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).getString("FREE", ""))){
                User.add("Used promo code: "+getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).getString("FREE", ""));
                switch (PURCHASE_CODE){
                    case "silver_500":
                        sql.addCoins(500, "silver");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(500), Toast.LENGTH_SHORT).show();
                        User.add("Added 500 silver.");
                        break;
                    case "silver_1000":
                        sql.addCoins(1000, "silver");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(1000), Toast.LENGTH_SHORT).show();
                        User.add("Added 1000 silver.");
                        break;
                    case "silver_2000":
                        sql.addCoins(2000, "silver");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(2000), Toast.LENGTH_SHORT).show();
                        User.add("Added 2000 silver.");
                        break;
                    case "silver_5000":
                        sql.addCoins(5000, "silver");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(5000), Toast.LENGTH_SHORT).show();
                        User.add("Added 5000 silver.");
                        break;
                    case "silver_10000":
                        sql.addCoins(10000, "silver");
                        User.add("Added 10000 silver.");
                        break;
                    case "silver_20000":
                        sql.addCoins(20000, "silver");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(20000), Toast.LENGTH_SHORT).show();
                        User.add("Added 20000 silver.");
                        break;
                    case "silver_50000":
                        sql.addCoins(50000, "silver");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(50000), Toast.LENGTH_SHORT).show();
                        User.add("Added 50000 silver.");
                        break;
                    case "gold_10":
                        sql.addCoins(10, "gold");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(10), Toast.LENGTH_SHORT).show();
                        User.add("Added 10 gold.");
                        break;
                    case "gold_25":
                        sql.addCoins(25, "gold");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(25), Toast.LENGTH_SHORT).show();
                        User.add("Added 25 gold.");
                        break;
                    case "gold_50":
                        sql.addCoins(50, "gold");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(50), Toast.LENGTH_SHORT).show();
                        User.add("Added 50 gold.");
                        break;
                    case "gold_100":
                        sql.addCoins(100, "gold");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(100), Toast.LENGTH_SHORT).show();
                        User.add("Added 100 gold.");
                        break;
                    case "gold_200":
                        sql.addCoins(200, "gold");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(200), Toast.LENGTH_SHORT).show();
                        User.add("Added 200 gold.");
                        break;
                    case "gold_500":
                        sql.addCoins(500, "gold");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(500), Toast.LENGTH_SHORT).show();
                        User.add("Added 500 gold.");
                        break;
                    case "gold_1000":
                        sql.addCoins(1000, "gold");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(1000), Toast.LENGTH_SHORT).show();
                        User.add("Added 1000 gold.");
                        break;
                    case "gold_3000":
                        sql.addCoins(3000, "gold");
                        Toast.makeText(getDialog().getOwnerActivity(), "+"+Integer.toString(3000), Toast.LENGTH_SHORT).show();
                        User.add("Added 3000 gold.");
                        break;

                    default:
                }
                Set<String> usedCodes = getDialog().getContext().getSharedPreferences("PROMOCODES",Context.MODE_PRIVATE).getStringSet("USED",null);
                if(usedCodes == null){
                    usedCodes = new HashSet(Arrays.asList(new String[] {usedCode}));
                }else{
                    usedCodes.add(usedCode);
                }
                User.add("Used code: "+ usedCode);
                getDialog().getContext().getSharedPreferences("PROMOCODES",Context.MODE_PRIVATE).edit().putStringSet("USED", usedCodes).apply();
                if(!User.username.equals("")) {
                    ParseService.updateSync("user", false);
                    Intent userIntent = new Intent(getDialog().getContext(), ParseService.class);
                    userIntent.putExtra(ParseService.Object, "user");
                    getDialog().getContext().startService(userIntent);
                }
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "").apply();
                try {
                    Intent purchaseIntent = new Intent(getDialog().getContext(), PurchaseInfo.class);
                    purchaseIntent.putExtra(PurchaseInfo.Object, "Purchase");
                    getDialog().getContext().startService(purchaseIntent);
                } catch (Exception e) {
                }
                if(listener != null)listener.onCloseDialog();
                resetMessages();
                }else if(!PURCHASE_CODE.contains("silver") && !PURCHASE_CODE.equals("")) {
                    User.add("Initiating purchase for "+PURCHASE_CODE);
                if(doubleGold) {
                    getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putBoolean("2x_coins", true).apply();
                }
                bp.purchase(getDialog().getOwnerActivity(), PURCHASE_CODE);
            }else{
            }
        }else if(PURCHASE_CODE.equals("") || PURCHASE_CODE.contains("silver")) {
        }else{
            getDialog().dismiss();
            Toast.makeText(getDialog().getContext(), getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
        }
    }
    public void playAd(String coinType){
        long lastGold = getDialog().getContext().getSharedPreferences("Video", Context.MODE_PRIVATE).getLong("LastGold", 0);
        long lastSilver = getDialog().getContext().getSharedPreferences("Video", Context.MODE_PRIVATE).getLong("LastSilver", 0);
        long now = System.currentTimeMillis();
        cm = (ConnectivityManager) getDialog().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        int minsToGold = (int) Math.round(((Config.VIDEO_HOURS_GOLD * 60 * 60 * 1000) - (now - lastGold)) / 60000.0);
        String minsGold = String.format("%02d", minsToGold%60);
        String hoursGold = String.format("%02d", (Integer.valueOf(minsToGold) / 60));
        int minsToSilver = (int) Math.round(((Config.VIDEO_HOURS_SILVER * 60 * 60 * 1000) - (now - lastSilver)) / 60000.0);
        String minsSilver = String.format("%02d",minsToSilver%60);
        String hoursSilver = String.format("%02d", (Integer.valueOf(minsToSilver) / 60));
        boolean earnGold = (now-lastGold)>(Config.VIDEO_HOURS_GOLD*60*60*1000);
        boolean earnSilver = (now-lastSilver)>(Config.VIDEO_HOURS_SILVER*60*60*1000);
        if(coinType.equals("gold")) {
            if(netInfo == null) {
                Toast.makeText(getDialog().getContext(), getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
            }else if (earnGold && Ad.isIncentiveReady()) {
                getDialog().getContext().getSharedPreferences("Video", Context.MODE_PRIVATE).edit().putString("LastType", "gold").apply();
                Ad.showIncentive();
            } else if(earnGold && !Ad.isIncentiveReady()) {
                Toast.makeText(getDialog().getContext(), getResources().getString(R.string.video_not_available), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getDialog().getContext(), getResources().getString(R.string.earn_more_gold)+" "+(!hoursGold.equals("00") ? (hoursGold+" "+getResources().getString(R.string.hours)+" ") : "")+minsGold+" "+getResources().getString(R.string.mins), Toast.LENGTH_LONG).show();
            }
        }else {
            if(netInfo == null) {
                Toast.makeText(getDialog().getContext(), getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
            }else if (earnSilver && Ad.isIncentiveReady()) {
                getDialog().getContext().getSharedPreferences("Video", Context.MODE_PRIVATE).edit().putString("LastType", "silver").apply();
                Ad.showIncentive();
            } else if(earnSilver && !Ad.isIncentiveReady()) {
                Toast.makeText(getDialog().getContext(), getResources().getString(R.string.video_not_available), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getDialog().getContext(), getResources().getString(R.string.earn_more_silver)+" "+(!hoursSilver.equals("00") ? (hoursSilver+" "+getResources().getString(R.string.hours)+" ") : "")+minsSilver+" "+getResources().getString(R.string.mins), Toast.LENGTH_LONG).show();
            }
        }
    }
    public void getReward(){
        Random rand = new Random();
        String lastType = getDialog().getContext().getSharedPreferences("Video", Context.MODE_PRIVATE).getString("LastType", "silver");
        final int coins = lastType.equals("gold") ? 2 : rand.nextInt(101-1)+1;
        final String coinType = lastType;
        sql.addCoins(coins, coinType);
        if(coinType.equals("gold")){
            getDialog().getContext().getSharedPreferences("Video", Context.MODE_PRIVATE).edit().putLong("LastGold", System.currentTimeMillis()).apply();
        }else if(coinType.equals("silver")){
            getDialog().getContext().getSharedPreferences("Video", Context.MODE_PRIVATE).edit().putLong("LastSilver", System.currentTimeMillis()).apply();
        }
        User.add("Played "+coinType+" video ad.");
        Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.PRICE,coins);
        eventValue.put(AFInAppEventParameterName.CONTENT_TYPE,lastType);
        eventValue.put(AFInAppEventParameterName.CURRENCY, coinType);
        if(getDialog() != null && getDialog().getOwnerActivity() != null) {
            getDialog().getOwnerActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getDialog().getContext(), Integer.toString(coins) + " " + coinType, Toast.LENGTH_SHORT).show();
                    if(listener != null)listener.onCloseDialog();
                }
            });
        }
        if(!User.username.equals("")) {
            ParseService.updateSync("user", false);
            Intent userIntent = new Intent(getDialog().getContext(), ParseService.class);
            userIntent.putExtra(ParseService.Object, "user");
            getDialog().getContext().startService(userIntent);
        }
    }
    public void setCoinButtonBackgrounds(Button button){
        boolean silverPressed = silverButton.getBackground() == getResources().getDrawable(R.drawable.silver_button_pressed);
        boolean goldPressed = goldButton.getBackground() == getResources().getDrawable(R.drawable.gold_button_pressed);

        if(button == silverButton){
            if(!silverPressed){
                silverButton.setBackground(getResources().getDrawable(R.drawable.silver_button_pressed));
                goldButton.setBackground(getResources().getDrawable(R.drawable.gold_button));
            }
        }else{
            if(!goldPressed){
                silverButton.setBackground(getResources().getDrawable(R.drawable.silver_button));
                goldButton.setBackground(getResources().getDrawable(R.drawable.gold_button_pressed));
            }
        }
    }
    public void populateScrollView(String type){
        if(type.equals("silver")){
            scrollViewSilver.scrollTo(0, 0);
            scrollViewGold.setVisibility(View.INVISIBLE);
            scrollViewSilver.setVisibility(View.VISIBLE);
            setCoinButtonBackgrounds(silverButton);

        }else{
            scrollViewGold.scrollTo(0, 0);
            scrollViewSilver.setVisibility(View.INVISIBLE);
            scrollViewGold.setVisibility(View.VISIBLE);
            setCoinButtonBackgrounds(goldButton);
        }
    }
    public void convert(int gold, int silver){
        if(sql.useCoins(gold, "gold")){
            sql.addCoins(silver, "silver");
            User.add("Converted "+Integer.toString(gold)+" gold to "+Integer.toString(silver)+" silver.");
                Map<String, Object> eventValue = new HashMap<String, Object>();
                eventValue.put(AFInAppEventParameterName.PRICE, gold);
                eventValue.put(AFInAppEventParameterName.CONTENT_TYPE,"silver_convert");
                eventValue.put(AFInAppEventParameterName.CURRENCY, "gold");
                AppsFlyerLib.trackEvent(getDialog().getOwnerActivity().getApplicationContext(), "SPENDING", eventValue);
            Toast.makeText(getDialog().getContext(), getResources().getString(R.string.gold_to_silver), Toast.LENGTH_SHORT).show();
            if(listener != null)listener.onCloseDialog();
        }else{
            Toast.makeText(getDialog().getContext(), getResources().getString(R.string.not_enough_gold), Toast.LENGTH_SHORT).show();
        }
    }
    public void applyPromo(String promo){
        resetMessages();
        Set<String> usedCodes = getDialog().getContext().getSharedPreferences("PROMOCODES",Context.MODE_PRIVATE).getStringSet("USED",null);
        if(usedCodes != null && usedCodes.contains(promo)){
            Toast.makeText(getDialog().getContext(), getResources().getString(R.string.promo_code_used), Toast.LENGTH_SHORT).show();
            promoCode.setText("");
        }else {
            if (PromoCodes.FREE_SILVER_500.contains(promo)) {
                message500Silver.setVisibility(View.VISIBLE);
                message500Silver.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "silver_500").apply();
                populateScrollView("silver");
                focusOnView(scrollViewSilver, item1);
                usedCode = promo;
            } else if (PromoCodes.FREE_SILVER_1000.contains(promo)) {
                message1000Silver.setVisibility(View.VISIBLE);
                message1000Silver.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "silver_1000").apply();
                populateScrollView("silver");
                focusOnView(scrollViewSilver, item2);
                usedCode = promo;
            } else if (PromoCodes.FREE_SILVER_2000.contains(promo)) {
                message2000Silver.setVisibility(View.VISIBLE);
                message2000Silver.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "silver_2000").apply();
                populateScrollView("silver");
                focusOnView(scrollViewSilver, item3);
                usedCode = promo;
            } else if (PromoCodes.FREE_SILVER_5000.contains(promo)) {
                message5000Silver.setVisibility(View.VISIBLE);
                message5000Silver.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "silver_5000").apply();
                populateScrollView("silver");
                focusOnView(scrollViewSilver, item4);
                usedCode = promo;
            } else if (PromoCodes.FREE_SILVER_10000.contains(promo)) {
                message10000Silver.setVisibility(View.VISIBLE);
                message10000Silver.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "silver_10000").apply();
                populateScrollView("silver");
                focusOnView(scrollViewSilver, item5);
                usedCode = promo;
            } else if (PromoCodes.FREE_SILVER_20000.contains(promo)) {
                message20000Silver.setVisibility(View.VISIBLE);
                message20000Silver.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "silver_20000").apply();
                populateScrollView("silver");
                focusOnView(scrollViewSilver, item6);
                usedCode = promo;
            } else if (PromoCodes.FREE_SILVER_50000.contains(promo)) {
                message50000Silver.setVisibility(View.VISIBLE);
                message50000Silver.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "silver_50000").apply();
                populateScrollView("silver");
                focusOnView(scrollViewSilver, item7);
                usedCode = promo;
            } else if (PromoCodes.FREE_GOLD_10.contains(promo)) {
                message10Gold.setVisibility(View.VISIBLE);
                message10Gold.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "gold_10").apply();
                populateScrollView("gold");
                focusOnView(scrollViewGold, item8);
                usedCode = promo;
            }else if (PromoCodes.FREE_GOLD_25.contains(promo)) {
                message25Gold.setVisibility(View.VISIBLE);
                message25Gold.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "gold_25").apply();
                populateScrollView("gold");
                focusOnView(scrollViewGold, item9);
                usedCode = promo;
            }else if (PromoCodes.FREE_GOLD_50.contains(promo)) {
                message50Gold.setVisibility(View.VISIBLE);
                message50Gold.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "gold_50").apply();
                populateScrollView("gold");
                focusOnView(scrollViewGold, item10);
                usedCode = promo;
            }else if (PromoCodes.FREE_GOLD_100.contains(promo)) {
                message100Gold.setVisibility(View.VISIBLE);
                message100Gold.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "gold_100").apply();
                populateScrollView("gold");
                focusOnView(scrollViewGold, item11);
                usedCode = promo;
            }else if (PromoCodes.FREE_GOLD_200.contains(promo)) {
                message200Gold.setVisibility(View.VISIBLE);
                message200Gold.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "gold_200").apply();
                populateScrollView("gold");
                focusOnView(scrollViewGold, item12);
                usedCode = promo;
            }else if (PromoCodes.FREE_GOLD_500.contains(promo)) {
                message500Gold.setVisibility(View.VISIBLE);
                message500Gold.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "gold_500").apply();
                populateScrollView("gold");
                focusOnView(scrollViewGold, item13);
                usedCode = promo;
            }else if (PromoCodes.FREE_GOLD_1000.contains(promo)) {
                message1000Gold.setVisibility(View.VISIBLE);
                message1000Gold.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "gold_1000").apply();
                populateScrollView("gold");
                focusOnView(scrollViewGold, item14);
                usedCode = promo;
            }else if (PromoCodes.FREE_GOLD_3000.contains(promo)) {
                message3000Gold.setVisibility(View.VISIBLE);
                message3000Gold.setText(getResources().getString(R.string.free));
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putString("FREE", "gold_3000").apply();
                populateScrollView("gold");
                focusOnView(scrollViewGold, item15);
                usedCode = promo;
            }else if(PromoCodes.CLEAR_USED.contains(promo)) {
                getDialog().getContext().getSharedPreferences("PROMOCODES", Context.MODE_PRIVATE).edit().putStringSet("USED",null).apply();
                Toast.makeText(getDialog().getContext(), "All used codes cleared.", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getDialog().getContext(), getResources().getString(R.string.invalid_promo_code), Toast.LENGTH_SHORT).show();
            }
            promoCode.setText("");
        }
    }
    public void resetMessages(){
        message500Silver.setText(getResources().getString(R.string.free));
        message500Silver.setVisibility(View.INVISIBLE);
        message1000Silver.setText(getResources().getString(R.string.save_10));
        message2000Silver.setText(getResources().getString(R.string.save_25));
        message5000Silver.setText(getResources().getString(R.string.save_30));
        message10000Silver.setText(getResources().getString(R.string.save_40));
        message20000Silver.setText(getResources().getString(R.string.save_50));
        message50000Silver.setText(getResources().getString(R.string.save_60));

        message10Gold.setText(getResources().getString(R.string.free));
        message10Gold.setVisibility(View.INVISIBLE);
        message25Gold.setText(getResources().getString(R.string.save_10));
        message50Gold.setText(getResources().getString(R.string.save_15));
        message100Gold.setText(getResources().getString(R.string.save_25));
        message200Gold.setText(getResources().getString(R.string.save_30));
        message500Gold.setText(getResources().getString(R.string.save_35));
        message1000Gold.setText(getResources().getString(R.string.save_45));
        message3000Gold.setText(getResources().getString(R.string.save_67));
    }
    public void resetDoubleGold(){
        User.add("resetDoubleGold");
        gold1.setText(Integer.toString(origGold1));
        gold2.setText(Integer.toString(origGold2));
        gold3.setText(Integer.toString(origGold3));
        gold4.setText(Integer.toString(origGold4));
        gold5.setText(Integer.toString(origGold5));
        gold6.setText(Integer.toString(origGold6));
        gold7.setText(Integer.toString(origGold7));
        gold8.setText(Integer.toString(origGold8));

        gold1.setTextColor(Color.WHITE);
        gold2.setTextColor(Color.WHITE);
        gold3.setTextColor(Color.WHITE);
        gold4.setTextColor(Color.WHITE);
        gold5.setTextColor(Color.WHITE);
        gold6.setTextColor(Color.WHITE);
        gold7.setTextColor(Color.WHITE);
        gold8.setTextColor(Color.WHITE);

        getActivity().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putBoolean("2x_coins", false).apply();

    }
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        User.add("CoinsDialog Back Press");
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
    private final void focusOnView(final ScrollView scroll, final View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int vTop = view.getTop();
                int vBottom = view.getBottom();
                int sHeight = scroll.getHeight();
                scroll.smoothScrollTo(0, ((vTop + vBottom) / 2) - (sHeight / 2));
            }
        });
    }
    public void setGoldPrices(HashMap<String, String> prices){
        buy10Gold.setText(prices.get("gold_10")==null ? getString(R.string.buy) : prices.get("gold_10"));
        buy25Gold.setText(prices.get("gold_25")==null ? getString(R.string.buy) : prices.get("gold_25"));
        buy50Gold.setText(prices.get("gold_50")==null ? getString(R.string.buy) : prices.get("gold_50"));
        buy100Gold.setText(prices.get("gold_100")==null ? getString(R.string.buy) : prices.get("gold_100"));
        buy200Gold.setText(prices.get("gold_200")==null ? getString(R.string.buy) : prices.get("gold_200"));
        buy500Gold.setText(prices.get("gold_500")==null ? getString(R.string.buy) : prices.get("gold_500"));
        buy1000Gold.setText(prices.get("gold_1000") == null ? getString(R.string.buy) : prices.get("gold_1000"));
        buy3000Gold.setText(prices.get("gold_3000") == null ? getString(R.string.buy) : prices.get("gold_3000"));
    }
    public void setBillingProcessor(){
        bp = new BillingProcessor(getDialog().getContext(), getResources().getString(R.string.license_key), new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String s, TransactionDetails transactionDetails) {
                String state = transactionDetails.purchaseInfo.parseResponseData().purchaseState.name();
                User.add("Product Id = "+transactionDetails.productId);
                User.add("Purchase Token = "+transactionDetails.purchaseToken);
                User.add("Purchase Info Response = "+transactionDetails.purchaseInfo.responseData);
                User.add("Purchase Info Signature = "+transactionDetails.purchaseInfo.signature);
                User.add("Purchase State = "+state);
                User.add("Purchase Time = "+transactionDetails.purchaseInfo.parseResponseData().purchaseTime);
                User.add("Purchase Dev Payload = "+transactionDetails.purchaseInfo.parseResponseData().developerPayload);
                boolean doubleGoldReward = getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getBoolean("2x_coins", false);
                if (state.equals("PurchasedSuccessfully")) {
                    User.add("Product was purchased.");
                    switch (s) {
                        case "gold_10":
                            sql.addCoins(doubleGoldReward ? 20 : 10, "gold");
                            User.add("Added 10 gold");
                            break;
                        case "gold_25":
                            sql.addCoins(doubleGoldReward ? 50 : 25, "gold");
                            User.add("Added 25 gold");
                            break;
                        case "gold_50":
                            sql.addCoins(doubleGoldReward ? 100 : 50, "gold");
                            User.add("Added 50 gold");
                            break;
                        case "gold_100":
                            sql.addCoins(doubleGoldReward ? 200 : 100, "gold");
                            User.add("Added 100 gold");
                            break;
                        case "gold_200":
                            sql.addCoins(doubleGoldReward ? 400 : 200, "gold");
                            User.add("Added 200 gold");
                            break;
                        case "gold_500":
                            sql.addCoins(doubleGoldReward ? 1000 : 500, "gold");
                            User.add("Added 500 gold");
                            break;
                        case "gold_1000":
                            sql.addCoins(doubleGoldReward ? 2000 : 1000, "gold");
                            User.add("Added 1000 gold");
                            break;
                        case "gold_3000":
                            sql.addCoins(doubleGoldReward ? 6000 : 3000, "gold");
                            User.add("Added 3000 gold");
                            break;
                        default:
                    }
                    User.add("Sending AppsFlyer analytics.");
                    Map<String, Object> eventValue = new HashMap<String, Object>();
                    eventValue.put(AFInAppEventParameterName.CONTENT_ID, s);
                    eventValue.put(AFInAppEventParameterName.REVENUE, priceList.get(s));
                    eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, "gold");
                    eventValue.put(AFInAppEventParameterName.CURRENCY, currency);
                    eventValue.put(AFInAppEventParameterName.DESCRIPTION, transactionDetails.orderId + "," + transactionDetails.productId);
                    AppsFlyerLib.trackEvent(getDialog().getOwnerActivity().getApplicationContext(), AFInAppEventType.PURCHASE, eventValue);
                    bp.consumePurchase(transactionDetails.productId);
                    resetDoubleGold();
                    User.add("Consumed Purchase.");
                    try {
                        Intent purchaseIntent = new Intent(getDialog().getContext(), PurchaseInfo.class);
                        purchaseIntent.putExtra(PurchaseInfo.Object, "Purchase");
                        getDialog().getContext().startService(purchaseIntent);
                    } catch (Exception e) {
                    }
                    if (!User.username.equals("")) {
                        ParseService.updateSync("user", false);
                        Intent userIntent = new Intent(getDialog().getContext(), ParseService.class);
                        userIntent.putExtra(ParseService.Object, "user");
                        getDialog().getContext().startService(userIntent);
                    }
                }
                if(listener != null) listener.onCloseDialog();
            }
            @Override
            public void onPurchaseHistoryRestored() {

            }
            @Override
            public void onBillingError(int i, Throwable throwable) {
                if(throwable != null) {
                    User.add("Billing Error: " + throwable.getMessage());
                }else{
                    User.add("Billing Error: Throwable null");
                }
            }
            @Override
            public void onBillingInitialized() {
                HashMap<String, String> prices = new HashMap<>();
                ArrayList<String> skus = new ArrayList<>();
                skus.add("gold_10");
                skus.add("gold_25");
                skus.add("gold_50");
                skus.add("gold_100");
                skus.add("gold_200");
                skus.add("gold_500");
                skus.add("gold_1000");
                skus.add("gold_3000");
                List<SkuDetails> details = bp.getPurchaseListingDetails(skus);
                if(details != null) {
                    for (int i = 0; i < details.size(); i++) {
                        prices.put(details.get(i).productId, details.get(i).priceText);
                        priceList.put(details.get(i).productId, details.get(i).priceValue);
                    }
                    currency = details.get(0).currency;
                }
                setGoldPrices(prices);
            }
        });
    }
    public void onDestroy(){
        super.onDestroy();
        bp.release();
        getActivity().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putBoolean("double_coins", false).apply();
        System.gc();

    }
    public void showFinished(){}
}