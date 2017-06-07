package lucky8s.shift;

import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christian on 4/5/2015.
 */
public class FinishedDialog extends DialogFragment implements View.OnClickListener{

    SQL sql;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    SoundDriver sd;
    StoreDialog speedDialog;
    StoreDialog removeAdsDialog;
    StoreDialog portalsDialog;
    StoreDialog perfectDialog;
    Handler timerHandler = new Handler();
    DialogListener listener;

    Button next;
    Button reset;

    TextView totalMoves;
    TextView time;
    TextView score;
    TextView newBest;
    TextView moves;
    TextView level_num;
    TextView possibleMoves;
    TextView possibleTime;
    TextView getsBonus;
    TextView coinMessage;

    View dude;
    View rink;

    Star star1 = new Star();
    Star star2 = new Star();
    Star star3 = new Star();

    View tempStar1;
    View tempStar2;
    View tempStar3;

    LinearLayout coinsContainer;

    AnimatorSet run;

    String finalScoreSt;
    String level;
    String pack;
    String levelScore;

    int movesInt;
    int minute;
    int second;
    int totalSeconds;
    int coins;

    boolean showSpeedDialog = false;
    boolean showRemoveAdsDialog = false;
    boolean showPortalsDialog = false;
    boolean showPerfectDialog = false;

    boolean usedHints;
    boolean ran;
    boolean firstAttempt;

    int hintsUsed = 0;

    public FinishedDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.finished_dialog, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        User.add("In Finished Dialog");

        sd = MyApplication.getInstance().getSD();
        sql = new SQL(getDialog().getContext());
        cm = (ConnectivityManager) getDialog().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        reset = (Button) view.findViewById(R.id.reset);
        reset.setOnClickListener(this);
        next = (Button) view.findViewById(R.id.next);
        next.setOnClickListener(this);
        totalMoves = (TextView) view.findViewById(R.id.total_moves);
        score = (TextView) view.findViewById(R.id.score_total);
        level_num = (TextView) view.findViewById(R.id.level_num);
        coinMessage = (TextView) view.findViewById(R.id.coins);
        possibleMoves = (TextView) view.findViewById(R.id.possible_moves);
        possibleTime = (TextView) view.findViewById(R.id.possible_time);
        time = (TextView) view.findViewById(R.id.time);
        getsBonus = (TextView) view.findViewById(R.id.bonus_amount);
        newBest = (TextView) view.findViewById(R.id.new_best);

        coinsContainer = (LinearLayout) view.findViewById(R.id.coins_container);

        moves = (TextView) getActivity().findViewById(R.id.moves);
        dude = getActivity().findViewById(R.id.dude);
        rink = getActivity().findViewById(R.id.rink);
        tempStar1 = view.findViewById(R.id.star_1);
        tempStar2 = view.findViewById(R.id.star_2);
        tempStar3 = view.findViewById(R.id.star_3);

        movesInt = getDialog().getContext().getSharedPreferences("PLAY", Context.MODE_PRIVATE).getInt("moves", 0);
        minute = getDialog().getContext().getSharedPreferences("PLAY", Context.MODE_PRIVATE).getInt("minutes", 0);
        second = getDialog().getContext().getSharedPreferences("PLAY", Context.MODE_PRIVATE).getInt("seconds", 0);
        usedHints = getDialog().getContext().getSharedPreferences("PLAY", Context.MODE_PRIVATE).getBoolean("used_hints", false);
        hintsUsed = getDialog().getContext().getSharedPreferences("PLAY", Context.MODE_PRIVATE).getInt("hints_used", 0);

        totalSeconds = (minute*60) + second;

        pack = getDialog().getContext().getSharedPreferences("LEVELS", 0).getString("PACK", "Stout Temple");
        level = Integer.toString(getDialog().getContext().getSharedPreferences("LEVELS", 0).getInt("LEVEL", 1));

        firstAttempt = sql.getSingleResult("score_levels", "level_"+level+" ", " where pack = '"+pack+"' and username = '"+User.getUser()+"'").equals("0");

        time.setText(String.format("%02d",minute)+":"+String.format("%02d",second));
        levelScore = calculateScore();
        level_num.setText(level);
        showStars(Integer.valueOf(levelScore));


        totalMoves.setText(Integer.toString(movesInt));

        unlockNext();

        return view;
    }
    public void showStars(int score){
        if(star1 == null){
            star1 = new Star();
        }
        if(star2 == null){
            star2 = new Star();
        }
        if(star3 == null){
            star3 = new Star();
        }
        if(tempStar1 != null && star1 != null){
            star1.setView(tempStar1);
            star1.setDuration(1000);
        }else{
            star1 = null;
        }
        if(tempStar2 != null && star2 != null){
            star2.setView(tempStar2);
            star2.setDuration(1000);
        }else{
            star2 = null;
        }
        if(tempStar3 != null && star3 != null){
            star3.setView(tempStar3);
            star3.setDuration(1000);
        }else{
            star3 = null;
        }
        boolean go1 = false;
        boolean go2 = false;
        boolean go3 = false;

        if(score >= 30 && star1 != null){
            go1 = true;
        }
        if(score >= 75 && star2 != null){
            go2 = true;
        }
        if(score >= 95 && star3 != null){
            go3 = true;
        }
        run = new AnimatorSet();
        if(go1 && !go2 && !go3){
            run.play(star1.getAnimator());
        }else if(go1 && go2 && !go3){
            star2.delay(250);
            run.play(star1.getAnimator());
            run.play(star2.getAnimator());
        }else if(go1 && go2 && go3){
            star2.delay(250);
            star3.delay(500);
            run.play(star1.getAnimator());
            run.play(star2.getAnimator());
            run.play(star3.getAnimator());
        }
        if(timerHandler == null){
            timerHandler = new Handler();
        }
        timerHandler.postDelayed(timerRunnable, 0);
        System.out.println("good to gooooooo");
    }
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(!ran && star1 != null && star2 != null && star3 != null){
                if(star1.getSD().isLoaded("star") && star2.getSD().isLoaded("star") && star3.getSD().isLoaded("star")){
                    run.start();
                    ran = true;
                    timerHandler.removeCallbacksAndMessages(timerRunnable);
                }
            }
            timerHandler.postDelayed(this, 250);
        }
    };
    public String calculateScore(){

        int currLevel = Integer.valueOf(level);
        int lowestMoves = 0;
        String currPack = pack;
        Generator generator = new Generator(currPack, currLevel, getDialog().getContext(), getActivity());

        lowestMoves = generator.getMovesCount();

        final String currentBest = sql.getSingleResult("score_levels", "level_" + Integer.toString(currLevel), " where username = '" + User.getUser() + "' and pack = '" + currPack + "' ");
        int best = currentBest.equals("") ? 0 : Integer.valueOf(currentBest);

        double moves_weight = 0.6;
        double time_weight = 0.4;
        double lowestTime = lowestMoves/2.0;
        double lowestTimeMinutes = lowestTime/60.0;
        String lowestMinutes = String.format("%02d",Math.round((int) lowestTimeMinutes));

        String minutes = lowestTime > 60 ? (Double.toString(lowestTimeMinutes).contains(".") ? Double.toString(lowestTimeMinutes).substring(0,Double.toString(lowestTimeMinutes).indexOf(".")) : lowestMinutes) : String.format("%02d",00);
        String seconds = lowestTime < 60 ? String.format("%02d",(int)Math.ceil(lowestTime)) : String.format("%02d",(int)lowestTime%60);


        possibleMoves.setText(Integer.toString(lowestMoves));
        possibleTime.setText(minutes+":"+seconds);

        double moves_score = (((lowestMoves - (movesInt - lowestMoves))/(double)lowestMoves)*100.00)*moves_weight;
        if(moves_score > (100.00*moves_weight)) moves_score = 100.00*moves_weight;
        double time_score = (((Math.round(lowestTime) - (totalSeconds - Math.round(lowestTime)))/lowestTime)*100.00)*time_weight;
        if(time_score > (100.00*time_weight)) time_score = 100.00*time_weight;
        double finalScore = currPack.equals("Tutorial") ? ((double)lowestMoves/(double)movesInt)*100.00 : ((moves_score > 1 ? moves_score : 1) + (time_score > 1 ? time_score : 1));
        int bonus = (int)Math.round(finalScore*User.perfectBonusModifier);
        if(!usedHints && firstAttempt && (movesInt <= lowestMoves) && (totalSeconds <= (int)lowestTime) && !currPack.equals("Tutorial")){
            getsBonus.setText(Integer.toString(bonus));
            finalScore = finalScore + bonus;
            User.perfectBonusModifier = (User.perfectBonusModifier+User.perfectBonusAdd) <= 5.00 ? (User.perfectBonusModifier+User.perfectBonusAdd) : 5.00;
            sql.update("user", " where username = '"+User.getUser()+"' ", " set perfect_bonus_mod = '"+String.format("%.3f",User.perfectBonusModifier)+"' ");
            coins = 20;
        }
        if(usedHints){
            if(hintsUsed == 2){
                if(finalScore > 75){
                    finalScore = 75;
                }
            }else if(hintsUsed == 3){
                if(finalScore > 50){
                    finalScore = 50;
                }
            }
        }
        if(finalScore < 1) finalScore = 1;
        String currentScore = Integer.toString((int) Math.round(finalScore));
        if(finalScore <= best){
            finalScore = best;
        }else{
            newBest.setVisibility(View.VISIBLE);
        }

        finalScoreSt = Integer.toString((int) Math.round(finalScore));

        coins += (int)Math.floor((Integer.valueOf(currentScore)-best)/10.0);
        if(coins > 0){
            coinMessage.setText("+"+Integer.toString(coins));
            sql.addCoins(coins, "silver");
        }else{
            coinMessage.setVisibility(View.INVISIBLE);
            coinsContainer.setVisibility(View.INVISIBLE);
        }
        boolean hasPortals = getDialog().getContext().getSharedPreferences("Obstacles", Context.MODE_PRIVATE).getBoolean("Portals", false);
        int levelsLagged = getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getInt("levels_lagged", 0);
        int levelsLaggedPortals = getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getInt("levels_lagged_portals", 0);
        int levelsPerfect = getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).getInt("levels_perfect", 0);

        boolean sawSpeed = getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).getBoolean("saw_speed", false);
        boolean sawPortal = getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).getBoolean("saw_portal", false);
        boolean sawPerfect = getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).getBoolean("saw_perfect", false);

        if(!pack.equals("Tutorial") && User.speed < 1.5 && movesInt <= lowestMoves && totalSeconds > Math.round(lowestTime) && !sawSpeed && !hasPortals) {
            if (levelsLagged+1 >= Config.SPEED_DIALOG_INTERVAL) {
                showSpeedDialog = true;
                speedDialog = new StoreDialog(StoreDialog.SPEED_BOOST, true, .5, listener);
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.speed_boost_message)).apply();
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.save_50)).apply();
            }else{
                levelsLagged++;
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putInt("levels_lagged", levelsLagged).apply();
            }
        }
        if(!pack.equals("Tutorial") && User.portalSpeed < 1.5 && movesInt <= lowestMoves && totalSeconds > Math.round(lowestTime) && hasPortals && !sawPortal) {
            if (levelsLaggedPortals+1 >= Config.PORTALS_DIALOG_INTERVAL) {
                showPortalsDialog = true;
                portalsDialog = new StoreDialog(StoreDialog.PORTAL_MASTER, true, .5, listener);
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.portal_master_message)).apply();
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.save_50)).apply();
            }else{
                levelsLaggedPortals++;
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putInt("levels_lagged_portals", levelsLaggedPortals).apply();
            }
        }
        if(!pack.equals("Tutorial") && User.perfectBonusAdd == 0 && movesInt <= lowestMoves && totalSeconds <= Math.round(lowestTime) && !sawPerfect) {
            if (levelsPerfect+1 >= Config.PERFECT_DIALOG_INTERVAL) {
                showPerfectDialog = true;
                perfectDialog = new StoreDialog(StoreDialog.PERFECTIONIST, true, .5, listener);
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.perfectionist_message)).apply();
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.save_50)).apply();
            }else{
                levelsPerfect++;
                getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putInt("levels_perfect", levelsPerfect).apply();
            }
        }

        int numIntImpressions = getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).getInt("interstitial_impressions",0);
        boolean sawAds1 = getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).getBoolean("saw_ads_1",false);
        boolean sawAds2 = getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).getBoolean("saw_ads_2",false);
        boolean sawAds3 = getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).getBoolean("saw_ads_3",false);

        String no_ads = "";
        if(sql == null) {
            sql = new SQL(getDialog().getContext());
        }
        no_ads = sql.getSingleResult("user", "no_ads", " where username = '" + User.getUser() + "' ");
        if(numIntImpressions >= Config.ADS_DIALOG_INTERVAL_1 && numIntImpressions < Config.ADS_DIALOG_INTERVAL_2 && !sawAds1 && !no_ads.contains("all")){
            showRemoveAdsDialog = true;
            removeAdsDialog = new StoreDialog(StoreDialog.NO_ADS_BUNDLE, true, .1, null);
            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.remove_ads_message)).apply();
            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.save_10)).apply();
            getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_ads_1",true).apply();
        }else if(numIntImpressions >= Config.ADS_DIALOG_INTERVAL_2 && numIntImpressions < Config.ADS_DIALOG_INTERVAL_3 && !sawAds2 && !no_ads.contains("all")){
            showRemoveAdsDialog = true;
            removeAdsDialog = new StoreDialog(StoreDialog.NO_ADS_BUNDLE, true, .25, null);
            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.remove_ads_message)).apply();
            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.save_25)).apply();
            getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_ads_2",true).apply();
        }else if(numIntImpressions >= Config.ADS_DIALOG_INTERVAL_3 && !sawAds3 && !no_ads.contains("all")){
            showRemoveAdsDialog = true;
            removeAdsDialog = new StoreDialog(StoreDialog.NO_ADS_BUNDLE, true, .5, null);
            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.remove_ads_message)).apply();
            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.save_50)).apply();
            getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_ads_3",true).apply();
        }
        score.setText(currentScore);
        sql.update("score_levels", " where username = '" + User.getUser() + "' and pack = '" + pack + "' ", " set level_" + level + " = " + finalScoreSt);
        sql.update("score_levels", " where username = '" + User.getUser() + "' and pack = '" + pack + "' ", " set levels = " +
                "(select (case when level_1 > 0 then 1 else 0 end+ " +
                "case when level_2 > 0 then 1 else 0  end+ " +
                "case when level_3 > 0 then 1 else 0  end+ " +
                "case when level_4 > 0 then 1 else 0  end+ " +
                "case when level_5 > 0 then 1 else 0  end+ " +
                "case when level_6 > 0 then 1 else 0  end+ " +
                "case when level_7 > 0 then 1 else 0  end+ " +
                "case when level_8 > 0 then 1 else 0  end+ " +
                "case when level_9 > 0 then 1 else 0  end+ " +
                "case when level_10 > 0 then 1 else 0  end+ " +
                "case when level_11 > 0 then 1 else 0  end+ " +
                "case when level_12 > 0 then 1 else 0  end+ " +
                "case when level_13 > 0 then 1 else 0  end+ " +
                "case when level_14 > 0 then 1 else 0  end+ " +
                "case when level_15 > 0 then 1 else 0  end+ " +
                "case when level_16> 0 then 1 else 0  end+ " +
                "case when level_17 > 0 then 1 else 0  end+ " +
                "case when level_18 > 0 then 1 else 0  end+ " +
                "case when level_19 > 0 then 1 else 0  end+ " +
                "case when level_20 > 0 then 1 else 0  end+ " +
                "case when level_21 > 0 then 1 else 0  end+ " +
                "case when level_22 > 0 then 1 else 0  end+ " +
                "case when level_23 > 0 then 1 else 0  end+ " +
                "case when level_24 > 0 then 1 else 0  end+ " +
                "case when level_25 > 0 then 1 else 0  end+ " +
                "case when level_26> 0 then 1 else 0  end+ " +
                "case when level_27 > 0 then 1 else 0  end+ " +
                "case when level_28 > 0 then 1 else 0  end+ " +
                "case when level_29 > 0 then 1 else 0  end+ " +
                "case when level_30 > 0 then 1 else 0 end) from score_levels " +
                " where username = '" + User.getUser() + "' and pack = '" + pack + "') ");
        sql.update("score_levels", " where username = '" + User.getUser() + "' and pack = '" + pack + "' ", " set score = " +
                "(select (case when level_1 > 0 then level_1 else 0 end+ " +
                "case when level_2 > 0 then level_2 else 0  end+ " +
                "case when level_3 > 0 then level_3 else 0  end+ " +
                "case when level_4 > 0 then level_4 else 0  end+ " +
                "case when level_5 > 0 then level_5 else 0  end+ " +
                "case when level_6 > 0 then level_6 else 0  end+ " +
                "case when level_7 > 0 then level_7 else 0  end+ " +
                "case when level_8 > 0 then level_8 else 0  end+ " +
                "case when level_9 > 0 then level_9 else 0  end+ " +
                "case when level_10 > 0 then level_10 else 0  end+ " +
                "case when level_11 > 0 then level_11 else 0  end+ " +
                "case when level_12 > 0 then level_12 else 0  end+ " +
                "case when level_13 > 0 then level_13 else 0  end+ " +
                "case when level_14 > 0 then level_14 else 0  end+ " +
                "case when level_15 > 0 then level_15 else 0  end+ " +
                "case when level_16> 0 then level_16 else 0  end+ " +
                "case when level_17 > 0 then level_17 else 0  end+ " +
                "case when level_18 > 0 then level_18 else 0  end+ " +
                "case when level_19 > 0 then level_19 else 0  end+ " +
                "case when level_20 > 0 then level_20 else 0  end+ " +
                "case when level_21 > 0 then level_21 else 0  end+ " +
                "case when level_22 > 0 then level_22 else 0  end+ " +
                "case when level_23 > 0 then level_23 else 0  end+ " +
                "case when level_24 > 0 then level_24 else 0  end+ " +
                "case when level_25 > 0 then level_25 else 0  end+ " +
                "case when level_26> 0 then level_26 else 0  end+ " +
                "case when level_27 > 0 then level_27 else 0  end+ " +
                "case when level_28 > 0 then level_28 else 0  end+ " +
                "case when level_29 > 0 then level_29 else 0  end+ " +
                "case when level_30 > 0 then level_30 else 0 end) from score_levels " +
                " where username = '" + User.getUser() + "' and pack = '" + pack + "') ");
        if (!User.username.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2500);
                        ParseService.updateSync("permissions_packs", false);
                        Intent packsIntent = new Intent(getDialog().getContext(), ParseService.class);
                        packsIntent.putExtra(ParseService.Object, "permissions_packs");
                        getDialog().getContext().startService(packsIntent);
                        ParseService.updateSync("permissions_levels", false);
                        Intent levelsIntent = new Intent(getDialog().getContext(), ParseService.class);
                        levelsIntent.putExtra(ParseService.Object, "permissions_levels");
                        getDialog().getContext().startService(levelsIntent);
                        ParseService.updateSync("user", false);
                        Intent userIntent = new Intent(getDialog().getContext(), ParseService.class);
                        userIntent.putExtra(ParseService.Object, "user");
                        getDialog().getContext().startService(userIntent);
                        ParseService.updateSync("purchases", false);
                        Intent purchasesIntent = new Intent(getDialog().getContext(), ParseService.class);
                        purchasesIntent.putExtra(ParseService.Object, "purchases");
                        getDialog().getContext().startService(purchasesIntent);
                        ParseService.updateSync("score_levels", false);
                        Intent scoreLevelsIntent = new Intent(getDialog().getContext(), ParseService.class);
                        scoreLevelsIntent.putExtra(ParseService.Object, "score_levels");
                        getDialog().getContext().startService(scoreLevelsIntent);
                        ParseService.updateSync("score", false);
                        Intent scoreIntent = new Intent(getDialog().getContext(), ParseService.class);
                        scoreIntent.putExtra(ParseService.Object, "score");
                        getDialog().getContext().startService(scoreIntent);
                    }catch (Exception e){}
                }
            }).start();
        }

        return currentScore;


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
        getDialog().getWindow().setLayout((width / 10) * 8, (height / 10) * 7);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
    public void buttonClick(View button) {
        final View view = button;

        new Thread() {
            @Override
            public void run() {
                if(getDialog() != null && getDialog().getOwnerActivity() != null) {

                    getDialog().getOwnerActivity().runOnUiThread(new Runnable() {//null pointer when testing fire pack 27...but happened once and never again - NULL POINTER testing Sun Temple lvl 29, it might just be my phone...
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
        if(v instanceof Button){
            buttonClick(v);
        }
        switch (v.getId()) {
            case R.id.next:
                sd.buttonPress();
                if(showRemoveAdsDialog){
                    User.add("Showing Remove Ads Dialog");
                    showRemoveAdsDialog = false;
                    FragmentManager fm = getFragmentManager();
                    android.support.v4.app.Fragment dialog = getFragmentManager().findFragmentByTag("remove_ads_dialog");
                    if(dialog == null) {
                        if(!removeAdsDialog.isAdded()) {
                            removeAdsDialog.setCancelable(true);
                            removeAdsDialog.show(fm, "remove_ads_dialog");
                        }
                    }else if(dialog.getActivity() != getDialog().getOwnerActivity()) {
                    }else {
                    }
                }else if (showSpeedDialog) {
                    User.add("Showing Speed Dialog");
                    showSpeedDialog = false;
                    FragmentManager fm = getFragmentManager();
                    android.support.v4.app.Fragment dialog = getFragmentManager().findFragmentByTag("speed_dialog");
                    if(dialog == null) {
                        if(!speedDialog.isAdded()) {
                            speedDialog.setCancelable(true);
                            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putInt("levels_lagged", 0).apply();
                            getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_speed",true).apply();
                            speedDialog.show(fm, "speed_dialog");
                        }
                    }else if(dialog.getActivity() != getDialog().getOwnerActivity()) {
                    }else {
                    }
                }else if (showPortalsDialog) {
                    User.add("Showing Portals Dialog");
                    showPortalsDialog = false;
                    FragmentManager fm = getFragmentManager();
                    android.support.v4.app.Fragment dialog = getFragmentManager().findFragmentByTag("portals_dialog");
                    if(dialog == null) {
                        if(!portalsDialog.isAdded()) {
                            portalsDialog.setCancelable(true);
                            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putInt("levels_lagged_portals", 0).apply();
                            getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_portal",true).apply();
                            portalsDialog.show(fm, "portals_dialog");
                        }
                    }else if(dialog.getActivity() != getDialog().getOwnerActivity()) {
                    }else {
                    }
                }else if (showPerfectDialog) {
                    User.add("Showing Perfect Dialog");
                    showPerfectDialog = false;
                    FragmentManager fm = getFragmentManager();
                    android.support.v4.app.Fragment dialog = getFragmentManager().findFragmentByTag("perfect_dialog");
                    if(dialog == null) {
                        if(!perfectDialog.isAdded()) {
                            perfectDialog.setCancelable(true);
                            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putInt("levels_perfect", 0).apply();
                            getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_perfect",true).apply();
                            perfectDialog.show(fm, "perfect_dialog");
                        }
                    }else if(dialog.getActivity() != getDialog().getOwnerActivity()) {
                    }else {
                    }
                } else {
                    User.add("Going to next level");
                    goToNext();
                }
                break;
            case R.id.reset:
                sd.buttonPress();
                if(showRemoveAdsDialog){
                    User.add("Showing Remove Ads Dialog");
                    showRemoveAdsDialog = false;
                    FragmentManager fm = getFragmentManager();
                    android.support.v4.app.Fragment dialog = getFragmentManager().findFragmentByTag("remove_ads_dialog");
                    if(dialog == null) {
                        if(!removeAdsDialog.isAdded()) {
                            removeAdsDialog.setCancelable(true);
                            removeAdsDialog.show(fm, "remove_ads_dialog");
                        }
                    }else if(dialog.getActivity() != getDialog().getOwnerActivity()) {
                    }else {
                    }
                }else if (showSpeedDialog) {
                    User.add("Showing Speed Dialog");
                    showSpeedDialog = false;
                    FragmentManager fm = getFragmentManager();
                    android.support.v4.app.Fragment dialog = getFragmentManager().findFragmentByTag("speed_dialog");
                    if(dialog == null) {
                        if(!speedDialog.isAdded()) {
                            speedDialog.setCancelable(true);
                            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putInt("levels_lagged", 0).apply();
                            getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_speed",true).apply();
                            speedDialog.show(fm, "speed_dialog");
                        }
                    }else if(dialog.getActivity() != getDialog().getOwnerActivity()) {
                    }else {
                    }
                }else if (showPortalsDialog) {
                    User.add("Showing Portals Dialog");
                    showPortalsDialog = false;
                    FragmentManager fm = getFragmentManager();
                    android.support.v4.app.Fragment dialog = getFragmentManager().findFragmentByTag("portals_dialog");
                    if(dialog == null) {
                        if(!portalsDialog.isAdded()) {
                            portalsDialog.setCancelable(true);
                            getDialog().getContext().getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putInt("levels_lagged_portals", 0).apply();
                            getDialog().getContext().getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_portal",true).apply();
                            portalsDialog.show(fm, "portals_dialog");
                        }
                    }else if(dialog.getActivity() != getDialog().getOwnerActivity()) {
                    }else {
                    }
                } else{

                    int currentLevel = Integer.valueOf(level);
                    String currentPack = pack;
                    int nextLevel = currentLevel<(currentPack.equals("Tutorial") ? 6 : 30) ? currentLevel+1 : 1;
                    User.add("Resetting Level");
                    if (currentLevel<nextLevel) {
                        reset();
                    }else if(firstAttempt){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getDialog().getContext());
                        alertDialogBuilder.setTitle(getDialog().getContext().getResources().getString(R.string.pack_finish_congratulation)).setMessage(getDialog().getContext().getResources().getString(R.string.pack_finish_message) + " " + pack).
                                setPositiveButton(getDialog().getContext().getResources().getString(R.string.pack_finish_reward_gold), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getDialog().dismiss();
                                        sql.addCoins(2, "gold");
                                        reset();
                                    }
                                }).
                                setNegativeButton(getDialog().getContext().getResources().getString(R.string.pack_finish_reward_silver), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getDialog().dismiss();
                                        sql.addCoins(200, "silver");
                                        reset();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        if (alertDialog.isShowing()) {
                            int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                            View divider = alertDialog.findViewById(dividerId);
                            divider.setBackgroundColor(getResources().getColor(R.color.gold));
                        }
                    }else{
                        reset();
                    }
                }
                break;
            default:
                break;
        }
        v.getBackground().clearColorFilter();
    }
    public void reset(){
        getDialog().dismiss();
        Intent intent = new Intent(getDialog().getContext(), Play.class);
        startActivity(intent);
        getDialog().getOwnerActivity().finish();
    }
    public void goToNext(){
        int currentLevel = Integer.valueOf(level);
        String currentPack = pack;
        int nextLevel = currentLevel<(currentPack.equals("Tutorial") ? 6 : 30) ? currentLevel+1 : 1;
        getDialog().getContext().getSharedPreferences("LEVELS", Context.MODE_PRIVATE)
                .edit()
                .putInt("LEVEL", nextLevel)
                .apply();
        Intent a;

        if(currentLevel<nextLevel){
            a = new Intent(getDialog().getContext(), Play.class);
            startActivity(a);
            getDialog().getOwnerActivity().finish();
        }else {
            if (firstAttempt) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getDialog().getOwnerActivity());
                alertDialogBuilder.setCancelable(false).setTitle(getDialog().getContext().getResources().getString(R.string.pack_finish_congratulation)).setMessage(getDialog().getContext().getResources().getString(R.string.pack_finish_message) + " " + currentPack).
                        setPositiveButton(getDialog().getContext().getResources().getString(R.string.pack_finish_reward_gold), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getDialog().dismiss();
                                sql.addCoins(2, "gold");
                                Intent a = new Intent(getDialog().getContext(), Levels.class);
                                startActivity(a);
                                getDialog().getOwnerActivity().finish();
                            }
                        }).
                        setNegativeButton(getDialog().getContext().getResources().getString(R.string.pack_finish_reward_silver), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getDialog().dismiss();
                                sql.addCoins(200, "silver");
                                Intent a = new Intent(getDialog().getContext(), Levels.class);
                                startActivity(a);
                                getDialog().getOwnerActivity().finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                if(alertDialog.isShowing()) {
                    int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                    View divider = alertDialog.findViewById(dividerId);
                    divider.setBackgroundColor(getResources().getColor(R.color.gold));
                }
            }else{
                a = new Intent(getDialog().getContext(), Levels.class);
                startActivity(a);
                getDialog().getOwnerActivity().finish();
            }
        }
    }
    public void unlockNext(){
        final int currentLevel = Integer.valueOf(level);
        int nextLevel = currentLevel<30?currentLevel+1:1;

        String unlockPack = "";
        /*        levelPacks.put("Tutorial", 1);
        levelPacks.put("Stout Temple", 1);
        levelPacks.put("Brittle Temple", 1);
        levelPacks.put("Flame Temple", 1);
        levelPacks.put("Chilled Temple", 1);
        levelPacks.put("Tidal Temple", 1);
        levelPacks.put("Spirit Temple", 1);
        levelPacks.put("Earth Temple", 1);
        levelPacks.put("Light Temple", 1);*/
        //leaving this in there because it doesn't hurt anything

        if(currentLevel<nextLevel) {
            sql.update("permissions_levels", " where username = '" + User.getUser() + "' and pack = '" + pack + "' ", " set level_" + Integer.toString(nextLevel) + " = 1 ");
        }else{
            switch(pack){
                case "Stout Temple":
                    unlockPack = "Brittle Temple";
                    break;
                case "Brittle Temple":
                    unlockPack = "Flame Temple";
                    break;
                case "Flame Temple":
                    unlockPack = "Chilled Temple";
                    break;
                /*
                case "Aqua Temple":
                    unlockPack = "Chilled Temple";
                    break;
                    */
                case "Chilled Temple":
                    unlockPack = "Tidal Temple";
                    break;
                case "Tidal Temple":
                    unlockPack = "Spirit Temple";
                    break;
                case "Spirit Temple":
                    unlockPack = "Earth Temple";
                    break;
                case "Earth Temple":
                    unlockPack = "Light Temple";
                    break;
                default: unlockPack = "";
            }

            if(!unlockPack.equals("")){
                sql.update("permissions_levels", " where username = '" + User.getUser() + "' and pack = '" + unlockPack + "' ", " set level_1 = 1 ");
                sql.update("permissions_packs", " where username = '" + User.getUser() + "' and pack = '" + unlockPack + "' ", " set permission = 1 ");
            }
        }
    }
    public void onDestroy(){
        super.onDestroy();

        timerHandler.removeCallbacks(timerRunnable);

        //sql = null;
        //cm = null;
        //netInfo = null;
        //sd = null;
        //timerHandler= null;

        //next = null;
        //reset = null;

         //totalMoves = null;
        //time = null;
         //score = null;
         //newBest = null;
         //moves = null;
         //level_num = null;
         //possibleMoves = null;
         //possibleTime = null;
         //getsBonus = null;
         //coinMessage = null;
        //coinsContainer = null;

         //dude = null;
         //rink = null;

        star1 = null;
        star2 = null;
        star3 = null;

         tempStar1 = null;
         tempStar2 = null;
         tempStar3 = null;

        //run = null;

        System.gc();

    }
 }
