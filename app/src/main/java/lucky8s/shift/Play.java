package lucky8s.shift;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.rateme.OnRatingListener;
import com.androidsx.rateme.RateMeDialog;
import com.androidsx.rateme.RateMeDialogTimer;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.chartboost.sdk.Chartboost;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jirbo.adcolony.AdColony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static lucky8s.shift.User.add;

public class Play extends FragmentActivity implements DialogListener, AdInterface{

    Context context;
    SQL sql;
    FragmentManager fm = getSupportFragmentManager();
    TelephonyManager tm;
    FinishedDialog finishedDialog = new FinishedDialog();
    HintsDialog hintsDialog = new HintsDialog(this);
    CoinsDialog coinsDialog = new CoinsDialog(this);
    TutorialDialog tutorialDialog;
    AdRequest adRequest;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    SoundDriver sd;
    GestureDetectorCompat gDetect;
    ProgressDialog progress;
    Generator generator;

    TextView moves;
    TextView seconds;
    TextView minutes;
    TextView number_hints;

    Button restart;
    Button previous;
    Button hint;

    View finish;
    View rink;
    View dude;
    View closest;
    View incentiveObstacle;
    View incentive;
    View lastPortal;

    lucky8s.shift.EtchView packName;
    lucky8s.shift.EtchView levelName;

    AdView banner;

    ObjectAnimator translateRight;
    ObjectAnimator translateLeft;
    ObjectAnimator translateUp;
    ObjectAnimator translateDown;
    ObjectAnimator moveToPortal;
    ObjectAnimator moveFromPortal;
    ObjectAnimator shrinkX;
    ObjectAnimator shrinkY;
    ObjectAnimator expandX;
    ObjectAnimator expandY;
    ObjectAnimator startX;
    ObjectAnimator startY;
    ObjectAnimator finishX;
    ObjectAnimator finishY;
    ObjectAnimator bump;

    AnimatorSet scaleDown;
    AnimatorSet scaleUp;
    AnimatorSet startExpand;
    AnimatorSet finishShrink;

    Animation fadeOut;
    Animation pop;

    ArrayList<View> obstacles = new ArrayList<View>();
    ArrayList<Integer> moveDirs;

    HashMap<View, Integer> hits = new HashMap<>();

    boolean[] alignedVerticle;
    boolean[] alignedHorizontal;

    boolean onCreate;
    boolean usedHints;
    boolean leaving;
    boolean firstMove = true;
    boolean paused = false;
    boolean isFrozen = false;

    long pausedStart;
    long pausedEnd;
    long pausedTime;
    long startTime;
    long lastTime;
    long frozenTime;

    int screenHeight;
    int screenWidth;
    int numMoves;
    int level;
    int translateX;
    int translateY;
    int step;
    int frozenModifier=1;
    int rewardSilver = 0;
    int msFrozen=(int) Math.round(3000/User.frozenModifier);
    int movesFromPortal = 0;
    int movesDirCount = 0;
    int dudeX = 0;
    int dudeY = 0;
    int hintsUsed = 0;
    int hintMovesIn = 0;

    String lastDirection;
    String pack;
    String deviceId;

    HashMap<String, Double> priceList = new HashMap<>();

    BillingProcessor bp;

    String currency = "";

    boolean usedHint1;
    boolean usedHint2;
    boolean usedHint3;
    boolean locked;
    boolean isMoving;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(paused){

            }else if(pausedTime != 0){
                startTime = startTime + pausedTime;
                pausedTime = 0;
                pausedStart = 0;
                pausedEnd = 0;

                long millis = System.currentTimeMillis() - startTime;
                int secondsCount = (int) ((millis*1.00)/1000.00);
                int minutesCount = (int)Math.floor((secondsCount*1.00)/60.00);
                secondsCount = secondsCount % 60;
                minutes.setText(String.format("%02d", minutesCount));
                seconds.setText(String.format("%02d", secondsCount));

            }else{

                long millis = System.currentTimeMillis() - startTime;
                int secondsCount = (int) (millis / 1000);
                int minutesCount = secondsCount / 60;
                secondsCount = secondsCount % 60;
                minutes.setText(String.format("%02d", minutesCount));
                seconds.setText(String.format("%02d", secondsCount));

            }
            if(isFrozen){
                long millis = System.currentTimeMillis() - startTime;
                frozenTime += millis-(lastTime>0?lastTime:millis);
                lastTime = millis;
                frozenModifier = 3;
                if(frozenTime > msFrozen){
                    unFreeze();
                }
            }
            timerHandler.postDelayed(this, 250);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pack_layout_template);
        context = this;
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        sql = new SQL(this);
        sd = MyApplication.getInstance().getSD();
        gDetect = new GestureDetectorCompat(this, new GestureListener());
        onCreate = true;
        tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tm.getDeviceId();
        Chartboost.onCreate(this);

        pack = getSharedPreferences("LEVELS", MODE_PRIVATE).getString("PACK", "Stout Temple");
        level = getSharedPreferences("LEVELS", MODE_PRIVATE).getInt("LEVEL", 1);

        rink = findViewById(R.id.rink);

        generator = new Generator(pack, level, context, this);
        generator.generate(context, this);

        tutorialDialog = new TutorialDialog(generator);

        paused = pack.equals("Tutorial");
        getSharedPreferences("PLAY", MODE_PRIVATE).edit().putBoolean("used_hints",false).apply();
        getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("hints_used",0).apply();

        User.add("Went to Play");

        setBillingProcessor();
        Ad.setAdInterface(this);

        moves = (TextView) findViewById(R.id.moves);
        minutes = (TextView) findViewById(R.id.minutes);
        seconds = (TextView) findViewById(R.id.seconds);
        number_hints = (TextView) findViewById(R.id.num_hints);
        numMoves = 0;
        if(moves!=null)moves.setText(Integer.toString(numMoves));
        if(number_hints!=null)number_hints.setText(sql.getSingleResult("user", "hints", " where username = '" + User.getUser() + "' ").equals("-1") ? "\u221E" : sql.getSingleResult("user", "hints", " where username = '" + User.getUser() + "' "));

        restart = (Button) this.findViewById(R.id.restart);
        restart.setOnClickListener(onClickListener);
        previous = (Button) this.findViewById(R.id.previous);
        previous.setOnClickListener(onClickListener);
        hint = (Button) this.findViewById(R.id.hint);
        hint.getBackground().clearColorFilter();
        hint.setOnClickListener(onClickListener);

        packName = (lucky8s.shift.EtchView) this.findViewById(R.id.pack);
        levelName = (lucky8s.shift.EtchView) this.findViewById(R.id.level);

        packName.setText(pack);
        levelName.setText(getString(R.string.level) + " " + Integer.toString(level));

        rink.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!(screenWidth > 0) && generator.isDrawn()) {
                    screenHeight = rink.getHeight();
                    screenWidth = rink.getWidth();
                    generator.replicate();
                    obstacles = generator.getObstacles();
                    dude = generator.getDude();
                    dudeX = (int)dude.getX();
                    dudeY = (int)dude.getY();
                    finish = generator.getFinish();
                    moveDirs = generator.getMoves();

                    movesDirCount = moveDirs.size();

                    setAnimations();
                    dudeAnimate("expand");

                    getCounts();

                    if (!pack.equals("Tutorial")) {
                        putIncentive();
                    }
                    if (pack.equals("Tutorial")) {
                        switch (level) {
                            case 1:
                                step = 1;
                                getSharedPreferences("Tutorial", MODE_PRIVATE).edit().putInt("step", step).apply();
                                break;
                            case 2:
                                step = 11;
                                getSharedPreferences("Tutorial", MODE_PRIVATE).edit().putInt("step", step).apply();
                                break;
                            case 3:
                                step = 16;
                                getSharedPreferences("Tutorial", MODE_PRIVATE).edit().putInt("step", step).apply();
                                break;
                            case 4:
                                step = 18;
                                getSharedPreferences("Tutorial", MODE_PRIVATE).edit().putInt("step", step).apply();
                                break;
                            case 5:
                                step = 21;
                                getSharedPreferences("Tutorial", MODE_PRIVATE).edit().putInt("step", step).apply();
                                break;
                            case 6:
                                step = 23;
                                getSharedPreferences("Tutorial", MODE_PRIVATE).edit().putInt("step", step).apply();
                                break;
                        }
                    }
                }
            }
        });


        banner = (AdView) this.findViewById(R.id.banner);

        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(deviceId)
                .build();

        RateMeDialogTimer.onStart(this);
        boolean isRated = getSharedPreferences("RATED", MODE_PRIVATE).getBoolean("Rated", false);
        long lastRated = getSharedPreferences("RATED", MODE_PRIVATE).getLong("RatedTime", 0);
        String levelsCompleted = sql.getSingleResult("permissions_levels", "count(*)", " where username = '" + User.getUser() + "' and level_30 = 1 ");
        int packsComp = levelsCompleted.equals("") ? 0 : Integer.valueOf(levelsCompleted);
        boolean rateNow = !isRated && ((System.currentTimeMillis()-lastRated)> (1000*60*60*24*Config.RATED_DAYS)) && (packsComp > Config.RATED_PACKS) && netInfo != null;
        if(rateNow){
            new RateMeDialog.Builder(getPackageName(), getString(R.string.app_name))
                    .setHeaderBackgroundColor(getResources().getColor(R.color.dkgrey))
                    .setBodyBackgroundColor(getResources().getColor(R.color.ltgrey))
                    .setBodyTextColor(getResources().getColor(R.color.white))
                    .showAppIcon(R.mipmap.ic_launcher)
                    .setShowShareButton(true)
                    .setDefaultNumberOfStars(5)
                    .setRateButtonBackgroundColor(getResources().getColor(R.color.turquise))
                    .setRateButtonPressedBackgroundColor(getResources().getColor(R.color.green))
                    .enableFeedbackByEmail(Config.ERRORS_EMAIL)
                    .setOnRatingListener(onRatingListener)
                    .build()
                    .show(getFragmentManager(), "plain-dialog");
        }
    }
    public void setAnimations(){

        shrinkX = ObjectAnimator.ofFloat(dude, View.SCALE_X, 1f, .1f);
        shrinkX.setDuration(Math.round(500.0 / User.portalSpeed));
        shrinkY = ObjectAnimator.ofFloat(dude, View.SCALE_Y, 1f, .1f);
        shrinkY.setDuration(Math.round(500.0/User.portalSpeed));
        expandX = ObjectAnimator.ofFloat(dude, View.SCALE_X, .1f, 1f);
        expandX.setDuration(Math.round(500.0/User.portalSpeed));
        expandY = ObjectAnimator.ofFloat(dude, View.SCALE_Y, .1f, 1f);
        expandX.setDuration(Math.round(500.0/User.portalSpeed));
        finishX = ObjectAnimator.ofFloat(dude, View.SCALE_X, 1f, 0f);
        finishX.setDuration(500);
        finishY = ObjectAnimator.ofFloat(dude, View.SCALE_Y, 1f, 0f);
        finishY.setDuration(500);
        startX = ObjectAnimator.ofFloat(dude, View.SCALE_X, 0f, 1f);
        startX.setDuration(1000);
        startY = ObjectAnimator.ofFloat(dude, View.SCALE_Y, 0f, 1f);
        startY.setDuration(1000);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(500);
        pop = new ScaleAnimation(1f, 1.1f, 1f, 1.1f);
        pop.setDuration(50);
    }
     OnRatingListener onRatingListener = new OnRatingListener() {
         @Override
         public int describeContents() {
             return 0;
         }

         @Override
         public void writeToParcel(Parcel parcel, int i) {

         }

         @Override
         public void onRating(RatingAction ratingAction, float v) {
             String rated = ratingAction.toString();
             sql.update("user", " where username = '" + User.getUser() + "' ", " set rated = '" + Float.toString(v) + " " + rated + "'");
             if(!(rated.equals("") || rated.equals("null") || rated.contains("DISMISSED"))){
                 getSharedPreferences("RATED", MODE_PRIVATE).edit().putBoolean("Rated", true).apply();
             }
             getSharedPreferences("RATED", MODE_PRIVATE).edit().putLong("RatedTime", System.currentTimeMillis()).apply();
         }
    };
    public void getCounts(){
        int portals = 0;

        for(View obstacle : obstacles){
            if(obstacle instanceof Portal){
                portals++;
            }
        }
        if(portals >= 2){
            getSharedPreferences("Obstacles", MODE_PRIVATE).edit().putBoolean("Portals", true).apply();
        }else{
            getSharedPreferences("Obstacles", MODE_PRIVATE).edit().putBoolean("Portals", false).apply();
        }
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
        @Override
        public void onClick(View view) {
            if(view instanceof Button && view != hint){
                buttonClick(view);
            }
            switch(view.getId()){
                case R.id.restart:
                    User.add("Clicked Restart");
                    sd.buttonPress();
                    Intent b = new Intent(Play.this, Play.class);
                    leaving = true;
                    startActivity(b);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    Play.this.finish();
                    break;
                case R.id.previous:
                    onBackPressed();
                    break;
                case R.id.hint:
                    if(!isMoving) {
                        if(!locked) {
                            locked = true;
                            if((usedHint1 && usedHint2 && usedHint3) || (movesDirCount == 2 && usedHint1) || (movesDirCount == 3 && usedHint1 && usedHint2)){

                            }else {
                                showHint();
                                User.add("Clicked Hint");
                            }
                        }
                    }
                    break;
            }
        }
    };
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gDetect.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    public void putIncentive(){
        Random random = new Random();
        int chosenObstacle = 0;
        while(incentiveObstacle == null || incentiveObstacle instanceof Molten) {
            chosenObstacle = random.nextInt((obstacles.size() - 0))+0;
            incentiveObstacle = obstacles.get(chosenObstacle);
        }
        incentive = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(incentiveObstacle.getLayoutParams().width, incentiveObstacle.getLayoutParams().height);
        incentive.setLayoutParams(lp);
        incentive.getLayoutParams().width=(int)Math.round((incentive.getLayoutParams().width/2.0));
        incentive.getLayoutParams().height=(int)Math.round((incentive.getLayoutParams().height/ 2.0));
        incentive.setBackground(getResources().getDrawable(R.drawable.silver_coin));
        incentive.setX((incentiveObstacle.getX() + Math.round(incentiveObstacle.getLayoutParams().width / 2.0)) - Math.round(incentive.getLayoutParams().width / 2.0));
        incentive.setY((incentiveObstacle.getY() + Math.round(incentiveObstacle.getLayoutParams().height / 2.0)) - Math.round(incentive.getLayoutParams().height / 2.0));
        ((RelativeLayout) incentiveObstacle.getParent()).addView(incentive);
        incentive.bringToFront();
    }
    public class GestureListener extends GestureDetector.SimpleOnGestureListener {


        private float flingMin = 100;
        private float velocityMin = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            //user will move forward through messages on fling up or left
            boolean left = false;
            //user will move backward through messages on fling down or right
            boolean right = false;
            boolean up = false;
            boolean down = false;

            float horizontalDiff = event2.getX() - event1.getX();
            float verticalDiff = event2.getY() - event1.getY();

            float absHDiff = Math.abs(horizontalDiff);
            float absVDiff = Math.abs(verticalDiff);
            float absVelocityX = Math.abs(velocityX);
            float absVelocityY = Math.abs(velocityY);

            if (absHDiff > absVDiff && absHDiff > flingMin && absVelocityX > velocityMin) {
                //move forward or backward
                if (horizontalDiff > 0) left = true;
                else right = true;
            } else if (absVDiff > flingMin && absVelocityY > velocityMin) {
                if (verticalDiff > 0) down = true;
                else up = true;
            }
            if(!startExpand.isRunning() && !fadeOut.hasEnded()) {
                    if (right) {
                        move("left");
                    } else if (left) {
                        move("right");
                    } else if (down) {
                        move("down");
                    } else if (up) {
                        move("up");
                    }
            }
            return true;
        }
    }
    public void autoMove(final int hintMoves){
            if (paused && !pack.equals("Tutorial")) {
                pausedEnd = System.currentTimeMillis();
                pausedTime = pausedEnd - pausedStart;
                paused = false;
            }
            progress = new ProgressDialog(context, R.style.MyTheme);
            progress.setIndeterminate(true);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(false);
            progress.show();
            final int goNum = hintMoves - hintMovesIn;
            new Thread() {
                @Override
                public void run() {
                    int numMoved = 0;
                    while (numMoved < goNum && moveDirs.size() > 0) {
                        if (!isMoving) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (moveDirs.get(0)) {
                                        case 1:
                                            move("up");
                                            break;
                                        case 2:
                                            move("right");
                                            break;
                                        case 3:
                                            move("down");
                                            break;
                                        case 4:
                                            move("left");
                                            break;
                                    }
                                    ArrayList<Integer> tempMoves = new ArrayList<>();
                                    for (int i = 1; i < moveDirs.size(); i++) {
                                        tempMoves.add(moveDirs.get(i));
                                    }
                                    moveDirs = tempMoves;
                                }
                            });
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            numMoved++;
                        }
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while(isMoving) {
                        try {
                            Thread.sleep(Math.round(750.0 / User.portalSpeed));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    hintMovesIn += numMoved;
                    progress.dismiss();
                    paused = true;
                    pausedStart = System.currentTimeMillis();
                    locked = false;
                }
            }.start();
    }
    public void move(String direction) {
        sd.swipe();
        if(firstMove && !startExpand.isRunning()){
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            firstMove = false;
        }
        if(paused && !pack.equals("Tutorial")){
            pausedEnd = System.currentTimeMillis();
            pausedTime = pausedEnd - pausedStart;
            paused = false;
        }
        numMoves = Integer.valueOf(moves.getText().toString());
        if(!isMoving){
            isMoving = true;
            alignedHorizontal = new boolean[obstacles.size()];
            alignedVerticle = new boolean[obstacles.size()];
            for (int x = 0; x < obstacles.size(); x++) {
                if ((dude.getX() <= obstacles.get(x).getX() && getRight(dude) > obstacles.get(x).getX()) || (getRight(dude) >= getRight(obstacles.get(x)) && dude.getX() < getRight(obstacles.get(x)))) {
                    alignedVerticle[x] = true;
                } else if ((dude.getY() <= obstacles.get(x).getY() && getBottom(dude) > obstacles.get(x).getY()) || (getBottom(dude) >= getBottom(obstacles.get(x)) && dude.getY() < getBottom(obstacles.get(x))) ) {
                    alignedHorizontal[x] = true;
                }
            }
            movesFromPortal++;
            if (direction.equals("up")) {
                moveUp();
            } else if (direction.equals("down")) {
                moveDown();
            } else if (direction.equals("right")) {
                moveRight();
            } else if (direction.equals("left")) {
                moveLeft();
            }
        }
    }
    public void moveRight(){
        closest = findClosest("right");
        float endX = 0;
        float startX = 0;
        if(closest == rink){
            startX = dude.getX();
            endX = getRight(closest) - dude.getWidth();
            translateRight = ObjectAnimator.ofFloat(dude, "X", startX, endX);
            translateRight.setDuration(locked ? 250 : Math.round((500.0*frozenModifier)/User.speed));
            translateRight.start();
        }else {
            endX = (int)(closest.getX() - dude.getWidth());
            translateRight = ObjectAnimator.ofFloat(dude, "X", dude.getX(), endX);
            translateRight.setDuration(locked ? 250 : Math.round((500.0 * frozenModifier) / User.speed));
            translateRight.start();
        }

        numMoves++;
        moves.setText(Integer.toString(numMoves));
        translateRight.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }
            @Override
            public void onAnimationEnd(Animator animator) {
                lastDirection = "right";
                refreshDrawables(closest);
                if(insideFinish()){//due to 1 pixel rounding error....
                    timerHandler.removeCallbacks(timerRunnable);
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("moves", numMoves).apply();
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("minutes", Integer.valueOf(minutes.getText().toString())).apply();
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("seconds", Integer.valueOf(seconds.getText().toString())).apply();
                    dudeAnimate("shrink");
                }
                if(pack.equals("Tutorial") && step == 6) {
                    getSharedPreferences("Tutorial", MODE_PRIVATE).edit().putInt("step", step++).apply();
                    final FragmentManager fm = getSupportFragmentManager();
                    if (fm != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (fm != null) {
                                    try {
                                        fm.executePendingTransactions();
                                    }catch (Exception e){
                                        User.add("executePendingTransactions error");
                                        User.add(Log.getStackTraceString(e));
                                    }
                                }
                            }
                        });
                    }
                    Fragment dialog = getFragmentManager().findFragmentByTag("tutorial_dialog");
                    if (dialog == null) {
                        if (!tutorialDialog.isAdded()) {
                            tutorialDialog.setCancelable(true);
                            tutorialDialog.show(fm, "tutorial_dialog");
                        }
                    } else if (dialog.getActivity() != Play.this) {
                    } else {
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    public void moveLeft(){
        closest = findClosest("left");
        if(closest == rink){
            translateLeft = ObjectAnimator.ofFloat(dude, "X", dude.getX(), closest.getX());
            translateLeft.setDuration(locked ? 250 : Math.round((500.0*frozenModifier)/User.speed));
            translateLeft.start();
        }else {
            translateLeft = ObjectAnimator.ofFloat(dude, "X", dude.getX(), getRight(closest));
            translateLeft.setDuration(locked ? 250 : Math.round((500.0 * frozenModifier) / User.speed));
            translateLeft.start();
        }
        numMoves++;
        moves.setText(Integer.toString(numMoves));
        translateLeft.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                lastDirection = "left";
                refreshDrawables(closest);
                if(insideFinish()){//due to 1 pixel rounding error....
                    timerHandler.removeCallbacks(timerRunnable);
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("moves", numMoves).apply();
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("minutes", Integer.valueOf(minutes.getText().toString())).apply();
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("seconds", Integer.valueOf(seconds.getText().toString())).apply();
                    dudeAnimate("shrink");
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    public void moveUp(){
        closest = findClosest("up");
        if(closest == rink){
            translateUp = ObjectAnimator.ofFloat(dude, "Y", dude.getY(), closest.getY());
            translateUp.setDuration(locked ? 250 : Math.round(500.0*frozenModifier/User.speed));
            translateUp.start();
        }else {
            translateUp = ObjectAnimator.ofFloat(dude, "Y", dude.getY(), getBottom(closest));
            translateUp.setDuration(locked ? 250 : Math.round((500.0 * frozenModifier) / User.speed));
            translateUp.start();
        }
        numMoves++;
        moves.setText(Integer.toString(numMoves));
        translateUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                lastDirection = "up";
                refreshDrawables(closest);
                if(insideFinish()){//due to 1 pixel rounding error....
                    timerHandler.removeCallbacks(timerRunnable);
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("moves", numMoves).apply();
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("minutes", Integer.valueOf(minutes.getText().toString())).apply();
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("seconds", Integer.valueOf(seconds.getText().toString())).apply();
                    dudeAnimate("shrink");
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    public void moveDown(){
        closest = findClosest("down");
        if(closest == rink){
            translateDown = ObjectAnimator.ofFloat(dude, "Y", dude.getY(), getBottom(closest) - dude.getHeight());
            translateDown.setDuration(locked ? 250 : Math.round((500.0*frozenModifier)/User.speed));
            translateDown.start();
        }else {
            translateDown = ObjectAnimator.ofFloat(dude, "Y", dude.getY(), closest.getY() - closest.getHeight());
            translateDown.setDuration(locked ? 250 : Math.round((500.0*frozenModifier)/User.speed));
            translateDown.start();
        }
        numMoves++;
        moves.setText(Integer.toString(numMoves));
        translateDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                lastDirection = "down";
                refreshDrawables(closest);
                if(insideFinish()){//due to 1 pixel rounding error....
                    timerHandler.removeCallbacks(timerRunnable);
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("moves", numMoves).apply();
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("minutes", Integer.valueOf(minutes.getText().toString())).apply();
                    getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("seconds", Integer.valueOf(seconds.getText().toString())).apply();
                    dudeAnimate("shrink");
                }
                if (pack.equals("Tutorial") && step == 8) {
                    getSharedPreferences("Tutorial", MODE_PRIVATE).edit().putInt("step", step++).apply();
                    final FragmentManager fm = getSupportFragmentManager();
                    if (fm != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (fm != null) {
                                    try {
                                        fm.executePendingTransactions();
                                    }catch (Exception e){
                                        User.add("executePendingTransactions error");
                                        User.add(Log.getStackTraceString(e));
                                    }
                                }
                            }
                        });
                    }
                    Fragment dialog = getFragmentManager().findFragmentByTag("tutorial_dialog");
                    if (dialog == null) {
                        if (tutorialDialog != null && !tutorialDialog.isAdded()) {
                            tutorialDialog.setCancelable(true);
                            tutorialDialog.show(fm, "tutorial_dialog");
                        }
                    } else if (dialog.getActivity() != Play.this) {
                    } else {
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    public void showBanner(){
        String no_ads = sql.getSingleResult("user", "no_ads", " where username = '" + User.getUser() + "' ");
        boolean showBanner = (no_ads.contains(pack) || no_ads.contains("all")) ? false : true;
        if(showBanner) banner.loadAd(adRequest);
    }
    public void showAd(){
        cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        String no_ads = "";
        if(sql == null) {
            sql = new SQL(this);
        }
        no_ads = sql.getSingleResult("user", "no_ads", " where username = '" + User.getUser() + "' ");
        boolean showAd = (no_ads.contains(pack) || no_ads.contains("all") || !Ad.isInterstitialReady()) ? false : true;

        int lastCompleted = getSharedPreferences("ADS", MODE_PRIVATE).getInt("levels_completed", Config.INTERSTITIAL_INTERVAL-2);
        int nowCompleted = lastCompleted+1;
        boolean goodTime = nowCompleted >= Config.INTERSTITIAL_INTERVAL; //num levels between interstitials
        getSharedPreferences("ADS", MODE_PRIVATE).edit().putInt("levels_completed", nowCompleted).apply();
        getSharedPreferences("PLAY",MODE_PRIVATE).edit().putBoolean("used_hints",usedHints).apply();
        getSharedPreferences("PLAY",MODE_PRIVATE).edit().putInt("hints_used",hintsUsed).apply();


        sql.addCoins(rewardSilver, "silver");

        if(netInfo != null && showAd && goodTime && !pack.equals("Tutorial")) {
            try {
                Ad.showInterstitial();
            }catch (NullPointerException np){
                User.add(Log.getStackTraceString(np));
                showFinished();
            }catch(Exception e){
                User.add(Log.getStackTraceString(e));
                showFinished();
            }

        }else{
            showFinished();
        }
    }
    public void showFinished(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(500);
                } catch (Exception e) {
                }
                final FragmentManager fm = getSupportFragmentManager();
                if (fm != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (fm != null) {
                                try {
                                    fm.executePendingTransactions();
                                }catch(Exception ie){
                                    User.add("executePendingTransactions error");
                                    User.add(Log.getStackTraceString(ie));
                                }
                            }
                        }
                    });
                }
            }
        }).start();
        FragmentManager fm = getSupportFragmentManager();
        Fragment dialog = getFragmentManager().findFragmentByTag("finished_dialog");
        if (dialog == null) {
            if (finishedDialog != null && !finishedDialog.isAdded()) {
                finishedDialog.setCancelable(false);
                try {
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    if (finishedDialog != null && !finishedDialog.isAdded()) {
                        fragmentTransaction.add(finishedDialog, "finished_dialog");
                    }
                    fragmentTransaction.commitAllowingStateLoss();
                }catch (IllegalStateException ie){
                    User.add(Log.getStackTraceString(ie));
                }
            }
        } else if (dialog.getActivity() != Play.this) {
        } else {
        }
    }
    public void onResume() {
        super.onResume();
        Chartboost.onResume(this);
        AdColony.resume(this);
        AppsFlyerLib.onActivityResume(this);
        /*
        if(!sd.isPlaying("gameBackground")) {
            sd.stop();
            sd.gameBackground();
        }
        */
        showBanner();
        leaving = false;
        step = getSharedPreferences("Tutorial", MODE_PRIVATE).getInt("step",1);
    }
    public void refreshDrawables(View view){
        if(view == incentiveObstacle && incentive != null){
            collectIncentive();
        }
        if(view instanceof Breakable) {
            hitBreakable(view);
        }else if(view instanceof Molten) {
            hitMolten(view);
        }else if(view instanceof Frozen){
            hitFrozen(view);
        }else if(view instanceof Portal){
            hitPortal(view);
        }else if(view instanceof Bubble) {
            hitBubble(view);
        }else{
            if(locked) {
                dudeX = (int) dude.getX();
                dudeY = (int) dude.getY();
            }
            isMoving = false;
        }

    }
    public void collectIncentive(){
        boolean firstTime = Integer.valueOf(sql.getSingleResult("score_levels", "level_"+Integer.toString(level)," where username = '"+User.getUser()+"' and pack = '"+pack+"' ")) == 0;
        if(firstTime){
            Random randSilver = new Random();
            rewardSilver = randSilver.nextInt((20-1)+1)+1;
            incentive.setVisibility(View.INVISIBLE);
            incentive = null;
            Toast.makeText(this, Integer.toString(rewardSilver) + " silver found!", Toast.LENGTH_SHORT).show();
        }else{
            rewardSilver = 1;
            incentive.setVisibility(View.INVISIBLE);
            incentive = null;
            Toast.makeText(this, Integer.toString(rewardSilver) + " silver found!", Toast.LENGTH_SHORT).show();
        }
    }
    public void hitBreakable(View view){
        sd.hitBreakable();
        if (!hits.containsKey(view)) {
            view.setBackground(getResources().getDrawable(R.drawable.play_breakable2));
            hits.put(view, 1);
        } else if (hits.get(view) == 1) {
            view.setBackground(getResources().getDrawable(R.drawable.play_breakable3));
            hits.put(view, 2);
        } else if (hits.get(view) == 2) {
            obstacles.remove(view);
            view.setBackground(getResources().getDrawable(R.drawable.play_breakable4));
            hits.put(view, 3);
        }
        if(locked) {
            dudeX = (int) dude.getX();
            dudeY = (int) dude.getY();
        }
        isMoving = false;
    }
    public void hitFrozen(View view){
        //sd.hitFrozen();
        if(!isFrozen) {
            dude.setBackground(getResources().getDrawable(R.drawable.play_dude_frozen));
            lastTime = 0;
            msFrozen = (int)Math.round(3000/User.frozenModifier);
            isFrozen = true;
            frozenModifier = 3;
        }else{
            msFrozen += (int) Math.round(3000/User.frozenModifier);
        }
        if(locked) {
            dudeX = (int) dude.getX();
            dudeY = (int) dude.getY();
        }
        isMoving = false;
    }
    public boolean insideFinish(){
        int dudeMidX = (int)dude.getX()+(dude.getWidth()/2);
        int dudeMidY = (int)dude.getY()+(dude.getHeight()/2);

        //if finish contains the midpoint, it's majority inside
        if(dude.getX() == finish.getX() && dude.getY() == finish.getY()){
            return true;
        }else if(dudeMidX >= finish.getX() && dudeMidX <= getRight(finish) && dudeMidY >= finish.getY() && dudeMidY <= getBottom(finish)){
            return true;
        }else{
            return false;
        }
    }
    public void unFreeze(){
        dude.setBackground(getResources().getDrawable(R.drawable.play_dude));
        lastTime = 0;
        frozenTime = 0;
        isFrozen = false;
        frozenModifier = 1;
    }
    public void hitMolten(View view){
        sd.hitMolten();
        dude.startAnimation(fadeOut);
        if(locked) {
            dudeX = (int) dude.getX();
            dudeY = (int) dude.getY();
        }
        isMoving = false;
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dude.setAlpha(0);
                Intent b = new Intent(Play.this, Play.class);
                leaving = true;
                startActivity(b);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                Play.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void hitPortal(View view){
        final View goToPortal = ((Portal)view).getBrother();
        int goToX = 0;
        int goToY = 0;
        switch (lastDirection){
            case "up":
                goToX = (int)goToPortal.getX();
                goToY = (int)goToPortal.getY()+goToPortal.getHeight();
                translateX = 0;
                translateY = -goToPortal.getHeight();
                break;
            case "down":
                goToX = (int)goToPortal.getX();
                goToY = (int)goToPortal.getY()-goToPortal.getHeight();
                translateX = 0;
                translateY = goToPortal.getHeight();
                break;
            case "right":
                goToX = (int)goToPortal.getX()-goToPortal.getWidth();
                goToY = (int)goToPortal.getY();
                translateX = goToPortal.getWidth();
                translateY = 0;
                break;
            case "left":
                goToX = (int)goToPortal.getX()+goToPortal.getWidth();
                goToY = (int)goToPortal.getY();
                translateX = -goToPortal.getWidth();
                translateY = 0;
                break;
        }
        final int goX = goToX;
        final int goY = goToY;
        if(!conflicts(goToX, goToY) && (view != lastPortal || (view == lastPortal && movesFromPortal > 1))){
            lastPortal = goToPortal;
            movesFromPortal = 0;
            scaleUp = new AnimatorSet();
            scaleDown = new AnimatorSet();

            if(translateX != 0){
                moveToPortal = ObjectAnimator.ofFloat(dude, "X", dude.getX(), (dude.getX()-(dude.getWidth()*(9/10)))+translateX);
            }else {
                moveToPortal = ObjectAnimator.ofFloat(dude, "Y", dude.getY(), dude.getY()-(dude.getHeight()*(9/10)) + translateY);
            }
            moveToPortal.setDuration(locked ? 100 : Math.round(750.0 / User.portalSpeed));
            scaleDown.play(shrinkX).with(shrinkY).with(moveToPortal);
            sd.portalIn();
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    dude.setX((float) goX);
                    dude.setY((float) goY);

                    if (translateX != 0) {
                        moveFromPortal = ObjectAnimator.ofFloat(dude, "X", dude.getX(), (goToPortal.getX() - (dude.getWidth() * (45 / 100))) - translateX);
                    } else {
                        moveFromPortal = ObjectAnimator.ofFloat(dude, "Y", dude.getY(), (goToPortal.getY() - (dude.getHeight() * (45 / 100))) - translateY);
                    }
                    moveFromPortal.setDuration(locked ? 100 : Math.round(1250.0 / User.portalSpeed));
                    scaleUp.play(expandX).with(expandY).with(moveFromPortal);
                    sd.portalOut();
                    scaleUp.start();
                    scaleUp.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if(locked) {
                                dudeX = (int) dude.getX();
                                dudeY = (int) dude.getY();
                            }
                            isMoving = false;
                            if(insideFinish()){
                                timerHandler.removeCallbacks(timerRunnable);
                                getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("moves", numMoves).apply();
                                getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("minutes", Integer.valueOf(minutes.getText().toString())).apply();
                                getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("seconds", Integer.valueOf(seconds.getText().toString())).apply();
                                dudeAnimate("shrink");
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }else if(lastPortal == view && movesFromPortal <= 1){
            movesFromPortal--;
            if(locked) {
                dudeX = (int) dude.getX();
                dudeY = (int) dude.getY();
            }
            isMoving = false;
        }else{
            if(locked) {
                dudeX = (int) dude.getX();
                dudeY = (int) dude.getY();
            }
            isMoving = false;
        }
    }

    public boolean conflicts(int coordX, int coordY){
       if(coordX < rink.getX() || (coordX+dude.getWidth()) > getRight(rink) || coordY < rink.getY() || (coordY+dude.getHeight()) > getBottom(rink)){
           return true;
       }else{
           for(int i = 0; i < obstacles.size(); i++){
               if(intersects(obstacles.get(i), coordX, coordY)){
                   return true;
               }
           }
       }
        return false;
    }
    public boolean intersects(View obstacle, int coordX, int coordY){
        if((coordX >= obstacle.getX() && coordX < getRight(obstacle))){
            if(coordY >= obstacle.getY() && coordY < getBottom(obstacle)){
                return true;
            }else if((coordY+dude.getHeight())>obstacle.getY() && (coordY+dude.getHeight())<=getBottom(obstacle)){
                return true;
            }else{
                return false;
            }
        }else if((coordX+dude.getWidth())>obstacle.getX() && (coordX+dude.getWidth())<getRight(obstacle)){
            if(coordY >= obstacle.getY() && coordY < getBottom(obstacle)){
                return true;
            }else if((coordY+dude.getHeight())>obstacle.getY() && (coordY+dude.getHeight())<=getBottom(obstacle)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    public void hitBubble(View view){
        sd.hitPoppable();
        int height = 0;
        for(View obst: obstacles){
            if(height == 0){
                height = obst.getHeight();
            }else{
                if(height < obst.getHeight()){
                    height = obst.getHeight();
                }
            }
        }
        switch (lastDirection) {
            case "up":
                translateX = 0;
                translateY = height == 0 ? dude.getHeight() : height;
                break;
            case "down":
                translateX = 0;
                translateY = -(height == 0 ? dude.getHeight() : height);
                break;
            case "right":
                translateX = -dude.getWidth();
                translateY = 0;
                break;
            case "left":
                translateX = dude.getWidth();
                translateY = 0;
                break;
        }
        boolean goBump = false;
        if (translateX != 0) {
            if(!conflicts((int)dude.getX()+translateX, (int)dude.getY())) {
                bump = ObjectAnimator.ofFloat(dude, "X", dude.getX(), dude.getX() + translateX);
                bump.setDuration(100);
                goBump=true;
            }
        } else {
            if(!conflicts((int)dude.getX(), (int)dude.getY()+translateY)) {
                bump = ObjectAnimator.ofFloat(dude, "Y", dude.getY(), dude.getY() + translateY);
                bump.setDuration(100);
                goBump=true;
            }
        }
        final boolean goBumped = goBump;
        final View bubble = view;
        bubble.startAnimation(pop);
        pop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bubble.setVisibility(View.INVISIBLE);
                if(goBumped) {
                    bump.start();
                    bump.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if(locked) {
                                dudeX = (int) dude.getX();
                                dudeY = (int) dude.getY();
                            }
                            isMoving = false;
                            if(insideFinish()){
                                timerHandler.removeCallbacks(timerRunnable);
                                getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("moves", numMoves).apply();
                                getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("minutes", Integer.valueOf(minutes.getText().toString())).apply();
                                getSharedPreferences("PLAY", MODE_PRIVATE).edit().putInt("seconds", Integer.valueOf(seconds.getText().toString())).apply();
                                dudeAnimate("shrink");
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                }else{
                    if(locked) {
                        dudeX = (int) dude.getX();
                        dudeY = (int) dude.getY();
                    }
                    isMoving = false;
                }
                obstacles.remove(bubble);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    public View findClosest(String direction) {
        View closest = rink;
        dude.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        if(direction.equals("up")){
            for(int x = 0; x < obstacles.size(); x++){
                    if(alignedVerticle[x] && (dude.getY() >= getBottom(obstacles.get(x))) && (closest == rink ? obstacles.get(x).getY() >= closest.getY() : obstacles.get(x).getY() >= closest.getY())){
                        closest = obstacles.get(x);
                    }
            }
        }else if(direction.equals("down")){
            for(int x = 0; x < obstacles.size(); x++){
                if(alignedVerticle[x] && (dude.getY() < getBottom(obstacles.get(x))) && (closest == rink ? obstacles.get(x).getY() <= getBottom(closest) : obstacles.get(x).getY() <= closest.getY())){
                    closest = obstacles.get(x);
                }
            }
        }else if(direction.equals("right")){
            for(int x = 0; x < obstacles.size(); x++){
                if(alignedHorizontal[x] && (dude.getX() < obstacles.get(x).getX()) && (closest == rink ? obstacles.get(x).getX() <= getRight(closest) : obstacles.get(x).getX() <= closest.getX())){
                    closest = obstacles.get(x);
                }
            }
        }else if(direction.equals("left")){
            for(int x = 0; x < obstacles.size(); x++){
                if(alignedHorizontal[x] && (dude.getX() >= getRight(obstacles.get(x))) && (closest == rink ? obstacles.get(x).getX() >= closest.getX() : obstacles.get(x).getX() >= closest.getX())){
                    closest = obstacles.get(x);
                }
            }
        }
        return closest;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        User.add("Play Back Pressed");
        sd.backPress();
        Intent a;
        if(pack.equals("Tutorial")){
            a = new Intent(Play.this, TutorialPack.class);
        }else {
            a = new Intent(Play.this, Pack.class);
        }
        leaving = true;
        startActivity(a);
        Play.this.finish();
    }
    public void showHint(){
        if(hintsAvailable() || pack.equals("Tutorial")) {
            int hint = 0;
            if(!usedHint1){
                hint = 1;
            }else if(!usedHint2){
                hint = 2;
            }else if(!usedHint3){
                hint = 3;
            }
            usedHints = true;
            hintsUsed = hint;
            int goNumMoves = 0;
            switch (hint) {
                case 1:
                    usedHint1 = true;
                    updateHints(-1);
                    goNumMoves = ((int)Math.floor(movesDirCount*1.00/2.00));
                    break;
                case 2:
                    usedHint2 = true;
                    updateHints(-1);
                    if(movesDirCount == 3){
                        goNumMoves = 2;
                    }else {
                        goNumMoves = ((int) Math.floor((movesDirCount * 1.00 / 4.00) * 3.00));
                        if(goNumMoves == movesDirCount-1) goNumMoves--;
                    }
                    break;
                case 3:
                    usedHint3 = true;
                    updateHints(-1);
                    goNumMoves = (movesDirCount-1);
                    break;
            }
            if(goNumMoves > 0){
                refreshLevel();
                final int movesInt = goNumMoves;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        while(dude == null || dude.getWidth() == 0){
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                autoMove(movesInt);
                            }
                        });
                    }
                }).start();

            }
        } else {
            if (numMoves > 0) {
                pausedStart = currentTimeMillis();
                paused = true;
            }
            final FragmentManager fm = getSupportFragmentManager();
            if (fm != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fm != null) {
                            try {
                                fm.executePendingTransactions();
                            }catch (Exception e){
                                User.add("executePendingTransactions error");
                                User.add(Log.getStackTraceString(e));
                            }
                        }
                    }
                });
            }
            Fragment dialog = getFragmentManager().findFragmentByTag("hints_dialog");
            if (dialog == null) {
                if (!hintsDialog.isAdded()) {
                    User.add("Showing HintsDialog");
                    hintsDialog.setCancelable(true);
                    hintsDialog.show(fm, "hints_dialog");
                }
            } else if (dialog.getActivity() != Play.this) {
            } else {
            }
        }
    }
    public boolean isNotAnimating(){
        if((bump == null || !bump.isRunning()) && (moveToPortal == null || !moveToPortal.isRunning()) && (moveFromPortal == null || !moveFromPortal.isRunning()) && (scaleDown == null || !scaleDown.isRunning()) && (scaleUp == null || !scaleUp.isRunning()) && (translateDown == null || !translateDown.isRunning()) && (translateUp == null || !translateUp.isRunning()) && (translateRight == null || !translateRight.isRunning()) && (translateLeft == null || !translateLeft.isRunning()) ){
            return true;
        }else{
            return false;
        }
    }
    public void refreshLevel(){
        obstacles = generator.getObstacles();
        for (int i = 0; i < obstacles.size(); i++){
            refreshDrawable(obstacles.get(i));
            obstacles.get(i).setVisibility(View.VISIBLE);
        }
        dude.setX(dudeX);
        dude.setY(dudeY);
    }
    public void refreshDrawable(View view){
        if(view instanceof Bubble){
            view.setBackground(getResources().getDrawable(R.drawable.play_bubble));
        }else if(view instanceof Breakable){
            view.setBackground(getResources().getDrawable(R.drawable.play_breakable));
        }
    }
    public boolean hintsAvailable(){
        if(Integer.valueOf(sql.getSingleResult("user", "hints", " where username = '" + User.getUser() + "' ")) != 0){
            return true;
        }else{
            return false;
        }
    }
    public void updateHints(int value){
        if(!pack.equals("Tutorial")) {
            int prev_hints = Integer.valueOf(sql.getSingleResult("user", "hints", " where username = '" + User.getUser() + "' "));
            if (prev_hints != -1) {
                int num_hints = Integer.valueOf(sql.getSingleResult("user", "hints", " where username = '" + User.getUser() + "' ")) + value;
                String hints = Integer.toString(num_hints);

                number_hints.setText(hints);
                sql.update("user", " where username = '" + User.getUser() + "' ", " set hints = " + hints + " ");
            } else if (prev_hints == -1) {
                number_hints.setText("\u221E");
            } else {
                number_hints.setText("0");
            }
        }
        if((usedHint1 && usedHint2 && usedHint3)||(movesDirCount == 2 && usedHint1)||(movesDirCount == 3 && usedHint1 && usedHint2)){
            hint.getBackground().setColorFilter(getResources().getColor(R.color.ltgrey_tint), PorterDuff.Mode.SRC_ATOP);
        }
        if(!User.username.equals("")) {
            ParseService.updateSync("user", false);
            Intent userIntent = new Intent(context, ParseService.class);
            userIntent.putExtra(ParseService.Object, "user");
            context.startService(userIntent);
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        step = getSharedPreferences("Tutorial", MODE_PRIVATE).getInt("step", 1);
        if (hasFocus) {
            if(pack.equals("Tutorial") && !tutorialDialog.isVisible()) {
                if(step == 1 || step == 11 || step == 16 || step == 18 || step == 21 || step == 23) {
                    getSharedPreferences("Tutorial", MODE_PRIVATE).edit().putInt("step", step).apply();
                    final FragmentManager fm = getSupportFragmentManager();
                    if (fm != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (fm != null) {
                                    try {
                                        fm.executePendingTransactions();
                                    }catch (Exception e){
                                        User.add("executePendingTransactions error");
                                        User.add(Log.getStackTraceString(e));
                                    }
                                }
                            }
                        });
                    }
                    Fragment dialog = getFragmentManager().findFragmentByTag("tutorial_dialog");
                    if (dialog == null) {
                        if (!tutorialDialog.isAdded()) {
                            tutorialDialog.setCancelable(true);
                            tutorialDialog.show(fm, "tutorial_dialog");
                        }
                    } else if (dialog.getActivity() != Play.this) {
                    } else {
                    }
                }
            }
        }
    }
    public void dudeAnimate(String action){
        if(action.equals("shrink")) {
            finishShrink = new AnimatorSet();
            finishShrink.play(finishX).with(finishY);
            sd.fadeIn();
            finishShrink.start();
            finishShrink.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    showAd();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }else if(action.equals("expand")){
            startExpand = new AnimatorSet();
            startExpand.play(startX).with(startY);
            sd.fadeIn();
            startExpand.start();
        }
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
    public void onCloseDialog(){
        if(numMoves > 0){
            pausedEnd = System.currentTimeMillis();
            pausedTime = pausedEnd - pausedStart;
            paused = false;
        }
        number_hints.setText(sql.getSingleResult("user", "hints", " where username = '"+User.getUser()+"' ").equals("-1") ? "\u221E" : sql.getSingleResult("user", "hints", " where username = '"+User.getUser()+"' "));
    }
    public void showCoinsStore() {
        final FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fm != null) {
                        try {
                            fm.executePendingTransactions();
                        }catch (Exception e){
                            User.add("executePendingTransactions error");
                            User.add(Log.getStackTraceString(e));
                        }
                    }
                }
            });
        }
        Fragment dialog = getFragmentManager().findFragmentByTag("coins_dialog");
        if (dialog == null) {
            if (!coinsDialog.isAdded()) {
                coinsDialog.setCancelable(true);
                User.add("Showing CoinsDialog from Play");
                hintsDialog.dismiss();
                coinsDialog.show(fm, "coins_dialog");
            }
        } else if (dialog.getActivity() != Play.this) {
        } else {
        }
    }
    public int getBottom(View view){
        return (int)(view.getY()+view.getHeight());
    }
    public int getRight(View view){
        return (int)(view.getX()+view.getWidth());
    }
    public void onDestroy(){
        super.onDestroy();
        bp.release();
        Chartboost.onDestroy(this);
        timerHandler.removeCallbacks(timerRunnable);
/*
        context = null;
        sql = null;
        fm = null;
        tm = null;
        finishedDialog = null;
        hintsDialog = null;
        coinsDialog = null;
        tutorialDialog = null;
        cm = null;
        netInfo = null;
        sd = null;
        gDetect = null;
        progress = null;
        generator = null;

        moves = null;
        seconds = null;
        minutes = null;
        number_hints = null;

        restart = null;
        previous = null;
        hint = null;

        finish = null;
        rink = null;
        dude = null;
        closest = null;
        packName = null;
        levelName = null;
*/
        banner = null;

         translateRight = null;
         translateLeft = null;
         translateUp = null;
         translateDown = null;
         moveToPortal = null;
         moveFromPortal = null;
         shrinkX = null;
         shrinkY = null;
         expandX = null;
         expandY = null;
         startX = null;
         startY = null;
         finishX = null;
         finishY = null;
         bump = null;

        scaleDown = null;
        scaleUp = null;
        startExpand = null;
        finishShrink = null;

        fadeOut = null;
        pop = null;

        obstacles = null;

        hits = null;

        System.gc();

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
                        Intent userIntent = new Intent(Play.this, ParseService.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
    public void getReward(){}
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
    public int getPositionLeft(View view){
        View temp = view;
        int [] viewCoords = new int[2];
        temp.getLocationOnScreen(viewCoords);

        return viewCoords[0];
    }
    public int getPositionTop(View view){
        View temp = view;
        int [] viewCoords = new int[2];
        temp.getLocationOnScreen(viewCoords);
        return viewCoords[1];
    }
    public int getPositionRight(View view){
        return getPositionLeft(view)+view.getWidth();
    }
    public int getPositionBottom(View view){
        return getPositionTop(view)+view.getHeight();
    }
}
