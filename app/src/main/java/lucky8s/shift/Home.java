package lucky8s.shift;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.chartboost.sdk.Chartboost;
import com.jirbo.adcolony.AdColony;
import com.parse.ConfigCallback;
import com.parse.GetCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.app.AlertDialog.Builder;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.DialogInterface.OnClickListener;
import static android.util.Log.getStackTraceString;
import static android.view.View.INVISIBLE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static java.lang.Double.valueOf;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Locale.getDefault;
import static lucky8s.shift.Ad.isIncentiveReady;
import static lucky8s.shift.Ad.showIncentive;
import static lucky8s.shift.Config.VIDEO_HOURS_GOLD;
import static lucky8s.shift.Config.VIDEO_HOURS_SILVER;
import static lucky8s.shift.R.color.gold;
import static lucky8s.shift.R.string.continue_as_guest;
import static lucky8s.shift.R.string.earn_more_gold;
import static lucky8s.shift.R.string.earn_more_silver;
import static lucky8s.shift.R.string.enable_internet;
import static lucky8s.shift.R.string.guest;
import static lucky8s.shift.R.string.hours;
import static lucky8s.shift.R.string.login;
import static lucky8s.shift.R.string.mins;
import static lucky8s.shift.R.string.not_logged_in;
import static lucky8s.shift.R.string.not_signed_up_question;
import static lucky8s.shift.R.string.prompt_to_login_beginning;
import static lucky8s.shift.R.string.prompt_to_login_end;
import static lucky8s.shift.R.string.sign_up;
import static lucky8s.shift.R.string.transfer_guest_progress;
import static lucky8s.shift.R.string.video_not_available;
import static lucky8s.shift.User.add;
import static lucky8s.shift.User.allPacks;
import static lucky8s.shift.User.country;
import static lucky8s.shift.User.frozenModifier;
import static lucky8s.shift.User.getUser;
import static lucky8s.shift.User.hint;
import static lucky8s.shift.User.password;
import static lucky8s.shift.User.perfectBonusAdd;
import static lucky8s.shift.User.perfectBonusModifier;
import static lucky8s.shift.User.portalSpeed;
import static lucky8s.shift.User.pro;
import static lucky8s.shift.User.speed;
import static lucky8s.shift.User.username;


/**
 * Created by Christian on 5/19/2015.
 */
public class Home extends FragmentActivity implements DialogListener, AdInterface {

    Context context;
    SQL sql;
    SoundDriver sd;
    SetRanks task;
    CoinsDialog coinsDialog = new CoinsDialog(this);
    Handler timerHandler = new Handler();
    Handler forceHandler = new Handler();
    BillingProcessor bp;
    AlertDialog alertDialog2;
    ProgressDialog progress;

    Button login;
    Button leader;
    Button play;
    Button store;
    Button settings;
    Button about;
    Button profile;
    Button refresh;
    Button videoCoinsGold;
    Button videoCoinsSilver;
    Button refer;

    LinearLayout coinsStore;

    TextView loginStatus;
    TextView world;
    TextView national;
    TextView personal;
    TextView error;
    TextView goldCount;
    TextView silverCount;

    ArrayList<Leader> nationalLeaders = new ArrayList<Leader>();
    ArrayList<Leader> worldLeaders = new ArrayList<Leader>();
    ArrayList<Leader> personalLeaders = new ArrayList<Leader>();

    HashMap<String, Double> priceList = new HashMap<>();

    String currency = "";

    ParseObject friendsArray;

    NetworkInfo netInfo;
    ConnectivityManager cm;

    boolean leaving;

    ButtonAnimator videoGold;
    ButtonAnimator videoSilver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.add("At Home");
        setContentView(R.layout.home);
        Activity activity = this; // must be an Activity
        if(!Ad.initialized) {
            Ad.init(activity);
        }
        Chartboost.onCreate(this);
        Ad.setAdInterface(this);
        Ad.setAct(this);

        context = this;
        sql = new SQL(context);
        sd = MyApplication.getInstance().getSD();

        AppsFlyerLib.setAppsFlyerKey("8naNAkhVjmH6VsGfRpStHe");
        AppsFlyerLib.sendTracking(getApplicationContext());
        AppsFlyerLib.setCustomerUserId(User.getUser());


        setupConfig();

        loginStatus = (TextView) this.findViewById(R.id.login_status);
        world = (TextView) this.findViewById(R.id.world_rank);
        national = (TextView) this.findViewById(R.id.national_rank);
        personal = (TextView) this.findViewById(R.id.personal_rank);
        error = (TextView) this.findViewById(R.id.error);
        goldCount = (TextView) this.findViewById(R.id.gold_count);
        silverCount = (TextView) this.findViewById(R.id.silver_count);

        play = (Button) this.findViewById(R.id.play);
        play.setOnClickListener(onClickListener);
        store = (Button) this.findViewById(R.id.store);
        store.setOnClickListener(onClickListener);
        settings = (Button) this.findViewById(R.id.settings);
        settings.setOnClickListener(onClickListener);
        about = (Button) this.findViewById(R.id.about);
        about.setOnClickListener(onClickListener);
        profile = (Button) this.findViewById(R.id.profile);
        profile.setOnClickListener(onClickListener);
        login = (Button) this.findViewById(R.id.login);
        login.setOnClickListener(onClickListener);
        leader = (Button) this.findViewById(R.id.leader);
        leader.setOnClickListener(onClickListener);
        refresh = (Button) this.findViewById(R.id.refresh);
        refresh.setOnClickListener(onClickListener);
        videoCoinsGold = (Button) this.findViewById(R.id.video_coins_gold);
        videoCoinsGold.setOnClickListener(onClickListener);
        videoCoinsSilver = (Button) this.findViewById(R.id.video_coins_silver);
        videoCoinsSilver.setOnClickListener(onClickListener);
        refer = (Button) this.findViewById(R.id.refer);
        refer.setOnClickListener(onClickListener);

        coinsStore = (LinearLayout) findViewById(R.id.coins_store);
        coinsStore.setOnClickListener(onClickListener);

        videoGold = new ButtonAnimator(videoCoinsGold);
        videoSilver = new ButtonAnimator(videoCoinsSilver);
        timerHandler.postDelayed(timerRunnable, 0);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false).setTitle(getResources().getString(R.string.sync_data)).setMessage(getResources().getString(R.string.please_sync)).
                setPositiveButton(context.getResources().getString(R.string.synchronize), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                        netInfo = cm.getActiveNetworkInfo();
                        if(netInfo != null) {
                            progress = new ProgressDialog(context, R.style.MyTheme);
                            progress.setIndeterminate(true);
                            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progress.setCancelable(false);
                            progress.show();
                                    forceHandler.postDelayed(forceRunnable, 0);
                                    ParseService.updateSync("permissions_packs", false);
                                    Intent packsIntent = new Intent(context, ParseService.class);
                                    packsIntent.putExtra(ParseService.Object, "permissions_packs_force");
                                    startService(packsIntent);
                                    ParseService.updateSync("permissions_levels", false);
                                    Intent levelsIntent = new Intent(context, ParseService.class);
                                    levelsIntent.putExtra(ParseService.Object, "permissions_levels_force");
                                    startService(levelsIntent);
                                    ParseService.updateSync("user", false);
                                    Intent userIntent = new Intent(context, ParseService.class);
                                    userIntent.putExtra(ParseService.Object, "user_force");
                                    startService(userIntent);
                                    ParseService.updateSync("purchases", false);
                                    Intent purchasesIntent = new Intent(context, ParseService.class);
                                    purchasesIntent.putExtra(ParseService.Object, "purchases_force");
                                    startService(purchasesIntent);
                                    ParseService.updateSync("score_levels", false);
                                    Intent scoreLevelsIntent = new Intent(context, ParseService.class);
                                    scoreLevelsIntent.putExtra(ParseService.Object, "score_levels_force");
                                    startService(scoreLevelsIntent);
                                    ParseService.updateSync("score", false);
                                    Intent scoreIntent = new Intent(context, ParseService.class);
                                    scoreIntent.putExtra(ParseService.Object, "score_force");
                                    startService(scoreIntent);
                        }else {
                            if(alertDialog2.isShowing()){
                                alertDialog2.dismiss();
                            }
                            Toast.makeText(context, getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog2 = alertDialogBuilder.create();

        setVideoReward();

        checkTables();

        verifyLogin();

        setCoins();

        setBillingProcessor();

        sendErrors();


    }
    Runnable forceRunnable = new Runnable() {
        @Override
        public void run() {
            if(ParseService.synced){
                if(progress.isShowing()) {
                    progress.dismiss();
                    forceHandler.removeCallbacks(forceRunnable);
                }
            }
            forceHandler.postDelayed(this, 250);
        }
    };
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (Ad.isIncentiveReady()) {
                if(videoCoinsGold.getText().toString().equals("!") && videoCoinsGold.getVisibility() == View.INVISIBLE){
                    videoCoinsGold.setVisibility(View.VISIBLE);
                }else if(!videoCoinsGold.getText().toString().equals("!") && videoCoinsGold.getVisibility() == View.INVISIBLE){
                    videoCoinsGold.setVisibility(View.VISIBLE);
                }
                if(videoCoinsSilver.getText().toString().equals("!") && videoCoinsSilver.getVisibility() == View.INVISIBLE){
                    videoCoinsSilver.setVisibility(View.VISIBLE);
                }else if(!videoCoinsSilver.getText().toString().equals("!") && videoCoinsSilver.getVisibility() == View.INVISIBLE){
                    videoCoinsSilver.setVisibility(View.VISIBLE);
                }


                if(!videoGold.isRunning() && videoCoinsGold.getText().toString().equals("!")){
                    videoGold.start();
                }else if(videoGold.isRunning() && !videoCoinsGold.getText().toString().equals("!")){
                    videoGold.stop();
                }
                if(!videoSilver.isRunning() && videoCoinsSilver.getText().toString().equals("!")){
                    videoSilver.start();
                }else if(videoSilver.isRunning() && !videoCoinsSilver.getText().toString().equals("!")){
                    videoSilver.stop();
                }
            } else {
                if(!videoCoinsGold.getText().toString().equals("!") && videoCoinsGold.getVisibility() == View.INVISIBLE){
                    videoCoinsGold.setVisibility(View.VISIBLE);
                }else if(videoCoinsGold.getText().toString().equals("!") && videoCoinsGold.getVisibility() == View.VISIBLE){
                    videoCoinsGold.setVisibility(View.INVISIBLE);
                }
                if(!videoCoinsSilver.getText().toString().equals("!") && videoCoinsSilver.getVisibility() == View.INVISIBLE){
                    videoCoinsSilver.setVisibility(View.VISIBLE);
                }else if(videoCoinsSilver.getText().toString().equals("!") && videoCoinsSilver.getVisibility() == View.VISIBLE){
                    videoCoinsSilver.setVisibility(View.INVISIBLE);
                }
                if(videoGold.isRunning() || !videoCoinsGold.getText().toString().equals("!")){
                    videoGold.stop();
                }
                if(videoSilver.isRunning() || !videoCoinsSilver.getText().toString().equals("!")){
                    videoSilver.stop();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setVideoReward();
                }
            });
            timerHandler.postDelayed(this, 1000);
        }
    };

    public void setupConfig() {
        new Thread() {
            @Override
            public void run() {
                ParseConfig.getInBackground(new ConfigCallback() {
                    @Override
                    public void done(ParseConfig parseConfig, ParseException e) {
                        if (e == null) {

                        } else {
                            parseConfig = ParseConfig.getCurrentConfig();
                        }

                        Config.SENDGRID_USERNAME = parseConfig.getString("SENDGRID_USERNAME");
                        Config.SENDGRID_PASSWORD = parseConfig.getString("SENDGRID_PASSWORD");
                        Config.SENDGRID_EMAIL = parseConfig.getString("SENDGRID_EMAIL");
                        Config.SENDGRID_NAME = parseConfig.getString("SENDGRID_NAME");
                        Config.ERRORS_EMAIL = parseConfig.getString("ERRORS_EMAIL");
                        Config.DAYS_LOGGED = parseConfig.getDouble("DAYS_LOGGED");
                        Config.WELCOME_EMAIL = parseConfig.getString("WELCOME_EMAIL");
                        Config.WELCOME_EMAIL_NO_PROMO = parseConfig.getString("WELCOME_EMAIL_NO_PROMO");
                        Config.RESET_PASSWORD = parseConfig.getString("RESET_PASSWORD");
                        Config.APP_ID = parseConfig.getString("VUNGLE_APP_ID");
                        Config.VIDEO_HOURS_GOLD = parseConfig.getInt("VIDEO_HOURS_GOLD");
                        Config.VIDEO_HOURS_SILVER = parseConfig.getInt("VIDEO_HOURS_SILVER");
                        Config.RATED_DAYS = parseConfig.getDouble("RATED_DAYS");
                        Config.RATED_PACKS = parseConfig.getInt("RATED_PACKS");
                        Config.SPEED_DIALOG_INTERVAL = parseConfig.getInt("STORE_DIALOG_INTERVAL");
                        Config.ADS_DIALOG_INTERVAL_1 = parseConfig.getInt("ADS_DIALOG_INTERVAL_1");
                        Config.ADS_DIALOG_INTERVAL_2 = parseConfig.getInt("ADS_DIALOG_INTERVAL_2");
                        Config.ADS_DIALOG_INTERVAL_3 = parseConfig.getInt("ADS_DIALOG_INTERVAL_3");
                        Config.ALL_DIALOG_INTERVAL_1 = parseConfig.getInt("ALL_DIALOG_INTERVAL_1");
                        Config.ALL_DIALOG_INTERVAL_2 = parseConfig.getInt("ALL_DIALOG_INTERVAL_2");
                        Config.ALL_DIALOG_INTERVAL_3 = parseConfig.getInt("ALL_DIALOG_INTERVAL_3");
                        Config.DOUBLE_COINS_INTERVAL = parseConfig.getInt("DOUBLE_COINS_INTERVAL");
                        Config.INTERSTITIAL_INTERVAL = parseConfig.getInt("INTERSTITIAL_INTERVAL");
                        Config.PORTALS_DIALOG_INTERVAL = parseConfig.getInt("PORTALS_DIALOG_INTERVAL");
                        Config.PERFECT_DIALOG_INTERVAL = parseConfig.getInt("PERFECT_DIALOG_INTERVAL");
                        Config.PRO_DIALOG_INTERVAL = parseConfig.getInt("PRO_DIALOG_INTERVAL");
                        Config.ADCOLONY_APPID = parseConfig.getString("ADCOLONY_APPID");
                        Config.INCENTIVE_1 = parseConfig.getString("INCENTIVE_1");
                        Config.INCENTIVE_2 = parseConfig.getString("INCENTIVE_2");
                        Config.INCENTIVE_3 = parseConfig.getString("INCENTIVE_3");
                        Config.INCENTIVE_4 = parseConfig.getString("INCENTIVE_4");
                        Config.INTERSTITIAL_1 = parseConfig.getString("INTERSTITIAL_1");
                        Config.INTERSTITIAL_2 = parseConfig.getString("INTERSTITIAL_2");
                        Config.INTERSTITIAL_3 = parseConfig.getString("INTERSTITIAL_3");
                        Config.INTERSTITIAL_4 = parseConfig.getString("INTERSTITIAL_4");
                        Config.INTERSTITIAL_5 = parseConfig.getString("INTERSTITIAL_5");
                        Config.SKIP_LAST = parseConfig.getBoolean("SKIP_LAST");
                        JSONArray clearUsed = parseConfig.getJSONArray("CLEAR_USED");
                        JSONArray unlockLevel = parseConfig.getJSONArray("UNLOCK_LEVEL");
                        JSONArray unlockLevelNoAds = parseConfig.getJSONArray("UNLOCK_NO_ADS");
                        JSONArray unlockAll = parseConfig.getJSONArray("UNLOCK_ALL");
                        JSONArray freeSilver500 = parseConfig.getJSONArray("FREE_SILVER_500");
                        JSONArray freeSilver1000 = parseConfig.getJSONArray("FREE_SILVER_1000");
                        JSONArray freeSilver2000 = parseConfig.getJSONArray("FREE_SILVER_2000");
                        JSONArray freeSilver5000 = parseConfig.getJSONArray("FREE_SILVER_5000");
                        JSONArray freeSilver10000 = parseConfig.getJSONArray("FREE_SILVER_10000");
                        JSONArray freeSilver20000 = parseConfig.getJSONArray("FREE_SILVER_20000");
                        JSONArray freeSilver50000 = parseConfig.getJSONArray("FREE_SILVER_50000");
                        JSONArray freeGold10 = parseConfig.getJSONArray("FREE_GOLD_10");
                        JSONArray freeGold25 = parseConfig.getJSONArray("FREE_GOLD_25");
                        JSONArray freeGold50 = parseConfig.getJSONArray("FREE_GOLD_50");
                        JSONArray freeGold100 = parseConfig.getJSONArray("FREE_GOLD_100");
                        JSONArray freeGold200 = parseConfig.getJSONArray("FREE_GOLD_200");
                        JSONArray freeGold500 = parseConfig.getJSONArray("FREE_GOLD_500");
                        JSONArray freeGold1000 = parseConfig.getJSONArray("FREE_GOLD_1000");
                        JSONArray freeGold3000 = parseConfig.getJSONArray("FREE_GOLD_3000");
                        JSONArray speed1 = parseConfig.getJSONArray("SPEED_1");
                        JSONArray speed2 = parseConfig.getJSONArray("SPEED_2");
                        JSONArray speed3 = parseConfig.getJSONArray("SPEED_3");
                        JSONArray speed4 = parseConfig.getJSONArray("SPEED_4");
                        JSONArray speed5 = parseConfig.getJSONArray("SPEED_5");
                        JSONArray speed6 = parseConfig.getJSONArray("SPEED_6");
                        JSONArray speed7 = parseConfig.getJSONArray("SPEED_7");
                        JSONArray speed8 = parseConfig.getJSONArray("SPEED_8");
                        JSONArray speed9 = parseConfig.getJSONArray("SPEED_9");
                        JSONArray speed10 = parseConfig.getJSONArray("SPEED_10");
                        JSONArray blackList = parseConfig.getJSONArray("BLACKLIST");
                        if (unlockLevel != null) {
                            PromoCodes.UNLOCK_LEVEL = new ArrayList<String>();
                            for (int i = 0; i < unlockLevel.length(); i++) {
                                try {
                                    PromoCodes.UNLOCK_LEVEL.add(unlockLevel.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (unlockLevelNoAds != null) {
                            PromoCodes.UNLOCK_NO_ADS = new ArrayList<String>();
                            for (int i = 0; i < unlockLevelNoAds.length(); i++) {
                                try {
                                    PromoCodes.UNLOCK_NO_ADS.add(unlockLevelNoAds.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (unlockAll != null) {
                            PromoCodes.UNLOCK_ALL = new ArrayList<String>();
                            for (int i = 0; i < unlockAll.length(); i++) {
                                try {
                                    PromoCodes.UNLOCK_ALL.add(unlockAll.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeSilver500 != null) {
                            PromoCodes.FREE_SILVER_500 = new ArrayList<String>();
                            for (int i = 0; i < freeSilver500.length(); i++) {
                                try {
                                    PromoCodes.FREE_SILVER_500.add(freeSilver500.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeSilver1000 != null) {
                            PromoCodes.FREE_SILVER_1000 = new ArrayList<String>();
                            for (int i = 0; i < freeSilver1000.length(); i++) {
                                try {
                                    PromoCodes.FREE_SILVER_1000.add(freeSilver1000.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeSilver2000 != null) {
                            PromoCodes.FREE_SILVER_2000 = new ArrayList<String>();
                            for (int i = 0; i < freeSilver2000.length(); i++) {
                                try {
                                    PromoCodes.FREE_SILVER_2000.add(freeSilver2000.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeSilver5000 != null) {
                            PromoCodes.FREE_SILVER_5000 = new ArrayList<String>();
                            for (int i = 0; i < freeSilver5000.length(); i++) {
                                try {
                                    PromoCodes.FREE_SILVER_5000.add(freeSilver5000.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeSilver10000 != null) {
                            PromoCodes.FREE_SILVER_10000 = new ArrayList<String>();
                            for (int i = 0; i < freeSilver10000.length(); i++) {
                                try {
                                    PromoCodes.FREE_SILVER_10000.add(freeSilver10000.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeSilver20000 != null) {
                            PromoCodes.FREE_SILVER_20000 = new ArrayList<String>();
                            for (int i = 0; i < freeSilver20000.length(); i++) {
                                try {
                                    PromoCodes.FREE_SILVER_20000.add(freeSilver20000.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeSilver50000 != null) {
                            PromoCodes.FREE_SILVER_50000 = new ArrayList<String>();
                            for (int i = 0; i < freeSilver50000.length(); i++) {
                                try {
                                    PromoCodes.FREE_SILVER_50000.add(freeSilver50000.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeGold10 != null) {
                            PromoCodes.FREE_GOLD_10 = new ArrayList<String>();
                            for (int i = 0; i < freeGold10.length(); i++) {
                                try {
                                    PromoCodes.FREE_GOLD_10.add(freeGold10.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeGold25 != null) {
                            PromoCodes.FREE_GOLD_25 = new ArrayList<String>();
                            for (int i = 0; i < freeGold25.length(); i++) {
                                try {
                                    PromoCodes.FREE_GOLD_25.add(freeGold25.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeGold50 != null) {
                            PromoCodes.FREE_GOLD_50 = new ArrayList<String>();
                            for (int i = 0; i < freeGold50.length(); i++) {
                                try {
                                    PromoCodes.FREE_GOLD_50.add(freeGold50.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeGold100 != null) {
                            PromoCodes.FREE_GOLD_100 = new ArrayList<String>();
                            for (int i = 0; i < freeGold100.length(); i++) {
                                try {
                                    PromoCodes.FREE_GOLD_100.add(freeGold100.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeGold200 != null) {
                            PromoCodes.FREE_GOLD_200 = new ArrayList<String>();
                            for (int i = 0; i < freeGold200.length(); i++) {
                                try {
                                    PromoCodes.FREE_GOLD_200.add(freeGold200.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeGold500 != null) {
                            PromoCodes.FREE_GOLD_500 = new ArrayList<String>();
                            for (int i = 0; i < freeGold500.length(); i++) {
                                try {
                                    PromoCodes.FREE_GOLD_500.add(freeGold500.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeGold1000 != null) {
                            PromoCodes.FREE_GOLD_1000 = new ArrayList<String>();
                            for (int i = 0; i < freeGold1000.length(); i++) {
                                try {
                                    PromoCodes.FREE_GOLD_1000.add(freeGold1000.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (freeGold3000 != null) {
                            PromoCodes.FREE_GOLD_3000 = new ArrayList<String>();
                            for (int i = 0; i < freeGold3000.length(); i++) {
                                try {
                                    PromoCodes.FREE_GOLD_3000.add(freeGold3000.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (speed1 != null) {
                            PromoCodes.SPEED_1 = new ArrayList<String>();
                            for (int i = 0; i < speed1.length(); i++) {
                                try {
                                    PromoCodes.SPEED_1.add(speed1.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (speed2 != null) {
                            PromoCodes.SPEED_2 = new ArrayList<String>();
                            for (int i = 0; i < speed2.length(); i++) {
                                try {
                                    PromoCodes.SPEED_2.add(speed2.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (speed3 != null) {
                            PromoCodes.SPEED_3 = new ArrayList<String>();
                            for (int i = 0; i < speed3.length(); i++) {
                                try {
                                    PromoCodes.SPEED_3.add(speed3.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (speed4 != null) {
                            PromoCodes.SPEED_4 = new ArrayList<String>();
                            for (int i = 0; i < speed4.length(); i++) {
                                try {
                                    PromoCodes.SPEED_4.add(speed4.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (speed5 != null) {
                            PromoCodes.SPEED_5 = new ArrayList<String>();
                            for (int i = 0; i < speed5.length(); i++) {
                                try {
                                    PromoCodes.SPEED_5.add(speed5.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (speed6 != null) {
                            PromoCodes.SPEED_6 = new ArrayList<String>();
                            for (int i = 0; i < speed6.length(); i++) {
                                try {
                                    PromoCodes.SPEED_6.add(speed6.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (speed7 != null) {
                            PromoCodes.SPEED_7 = new ArrayList<String>();
                            for (int i = 0; i < speed7.length(); i++) {
                                try {
                                    PromoCodes.SPEED_7.add(speed7.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (speed8 != null) {
                            PromoCodes.SPEED_8 = new ArrayList<String>();
                            for (int i = 0; i < speed8.length(); i++) {
                                try {
                                    PromoCodes.SPEED_8.add(speed8.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (speed9 != null) {
                            PromoCodes.SPEED_9 = new ArrayList<String>();
                            for (int i = 0; i < speed9.length(); i++) {
                                try {
                                    PromoCodes.SPEED_9.add(speed9.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (speed10 != null) {
                            PromoCodes.SPEED_10 = new ArrayList<String>();
                            for (int i = 0; i < speed10.length(); i++) {
                                try {
                                    PromoCodes.SPEED_10.add(speed10.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (clearUsed != null) {
                            PromoCodes.CLEAR_USED = new ArrayList<String>();
                            for (int i = 0; i < clearUsed.length(); i++) {
                                try {
                                    PromoCodes.CLEAR_USED.add(clearUsed.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        if (blackList != null) {
                            Config.BLACKLIST = new ArrayList<String>();
                            for (int i = 0; i < clearUsed.length(); i++) {
                                try {
                                    Config.BLACKLIST.add(blackList.get(i).toString());
                                } catch (JSONException je) {
                                }
                            }
                        }
                        PromoCodes.initialize();
                    }
                });
            }
        }.start();
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

    public void onCloseDialog() {
        setCoins();
    }

    public void showCoinsStore() {
    }

    public void setCoins() {
        String numSilver = Integer.toString(sql.getCoins("silver"));
        String numGold = Integer.toString(sql.getCoins("gold"));
        silverCount.setText(numSilver);
        goldCount.setText(numGold);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if(view instanceof Button) {
                buttonClick(view);
            }
            switch (view.getId()) {
                case R.id.play:
                    //if the user is a guest prompt them to login
                    sd.buttonPress();
                    add("Clicked on Play");
                    String possibleUser = sql.getSingleResult("user", "username", " where username != '"+getString(R.string.guest)+"'");
                    boolean loggedIn = getSharedPreferences("Login", MODE_PRIVATE).getBoolean("LoggedIn", false);
                    if (!possibleUser.equals("")) {//this means there is a user in the local DB thats NOT a guest, and not "", thus log them in as such if they want
                        if (!loginStatus.getText().equals("")) {
                            leaving = true;
                            Intent a = new Intent(Home.this, Levels.class);
                            startActivity(a);
                            Home.this.finish();
                        } else {
                            Builder alertDialogBuilder = new Builder(context);
                            alertDialogBuilder.setTitle(context.getResources().getString(not_logged_in)).setMessage(context.getResources().getString(prompt_to_login_beginning) + " " + possibleUser + " " + context.getResources().getString(prompt_to_login_end)).
                                    setPositiveButton(context.getResources().getString(prompt_to_login_beginning) + " " + possibleUser, new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (login.getText().toString().equals(context.getResources().getString(R.string.login))) {
                                                leaving = true;
                                                add("Login as user");
                                                Intent b = new Intent(context, Login.class);
                                                startActivity(b);
                                                Home.this.finish();
                                            } else {
                                                login.setText(getResources().getString(R.string.login));
                                                loginStatus.setText("");
                                                loginStatus.setVisibility(INVISIBLE);
                                                username = "";
                                                password = "";
                                                hint = "";
                                                country = getDefault().getCountry();
                                                pro = sql.getSingleResult("purchases", "pro", " where username = '" + getUser() + "' ").equals("1") ? true : false;
                                                allPacks = sql.getSingleResult("purchases", "all_packs", " where username = '" + getUser() + "' ").equals("1") ? true : false;
                                                speed = valueOf((sql.getSingleResult("purchases", "speed", " where username = '" + getUser() + "' ").equals("") || sql.getSingleResult("purchases", "speed", " where username = '" + getUser() + "' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "speed", " where username = '" + getUser() + "' "), "1.0"));
                                                frozenModifier = valueOf((sql.getSingleResult("purchases", "frozen_mod", " where username = '" + getUser() + "' ").equals("") || sql.getSingleResult("purchases", "frozen_mod", " where username = '" + getUser() + "' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "frozen_mod", " where username = '" + getUser() + "' "), "1.0"));
                                                portalSpeed = valueOf((sql.getSingleResult("purchases", "portal_speed", " where username = '" + getUser() + "' ").equals("") || sql.getSingleResult("purchases", "portal_speed", " where username = '" + getUser() + "' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "portal_speed", " where username = '" + getUser() + "' "), "1.0"));
                                                perfectBonusAdd = valueOf((sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '" + getUser() + "' ").equals("") || sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '" + getUser() + "' ").equals("nul")) ? "0.0" : forceDecimal(sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '" + getUser() + "' "), "0.0"));
                                                perfectBonusModifier = valueOf((sql.getSingleResult("user", "perfect_bonus_mod", " where username = '" + getUser() + "' ").equals("") || sql.getSingleResult("user", "perfect_bonus_mod", " where username = '" + getUser() + "' ").equals("nul")) ? "0.1" : forceDecimal(sql.getSingleResult("user", "perfect_bonus_mod", " where username = '" + getUser() + "' "), "0.1"));
                                                getSharedPreferences("Login", MODE_PRIVATE).edit().putLong("Time", 0).apply();
                                                getSharedPreferences("Login", MODE_PRIVATE).edit().putBoolean("LoggedIn", false).apply();
                                                getSharedPreferences("Login", MODE_PRIVATE).edit().putString("Username", getString(guest)).apply();
                                                setCoins();
                                            }
                                        }
                                    }).
                                    setNegativeButton(context.getResources().getString(continue_as_guest), new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            leaving = true;
                                            add("Continuing as guest.");
                                            Intent a = new Intent(Home.this, Levels.class);
                                            startActivity(a);
                                            Home.this.finish();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            if(!alertDialog.isShowing()) {
                                alertDialog.show();
                            }
                            if (alertDialog.isShowing()) {
                                int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                                View divider = alertDialog.findViewById(dividerId);
                                divider.setBackgroundColor(getResources().getColor(gold));
                            }

                        }
                    } else if (possibleUser.equals("")) {//they only have a guest profile so prompt them to create an account!
                        Builder alertDialogBuilder = new Builder(context);
                        alertDialogBuilder.setTitle(context.getResources().getString(not_signed_up_question)).setMessage(context.getResources().getString(transfer_guest_progress) + " " + possibleUser).
                                setPositiveButton(context.getResources().getString(sign_up), new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        sd.buttonPress();
                                        User.add("Going to SignUp");
                                        if(context == null){
                                            context = Home.this;
                                        }
                                        Intent a = new Intent(context, SignUp.class);
                                        leaving = true;
                                        startActivity(a);
                                        Home.this.finish();
                                    }
                                }).
                                setNegativeButton(context.getResources().getString(continue_as_guest), new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        leaving = true;
                                        add("Continuing as guest.");
                                        Intent a = new Intent(Home.this, Levels.class);
                                        startActivity(a);
                                        Home.this.finish();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        if (alertDialog.isShowing()) {
                            int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                            View divider = alertDialog.findViewById(dividerId);
                            divider.setBackgroundColor(getResources().getColor(gold));
                        }
                    }
                    break;
                case R.id.store:
                    sd.buttonPress();
                    leaving = true;
                    add("Going to Store from Home");
                    Intent q = new Intent(Home.this, Store.class);
                    startActivity(q);
                    Home.this.finish();
                    break;
                case R.id.settings:
                    add("Going to Settings from Home");
                    sd.buttonPress();
                    leaving = true;
                    Intent c = new Intent(Home.this, Settings.class);
                    startActivity(c);
                    Home.this.finish();
                    break;
                case R.id.about:
                    add("Going to About from Home");
                    sd.buttonPress();
                    leaving = true;
                    Intent d = new Intent(Home.this, About.class);
                    startActivity(d);
                    Home.this.finish();
                    break;
                case R.id.profile:
                    add("Going to Profile from Home");
                    sd.buttonPress();
                    leaving = true;
                    Intent r = new Intent(Home.this, Profile.class);
                    startActivity(r);
                    Home.this.finish();
                    break;
                case R.id.leader:
                    add("Going to Leaderboard from Home");
                    sd.buttonPress();
                    leaving = true;
                    Intent e = new Intent(Home.this, Leaderboard.class);
                    startActivity(e);
                    Home.this.finish();
                    break;
                case R.id.refer:
                    add("Going to Refer from Home");
                    sd.buttonPress();
                    leaving = true;
                    Intent s = new Intent(Home.this, Refer.class);
                    startActivity(s);
                    Home.this.finish();
                    break;
                case R.id.refresh:
                    add("Clicked Refresh");
                    sd.buttonPress();
                    task = new SetRanks(getApplicationContext());
                    try {
                        task.execute();
                    } catch (Exception ex) {
                        add(getStackTraceString(ex));
                    }
                    break;
                case R.id.coins_store://here
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
                    Fragment dialog = getFragmentManager().findFragmentByTag("coins_dialog");
                    if (dialog == null) {
                        if (!coinsDialog.isAdded()) {
                            coinsDialog.setCancelable(true);
                            add("Went to CoinsDialog from Home");
                            coinsDialog.show(fm, "coins_dialog");
                        }
                    } else if (dialog.getActivity() != Home.this) {
                    } else {
                    }
                    setCoins();
                    break;
                case R.id.login:
                    sd.buttonPress();
                    if (login.getText().toString().equals(context.getResources().getString(R.string.login))) {
                        leaving = true;
                        add("Going to Login from Home");
                        Intent b = new Intent(context, Login.class);
                        startActivity(b);
                        Home.this.finish();
                    } else {
                        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                        netInfo = cm.getActiveNetworkInfo();
                        ParseService.updateSync("All", true);
                        if(ParseService.synced){
                            User.add("Logging out");
                            login.setText(getResources().getString(R.string.login));
                            loginStatus.setText("");
                            loginStatus.setVisibility(INVISIBLE);
                            username = "";
                            password = "";
                            hint = "";
                            country = getDefault().getCountry();
                            pro = sql.getSingleResult("purchases", "pro", " where username = '" + getUser() + "' ").equals("1") ? true : false;
                            allPacks = sql.getSingleResult("purchases", "all_packs", " where username = '" + getUser() + "' ").equals("1") ? true : false;
                            speed = valueOf((sql.getSingleResult("purchases", "speed", " where username = '" + getUser() + "' ").equals("") || sql.getSingleResult("purchases", "speed", " where username = '" + getUser() + "' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "speed", " where username = '" + getUser() + "' "), "1.0"));
                            frozenModifier = valueOf((sql.getSingleResult("purchases", "frozen_mod", " where username = '" + getUser() + "' ").equals("") || sql.getSingleResult("purchases", "frozen_mod", " where username = '" + getUser() + "' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "frozen_mod", " where username = '" + getUser() + "' "), "1.0"));
                            portalSpeed = valueOf((sql.getSingleResult("purchases", "portal_speed", " where username = '" + getUser() + "' ").equals("") || sql.getSingleResult("purchases", "portal_speed", " where username = '" + getUser() + "' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "portal_speed", " where username = '" + getUser() + "' "), "1.0"));
                            perfectBonusAdd = valueOf((sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '" + getUser() + "' ").equals("") || sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '" + getUser() + "' ").equals("nul")) ? "0.0" : forceDecimal(sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '" + getUser() + "' "), "0.0"));
                            perfectBonusModifier = valueOf((sql.getSingleResult("user", "perfect_bonus_mod", " where username = '" + getUser() + "' ").equals("") || sql.getSingleResult("user", "perfect_bonus_mod", " where username = '" + getUser() + "' ").equals("nul")) ? "0.1" : forceDecimal(sql.getSingleResult("user", "perfect_bonus_mod", " where username = '" + getUser() + "' "), "0.1"));
                            getSharedPreferences("Login", MODE_PRIVATE).edit().putLong("Time", 0).apply();
                            getSharedPreferences("Login", MODE_PRIVATE).edit().putBoolean("LoggedIn", false).apply();
                            getSharedPreferences("Login", MODE_PRIVATE).edit().putString("Username", getString(guest)).apply();
                            setCoins();
                        }else{
                            alertDialog2.show();
                            if(alertDialog2.isShowing()) {
                                int dividerId = alertDialog2.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                                View divider = alertDialog2.findViewById(dividerId);
                                divider.setBackgroundColor(getResources().getColor(R.color.gold));
                            }
                        }
                    }
                    break;
                case R.id.video_coins_gold:
                    add("Clicked VideoCoinsGold");
                    long lastGold = getSharedPreferences("Video", MODE_PRIVATE).getLong("LastGold", 0);
                    long now = currentTimeMillis();
                    cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    netInfo = cm.getActiveNetworkInfo();
                    int minsToGold = (int) round(((VIDEO_HOURS_GOLD * 60 * 60 * 1000) - (now - lastGold)) / 60000.0);
                    String minsGold = format("%02d", minsToGold % 60);
                    String hoursGold = format("%02d", (Integer.valueOf(minsToGold) / 60));
                    boolean earnGold = (now - lastGold) > (VIDEO_HOURS_GOLD * 60 * 60 * 1000);
                    if (earnGold && isIncentiveReady()) {
                        getSharedPreferences("Video", MODE_PRIVATE).edit().putString("LastType", "gold").apply();
                        showIncentive();
                    } else if (earnGold && isIncentiveReady() && netInfo != null) {
                        makeText(context, getResources().getString(video_not_available), LENGTH_SHORT).show();
                    } else if (earnGold && isIncentiveReady() && netInfo == null) {
                        makeText(context, getResources().getString(enable_internet), LENGTH_SHORT).show();
                    } else {
                        makeText(context, getResources().getString(earn_more_gold) + " " + (!hoursGold.equals("00") ? (hoursGold + " " + getResources().getString(hours) + " ") : "") + minsGold + " " + getResources().getString(mins), LENGTH_LONG).show();
                    }
                    break;
                case R.id.video_coins_silver:
                    add("Clicked VideoCoinsSilver");
                    long lastSilver = getSharedPreferences("Video", MODE_PRIVATE).getLong("LastSilver", 0);
                    now = currentTimeMillis();
                    int minsToSilver = (int) round(((VIDEO_HOURS_SILVER * 60 * 60 * 1000) - (now - lastSilver)) / 60000.0);
                    String minsSilver = format("%02d", minsToSilver % 60);
                    String hoursSilver = format("%02d", (Integer.valueOf(minsToSilver) / 60));
                    boolean earnSilver = (now - lastSilver) > (VIDEO_HOURS_SILVER * 60 * 60 * 1000);
                    if (earnSilver && isIncentiveReady()) {
                        getSharedPreferences("Video", MODE_PRIVATE).edit().putString("LastType", "silver").apply();
                        showIncentive();
                    } else if (isIncentiveReady() && earnSilver && netInfo != null) {
                        makeText(context, getResources().getString(video_not_available), LENGTH_SHORT).show();
                    } else if (isIncentiveReady() && earnSilver && netInfo == null) {
                        makeText(context, getResources().getString(enable_internet), LENGTH_SHORT).show();
                    } else {
                        makeText(context, getResources().getString(earn_more_silver) + " " + (!hoursSilver.equals("00") ? (hoursSilver + " " + getResources().getString(hours) + " ") : "") + minsSilver + " " + getResources().getString(mins), LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

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
    public void setVideoReward(){
        long lastGold = getSharedPreferences("Video", MODE_PRIVATE).getLong("LastGold", 0);
        long lastSilver = getSharedPreferences("Video", MODE_PRIVATE).getLong("LastSilver", 0);
        long now = System.currentTimeMillis();
        int minsToGold = (int) Math.round(((Config.VIDEO_HOURS_GOLD * 60 * 60 * 1000) - (now - lastGold)) / 60000.0);
        String minsGold = String.format("%02d", minsToGold%60);
        String hoursGold = String.format("%02d", (Integer.valueOf(minsToGold) / 60));
        int minsToSilver = (int) Math.round(((Config.VIDEO_HOURS_SILVER * 60 * 60 * 1000) - (now - lastSilver)) / 60000.0);
        String minsSilver = String.format("%02d",minsToSilver%60);
        String hoursSilver = String.format("%02d", (Integer.valueOf(minsToSilver) / 60));
        boolean earnGold = (now-lastGold)>(Config.VIDEO_HOURS_GOLD*60* 60 * 1000);
        boolean earnSilver = (now-lastSilver)>(Config.VIDEO_HOURS_SILVER*60*60*1000);
        if(earnGold){
            videoCoinsGold.setText("!");
        }else{
            videoCoinsGold.setText(hoursGold + ":" + minsGold);
        }
        if(earnSilver){
            videoCoinsSilver.setText("!");
        }else{
            videoCoinsSilver.setText(hoursSilver+":"+minsSilver);
        }
    }
    public void getReward(){
        Random rand = new Random();
        String lastType = getSharedPreferences("Video", MODE_PRIVATE).getString("LastType", "silver");
        final int coins = lastType.equals("gold") ? 2 : rand.nextInt(101-1)+1;
        final String coinType = lastType;
        sql.addCoins(coins, coinType);
        if(coinType.equals("gold")){
            getSharedPreferences("Video", MODE_PRIVATE).edit().putLong("LastGold", System.currentTimeMillis()).apply();
        }else if(coinType.equals("silver")){
            getSharedPreferences("Video", MODE_PRIVATE).edit().putLong("LastSilver", System.currentTimeMillis()).apply();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Home.this, Integer.toString(coins) + " " + coinType, Toast.LENGTH_SHORT).show();
                setCoins();
            }
        });
        if(!User.username.equals("")) {
            Intent userIntent = new Intent(this, ParseService.class);
            userIntent.putExtra(ParseService.Object, "user");
            this.startService(userIntent);
        }
    }
    public void onResume(){
        super.onResume();
        Chartboost.onResume(this);
        AdColony.resume(this);
        AppsFlyerLib.onActivityResume(this);
        if(!sd.isPlaying("homeBackground")) {
            sd.stop();
            sd.homeBackground();
        }
        leaving = false;
        verifyLogin();
        setCoins();//set coins from coins dialog
        task = new SetRanks(getApplicationContext());
        try {
            task.execute();
        }catch (Exception ex){
        }
    }
    public void verifyLogin(){
        User.add("Verifying Login");
        Calendar cal = Calendar.getInstance();
        String username = getSharedPreferences("Login", MODE_PRIVATE).getString("Username", getString(R.string.guest));
        long loginTime = getSharedPreferences("Login", MODE_PRIVATE).getLong("Time", 0);
        boolean loggedIn = getSharedPreferences("Login", MODE_PRIVATE).getBoolean("LoggedIn", false);
        boolean timeVerified = (cal.getTimeInMillis() - loginTime) < (1000*60*60*24*Config.DAYS_LOGGED);
        //test comment
        if(loggedIn){
            if(timeVerified){
                if(!username.equals(getString(R.string.guest))){
                    User.add(("User is logged in."));
                    User.username = username;
                    User.password = sql.getSingleResult("user", "password", " where username = '"+username+"' ");
                    User.hint = sql.getSingleResult("user", "hint", " where username = '"+username+"' ");
                    User.email = sql.getSingleResult("user", "email", " where username = '"+username+"' ");
                    User.country = sql.getSingleResult("user", "country", " where username = '"+username+"' ");
                    User.pro = sql.getSingleResult("purchases", "pro", " where username = '"+username+"' ").equals("1") ? true : false;
                    User.allPacks = sql.getSingleResult("purchases", "all_packs", " where username = '"+username+"' ").equals("1") ? true : false;
                    User.speed = Double.valueOf((sql.getSingleResult("purchases", "speed", " where username = '"+username+"' ").equals("") || sql.getSingleResult("purchases", "speed", " where username = '"+username+"' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "speed", " where username = '" + username + "' "), "1.0"));
                    User.frozenModifier = Double.valueOf((sql.getSingleResult("purchases", "frozen_mod", " where username = '"+username+"' ").equals("") || sql.getSingleResult("purchases", "frozen_mod", " where username = '"+username+"' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "frozen_mod", " where username = '" + username + "' "), "1.0"));
                    User.portalSpeed = Double.valueOf((sql.getSingleResult("purchases", "portal_speed", " where username = '"+username+"' ").equals("") || sql.getSingleResult("purchases", "portal_speed", " where username = '"+username+"' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "portal_speed", " where username = '" + username + "' "), "1.0"));
                    User.perfectBonusAdd = Double.valueOf((sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '"+username+"' ").equals("") || sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '"+username+"' ").equals("nul"))? "0.0" :forceDecimal(sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '" + username + "' "), "0.0"));
                    User.perfectBonusModifier = Double.valueOf((sql.getSingleResult("user", "perfect_bonus_mod", " where username = '"+username+"' ").equals("") || sql.getSingleResult("user", "perfect_bonus_mod", " where username = '"+username+"' ").equals("nul")) ? "0.1" : forceDecimal(sql.getSingleResult("user", "perfect_bonus_mod", " where username = '" + username + "' "), "0.1"));
                    loginStatus.setText(User.username);
                    login.setText(context.getResources().getString(R.string.logout));
                    loginStatus.setVisibility(View.VISIBLE);
                }
            }else {
                User.add("User is not logged in.");
                getSharedPreferences("Login", MODE_PRIVATE).edit().putBoolean("LoggedIn", false);
                getSharedPreferences("Login", MODE_PRIVATE).edit().putString("Username", getString(R.string.guest));
                loginStatus.setText("");
                login.setText(getResources().getString(R.string.login));
                loginStatus.setVisibility(View.INVISIBLE);
                User.username = "";
                User.password = "";
                User.hint = "";
                User.country = Locale.getDefault().getCountry();
                User.pro = sql.getSingleResult("purchases", "pro", " where username = '"+User.getUser()+"' ").equals("1") ? true : false;
                User.allPacks = sql.getSingleResult("purchases", "all_packs", " where username = '"+User.getUser()+"' ").equals("1") ? true : false;
                User.speed = Double.valueOf((sql.getSingleResult("purchases", "speed", " where username = '"+User.getUser()+"' ").equals("") || sql.getSingleResult("purchases", "speed", " where username = '"+User.getUser()+"' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "speed", " where username = '" + User.getUser() + "' "), "1.0"));
                User.frozenModifier = Double.valueOf((sql.getSingleResult("purchases", "frozen_mod", " where username = '"+username+"' ").equals("") ||  sql.getSingleResult("purchases", "frozen_mod", " where username = '"+username+"' ").equals("nul"))? "1.0" : forceDecimal(sql.getSingleResult("purchases", "frozen_mod", " where username = '" + username + "' "), "1.0"));
                User.portalSpeed = Double.valueOf((sql.getSingleResult("purchases", "portal_speed", " where username = '"+User.getUser()+"' ").equals("") || sql.getSingleResult("purchases", "portal_speed", " where username = '"+User.getUser()+"' ").equals("nul"))? "1.0" :forceDecimal(sql.getSingleResult("purchases", "portal_speed", " where username = '" + User.getUser() + "' "), "1.0") );
                User.perfectBonusAdd = Double.valueOf((sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '"+username+"' ").equals("") || sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '"+username+"' ").equals("nul")) ? "0.0" : forceDecimal(sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '" + username + "' "), "0.0"));
                User.perfectBonusModifier = Double.valueOf((sql.getSingleResult("user", "perfect_bonus_mod", " where username = '"+username+"' ").equals("") || sql.getSingleResult("user", "perfect_bonus_mod", " where username = '"+username+"' ").equals("nul")) ? "0.1" : forceDecimal(sql.getSingleResult("user", "perfect_bonus_mod", " where username = '" + username + "' "), "0.1"));
            }
        }else{
            User.add("User is not logged in.");
            login.setText(getResources().getString(R.string.login));
            loginStatus.setVisibility(View.INVISIBLE);
            User.username = "";
            User.password = "";
            User.hint = "";
            User.country = Locale.getDefault().getCountry();
            User.pro = sql.getSingleResult("purchases", "pro", " where username = '"+User.getUser()+"' ").equals("1") ? true : false;
            User.allPacks = sql.getSingleResult("purchases", "all_packs", " where username = '"+User.getUser()+"' ").equals("1") ? true : false;
            User.speed = Double.valueOf((sql.getSingleResult("purchases", "speed", " where username = '"+User.getUser()+"' ").equals("") || sql.getSingleResult("purchases", "speed", " where username = '"+User.getUser()+"' ").equals("nul")) ?  "1.0" : forceDecimal(sql.getSingleResult("purchases", "speed", " where username = '" + User.getUser() + "' "), "1.0"));
            User.frozenModifier = Double.valueOf((sql.getSingleResult("purchases", "frozen_mod", " where username = '"+username+"' ").equals("") || sql.getSingleResult("purchases", "frozen_mod", " where username = '"+username+"' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "frozen_mod", " where username = '" + username + "' "), "1.0"));
            User.portalSpeed = Double.valueOf((sql.getSingleResult("purchases", "portal_speed", " where username = '"+User.getUser()+"' ").equals("") || sql.getSingleResult("purchases", "portal_speed", " where username = '"+User.getUser()+"' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "portal_speed", " where username = '" + User.getUser() + "' "), "1.0"));
            User.perfectBonusAdd = Double.valueOf((sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '"+username+"' ").equals("") || sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '"+username+"' ").equals("nul")) ? "0.0" : forceDecimal(sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '" + username + "' "), "0.0"));
            User.perfectBonusModifier = Double.valueOf((sql.getSingleResult("user", "perfect_bonus_mod", " where username = '"+username+"' ").equals("") || sql.getSingleResult("user", "perfect_bonus_mod", " where username = '"+username+"' ").equals("nul")) ? "0.1" : forceDecimal(sql.getSingleResult("user", "perfect_bonus_mod", " where username = '" + username + "' "), "0.1"));
        }
        if(!User.getUser().equals(getString(R.string.guest)) && Config.BLACKLIST.contains(User.getUser())){
            removeBadUser();
        }
    }
    public void removeBadUser(){
        if(!User.getUser().equals(getString(R.string.guest))){
            User.add("Logging out");
            login.setText(getResources().getString(R.string.login));
            loginStatus.setText("");
            loginStatus.setVisibility(View.INVISIBLE);
            User.username = "";
            User.password = "";
            User.hint = "";
            User.country = Locale.getDefault().getCountry();
            User.pro = sql.getSingleResult("purchases", "pro", " where username = '"+User.getUser()+"' ").equals("1") ? true : false;
            User.allPacks = sql.getSingleResult("purchases", "all_packs", " where username = '"+User.getUser()+"' ").equals("1") ? true : false;
            User.speed = Double.valueOf((sql.getSingleResult("purchases", "speed", " where username = '"+User.getUser()+"' ").equals("") || sql.getSingleResult("purchases", "speed", " where username = '"+User.getUser()+"' ").equals("nul")) ?  "1.0" : forceDecimal(sql.getSingleResult("purchases", "speed", " where username = '" + User.getUser() + "' "), "1.0"));
            User.frozenModifier = Double.valueOf((sql.getSingleResult("purchases", "frozen_mod", " where username = '"+User.getUser()+"' ").equals("") || sql.getSingleResult("purchases", "frozen_mod", " where username = '"+User.getUser()+"' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "frozen_mod", " where username = '" + User.getUser() + "' "), "1.0"));
            User.portalSpeed = Double.valueOf((sql.getSingleResult("purchases", "portal_speed", " where username = '"+User.getUser()+"' ").equals("") || sql.getSingleResult("purchases", "portal_speed", " where username = '"+User.getUser()+"' ").equals("nul")) ? "1.0" : forceDecimal(sql.getSingleResult("purchases", "portal_speed", " where username = '" + User.getUser() + "' "), "1.0"));
            User.perfectBonusAdd = Double.valueOf((sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '"+User.getUser()+"' ").equals("") || sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '"+User.getUser()+"' ").equals("nul")) ? "0.0" : forceDecimal(sql.getSingleResult("purchases", "perfect_bonus_add", " where username = '" + User.getUser() + "' "), "0.0"));
            User.perfectBonusModifier = Double.valueOf((sql.getSingleResult("user", "perfect_bonus_mod", " where username = '"+User.getUser()+"' ").equals("") || sql.getSingleResult("user", "perfect_bonus_mod", " where username = '"+User.getUser()+"' ").equals("nul")) ? "0.1" : forceDecimal(sql.getSingleResult("user", "perfect_bonus_mod", " where username = '" + User.getUser() + "' "), "0.1"));
            getSharedPreferences("Login", MODE_PRIVATE).edit().putLong("Time", 0).apply();
            getSharedPreferences("Login", MODE_PRIVATE).edit().putBoolean("LoggedIn", false).apply();
            getSharedPreferences("Login", MODE_PRIVATE).edit().putString("Username", getString(R.string.guest)).apply();
            setCoins();

            removeUserData();
        }
    }
    public void removeUserData(){
        sql.deleteAllUserData(User.getUser());
    }
    @Override
    public void onBackPressed() {
        if (Chartboost.onBackPressed()) {
            return;
        }else {
            User.add("Home Back Press");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.exit)).setMessage(context.getResources().getString(R.string.confirm_exit)).
                    setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            leaving = true;
                            sd.stop();
                            finishAffinity();
                            int pid = android.os.Process.myPid();
                            android.os.Process.killProcess(pid);
                        }
                    }).
                    setNegativeButton(context.getResources().getString(R.string.no), null);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            if(alertDialog.isShowing()) {
                int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                View divider = alertDialog.findViewById(dividerId);
                divider.setBackgroundColor(getResources().getColor(R.color.gold));
            }
        }
    }

    public void checkTables(){
        User.add("Checking Tables in Home");
        ArrayList<String> fPaks = new ArrayList<>();
        fPaks.add("Brittle Temple");
        fPaks.add("Flame Temple");
        fPaks.add("Chilled Temple");
        fPaks.add("Tidal Temple");
        fPaks.add("Spirit Temple");
        fPaks.add("Earth Temple");
        fPaks.add("Light Temple");
        HashMap<String, Integer> levelPacks = new HashMap<>();
        levelPacks.put("Tutorial", 1);
        levelPacks.put("Stout Temple", 1);
        levelPacks.put("Brittle Temple", 1);
        levelPacks.put("Flame Temple", 1);
        levelPacks.put("Chilled Temple", 1);
        levelPacks.put("Tidal Temple", 1);
        levelPacks.put("Spirit Temple", 1);
        levelPacks.put("Earth Temple", 1);
        levelPacks.put("Light Temple", 1);

        levelPacks.put("Crumbling Temple", 0);
        levelPacks.put("Elemental Temple", 0);
        levelPacks.put("Stalwart Temple", 0);
        levelPacks.put("Mystic Temple", 0);
        levelPacks.put("Sun Temple", 0);
        levelPacks.put("Sunken Temple", 0);
        levelPacks.put("Volcano Temple", 0);
        levelPacks.put("Cryptic Temple", 0);
        levelPacks.put("Arctic Temple", 0);
        //last three are unplayable atm - purposely
        levelPacks.put("Lost Temple", 0);
        levelPacks.put("Steam Temple", 0);
        levelPacks.put("Aqua Temple", 0);



        if(getSharedPreferences("DoThisOnce", MODE_PRIVATE).getBoolean("DropTables", false)){
            sql.dropTable("user");
            sql.dropTable("purchases");
            sql.dropTable("permissions_packs");
            sql.dropTable("permissions_levels");
            sql.dropTable("score_levels");
            sql.dropTable("purchases");
            getSharedPreferences("DoThisOnce",MODE_PRIVATE).edit().putBoolean("DropTables", false).apply();
            getSharedPreferences("Login", MODE_PRIVATE).edit().putString("Username", getString(R.string.guest)).apply();
            getSharedPreferences("Login", MODE_PRIVATE).edit().putLong("Time", 0).apply();
            getSharedPreferences("Login", MODE_PRIVATE).edit().putBoolean("LoggedIn", false).apply();
            getSharedPreferences("Login", MODE_PRIVATE).edit().putBoolean("LoggedIn", false).apply();

        }

            if(!sql.checkTable("user")){
                sql.createTable("user", "email TEXT, username TEXT, password TEXT, hint TEXT , country TEXT, hints INT, no_ads TEXT, rated TEXT, coins int, gold int, perfect_bonus_mod TEXT ");
            }
            if(!sql.checkTable("purchases")){
                sql.createTable("purchases", "username TEXT, speed TEXT,portal_speed TEXT, pro int, all_packs int, perfect_bonus_add TEXT, frozen_mod TEXT ");
            }
            if(!sql.checkTable("permissions_packs")){
                sql.createTable("permissions_packs", "username TEXT, pack TEXT, permission INT ");
            }
            if(!sql.checkTable("permissions_levels")){
                sql.createTable("permissions_levels", "username TEXT, pack TEXT, " +
                        "level_1 int," +
                        "level_2 int," +
                        "level_3 int," +
                        "level_4 int," +
                        "level_5 int," +
                        "level_6 int," +
                        "level_7 int," +
                        "level_8 int," +
                        "level_9 int," +
                        "level_10 int," +
                        "level_11 int," +
                        "level_12 int," +
                        "level_13 int," +
                        "level_14 int," +
                        "level_15 int," +
                        "level_16 int," +
                        "level_17 int," +
                        "level_18 int," +
                        "level_19 int," +
                        "level_20 int," +
                        "level_21 int," +
                        "level_22 int," +
                        "level_23 int," +
                        "level_24 int," +
                        "level_25 int," +
                        "level_26 int," +
                        "level_27 int," +
                        "level_28 int," +
                        "level_29 int," +
                        "level_30 int " +
                        "");
            }
            if(!sql.checkTable("score_levels")){
                sql.createTable("score_levels", "username TEXT, pack TEXT, levels INT, score INT, " +
                        "level_1 int," +
                        "level_2 int," +
                        "level_3 int," +
                        "level_4 int," +
                        "level_5 int," +
                        "level_6 int," +
                        "level_7 int," +
                        "level_8 int," +
                        "level_9 int," +
                        "level_10 int," +
                        "level_11 int," +
                        "level_12 int," +
                        "level_13 int," +
                        "level_14 int," +
                        "level_15 int," +
                        "level_16 int," +
                        "level_17 int," +
                        "level_18 int," +
                        "level_19 int," +
                        "level_20 int," +
                        "level_21 int," +
                        "level_22 int," +
                        "level_23 int," +
                        "level_24 int," +
                        "level_25 int," +
                        "level_26 int," +
                        "level_27 int," +
                        "level_28 int," +
                        "level_29 int," +
                        "level_30 int " +
                        "");
            }
        if(!sql.checkEntry("user", " where username = '"+User.getUser()+"' ")){
            sql.insert("user","email, username, password, hint, country, hints, no_ads, rated, coins, gold, perfect_bonus_mod ", "'','"+User.getUser()+"','','','', 3 ,'', '', 0, 0, '0.1' ");
        }
        if(!sql.checkEntry("purchases", " where username = '"+User.getUser()+"' ")){
            sql.insert("purchases","username, speed,portal_speed, pro, all_packs, perfect_bonus_add, frozen_mod ","'"+User.getUser()+"', '1.0','1.0', 0, 0, '0', '1.0' ");
        }
        for(String key : levelPacks.keySet()){
            if(!sql.checkEntry("permissions_packs", " where username = '"+User.getUser()+"' and pack = '"+key+"' ")){
                sql.insert("permissions_packs", " '"+User.getUser()+"', '"+key+"'," +
                                levelPacks.get(key)
                );
            }else{
                //if the user HAS the pack entry in the db, but its locked UNLOCK IT. but only if it is one of the free packs
                if(fPaks.contains(key) && (sql.getSingleResult("permissions_packs", "permission", " where username = '" + User.getUser() + "' and pack = '" + key + "' ")+"").equals("0")) {//if its already unlocked who cares.
                    sql.update("permissions_packs", " where username ='" + User.getUser() + "' and pack='" + key + "' ", "set permission = " + 1);
                }
            }
            if(!sql.checkEntry("permissions_levels", " where username = '"+User.getUser()+"' and pack = '"+key+"' ")){
                sql.insert("permissions_levels", "'"+User.getUser()+"'," +
                        "'"+key+"'," +
                        "1," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0 ");
            }
            if(!sql.checkEntry("score_levels", " where username = '"+User.getUser()+"' and pack = '"+key+"' ")) {
                sql.insert("score_levels", "'"+User.getUser()+"'," +
                        "'"+key+"'," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0," +
                        "0 ");
            }
        }
    }
    private class SetRanks extends AsyncTask<String, String, String> {

        private Context context;

        private SetRanks(Context context) {
            this.context = context;
        }
        @Override
        protected  void onPreExecute()
        {
            TextView world = (TextView) findViewById(R.id.world_rank);
            TextView nation = (TextView) findViewById(R.id.national_rank);
            TextView personal = (TextView) findViewById(R.id.personal_rank);
            TextView errors = (TextView) findViewById(R.id.error);

            errors.setVisibility(View.INVISIBLE);
            world.setText("-");
            nation.setText("-");
            personal.setText("-");
        }
        @Override
        protected void onProgressUpdate(String... params){
            if (!isCancelled()) {
                // Display current found address in a TextView
                TextView world = (TextView) findViewById(R.id.world_rank);
                TextView nation = (TextView) findViewById(R.id.national_rank);
                TextView personal = (TextView) findViewById(R.id.personal_rank);
                TextView errors = (TextView) findViewById(R.id.error);
                world.setText(params[0]);
                nation.setText(params[1]);
                personal.setText(params[2]);
                if(params[3].equals("N"))errors.setVisibility(View.VISIBLE);
                if(params[4].equals("N"))Toast.makeText(context, getResources().getString(R.string.enable_internet),Toast.LENGTH_SHORT).show();
            }
            super.onProgressUpdate(params);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                int nationalRank = 0;
                int worldRank = 0;
                int personalRank = 0;
                boolean internet = true;
                boolean logged = true;

                cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = cm.getActiveNetworkInfo();

                if(netInfo != null && !User.username.equals("")) {
                    ParseQuery worldQuery = new ParseQuery("score");
                    worldQuery.orderByDescending("score");
                    int count = 0;
                    try {
                        count = worldQuery.count();
                    } catch (ParseException pe) {
                    }
                    worldQuery.setLimit(count);
                    List<ParseObject> world = null;
                    try {
                        world = worldQuery.find();
                    }catch (ParseException pe){}
                    if(world != null) {
                        for (int x = 0; x < world.size(); x++) {
                            Leader leader = new Leader();
                            ParseObject temp = world.get(x);
                            leader.setPosition(x + 1);
                            leader.setUsername(temp.getString("username"));
                            leader.setScore(temp.getInt("score"));
                            if (worldLeaders != null) worldLeaders.add(leader);
                        }
                        if (worldLeaders != null) worldLeaders = filter(worldLeaders);
                    }

                    ParseQuery nationalQuery = new ParseQuery("score");
                    nationalQuery.whereContainedIn("username", getCountryUsers());
                    nationalQuery.orderByDescending("score");
                    count = 0;
                    try {
                        count = nationalQuery.count();
                    } catch (ParseException pe) {
                    }
                    nationalQuery.setLimit(count);
                    List<ParseObject> national = null;
                    try {
                        national = nationalQuery.find();
                    }catch (ParseException pe){}
                    if(national != null) {
                        for (int x = 0; x < national.size(); x++) {
                            Leader leader = new Leader();
                            ParseObject temp = national.get(x);
                            leader.setPosition(x + 1);
                            leader.setUsername(temp.getString("username"));
                            leader.setScore(temp.getInt("score"));
                            if (nationalLeaders != null) nationalLeaders.add(leader);
                        }
                        if (nationalLeaders != null) nationalLeaders = filter(nationalLeaders);
                    }


                    ParseQuery personalQuery = new ParseQuery("score");
                    personalQuery.whereContainedIn("username", getFriends(User.username));
                    personalQuery.orderByDescending("score");
                    count = 0;
                    try {
                        count = personalQuery.count();
                    } catch (ParseException pe) {
                    }
                    personalQuery.setLimit(count);
                    List<ParseObject> personal = null;
                    try {
                        personal = personalQuery.find();
                    }catch (ParseException pe){}
                    if(personal != null) {
                        for (int x = 0; x < personal.size(); x++) {
                            Leader leader = new Leader();
                            ParseObject temp = personal.get(x);
                            leader.setPosition(x + 1);
                            leader.setUsername(temp.getString("username"));
                            leader.setScore(temp.getInt("score"));
                            if (personalLeaders != null) personalLeaders.add(leader);
                        }
                        if (personalLeaders != null) personalLeaders = filter(personalLeaders);
                    }

                    for (int i = 0; i < (worldLeaders == null ? 0 : worldLeaders.size()); i++) {
                        if (worldLeaders.get(i).getUsername().equals(User.username)) {
                            worldRank = worldLeaders.get(i).getPosition();
                        }
                    }
                    for (int i = 0; i < (nationalLeaders == null ? 0 : nationalLeaders.size()); i++) {
                        if (nationalLeaders.get(i).getUsername().equals(User.username)) {
                            nationalRank = nationalLeaders.get(i).getPosition();
                        }
                    }
                    for (int i = 0; i < (personalLeaders == null ? 0 : personalLeaders.size()); i++) {
                        if (personalLeaders.get(i).getUsername().equals(User.username)) {
                            personalRank = personalLeaders.get(i).getPosition();
                        }
                    }
                }else if(User.username.equals("")){
                    logged = false;
                }else if(netInfo == null){
                    internet = false;
                }
                publishProgress(worldRank==0 ? "-" : Integer.toString(worldRank),nationalRank==0 ? "-" :  Integer.toString(nationalRank),personalRank==0 ? "-" :  Integer.toString(personalRank), (logged ? "Y" : "N"),(internet ? "Y" : "N"));
            }finally {}
            return null;
        }
    }
    public ArrayList<String> getFriends(String username) {
        final ArrayList<String> friendsUsername = new ArrayList<>();
        ParseQuery<ParseObject> friendsQuery = new ParseQuery<ParseObject>("user");
        friendsQuery.whereEqualTo("username", username);
        try {
            friendsArray = friendsQuery.getFirst();
        }catch (ParseException pe){}
        if (friendsArray != null) {
            JSONArray temp = friendsArray.getJSONArray("friends");
            if (temp != null) {
                for (int i=0;i<temp.length();i++){
                    try {
                        friendsUsername.add(temp.get(i).toString());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        friendsUsername.add(User.username);
        return friendsUsername;
    }
    public ArrayList<String> getCountryUsers(){
        Locale locale = Locale.getDefault();
        final ArrayList<String> usernames = new ArrayList<>();
        ParseQuery<ParseObject> usersQuery = new ParseQuery<ParseObject>("user");
        usersQuery.whereEqualTo("country", locale.getCountry());
        int count = 0;
        try {
            count = usersQuery.count();
        } catch (ParseException pe) {
        }
        usersQuery.setLimit(count);
        List<ParseObject> parseObjects = new ArrayList<>();
        try {
            parseObjects = usersQuery.find();
        }catch (ParseException pe){}
        for(int x = 0; x < parseObjects.size();x++){
            usernames.add(parseObjects.get(x).getString("username"));
        }
        return usernames;
    }
    public ArrayList<Leader> filter(ArrayList<Leader> leaders){
        ArrayList<Leader> distinctUsers = new ArrayList<>();
        ArrayList<String> distinctNames = new ArrayList<>();
        for(int i = 0; i < leaders.size();i++){
            if(distinctNames.contains(leaders.get(i).getUsername())){
                int index = distinctNames.indexOf(leaders.get(i).getUsername());
                if(distinctUsers.get(index).getScore() < leaders.get(i).getScore()){
                    distinctUsers.remove(index);
                    distinctNames.remove(index);
                    distinctUsers.add(leaders.get(i));
                    distinctNames.add(leaders.get(i).getUsername());
                }else{
                }
            }else{
                distinctNames.add(leaders.get(i).getUsername());
                distinctUsers.add(leaders.get(i));
            }
        }
        for(int i = 0; i < distinctUsers.size(); i++){
            distinctUsers.get(i).setPosition(i + 1);
        }
        return distinctUsers;
    }
    public void onPause(){
        super.onPause();
        Chartboost.onPause(this);
        AdColony.pause();
        AppsFlyerLib.onActivityPause(this);
        if(!leaving){
            sd.stop();
        }
    }
    public void onDestroy(){
        super.onDestroy();
        bp.release();
        Chartboost.onDestroy(this);
        timerHandler.removeCallbacks(timerRunnable);
        forceHandler.removeCallbacks(forceRunnable);
        task.cancel(true);

        //context = null;
        //sql = null;
        //sd = null;
        //task = null;
        //coinsDialog = null;
        //timerHandler = null;

        login = null;
        leader = null;
        play = null;
        store = null;
        settings = null;
        about = null;
        profile = null;
        refresh = null;
        silverCount = null;
        goldCount = null;
        videoCoinsGold = null;
        videoCoinsSilver = null;

         loginStatus = null;
         world = null;
         national = null;
         personal = null;
         error = null;

        //coinsStore = null;

        //nationalLeaders = null;
        //worldLeaders  = null;
        //personalLeaders = null;

        //friendsArray = null;

        //netInfo = null;
        //cm = null;

        System.gc();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
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
                boolean doubleGold =getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getBoolean("2x_coins", false);
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
                    coinsDialog.resetDoubleGold();
                    User.add("Consumed Purchase.");
                    try {
                        Intent purchaseIntent = new Intent(context, PurchaseInfo.class);
                        purchaseIntent.putExtra(PurchaseInfo.Object, "Purchase");
                        startService(purchaseIntent);
                    } catch (Exception e) {
                    }
                    if (!User.username.equals("")) {
                        ParseService.updateSync("user", false);
                        Intent userIntent = new Intent(Home.this, ParseService.class);
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
    public void showFinished(){}
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
    public void sendErrors(){
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        String error = getSharedPreferences("Exception", MODE_PRIVATE).getString("Uncaught", "");
        if(error == null){
            error = "";
        }
        if(!error.equals("") && netInfo != null){
            Intent purchaseIntent = new Intent(context, PurchaseInfo.class);
            purchaseIntent.putExtra(PurchaseInfo.Object, "Error");
            startService(purchaseIntent);
        }
    }


}