package lucky8s.shift;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.chartboost.sdk.Chartboost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static lucky8s.shift.R.id;
import static lucky8s.shift.R.id.back;
import static lucky8s.shift.R.id.coins;
import static lucky8s.shift.R.id.cost_1;
import static lucky8s.shift.R.id.cost_2;
import static lucky8s.shift.R.id.cost_3;
import static lucky8s.shift.R.id.cost_4;
import static lucky8s.shift.R.id.cost_5;
import static lucky8s.shift.R.id.cost_6;
import static lucky8s.shift.R.id.cost_7;
import static lucky8s.shift.R.id.gold;
import static lucky8s.shift.R.string;
import static lucky8s.shift.R.string.perfectionist_cost1;
import static lucky8s.shift.R.string.perfectionist_cost2;
import static lucky8s.shift.R.string.perfectionist_cost3;
import static lucky8s.shift.R.string.perfectionist_cost4;
import static lucky8s.shift.R.string.perfectionist_cost5;
import static lucky8s.shift.R.string.portal_master1_cost;
import static lucky8s.shift.R.string.portal_master2_cost;
import static lucky8s.shift.R.string.portal_master3_cost;
import static lucky8s.shift.R.string.portal_master4_cost;
import static lucky8s.shift.R.string.portal_master5_cost;
import static lucky8s.shift.R.string.space_heater1_cost;
import static lucky8s.shift.R.string.space_heater2_cost;
import static lucky8s.shift.R.string.space_heater3_cost;
import static lucky8s.shift.R.string.space_heater4_cost;
import static lucky8s.shift.R.string.space_heater5_cost;
import static lucky8s.shift.R.string.speed_boost_cost_1;
import static lucky8s.shift.R.string.speed_boost_cost_10;
import static lucky8s.shift.R.string.speed_boost_cost_2;
import static lucky8s.shift.R.string.speed_boost_cost_3;
import static lucky8s.shift.R.string.speed_boost_cost_4;
import static lucky8s.shift.R.string.speed_boost_cost_5;
import static lucky8s.shift.R.string.speed_boost_cost_6;
import static lucky8s.shift.R.string.speed_boost_cost_7;
import static lucky8s.shift.R.string.speed_boost_cost_8;
import static lucky8s.shift.R.string.speed_boost_cost_9;
import static lucky8s.shift.User.add;
import static lucky8s.shift.User.allPacks;
import static lucky8s.shift.User.getUser;
import static lucky8s.shift.User.pro;

/**
 * Created by Christian on 5/19/2015.
 */
public class Store extends FragmentActivity implements DialogListener {

    Context context;
    SoundDriver sd;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    BillingProcessor bp;
    SQL sql;
    CoinsDialog coinsDialog = new CoinsDialog(this);
    AlertDialog alertDialog;
    StoreDialog storeDialog;

    HorizontalScrollView scrollview;

    Button buy1;
    Button buy2;
    Button buy3;
    Button buy4;
    Button buy5;
    Button buy6;
    Button buy7;
    Button coins;
    Button gold;
    Button back;

    TextView message1;
    TextView message2;
    TextView message3;
    TextView message4;
    TextView message5;
    TextView message6;
    TextView message7;

    TextView description1;
    TextView description2;
    TextView description3;
    TextView description4;
    TextView description5;
    TextView description6;
    TextView description7;

    View progress1;
    View progress2;
    View progress3;
    View progress4;
    View progress5;
    View progress6;
    View progress7;

    View item1;
    View item2;
    View item3;
    View item4;
    View item5;
    View item6;
    View item7;

    String PURCHASE_CODE;
    String pack;
    String usedCode;
    String currency;
    String sale;
    String newCost;
    int currentCost;

    double reduction;


    boolean leaving;

    HashMap<String, Double> priceList = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.add("In Store");
        setContentView(R.layout.store);
        Chartboost.onCreate(this);
        Ad.setAct(this);


        context = this;
        sql = new SQL(this);
        sd = MyApplication.getInstance().getSD();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        scrollview = (HorizontalScrollView) findViewById(R.id.scrollview);

        setBillingProcessor();

        buy1 = (Button) findViewById(R.id.cost_1);
        buy1.setOnClickListener(onClickListener);
        buy2 = (Button) findViewById(R.id.cost_2);
        buy2.setOnClickListener(onClickListener);
        buy3 = (Button) findViewById(R.id.cost_3);
        buy3.setOnClickListener(onClickListener);
        buy4 = (Button) findViewById(R.id.cost_4);
        buy4.setOnClickListener(onClickListener);
        buy5 = (Button) findViewById(R.id.cost_5);
        buy5.setOnClickListener(onClickListener);
        buy6 = (Button) findViewById(R.id.cost_6);
        buy6.setOnClickListener(onClickListener);
        buy7 = (Button) findViewById(R.id.cost_7);
        buy7.setOnClickListener(onClickListener);
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(onClickListener);

        coins = (Button) findViewById(R.id.coins);
        coins.setOnClickListener(onClickListener);
        gold = (Button) findViewById(R.id.gold);
        gold.setOnClickListener(onClickListener);

        description1 = (TextView) findViewById(R.id.description_1);
        description2 = (TextView) findViewById(R.id.description_2);
        description3 = (TextView) findViewById(R.id.description_3);
        description4 = (TextView) findViewById(R.id.description_4);
        description5 = (TextView) findViewById(R.id.description_5);
        description6 = (TextView) findViewById(R.id.description_6);
        description7 = (TextView) findViewById(R.id.description_7);

        message1 = (TextView) findViewById(R.id.message_1);
        message2 = (TextView) findViewById(R.id.message_2);
        message3 = (TextView) findViewById(R.id.message_3);
        message4 = (TextView) findViewById(R.id.message_4);
        message5 = (TextView) findViewById(R.id.message_5);
        message6 = (TextView) findViewById(R.id.message_6);
        message7 = (TextView) findViewById(R.id.message_7);

        progress1 = findViewById(R.id.progress_1);
        progress2 = findViewById(R.id.progress_2);
        progress3 = findViewById(R.id.progress_3);
        progress4 = findViewById(R.id.progress_4);
        progress5 = findViewById(R.id.progress_5);
        progress6 = findViewById(R.id.progress_6);
        progress7 = findViewById(R.id.progress_7);

        item1 = findViewById(R.id.item_1);
        item2 = findViewById(R.id.item_2);
        item3 = findViewById(R.id.item_3);
        item4 = findViewById(R.id.item_4);
        item5 = findViewById(R.id.item_5);
        item6 = findViewById(R.id.item_6);
        item7 = findViewById(R.id.item_7);

        int storeVisits = getSharedPreferences("ADS", MODE_PRIVATE).getInt("store_visits", 0);
        getSharedPreferences("ADS", MODE_PRIVATE).edit().putInt("store_visits", storeVisits+1).apply();

        setCoins();
        setDescriptions();
        setCosts();
        sale = getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getString("Sale", "");
        reduction = getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getString("Reduction", "0").equals("") ? 0 : Double.valueOf(forceDecimal(getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getString("Reduction", "0"), "0"));
        if(!sale.equals("")){
            setSaleItem(sale, reduction);
        }
        showDoubleCoinsDialog();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false)
                .setTitle(getResources().getString(R.string.not_enough_coins))
                .setMessage(getResources().getString(R.string.get_more_coins))
                .setPositiveButton(context.getResources().getString(R.string.buy_coins),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final FragmentManager fm = getSupportFragmentManager();
                                if (fm != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (fm != null) {
                                                try {
                                                    fm.executePendingTransactions();
                                                }catch (Exception e){}
                                            }
                                        }
                                    });
                                }
                                DialogFragment dialogg = (DialogFragment) getFragmentManager().findFragmentByTag("coins_dialog");
                                if (dialogg == null) {
                                    if (!coinsDialog.isAdded()) {
                                        coinsDialog.setCancelable(false);
                                        add("Showing CoinsDialog from Store");
                                        coinsDialog.show(fm, "coins_dialog");
                                    }
                                } else if (dialogg.getActivity() != Store.this) {
                                } else {
                                }
                            }
                        })
                        .setNegativeButton(context.getResources().getString(R.string.okay),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

        alertDialog = alertDialogBuilder.create();
    }
    public void showDoubleCoinsDialog(){
        boolean sawCoins = getSharedPreferences("ADS", MODE_PRIVATE).getBoolean("saw_coins", false);
        int visitedStore = getSharedPreferences("ADS", MODE_PRIVATE).getInt("store_visits", 0);
        if(!sawCoins && visitedStore > Config.DOUBLE_COINS_INTERVAL){
            storeDialog = new StoreDialog(StoreDialog.COINS, true, 0, this);
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.double_gold_message)).apply();
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.get_gold)).apply();

            User.add("Showing Coins Dialog");
            FragmentManager fm = getSupportFragmentManager();
            Fragment dialog = getFragmentManager().findFragmentByTag("store_dialog");
            if(dialog == null) {
                if(!storeDialog.isAdded()) {
                    storeDialog.setCancelable(true);
                    storeDialog.show(fm, "store_dialog");
                    getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_coins",true).apply();
                }
            }else if(dialog.getActivity() != Store.this) {
            }else {
            }
        }
    }
    public void onCloseDialog() {
        setCoins();
    }
    public void showCoinsStore(){
        gold.performClick();
    }
    public void setCoins(){
        String numSilver = Integer.toString(sql.getCoins("silver"));
        String numGold = Integer.toString(sql.getCoins("gold"));
        coins.setText(numSilver);
        gold.setText(numGold);
    }
    public String forceDecimal(String str, String failsafe){
        if(str == null || str.equals("nul")){
            return failsafe;//failsafe so no null pointers
        }
        if(str.contains(",")){
            str = str.replace(",", ".");
        }
        return str;

    }
    public void setDescriptions(){
        String currentSpeed = String.format("%.2f",User.speed);
        String currentPortalSpeed = String.format("%.2f",User.portalSpeed);
        String currentPerfectBonusAdd = String.format("%.3f", User.perfectBonusAdd);
        String currentFrozenModifier = String.format("%.2f",User.frozenModifier);

        String speedBoostPercent;
        String portalMasterPercent;
        String perfectBonusAdd;
        String frozenMod;

        Drawable speedProgress;
        Drawable portalProgress;
        Drawable perfectionistProgress;
        Drawable frozenProgress;

        switch (currentSpeed){
            case "1.00":
                speedBoostPercent = "50%";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_0);
                break;
            case "1.50":
                speedBoostPercent = "100%";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_1);
                break;
            case "2.00":
                speedBoostPercent = "125%";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_2);
                break;
            case "2.25":
                speedBoostPercent = "150%";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_3);
                break;
            case "2.50":
                speedBoostPercent = "175%";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_4);
                break;
            case "2.75":
                speedBoostPercent = "200%";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_5);
                break;
            case "3.00":
                speedBoostPercent = "225%";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_6);
                break;
            case "3.25":
                speedBoostPercent = "250%";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_7);
                break;
            case "3.50":
                speedBoostPercent = "300%";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_8);
                break;
            case "4.00":
                speedBoostPercent = "500%";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_9);
                break;
            case "6.00":
                speedBoostPercent = "-";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_10);
                break;
            default:
                speedBoostPercent = "-";
                speedProgress = getResources().getDrawable(R.drawable.progress_bar_10_0);
                break;
        }
        switch (currentPortalSpeed){
            case "1.00":
                portalMasterPercent = "50%";
                portalProgress = getResources().getDrawable(R.drawable.progress_bar_5_0);
                break;
            case "1.50":
                portalMasterPercent = "100%";
                portalProgress = getResources().getDrawable(R.drawable.progress_bar_5_1);
                break;
            case "2.00":
                portalMasterPercent = "125%";
                portalProgress = getResources().getDrawable(R.drawable.progress_bar_5_2);
                break;
            case "2.25":
                portalMasterPercent = "150%";
                portalProgress = getResources().getDrawable(R.drawable.progress_bar_5_3);
                break;
            case "2.50":
                portalMasterPercent = "instant";
                portalProgress = getResources().getDrawable(R.drawable.progress_bar_5_4);
                break;
            case "750.00":
                portalMasterPercent = "-";
                portalProgress = getResources().getDrawable(R.drawable.progress_bar_5_5);
                break;
            default:
                portalMasterPercent = "-";
                portalProgress = getResources().getDrawable(R.drawable.progress_bar_5_0);
        }

        switch (currentPerfectBonusAdd){
            case "0.000":
                perfectBonusAdd = getResources().getString(R.string.percent_5);
                perfectionistProgress = getResources().getDrawable(R.drawable.progress_bar_5_0);
                break;
            case "0.050":
                perfectBonusAdd = getResources().getString(R.string.percent_7_5);
                perfectionistProgress = getResources().getDrawable(R.drawable.progress_bar_5_1);
                break;
            case "0.075":
                perfectBonusAdd = getResources().getString(R.string.percent_10);
                perfectionistProgress = getResources().getDrawable(R.drawable.progress_bar_5_2);
                break;
            case "0.100":
                perfectBonusAdd = getResources().getString(R.string.percent_12_5);
                perfectionistProgress = getResources().getDrawable(R.drawable.progress_bar_5_3);
                break;
            case "0.125":
                perfectBonusAdd = getResources().getString(R.string.percent_20);
                perfectionistProgress = getResources().getDrawable(R.drawable.progress_bar_5_4);
                break;
            case "0.200":
                perfectBonusAdd = "-";
                perfectionistProgress = getResources().getDrawable(R.drawable.progress_bar_5_5);
                break;
            default:
                perfectBonusAdd = "-";
                perfectionistProgress = getResources().getDrawable(R.drawable.progress_bar_5_0);
                break;
        }
        switch (currentFrozenModifier){
            case "1.00":
                frozenMod = "50%";
                frozenProgress = getResources().getDrawable(R.drawable.progress_bar_5_0);
                break;
            case "1.50":
                frozenMod = "100%";
                frozenProgress = getResources().getDrawable(R.drawable.progress_bar_5_1);
                break;
            case "2.00":
                frozenMod = "125%";
                frozenProgress = getResources().getDrawable(R.drawable.progress_bar_5_2);
                break;
            case "2.25":
                frozenMod = "150%";
                frozenProgress = getResources().getDrawable(R.drawable.progress_bar_5_3);
                break;
            case "2.50":
                frozenMod = "500%";
                frozenProgress = getResources().getDrawable(R.drawable.progress_bar_5_4);
                break;
            case "6.00":
                frozenMod = "-";
                frozenProgress = getResources().getDrawable(R.drawable.progress_bar_5_5);
                break;
            default:
                frozenMod = "-";
                frozenProgress = getResources().getDrawable(R.drawable.progress_bar_5_0);
        }
        if(speedBoostPercent.contains("-")){
            description1.setText(getResources().getString(R.string.purchased));
        }else {
            description1.setText(getResources().getString(R.string.speed_boost_description1) + " " + speedBoostPercent + " " + getResources().getString(R.string.speed_boost_description2));
        }

        if(portalMasterPercent.contains("-")){
            description2.setText(getResources().getString(R.string.purchased));
        }else if(portalMasterPercent.equals("instant")) {
            description2.setText(getResources().getString(R.string.portal_master_description1)+" "+getResources().getString(R.string.portal_master_description2));
        }else{
            description2.setText(getResources().getString(R.string.portal_master_description1) + " " + portalMasterPercent + " " + getResources().getString(R.string.portal_master_description2));
        }

        if(perfectBonusAdd.contains("-")){
            description3.setText(getResources().getString(R.string.purchased));
        }else {
            description3.setText(getResources().getString(R.string.perfectionist_description1) + " " + perfectBonusAdd + " " + getResources().getString(R.string.perfectionist_description2));
        }
        if(frozenMod.equals("-")){
            description7.setText(getResources().getString(R.string.purchased));
        }else{
            description7.setText(getResources().getString(R.string.space_heater_description1)+" "+frozenMod+" "+getResources().getString(R.string.space_heater_description2));
        }


        if(User.pro){
            description6.setText(getResources().getString(R.string.purchased));
            description5.setText(getResources().getString(R.string.purchased));
            description4.setText(getResources().getString(R.string.purchased));
            progress4.setBackground(getResources().getDrawable(R.drawable.progress_bar_1_1));
            progress5.setBackground(getResources().getDrawable(R.drawable.progress_bar_1_1));
            progress6.setBackground(getResources().getDrawable(R.drawable.progress_bar_1_1));
        }
        if(User.allPacks){
            description5.setText(getResources().getString(R.string.purchased));
            progress5.setBackground(getResources().getDrawable(R.drawable.progress_bar_1_1));
        }
        if(sql.getSingleResult("user", "no_ads", " where username = '"+User.getUser()+"' ").contains("all")){
            description4.setText(getResources().getString(R.string.purchased));
            progress4.setBackground(getResources().getDrawable(R.drawable.progress_bar_1_1));
        }

        progress1.setBackground(speedProgress);
        progress2.setBackground(portalProgress);
        progress3.setBackground(perfectionistProgress);
        progress7.setBackground(frozenProgress);
    }
    public void setCosts(){
        String currentSpeed = String.format("%.2f",User.speed);
        String currentPortalSpeed = String.format("%.2f",User.portalSpeed);
        String currentPerfectBonusAdd = String.format("%.3f",User.perfectBonusAdd);
        String currentFrozenModifier = String.format("%.2f",User.frozenModifier);
        String coins1;
        String coins2;
        String coins3;
        String coins7;
        if(sale == null || !sale.equals(StoreDialog.SPEED_BOOST)) {
            switch (currentSpeed) {
                case "1.00":
                    coins1 = getResources().getString(R.string.speed_boost_cost_1);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "1.50":
                    coins1 = getResources().getString(R.string.speed_boost_cost_2);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "2.00":
                    coins1 = getResources().getString(R.string.speed_boost_cost_3);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "2.25":
                    coins1 = getResources().getString(R.string.speed_boost_cost_4);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "2.50":
                    coins1 = getResources().getString(R.string.speed_boost_cost_5);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "2.75":
                    coins1 = getResources().getString(R.string.speed_boost_cost_6);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "3.00":
                    coins1 = getResources().getString(R.string.speed_boost_cost_7);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "3.25":
                    coins1 = getResources().getString(R.string.speed_boost_cost_8);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "3.50":
                    coins1 = getResources().getString(R.string.speed_boost_cost_9);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "4.00":
                    coins1 = getResources().getString(R.string.speed_boost_cost_10);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gold_coin, 0);
                    break;
                case "6.00":
                    coins1 = getResources().getString(R.string.maxed);
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gold_coin, 0);
                    break;
                default:
                    coins1 = " - ";
                    buy1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gold_coin, 0);
                    break;
            }
            buy1.setText(coins1);
        }
        if(sale == null || !sale.equals(StoreDialog.PORTAL_MASTER)) {
            switch (currentPortalSpeed) {
                case "1.00":
                    coins2 = getResources().getString(R.string.portal_master1_cost);
                    buy2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "1.50":
                    coins2 = getResources().getString(R.string.portal_master2_cost);
                    buy2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "2.00":
                    coins2 = getResources().getString(R.string.portal_master3_cost);
                    buy2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "2.25":
                    coins2 = getResources().getString(R.string.portal_master4_cost);
                    buy2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "2.50":
                    coins2 = getResources().getString(R.string.portal_master5_cost);
                    buy2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gold_coin, 0);
                    break;
                case "750.00":
                    coins2 = getResources().getString(R.string.maxed);
                    buy2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gold_coin, 0);
                    break;
                default:
                    coins2 = " - ";
                    break;
            }
            buy2.setText(coins2);
        }
        if(sale == null || !sale.equals(StoreDialog.PERFECTIONIST)) {
            switch (currentPerfectBonusAdd) {
                case "0.000":
                    coins3 = getResources().getString(R.string.perfectionist_cost1);
                    buy3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "0.050":
                    coins3 = getResources().getString(R.string.perfectionist_cost2);
                    buy3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "0.075":
                    coins3 = getResources().getString(R.string.perfectionist_cost3);
                    buy3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "0.100":
                    coins3 = getResources().getString(R.string.perfectionist_cost4);
                    buy3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "0.125":
                    coins3 = getResources().getString(R.string.perfectionist_cost5);
                    buy3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gold_coin, 0);
                    break;
                case "0.200":
                    coins3 = getResources().getString(R.string.maxed);
                    buy3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gold_coin, 0);
                    break;
                default:
                    coins3 = " - ";
                    break;
            }
            buy3.setText(coins3);
        }
        if(sale == null || !sale.equals(StoreDialog.SPACE_HEATER)) {
            switch (currentFrozenModifier) {
                case "1.00":
                    coins7 = getString(R.string.space_heater1_cost);
                    buy7.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "1.50":
                    coins7 = getString(R.string.space_heater2_cost);
                    buy7.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "2.00":
                    coins7 = getString(R.string.space_heater3_cost);
                    buy7.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "2.25":
                    coins7 = getString(R.string.space_heater4_cost);
                    buy7.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.silver_coin, 0);
                    break;
                case "2.50":
                    coins7 = getString(R.string.space_heater5_cost);
                    buy7.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gold_coin, 0);
                    break;
                case "6.00":
                    coins7 = getString(R.string.maxed);
                    buy7.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gold_coin, 0);
                    break;
                default:
                    coins7 = " - ";
                    break;
            }
            buy7.setText(coins7);
        }

        if(User.allPacks)buy5.setText(getResources().getString(R.string.maxed));
        if(User.pro)buy6.setText(getString(R.string.maxed));
        if(sql.getSingleResult("user", "no_ads", " where username = '"+User.getUser()+"' ").contains("all")){
            buy4.setText(getResources().getString(R.string.maxed));
        }

    }
    public void onResume(){
        super.onResume();
        Chartboost.onResume(this);
        AppsFlyerLib.onActivityResume(this);
        if(!sd.isPlaying("homeBackground")) {
            sd.stop();
            sd.homeBackground();
        }
        leaving = false;
        setCoins();//set coins from coin dialog
    }
    public void buttonClick(View button) {
        final View view = button;
        new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.getBackground().setColorFilter(getResources().getColor(R.color.gold_tint), PorterDuff.Mode.SRC_ATOP);
                    }
                });
                try {
                    Thread.sleep(100);
                }catch (Exception e){}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.getBackground().clearColorFilter();
                    }
                });
            }
        }.start();
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if(view instanceof Button){
                buttonClick(view);
            }
            PURCHASE_CODE = "";
            switch (view.getId()) {
                case R.id.cost_1:
                    add("Clicked cost_1");
                    TextView temp = (TextView) findViewById(view.getId());
                    String tempText = temp.getText().toString();
                    if (tempText.equals(getResources().getString(speed_boost_cost_1)) || (sale.equals(StoreDialog.SPEED_BOOST) && Integer.toString(currentCost).equals(getString(speed_boost_cost_1)))){
                        PURCHASE_CODE = "speed1";
                    } else if (tempText.equals(getResources().getString(speed_boost_cost_2)) || (sale.equals(StoreDialog.SPEED_BOOST) && Integer.toString(currentCost).equals(getString(speed_boost_cost_2)))) {
                        PURCHASE_CODE = "speed2";
                    } else if (tempText.equals(getResources().getString(speed_boost_cost_3)) || (sale.equals(StoreDialog.SPEED_BOOST) && Integer.toString(currentCost).equals(getString(speed_boost_cost_3)))) {
                        PURCHASE_CODE = "speed3";
                    } else if (tempText.equals(getResources().getString(speed_boost_cost_4)) || (sale.equals(StoreDialog.SPEED_BOOST) && Integer.toString(currentCost).equals(getString(speed_boost_cost_4)))) {
                        PURCHASE_CODE = "speed4";
                    } else if (tempText.equals(getResources().getString(speed_boost_cost_5)) || (sale.equals(StoreDialog.SPEED_BOOST) && Integer.toString(currentCost).equals(getString(speed_boost_cost_5)))) {
                        PURCHASE_CODE = "speed5";
                    } else if (tempText.equals(getResources().getString(speed_boost_cost_6)) || (sale.equals(StoreDialog.SPEED_BOOST) && Integer.toString(currentCost).equals(getString(speed_boost_cost_6)))) {
                        PURCHASE_CODE = "speed6";
                    } else if (tempText.equals(getResources().getString(speed_boost_cost_7)) || (sale.equals(StoreDialog.SPEED_BOOST) && Integer.toString(currentCost).equals(getString(speed_boost_cost_7)))) {
                        PURCHASE_CODE = "speed7";
                    } else if (tempText.equals(getResources().getString(speed_boost_cost_8)) || (sale.equals(StoreDialog.SPEED_BOOST) && Integer.toString(currentCost).equals(getString(speed_boost_cost_8)))) {
                        PURCHASE_CODE = "speed8";
                    } else if (tempText.equals(getResources().getString(speed_boost_cost_9)) || (sale.equals(StoreDialog.SPEED_BOOST) && Integer.toString(currentCost).equals(getString(speed_boost_cost_9)))) {
                        PURCHASE_CODE = "speed9";
                    } else if (tempText.equals(getResources().getString(speed_boost_cost_10)) || (sale.equals(StoreDialog.SPEED_BOOST) && Integer.toString(currentCost).equals(getString(speed_boost_cost_10)))) {
                        PURCHASE_CODE = "speed10";
                    }
                    break;
                case R.id.cost_2:
                    add("Clicked cost_2");
                    TextView temp2 = (TextView) findViewById(view.getId());
                    String tempText2 = temp2.getText().toString();
                    if (tempText2.equals(getResources().getString(portal_master1_cost)) || (sale.equals(StoreDialog.PORTAL_MASTER) && Integer.toString(currentCost).equals(getString(portal_master1_cost)))) {
                        PURCHASE_CODE = "portal_master1";
                    } else if (tempText2.equals(getResources().getString(portal_master2_cost)) || (sale.equals(StoreDialog.PORTAL_MASTER) && Integer.toString(currentCost).equals(getString(portal_master2_cost)))) {
                        PURCHASE_CODE = "portal_master2";
                    } else if (tempText2.equals(getResources().getString(portal_master3_cost)) || (sale.equals(StoreDialog.PORTAL_MASTER) && Integer.toString(currentCost).equals(getString(portal_master3_cost)))) {
                        PURCHASE_CODE = "portal_master3";
                    } else if (tempText2.equals(getResources().getString(portal_master4_cost)) || (sale.equals(StoreDialog.PORTAL_MASTER) && Integer.toString(currentCost).equals(getString(portal_master4_cost)))) {
                        PURCHASE_CODE = "portal_master4";
                    } else if (tempText2.equals(getResources().getString(portal_master5_cost)) || (sale.equals(StoreDialog.PORTAL_MASTER) && Integer.toString(currentCost).equals(getString(portal_master5_cost)))) {
                        PURCHASE_CODE = "portal_master5";
                    }
                    break;
                case R.id.cost_3:
                    add("Clicked cost_3");
                    TextView temp3 = (TextView) findViewById(view.getId());
                    String tempText3 = temp3.getText().toString();
                    if (tempText3.equals(getResources().getString(perfectionist_cost1)) || (sale.equals(StoreDialog.PERFECTIONIST) && Integer.toString(currentCost).equals(getString(perfectionist_cost1)))) {
                        PURCHASE_CODE = "perfectionist1";
                    } else if (tempText3.equals(getResources().getString(perfectionist_cost2)) || (sale.equals(StoreDialog.PERFECTIONIST) && Integer.toString(currentCost).equals(getString(perfectionist_cost2)))) {
                        PURCHASE_CODE = "perfectionist2";
                    } else if (tempText3.equals(getResources().getString(perfectionist_cost3)) || (sale.equals(StoreDialog.PERFECTIONIST) && Integer.toString(currentCost).equals(getString(perfectionist_cost3)))) {
                        PURCHASE_CODE = "perfectionist3";
                    } else if (tempText3.equals(getResources().getString(perfectionist_cost4)) || (sale.equals(StoreDialog.PERFECTIONIST) && Integer.toString(currentCost).equals(getString(perfectionist_cost4)))) {
                        PURCHASE_CODE = "perfectionist4";
                    } else if (tempText3.equals(getResources().getString(perfectionist_cost5)) || (sale.equals(StoreDialog.PERFECTIONIST) && Integer.toString(currentCost).equals(getString(perfectionist_cost5)))) {
                        PURCHASE_CODE = "perfectionist5";
                    }
                    break;
                case R.id.cost_4:
                    add("Clicked cost_4");
                    if (!sql.getSingleResult("user", "no_ads", " where username = '" + getUser() + "' ").contains("all")) {
                        PURCHASE_CODE = "no_ads";
                    }
                    break;
                case R.id.cost_5:
                    add("Clicked cost_5");
                    if (!allPacks) {
                        PURCHASE_CODE = "all_packs";
                    }
                    break;
                case R.id.cost_6:
                    add("Clicked cost_6");
                    if (!pro) {
                        PURCHASE_CODE = "pro";
                    }
                    break;
                case R.id.cost_7:
                    add("Clicked cost_7");
                    TextView temp7 = (TextView) findViewById(view.getId());
                    String tempText7 = temp7.getText().toString();
                    if (tempText7.equals(getResources().getString(space_heater1_cost)) || (sale.equals(StoreDialog.SPACE_HEATER) && Integer.toString(currentCost).equals(getString(space_heater1_cost)))) {
                        PURCHASE_CODE = "space_heater1";
                    } else if (tempText7.equals(getResources().getString(space_heater2_cost)) || (sale.equals(StoreDialog.SPACE_HEATER) && Integer.toString(currentCost).equals(getString(space_heater2_cost)))) {
                        PURCHASE_CODE = "space_heater2";
                    } else if (tempText7.equals(getResources().getString(space_heater3_cost)) || (sale.equals(StoreDialog.SPACE_HEATER) && Integer.toString(currentCost).equals(getString(space_heater3_cost)))) {
                        PURCHASE_CODE = "space_heater3";
                    } else if (tempText7.equals(getResources().getString(space_heater4_cost)) || (sale.equals(StoreDialog.SPACE_HEATER) && Integer.toString(currentCost).equals(getString(space_heater4_cost)))) {
                        PURCHASE_CODE = "space_heater4";
                    } else if (tempText7.equals(getResources().getString(space_heater5_cost)) || (sale.equals(StoreDialog.SPACE_HEATER) && Integer.toString(currentCost).equals(getString(space_heater5_cost)))) {
                        PURCHASE_CODE = "space_heater5";
                    }
                    break;
                case R.id.gold:
                case R.id.coins:
                    final FragmentManager fm = getSupportFragmentManager();
                    if (fm != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (fm != null) {
                                    fm.executePendingTransactions();
                                }
                            }
                        });
                    }
                    DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag("coins_dialog");
                    if (dialog == null) {
                        if (!coinsDialog.isAdded()) {
                            coinsDialog.setCancelable(false);
                            User.add("Going to CoinsDialog from Store");
                            coinsDialog.show(fm, "coins_dialog");
                        }
                    } else if (dialog.getActivity() != Store.this) {
                    } else {
                    }
                    break;
                case R.id.back:
                    onBackPressed();
                    break;
            }
            if(!PURCHASE_CODE.equals("")) {
                User.add("Clicked to purchase " + PURCHASE_CODE);
            }
            cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
            if(!PURCHASE_CODE.equals("") && netInfo != null && (view.getId() != R.id.coins) && (view.getId() != R.id.back)){
                    boolean enoughCoins = false;
                    int cost = 0;
                    String costType = "";
                    switch (PURCHASE_CODE){
                        case "speed1":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPEED_BOOST) ? newCost :getResources().getString(R.string.speed_boost_cost_1)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set speed = '1.5' ");
                                User.speed = 1.5;
                                cost  = Integer.valueOf(getString(R.string.speed_boost_cost_1));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "speed2":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPEED_BOOST) ? newCost :getResources().getString(R.string.speed_boost_cost_2)), "silver")){
                                sql.update("purchases", " where username = '"+User.getUser()+"' "," set speed = '2.0' ");
                                User.speed = 2.0;
                                cost  = Integer.valueOf(getString(R.string.speed_boost_cost_2));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "speed3":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPEED_BOOST) ? newCost :getResources().getString(R.string.speed_boost_cost_3)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set speed = '2.25' ");
                                User.speed = 2.25;
                                cost  = Integer.valueOf(getString(R.string.speed_boost_cost_3));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "speed4":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPEED_BOOST) ? newCost :getResources().getString(R.string.speed_boost_cost_4)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set speed = '2.5' ");
                                User.speed = 2.5;
                                cost  = Integer.valueOf(getString(R.string.speed_boost_cost_4));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "speed5":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPEED_BOOST) ? newCost :getResources().getString(R.string.speed_boost_cost_5)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set speed = '2.75' ");
                                User.speed = 2.75;
                                cost  = Integer.valueOf(getString(R.string.speed_boost_cost_5));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "speed6":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPEED_BOOST) ? newCost :getResources().getString(R.string.speed_boost_cost_6)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set speed = '3.0' ");
                                User.speed = 3.0;
                                cost  = Integer.valueOf(getString(R.string.speed_boost_cost_6));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "speed7":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPEED_BOOST) ? newCost :getResources().getString(R.string.speed_boost_cost_7)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set speed = '3.25' ");
                                User.speed = 3.25;
                                cost  = Integer.valueOf(getString(R.string.speed_boost_cost_7));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "speed8":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPEED_BOOST) ? newCost :getResources().getString(R.string.speed_boost_cost_8)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set speed = '3.5' ");
                                User.speed = 3.5;
                                cost  = Integer.valueOf(getString(R.string.speed_boost_cost_8));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "speed9":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPEED_BOOST) ? newCost :getResources().getString(R.string.speed_boost_cost_9)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set speed = '4.0' ");
                                User.speed = 4.0;
                                cost  = Integer.valueOf(getString(R.string.speed_boost_cost_9));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "speed10":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPEED_BOOST) ? newCost :getResources().getString(R.string.speed_boost_cost_10)), "gold")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set speed = '6.0' ");
                                User.speed = 6.0;
                                cost  = Integer.valueOf(getString(R.string.speed_boost_cost_10));
                                costType = "gold";
                                enoughCoins = true;
                            }
                            break;
                        case "pro":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PRO_BUNDLE) ? newCost :getResources().getString(R.string.pro_bundle_cost)), "gold")){
                                sql.update("purchases", " where username = '"+User.getUser()+"' "," set pro = 1 ");
                                sql.update("purchases", " where username = '"+User.getUser()+"' "," set all_packs = 1 ");
                                sql.update("user", " where username = '" + User.getUser() + "' ", " set no_ads = 'all' ");
                                sql.update("user", " where username = '" + User.getUser() + "' ", " set hints = -1 ");
                                User.allPacks = true;
                                User.pro = true;
                                cost  = Integer.valueOf(getString(R.string.pro_bundle_cost));
                                costType = "gold";
                                enoughCoins = true;
                                Toast.makeText(context, getResources().getString(R.string.pro_congratulations), Toast.LENGTH_LONG).show();
                            }
                            break;
                        case "all_packs":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.ALL_PACKS_BUNDLE) ? newCost :getResources().getString(R.string.all_packs_bundle_cost)), "gold")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set all_packs = 1 ");
                                User.allPacks = true;
                                cost  = Integer.valueOf(getString(R.string.all_packs_bundle_cost));
                                costType = "gold";
                                enoughCoins = true;
                                Toast.makeText(context, getResources().getString(R.string.all_packs_unlocked), Toast.LENGTH_LONG).show();
                            }
                            break;
                        case "no_ads":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.NO_ADS_BUNDLE) ? newCost :getResources().getString(R.string.no_ads_bundle_cost)), "gold")) {
                                sql.update("user", " where username = '" + User.getUser() + "' ", " set no_ads = 'all' ");
                                cost  = Integer.valueOf(getString(R.string.no_ads_bundle_cost));
                                costType = "gold";
                                enoughCoins = true;
                                Toast.makeText(context, getResources().getString(R.string.ads_removed), Toast.LENGTH_LONG).show();
                            }
                            break;
                        case "portal_master1":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PORTAL_MASTER) ? newCost :getResources().getString(R.string.portal_master1_cost)), "silver")){
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set portal_speed = '1.5' ");
                                User.portalSpeed = 1.5;
                                cost  = Integer.valueOf(getString(R.string.portal_master1_cost));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "portal_master2":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PORTAL_MASTER) ? newCost :getResources().getString(R.string.portal_master2_cost)), "silver")){
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set portal_speed = '2.0' ");
                                User.portalSpeed = 2.0;
                                cost  = Integer.valueOf(getString(R.string.portal_master2_cost));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "portal_master3":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PORTAL_MASTER) ? newCost :getResources().getString(R.string.portal_master3_cost)), "silver")){
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set portal_speed = '2.25' ");
                                User.portalSpeed = 2.25;
                                cost  = Integer.valueOf(getString(R.string.portal_master3_cost));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "portal_master4":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PORTAL_MASTER) ? newCost :getResources().getString(R.string.portal_master4_cost)), "silver")){
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set portal_speed = '2.5' ");
                                User.portalSpeed = 2.5;
                                cost  = Integer.valueOf(getString(R.string.portal_master4_cost));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "portal_master5":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PORTAL_MASTER) ? newCost :getResources().getString(R.string.portal_master5_cost)), "gold")){
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set portal_speed = '750.0' ");
                                User.portalSpeed = 750.0;
                                cost  = Integer.valueOf(getString(R.string.portal_master5_cost));
                                costType = "gold";
                                enoughCoins = true;
                                Toast.makeText(context, getResources().getString(R.string.portal_time_instant), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "perfectionist1":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PERFECTIONIST) ? newCost :getResources().getString(R.string.perfectionist_cost1)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set perfect_bonus_add = '0.05' ");
                                User.perfectBonusAdd = 0.05;
                                cost  = Integer.valueOf(getString(R.string.perfectionist_cost1));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "perfectionist2":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PERFECTIONIST) ? newCost :getResources().getString(R.string.perfectionist_cost2)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set perfect_bonus_add = '0.075' ");
                                User.perfectBonusAdd = 0.075;
                                cost  = Integer.valueOf(getString(R.string.perfectionist_cost2));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "perfectionist3":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PERFECTIONIST) ? newCost :getResources().getString(R.string.perfectionist_cost3)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set perfect_bonus_add = '0.10' ");
                                User.perfectBonusAdd = 0.1;
                                cost  = Integer.valueOf(getString(R.string.perfectionist_cost3));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "perfectionist4":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PERFECTIONIST) ? newCost :getResources().getString(R.string.perfectionist_cost4)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set perfect_bonus_add = '0.125' ");
                                User.perfectBonusAdd = 0.125;
                                cost  = Integer.valueOf(getString(R.string.perfectionist_cost4));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "perfectionist5":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.PERFECTIONIST) ? newCost :getResources().getString(R.string.perfectionist_cost5)), "gold")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set perfect_bonus_add = '0.20' ");
                                User.perfectBonusAdd = 0.2;
                                cost  = Integer.valueOf(getString(R.string.perfectionist_cost5));
                                costType = "gold";
                                enoughCoins = true;
                            }
                            break;
                        case "space_heater1":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPACE_HEATER) ? newCost :getResources().getString(R.string.space_heater1_cost)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set frozen_mod = '1.50' ");
                                User.frozenModifier = 1.50;
                                cost  = Integer.valueOf(getString(R.string.space_heater1_cost));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "space_heater2":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPACE_HEATER) ? newCost :getResources().getString(R.string.space_heater2_cost)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set frozen_mod = '2.00' ");
                                User.frozenModifier = 2.00;
                                cost  = Integer.valueOf(getString(R.string.space_heater2_cost));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "space_heater3":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPACE_HEATER) ? newCost :getResources().getString(R.string.space_heater3_cost)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set frozen_mod = '2.25' ");
                                User.frozenModifier = 2.25;
                                cost  = Integer.valueOf(getString(R.string.space_heater3_cost));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "space_heater4":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPACE_HEATER) ? newCost :getResources().getString(R.string.space_heater4_cost)), "silver")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set frozen_mod = '2.50' ");
                                User.frozenModifier = 2.50;
                                cost  = Integer.valueOf(getString(R.string.space_heater4_cost));
                                costType = "silver";
                                enoughCoins = true;
                            }
                            break;
                        case "space_heater5":
                            if(sql.useCoins(Integer.valueOf(sale.equals(StoreDialog.SPACE_HEATER) ? newCost :getResources().getString(R.string.space_heater5_cost)), "gold")) {
                                sql.update("purchases", " where username = '" + User.getUser() + "' ", " set frozen_mod = '6.00' ");
                                User.frozenModifier = 6.00;
                                cost  = Integer.valueOf(getString(R.string.space_heater5_cost));
                                costType = "gold";
                                enoughCoins = true;
                            }
                            break;
                        default:
                            break;
                    }
                    if(!enoughCoins){
                        User.add("Store-not enough coins");
                        alertDialog.show();
                        if(alertDialog.isShowing()) {
                            int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                            View divider = alertDialog.findViewById(dividerId);
                            divider.setBackgroundColor(getResources().getColor(R.color.gold));
                        }
                    }else{
                        User.add("Purchased "+PURCHASE_CODE);
                        if(PURCHASE_CODE.contains("speed")){
                            Toast.makeText(context, getResources().getString(R.string.speed_increased)+" "+ Integer.toString((int)(User.speed * 100))+"%!", Toast.LENGTH_SHORT).show();
                        }else if(PURCHASE_CODE.contains("portal") && !PURCHASE_CODE.equals("portal_master5")){
                            Toast.makeText(context, getResources().getString(R.string.portal_time_decreased_by)+" "+ Integer.toString((int)((User.portalSpeed-1) * 100))+"%!", Toast.LENGTH_SHORT).show();
                        }else if(PURCHASE_CODE.contains("perfectionist")){
                            Toast.makeText(context, getResources().getString(R.string.perfect_levels_add)+" "+String.format("%.1f",User.perfectBonusAdd * 100.0)+"% "+getResources().getString(R.string.to_perfect_bonus_modifier), Toast.LENGTH_SHORT).show();
                        }else if(PURCHASE_CODE.contains("heater")){
                            Toast.makeText(context, getResources().getString(R.string.frozen_time_decreased_by)+" "+ Integer.toString((int) ((User.frozenModifier - 1.0) * 100))+"%!", Toast.LENGTH_SHORT).show();
                        }
                        Map<String, Object> eventValue = new HashMap<String, Object>();
                        eventValue.put(AFInAppEventParameterName.PRICE,cost);
                        eventValue.put(AFInAppEventParameterName.CONTENT_TYPE,PURCHASE_CODE);
                        eventValue.put(AFInAppEventParameterName.PARAM_1, costType);
                        AppsFlyerLib.trackEvent(getApplicationContext(), "SPENDING", eventValue);
                        setDescriptions();
                        if(PURCHASE_CODE.contains("speed") && sale.equals(StoreDialog.SPEED_BOOST)){
                            resetSale();
                        }else if(PURCHASE_CODE.contains("portal") && sale.equals(StoreDialog.PORTAL_MASTER)){
                            resetSale();
                        }else if(PURCHASE_CODE.contains("heater") && sale.equals(StoreDialog.SPACE_HEATER)){
                            resetSale();
                        }else if(PURCHASE_CODE.contains("pro") && sale.equals(StoreDialog.PRO_BUNDLE)){
                            resetSale();
                        }else if(PURCHASE_CODE.contains("ads") && sale.equals(StoreDialog.NO_ADS_BUNDLE)){
                            resetSale();
                        }else if(PURCHASE_CODE.contains("packs") && sale.equals(StoreDialog.ALL_PACKS_BUNDLE)){
                            resetSale();
                        }else if(PURCHASE_CODE.contains("perfectionist") && sale.equals(StoreDialog.PERFECTIONIST)){
                            resetSale();
                        }
                        setCosts();
                        setCoins();
                    }
                    if(!User.username.equals("")) {
                        Intent userIntent = new Intent(context, ParseService.class);
                        userIntent.putExtra(ParseService.Object, "user");
                        startService(userIntent);

                        Intent purchasesIntent = new Intent(context, ParseService.class);
                        purchasesIntent.putExtra(ParseService.Object, "purchases");
                        startService(purchasesIntent);
                    }
            }else if(PURCHASE_CODE.equals("") || (view.getId() == R.id.coins) || (view.getId() == R.id.back)) {
            }else{
                Toast.makeText(context, getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
            }
        }
    };
    public void resetSale(){
        currentCost = 0;
        newCost = "";
        sale = "";
        reduction = 0;
        getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Sale", "").apply();
        getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Reduction", "").apply();
        resetMessages();
    }
    public void resetMessages(){
        message1.setVisibility(View.INVISIBLE);
        buy1.setTextColor(getResources().getColor(R.color.white));
        message2.setVisibility(View.INVISIBLE);
        buy2.setTextColor(getResources().getColor(R.color.white));
        message3.setVisibility(View.INVISIBLE);
        buy3.setTextColor(getResources().getColor(R.color.white));
        message4.setVisibility(View.INVISIBLE);
        buy4.setTextColor(getResources().getColor(R.color.white));
        message5.setVisibility(View.INVISIBLE);
        buy5.setTextColor(getResources().getColor(R.color.white));
        message6.setVisibility(View.INVISIBLE);
        buy6.setTextColor(getResources().getColor(R.color.white));
        message7.setVisibility(View.INVISIBLE);
        buy7.setTextColor(getResources().getColor(R.color.white));
    }
    public String getSaleCost(int current, double reduction){
        if(reduction < 1){
            return Integer.toString((int)Math.round((current*1.00)-(current*reduction)));
        }else{
            return Integer.toString((int)(current-reduction));
        }
    }
    public void setSaleItem(String item, double reduction){
        //scroll to item
        //set message to SALE
        //set cost to sale price
        switch(item){
            case StoreDialog.SPEED_BOOST:
                try {
                    currentCost = Integer.valueOf(buy1.getText().toString());
                }catch (NumberFormatException ne){
                    return;
                }
                if(reduction > 0) {
                    newCost = getSaleCost(currentCost, reduction);
                    buy1.setText(newCost);
                    buy1.setTextColor(getResources().getColor(R.color.red));
                    message1.setVisibility(View.VISIBLE);
                }
                focusOnView(scrollview, item1);
                break;
            case StoreDialog.PORTAL_MASTER:
                try {
                    currentCost = Integer.valueOf(buy2.getText().toString());
                }catch (NumberFormatException ne){
                    return;
                }
                newCost = getSaleCost(currentCost, reduction);
                buy2.setText(newCost);
                buy2.setTextColor(getResources().getColor(R.color.red));
                focusOnView(scrollview, item2);
                message2.setVisibility(View.VISIBLE);
                break;
            case StoreDialog.PERFECTIONIST:
                try {
                    currentCost = Integer.valueOf(buy3.getText().toString());
                }catch (NumberFormatException ne){
                    return;
                }
                newCost = getSaleCost(currentCost, reduction);
                buy3.setText(newCost);
                buy3.setTextColor(getResources().getColor(R.color.red));
                focusOnView(scrollview, item3);
                message3.setVisibility(View.VISIBLE);
                break;
            case StoreDialog.NO_ADS_BUNDLE:
                try {
                    currentCost = Integer.valueOf(buy4.getText().toString());
                }catch (NumberFormatException ne){
                    return;
                }
                newCost = getSaleCost(currentCost, reduction);
                buy4.setText(newCost);
                buy4.setTextColor(getResources().getColor(R.color.red));
                focusOnView(scrollview, item4);
                message4.setVisibility(View.VISIBLE);
                break;
            case StoreDialog.ALL_PACKS_BUNDLE:
                try {
                    currentCost = Integer.valueOf(buy5.getText().toString());
                }catch (NumberFormatException ne){
                    return;
                }
                newCost = getSaleCost(currentCost, reduction);
                buy5.setText(newCost);
                buy5.setTextColor(getResources().getColor(R.color.red));
                focusOnView(scrollview, item5);
                message5.setVisibility(View.VISIBLE);
                break;
            case StoreDialog.PRO_BUNDLE:
                try {
                    currentCost = Integer.valueOf(buy6.getText().toString());
                }catch (NumberFormatException ne){
                    return;
                }
                newCost = getSaleCost(currentCost, reduction);
                buy6.setText(newCost);
                buy6.setTextColor(getResources().getColor(R.color.red));
                focusOnView(scrollview, item6);
                message6.setVisibility(View.VISIBLE);
                break;
            case StoreDialog.SPACE_HEATER:
                try {
                    currentCost = Integer.valueOf(buy7.getText().toString());
                }catch (NumberFormatException ne){
                    return;
                }
                newCost = getSaleCost(currentCost, reduction);
                buy7.setText(newCost);
                buy7.setTextColor(getResources().getColor(R.color.red));
                focusOnView(scrollview, item7);
                message7.setVisibility(View.VISIBLE);
                break;
        }
    }
    private final void focusOnView(final HorizontalScrollView scroll, final View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int vLeft = view.getLeft();
                int vRight = view.getRight();
                int sWidth = scroll.getWidth();
                scroll.smoothScrollTo(((vLeft + vRight) / 2) - (sWidth / 2), 0);
            }
        });
    }
    public void setBillingProcessor(){
        bp = new BillingProcessor(this, getResources().getString(R.string.license_key), new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String s, TransactionDetails transactionDetails) {
                String state = transactionDetails.purchaseInfo.parseResponseData().purchaseState.name();
                User.add("Product Id = " + transactionDetails.productId);
                User.add("Purchase Token = " + transactionDetails.purchaseToken);
                User.add("Purchase Info Response = " + transactionDetails.purchaseInfo.responseData);
                User.add("Purchase Info Signature = " + transactionDetails.purchaseInfo.signature);
                User.add("Purchase State = " + state);
                User.add("Purchase Time = " + transactionDetails.purchaseInfo.parseResponseData().purchaseTime);
                User.add("Purchase Dev Payload = " + transactionDetails.purchaseInfo.parseResponseData().developerPayload);
                boolean doubleGold = getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getBoolean("2x_coins", false);
                if (state.equals("PurchasedSuccessfully")) {
                    User.add("Product was purchased.");
                    switch (s) {
                        case "gold_10":
                            sql.addCoins(doubleGold ? 20 : 10, "gold");
                            User.add("Added 10 gold");
                            break;
                        case "gold_25":
                            sql.addCoins(doubleGold ? 50 : 25, "gold");
                            User.add("Added 25 gold");
                            break;
                        case "gold_50":
                            sql.addCoins(doubleGold ? 100 : 50, "gold");
                            User.add("Added 50 gold");
                            break;
                        case "gold_100":
                            sql.addCoins(doubleGold ? 200 : 100, "gold");
                            User.add("Added 100 gold");
                            break;
                        case "gold_200":
                            sql.addCoins(doubleGold ? 400 : 200, "gold");
                            User.add("Added 200 gold");
                            break;
                        case "gold_500":
                            sql.addCoins(doubleGold ? 1000 : 500, "gold");
                            User.add("Added 500 gold");
                            break;
                        case "gold_1000":
                            sql.addCoins(doubleGold ? 2000 : 1000, "gold");
                            User.add("Added 1000 gold");
                            break;
                        case "gold_3000":
                            sql.addCoins(doubleGold ? 6000 : 3000, "gold");
                            User.add("Added 3000 gold");
                            break;
                        default:
                    }
                    setCoins();
                    User.add("Sending AppsFlyer analytics.");
                    Map<String, Object> eventValue = new HashMap<String, Object>();
                    eventValue.put(AFInAppEventParameterName.CONTENT_ID, s);
                    eventValue.put(AFInAppEventParameterName.REVENUE, transactionDetails.purchaseInfo);
                    eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, "gold");
                    eventValue.put(AFInAppEventParameterName.CURRENCY, currency);
                    eventValue.put(AFInAppEventParameterName.DESCRIPTION, transactionDetails.orderId + "," + transactionDetails.productId);
                    AppsFlyerLib.trackEvent(getApplicationContext(), AFInAppEventType.PURCHASE, eventValue);
                    bp.consumePurchase(transactionDetails.productId);
                    if(coinsDialog != null) {
                        coinsDialog.resetDoubleGold();
                    }
                    User.add("Consumed Purchase.");
                    try {
                        Intent purchaseIntent = new Intent(context, PurchaseInfo.class);
                        purchaseIntent.putExtra(PurchaseInfo.Object, "Purchase");
                        startService(purchaseIntent);
                    } catch (Exception e) {
                    }
                    if (!User.username.equals("")) {
                        ParseService.updateSync("user", false);
                        Intent userIntent = new Intent(Store.this, ParseService.class);
                        userIntent.putExtra(ParseService.Object, "user");
                        startService(userIntent);
                    }
                }
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
            }
        });
    }
    @Override
    public void onBackPressed() {
        sd.backPress();
        User.add("Store Back Pressed");
        leaving = true;
        Intent a = new Intent(Store.this, Home.class);
        startActivity(a);
        Store.this.finish();
    }
    public void onPause(){
        super.onPause();
        Chartboost.onPause(this);
        AppsFlyerLib.onActivityPause(this);
        if(!leaving){
            sd.stop();
        }
        sd.stop();
    }
    public void onDestroy(){
        super.onDestroy();
        getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Sale", "").apply();
        bp.release();
        Chartboost.onDestroy(this);
        System.gc();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onStart(){
        super.onStart();
        Chartboost.onStart(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        Chartboost.onStop(this);
    }
}