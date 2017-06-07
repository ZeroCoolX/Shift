package lucky8s.shift;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lucky8s.shift.R.id.bonus_info;
import static lucky8s.shift.R.id.breakables_free_info;
import static lucky8s.shift.R.id.breakables_paid_info;
import static lucky8s.shift.R.id.bubbles_breakables_frozens_paid_info;
import static lucky8s.shift.R.id.bubbles_free_info;
import static lucky8s.shift.R.id.bubbles_moltens_paid_info;
import static lucky8s.shift.R.id.bubbles_paid_info;
import static lucky8s.shift.R.id.bubbles_portals_paid_info;
import static lucky8s.shift.R.id.everything_one_free_info;
import static lucky8s.shift.R.id.everything_paid_info;
import static lucky8s.shift.R.id.fire_and_ice_info;
import static lucky8s.shift.R.id.fire_info;
import static lucky8s.shift.R.id.frozens_free_info;
import static lucky8s.shift.R.id.moltens_breakables_paid_info;
import static lucky8s.shift.R.id.portals_breakables_paid_info;
import static lucky8s.shift.R.id.portals_bubbles_paid_info;
import static lucky8s.shift.R.id.portals_free_info;
import static lucky8s.shift.R.id.portals_paid_info;
import static lucky8s.shift.R.id.standard_info;
import static lucky8s.shift.R.id.standard_paid_info;
import static lucky8s.shift.R.id.tutorial_info;
import static lucky8s.shift.User.add;

/**
 * Created by Christian on 5/24/2015.
 */
public class Levels extends FragmentActivity implements DialogListener {

    Context context;
    SQL sql;
    LevelDialog levelDialog;
    BillingProcessor bp;
    SoundDriver sd;
    CoinsDialog coinsDialog= new CoinsDialog(this);
    InfoDialog infoDialog = new InfoDialog();
    StoreDialog storeDialog;

    Button tutorial;
    Button standard;
    Button bonus;
    Button fire;
    Button bubblesFree;
    Button frozensFree;
    Button portalsFree;
    Button breakablesFree;
    Button everythingOneFree;
    Button fireAndIce;
    Button breakablesPaid;
    Button standardPaid;
    Button previous;
    Button bubblesPaid;
    Button portalsPaid;
    Button everythingPaid;
    Button moltensBreakablesPaid;
    Button bubblesPortalsPaid;
    Button portalsBreakablesPaid;
    Button portalsBubblesPaid;
    Button bubblesMoltensPaid;
    Button bubblesBreakablesFrozensPaid;


    Button tutorialInfo;
    Button standardInfo;
    Button bonusInfo;
    Button fireInfo;
    Button bubblesFreeInfo;
    Button frozensFreeInfo;
    Button portalsFreeInfo;
    Button breakablesFreeInfo;
    Button everythingOneFreeInfo;
    Button fireAndIceInfo;
    Button breakablesPaidInfo;
    Button standardPaidInfo;
    Button bubblesPaidInfo;
    Button portalsPaidInfo;
    Button everythingPaidInfo;
    Button moltensBreakablesPaidInfo;
    Button bubblesPortalsPaidInfo;
    Button portalsBreakablesPaidInfo;
    Button portalsBubblesPaidInfo;
    Button bubblesMoltensPaidInfo;
    Button bubblesBreakablesFrozensPaidInfo;

    TextView tutorialCounter;
    TextView standardCounter;
    TextView bonusCounter;
    TextView fireCounter;
    TextView bubblesFreeCounter;
    TextView frozensFreeCounter;
    TextView portalsFreeCounter;
    TextView breakablesFreeCounter;
    TextView everythingOneFreeCounter;
    TextView fireAndIceCounter;
    TextView breakablesPaidCounter;
    TextView standardPaidCounter;
    TextView bubblesPaidCounter;
    TextView portalsPaidCounter;
    TextView everythingPaidCounter;
    TextView moltensBreakablesPaidCounter;
    TextView bubblesPortalsPaidCounter;
    TextView portalsBreakablesPaidCounter;
    TextView portalsBubblesPaidCounter;
    TextView bubblesMoltensPaidCounter;
    TextView bubblesBreakablesFrozensPaidCounter;

    View standardLock;
    View bonusLock;
    View fireLock;
    View bubblesFreeLock;
    View frozensFreeLock;
    View portalsFreeLock;
    View breakablesFreeLock;
    View everythingOneFreeLock;
    View fireAndIceLock;
    View breakablesPaidLock;
    View standardPaidLock;
    View bubblesPaidLock;
    View portalsPaidLock;
    View everythingPaidLock;
    View moltensBreakablesPaidLock;
    View bubblesPortalsPaidLock;
    View portalsBreakablesPaidLock;
    View portalsBubblesPaidLock;
    View bubblesMoltensPaidLock;
    View bubblesBreakablesFrozensPaidLock;

    ArrayList<String> freePacks = new ArrayList<>();

    HashMap<String, Double> priceList = new HashMap<>();

    boolean leaving;

    String PURCHASE_CODE = "";
    String currency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.add("In Levels");
        setContentView(R.layout.levels);
        Chartboost.onCreate(this);

        context = this;
        sd = MyApplication.getInstance().getSD();
        sql = new SQL(context);

        tutorial = (Button) this.findViewById(R.id.tutorial);
        tutorial.setOnClickListener(onClickListener);
        standard = (Button) this.findViewById(R.id.standard_pack);
        standard.setOnClickListener(onClickListener);
        bonus = (Button) this.findViewById(R.id.bonus_pack);
        bonus.setOnClickListener(onClickListener);
        fire = (Button) this.findViewById(R.id.fire_pack);
        fire.setOnClickListener(onClickListener);
        previous = (Button) this.findViewById(R.id.previous);
        previous.setOnClickListener(onClickListener);
        bubblesFree = (Button) this.findViewById(R.id.bubbles_free_pack);
        bubblesFree.setOnClickListener(onClickListener);
        frozensFree = (Button) this.findViewById(R.id.frozens_free_pack);
        frozensFree.setOnClickListener(onClickListener);
        portalsFree = (Button) this.findViewById(R.id.portals_free_pack);
        portalsFree.setOnClickListener(onClickListener);
        breakablesFree = (Button) this.findViewById(R.id.breakables_free_pack);
        breakablesFree.setOnClickListener(onClickListener);
        everythingOneFree = (Button) this.findViewById(R.id.everything_one_free_pack);
        everythingOneFree.setOnClickListener(onClickListener);
        fireAndIce = (Button) this.findViewById(R.id.fire_and_ice_paid_pack);
        fireAndIce.setOnClickListener(onClickListener);
        breakablesPaid = (Button) this.findViewById(R.id.breakables_paid_pack);
        breakablesPaid.setOnClickListener(onClickListener);
        standardPaid = (Button) this.findViewById(R.id.standard_paid_pack);
        standardPaid.setOnClickListener(onClickListener);
        bubblesPaid = (Button) this.findViewById(R.id.bubbles_paid_pack);
        bubblesPaid.setOnClickListener(onClickListener);
        portalsPaid = (Button) this.findViewById(R.id.portals_paid_pack);
        portalsPaid.setOnClickListener(onClickListener);
        everythingPaid = (Button) this.findViewById(R.id.everything_paid_pack);
        everythingPaid.setOnClickListener(onClickListener);
        moltensBreakablesPaid = (Button) this.findViewById(R.id.moltens_breakables_paid_pack);
        moltensBreakablesPaid.setOnClickListener(onClickListener);
        bubblesPortalsPaid = (Button) this.findViewById(R.id.bubbles_portals_paid_pack);
        bubblesPortalsPaid.setOnClickListener(onClickListener);
        portalsBreakablesPaid = (Button) this.findViewById(R.id.portals_breakables_paid_pack);
        portalsBreakablesPaid.setOnClickListener(onClickListener);
        portalsBubblesPaid = (Button) this.findViewById(R.id.portals_bubbles_paid_pack);
        portalsBubblesPaid.setOnClickListener(onClickListener);
        bubblesMoltensPaid = (Button) this.findViewById(R.id.bubbles_moltens_paid_pack);
        bubblesMoltensPaid.setOnClickListener(onClickListener);
        bubblesBreakablesFrozensPaid = (Button) this.findViewById(R.id.bubbles_breakables_frozens_paid_pack);
        bubblesBreakablesFrozensPaid.setOnClickListener(onClickListener);

        tutorialInfo = (Button) this.findViewById(R.id.tutorial_info);
        tutorialInfo.setOnClickListener(onClickListener2);
        standardInfo = (Button) this.findViewById(R.id.standard_info);
        standardInfo.setOnClickListener(onClickListener2);
        bonusInfo = (Button) this.findViewById(R.id.bonus_info);
        bonusInfo.setOnClickListener(onClickListener2);
        fireInfo = (Button) this.findViewById(R.id.fire_info);
        fireInfo.setOnClickListener(onClickListener2);
        bubblesFreeInfo = (Button) this.findViewById(R.id.bubbles_free_info);
        bubblesFreeInfo.setOnClickListener(onClickListener2);
        frozensFreeInfo = (Button) this.findViewById(R.id.frozens_free_info);
        frozensFreeInfo.setOnClickListener(onClickListener2);
        portalsFreeInfo = (Button) this.findViewById(R.id.portals_free_info);
        portalsFreeInfo.setOnClickListener(onClickListener2);
        breakablesFreeInfo = (Button) this.findViewById(R.id.breakables_free_info);
        breakablesFreeInfo.setOnClickListener(onClickListener2);
        everythingOneFreeInfo = (Button) this.findViewById(R.id.everything_one_free_info);
        everythingOneFreeInfo.setOnClickListener(onClickListener2);
        fireAndIceInfo = (Button) this.findViewById(R.id.fire_and_ice_info);
        fireAndIceInfo.setOnClickListener(onClickListener2);
        breakablesPaidInfo = (Button) this.findViewById(R.id.breakables_paid_info);
        breakablesPaidInfo.setOnClickListener(onClickListener2);
        standardPaidInfo = (Button) this.findViewById(R.id.standard_paid_info);
        standardPaidInfo.setOnClickListener(onClickListener2);
        bubblesPaidInfo = (Button) this.findViewById(R.id.bubbles_paid_info);
        bubblesPaidInfo.setOnClickListener(onClickListener2);
        portalsPaidInfo = (Button) this.findViewById(R.id.portals_paid_info);
        portalsPaidInfo.setOnClickListener(onClickListener2);
        everythingPaidInfo = (Button) this.findViewById(R.id.everything_paid_info);
        everythingPaidInfo.setOnClickListener(onClickListener2);
        moltensBreakablesPaidInfo = (Button) this.findViewById(R.id.moltens_breakables_paid_info);
        moltensBreakablesPaidInfo.setOnClickListener(onClickListener2);
        bubblesPortalsPaidInfo = (Button) this.findViewById(R.id.bubbles_portals_paid_info);
        bubblesPortalsPaidInfo.setOnClickListener(onClickListener2);
        portalsBreakablesPaidInfo = (Button) this.findViewById(R.id.portals_breakables_paid_info);
        portalsBreakablesPaidInfo.setOnClickListener(onClickListener2);
        portalsBubblesPaidInfo = (Button) this.findViewById(R.id.portals_bubbles_paid_info);
        portalsBubblesPaidInfo.setOnClickListener(onClickListener2);
        bubblesMoltensPaidInfo = (Button) this.findViewById(R.id.bubbles_moltens_paid_info);
        bubblesMoltensPaidInfo.setOnClickListener(onClickListener2);
        bubblesBreakablesFrozensPaidInfo = (Button) this.findViewById(R.id.bubbles_breakables_frozens_paid_info);
        bubblesBreakablesFrozensPaidInfo.setOnClickListener(onClickListener2);

        tutorialCounter = (TextView) this.findViewById(R.id.tutorial_counter);
        standardCounter = (TextView) this.findViewById(R.id.standard_counter);
        bonusCounter = (TextView) this.findViewById(R.id.bonus_counter);
        fireCounter = (TextView) this.findViewById(R.id.fire_counter);
        bubblesFreeCounter = (TextView) this.findViewById(R.id.bubbles_free_counter);
        frozensFreeCounter = (TextView) this.findViewById(R.id.frozens_free_counter);
        portalsFreeCounter = (TextView) this.findViewById(R.id.portals_free_counter);
        breakablesFreeCounter = (TextView) this.findViewById(R.id.breakables_free_counter);
        everythingOneFreeCounter = (TextView) this.findViewById(R.id.everything_one_free_counter);
        fireAndIceCounter = (TextView) this.findViewById(R.id.fire_and_ice_counter);
        breakablesPaidCounter = (TextView) this.findViewById(R.id.breakables_paid_counter);
        standardPaidCounter = (TextView) this.findViewById(R.id.standard_paid_counter);
        bubblesPaidCounter = (TextView) this.findViewById(R.id.bubbles_paid_counter);
        portalsPaidCounter = (TextView) this.findViewById(R.id.portals_paid_counter);
        everythingPaidCounter = (TextView) this.findViewById(R.id.everything_paid_counter);
        moltensBreakablesPaidCounter = (TextView) this.findViewById(R.id.moltens_breakables_paid_counter);
        bubblesPortalsPaidCounter = (TextView) this.findViewById(R.id.bubbles_portals_paid_counter);
        portalsBreakablesPaidCounter = (TextView) this.findViewById(R.id.portals_breakables_paid_counter);
        portalsBubblesPaidCounter = (TextView) this.findViewById(R.id.portals_bubbles_paid_counter);
        bubblesMoltensPaidCounter = (TextView) this.findViewById(R.id.bubbles_moltens_paid_counter);
        bubblesBreakablesFrozensPaidCounter = (TextView) this.findViewById(R.id.bubbles_breakables_frozens_paid_counter);

        standardLock = this.findViewById(R.id.standard_lock);
        bonusLock = this.findViewById(R.id.bonus_lock);
        fireLock = this.findViewById(R.id.fire_lock);
        bubblesFreeLock = this.findViewById(R.id.bubbles_free_lock);
        frozensFreeLock = this.findViewById(R.id.frozens_free_lock);
        portalsFreeLock = this.findViewById(R.id.portals_free_lock);
        breakablesFreeLock = this.findViewById(R.id.breakables_free_lock);
        everythingOneFreeLock = this.findViewById(R.id.everything_one_free_lock);
        fireAndIceLock = this.findViewById(R.id.fire_and_ice_lock);
        breakablesPaidLock = this.findViewById(R.id.breakables_paid_lock);
        standardPaidLock = this.findViewById(R.id.standard_paid_lock);
        bubblesPaidLock = this.findViewById(R.id.bubbles_paid_lock);
        portalsPaidLock = this.findViewById(R.id.portals_paid_lock);
        everythingPaidLock = this.findViewById(R.id.everything_paid_lock);
        moltensBreakablesPaidLock = this.findViewById(R.id.moltens_breakables_paid_lock);
        bubblesPortalsPaidLock = this.findViewById(R.id.bubbles_portals_paid_lock);
        portalsBreakablesPaidLock = this.findViewById(R.id.portals_breakables_paid_lock);
        portalsBubblesPaidLock = this.findViewById(R.id.portals_bubbles_paid_lock);
        bubblesMoltensPaidLock = this.findViewById(R.id.bubbles_moltens_paid_lock);
        bubblesBreakablesFrozensPaidLock = this.findViewById(R.id.bubbles_breakables_frozens_paid_lock);

        levelDialog = new LevelDialog(this);

        setCounters();

        User.add("Went to Levels");

        freePacks.add("Stout Temple");
        freePacks.add("Brittle Temple");
        freePacks.add("Flame Temple");
        freePacks.add("Chilled Temple");
        freePacks.add("Tidal Temple");//Aqua and Tidal packs switched because Aqua has not hints
        freePacks.add("Spirit Temple");
        freePacks.add("Earth Temple");
        freePacks.add("Light Temple");

        setBillingProcessor();

        showDialogs();

    }
    public void showDialogs(){
        int packsBeat = sql.getCompletedPackCount();
        boolean sawPro = getSharedPreferences("ADS", Context.MODE_PRIVATE).getBoolean("saw_pro", false);
        boolean sawAll1 = getSharedPreferences("ADS", MODE_PRIVATE).getBoolean("saw_all1", false);
        boolean sawAll2 = getSharedPreferences("ADS", MODE_PRIVATE).getBoolean("saw_all2", false);
        boolean sawAll3 = getSharedPreferences("ADS", MODE_PRIVATE).getBoolean("saw_all3", false);
        if(packsBeat >= Config.PRO_DIALOG_INTERVAL && !sawPro && !User.pro){
            storeDialog = new StoreDialog(StoreDialog.PRO_BUNDLE, true, .25, null);
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.pro_bundle_message)).apply();
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.save_25)).apply();

            User.add("Showing Pro Dialog");
            FragmentManager fm = getSupportFragmentManager();
            Fragment dialog = getFragmentManager().findFragmentByTag("store_dialog");
            if(dialog == null) {
                if(!storeDialog.isAdded()) {
                    storeDialog.setCancelable(true);
                    storeDialog.show(fm, "store_dialog");
                    getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_pro",true).apply();
                }
            }else if(dialog.getActivity() != Levels.this) {
            }else {
            }
        }else if(packsBeat >= Config.ALL_DIALOG_INTERVAL_1 && !sawAll1 && !User.allPacks && !User.pro){
            storeDialog = new StoreDialog(StoreDialog.ALL_PACKS_BUNDLE, true, .1, null);
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.all_packs_message)).apply();
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.save_10)).apply();

            User.add("Showing All Packs Dialog 1");
            FragmentManager fm = getSupportFragmentManager();
            Fragment dialog = getFragmentManager().findFragmentByTag("store_dialog");
            if(dialog == null) {
                if(!storeDialog.isAdded()) {
                    storeDialog.setCancelable(true);
                    storeDialog.show(fm, "store_dialog");
                    getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_all1",true).apply();
                }
            }else if(dialog.getActivity() != Levels.this) {
            }else {
            }
        }else if(packsBeat >= Config.ALL_DIALOG_INTERVAL_2 && sawAll1 && !sawAll2 && !User.allPacks && !User.pro){
            storeDialog = new StoreDialog(StoreDialog.ALL_PACKS_BUNDLE, true, .25, null);
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.all_packs_message)).apply();
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.save_25)).apply();

            User.add("Showing All Packs Dialog 2");
            FragmentManager fm = getSupportFragmentManager();
            Fragment dialog = getFragmentManager().findFragmentByTag("store_dialog");
            if(dialog == null) {
                if(!storeDialog.isAdded()) {
                    storeDialog.setCancelable(true);
                    storeDialog.show(fm, "store_dialog");
                    getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_all2",true).apply();
                }
            }else if(dialog.getActivity() != Levels.this) {
            }else {
            }
        }else if(packsBeat >= Config.ALL_DIALOG_INTERVAL_3 && sawAll1 && sawAll2 && !sawAll3 && !User.allPacks && !User.pro){
            storeDialog = new StoreDialog(StoreDialog.ALL_PACKS_BUNDLE, true, .5, null);
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Title", getString(R.string.limited_time_offer)).apply();
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Message", getString(R.string.all_packs_message)).apply();
            getSharedPreferences("StoreDialog", Context.MODE_PRIVATE).edit().putString("Button", getString(R.string.save_50)).apply();

            User.add("Showing All Packs Dialog 3");
            FragmentManager fm = getSupportFragmentManager();
            Fragment dialog = getFragmentManager().findFragmentByTag("store_dialog");
            if(dialog == null) {
                if(!storeDialog.isAdded()) {
                    storeDialog.setCancelable(true);
                    storeDialog.show(fm, "store_dialog");
                    getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putBoolean("saw_all3",true).apply();
                }
            }else if(dialog.getActivity() != Levels.this) {
            }else {
            }
        }
    }
    public void onCloseDialog() {
        setCounters();
    }
    public void showCoinsStore() {
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
                add("Showing CoinsDialog");
                coinsDialog.setCancelable(true);
                coinsDialog.show(fm, "coins_dialog");
            }
        } else if (dialog.getActivity() != Levels.this) {
        } else {
        }
    }
    View.OnClickListener onClickListener2 = new View.OnClickListener() {
        public void onClick(View view) {
            if (view instanceof Button) {
                buttonClick(view);
            }
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
            Fragment dialog = getFragmentManager().findFragmentByTag("info_dialog");
            add("Showing InfoDialog");
            switch (view.getId()) {
                case tutorial_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Tutorial").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case standard_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Stout Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case bonus_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Earth Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case fire_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Flame Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case bubbles_free_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Aqua Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case everything_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Sun Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case bubbles_portals_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Sunken Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case moltens_breakables_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Volcano Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case frozens_free_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Chilled Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case portals_free_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Spirit Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case breakables_free_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Brittle Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case everything_one_free_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Light Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case portals_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Mystic Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case bubbles_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Tidal Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case fire_and_ice_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Elemental Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case breakables_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Crumbling Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case standard_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Stalwart Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case portals_breakables_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Cryptic Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case portals_bubbles_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Lost Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case bubbles_moltens_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Steam Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
                    }
                    break;
                case bubbles_breakables_frozens_paid_info:
                    getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putString("INFOPACK", "Arctic Temple").apply();
                    if (dialog == null) {
                        if (!infoDialog.isAdded()) {
                            infoDialog.setCancelable(true);
                            infoDialog.show(fm, "info_dialog");
                        }
                    } else if (dialog.getActivity() != Levels.this) {
                    } else {
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
    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if(view instanceof Button){
                buttonClick(view);
            }
            switch (view.getId()) {
                case R.id.tutorial:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Tutorial")
                            .apply();
                    PURCHASE_CODE = "tutorial";
                    break;
                case R.id.standard_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Stout Temple")
                            .apply();
                    PURCHASE_CODE = "standard_pack";
                    break;
                case R.id.bonus_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Earth Temple")
                            .apply();
                    PURCHASE_CODE = "bonus_pack";
                    break;
                case R.id.fire_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Flame Temple")
                            .apply();
                    PURCHASE_CODE = "fire_pack";
                    break;
                case R.id.bubbles_free_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Aqua Temple")
                            .apply();
                    PURCHASE_CODE = "bubbles_free_pack";
                    break;
                case R.id.frozens_free_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Chilled Temple")
                            .apply();
                    PURCHASE_CODE = "frozens_free_pack";
                    break;
                case R.id.portals_free_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Spirit Temple")
                            .apply();
                    PURCHASE_CODE = "portals_free_pack";
                    break;
                case R.id.everything_one_free_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Light Temple")
                            .apply();
                    PURCHASE_CODE = "everything_one_free_pack";
                    break;
                case R.id.bubbles_portals_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Sunken Temple")
                            .apply();
                    PURCHASE_CODE = "bubbles_portals_paid_pack";
                    break;
                case R.id.everything_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Sun Temple")
                            .apply();
                    PURCHASE_CODE = "everything_paid_pack";
                    break;
                case R.id.moltens_breakables_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Volcano Temple")
                            .apply();
                    PURCHASE_CODE = "moltens_breakables_paid_pack";
                    break;
                case R.id.portals_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Mystic Temple")
                            .apply();
                    PURCHASE_CODE = "portals_paid_pack";
                    break;
                case R.id.bubbles_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Tidal Temple")
                            .apply();
                    PURCHASE_CODE = "bubbles_paid_pack";
                    break;
                case R.id.fire_and_ice_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Elemental Temple")
                            .apply();
                    PURCHASE_CODE = "fire_and_ice_paid_pack";
                    break;
                case R.id.standard_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Stalwart Temple")
                            .apply();
                    PURCHASE_CODE = "standard_paid_pack";
                    break;
                case R.id.breakables_free_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Brittle Temple")
                            .apply();
                    PURCHASE_CODE = "breakables_free_pack";
                    break;
                case R.id.breakables_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Crumbling Temple")
                            .apply();
                    PURCHASE_CODE = "breakables_paid_pack";
                    break;
                case R.id.portals_breakables_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Cryptic Temple")
                            .apply();
                    PURCHASE_CODE = "portals_breakables_paid_pack";
                    break;
                case R.id.portals_bubbles_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Lost Temple")
                            .apply();
                    PURCHASE_CODE = "portals_bubbles_paid_pack";
                    break;
                case R.id.bubbles_moltens_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Steam Temple")
                            .apply();
                    PURCHASE_CODE = "bubbles_moltens_paid_pack";
                    break;
                case R.id.bubbles_breakables_frozens_paid_pack:
                    sd.buttonPress();
                    getSharedPreferences("LEVELS", MODE_PRIVATE)
                            .edit()
                            .putString("PACK", "Arctic Temple")
                            .apply();
                    PURCHASE_CODE = "bubbles_breakables_frozens_paid_pack";
                    break;
                case R.id.previous:
                    onBackPressed();
                    break;
            }
            if(view.getId() != R.id.previous && view.getId() != R.id.tutorial && view.getId() != R.id.bubbles_moltens_paid_pack && view.getId() != R.id.portals_bubbles_paid_pack && view.getId() != R.id.bubbles_free_pack) {
                if (sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = '" + getSharedPreferences("LEVELS", MODE_PRIVATE).getString("PACK", "Stout Temple") + "' ").equals("1") || User.allPacks) {
                    Intent a = new Intent(context, Pack.class);
                    leaving = true;
                    User.add("Going to Pack");
                    startActivity(a);
                    Levels.this.finish();
                } else {
                    if (freePacks.contains(getSharedPreferences("LEVELS", MODE_PRIVATE).getString("PACK", "Stout Temple"))) {
                        String message = getResources().getString(R.string.pack_not_available);
                        getSharedPreferences("Unlock", MODE_PRIVATE).edit().putString("Message", message).apply();
                        FragmentManager fm = getSupportFragmentManager();
                        fm.executePendingTransactions();
                        Fragment dialog = getFragmentManager().findFragmentByTag("level_dialog");
                        if(dialog == null) {
                            if(!levelDialog.isAdded()) {
                                levelDialog.setCancelable(true);
                                User.add("Showing Level Dialog to purchase.");
                                levelDialog.show(fm, "level_dialog");
                            }
                        }else if(dialog.getActivity() != Levels.this) {
                        }else {
                        }
                        //need to take away lock img
                    } else {
                        if (User.username.equals("")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle(getResources().getString(R.string.not_logged_in)).setMessage(getResources().getString(R.string.please_log_in)).setPositiveButton(context.getResources().getString(R.string.login), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent a = new Intent(Levels.this, Login.class);
                                    leaving = true;
                                    User.add("Going to Login from Levels");
                                    startActivity(a);
                                    Levels.this.finish();
                                }
                            }).setNegativeButton(getResources().getString(R.string.continue_purchase), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String message = getResources().getString(R.string.pack_not_available);
                                    getSharedPreferences("Unlock", MODE_PRIVATE).edit().putString("Message", message).apply();
                                    FragmentManager fm = getSupportFragmentManager();
                                    Fragment dialog = getFragmentManager().findFragmentByTag("level_dialog");
                                    if(dialog == null) {
                                        if(!levelDialog.isAdded()) {
                                            levelDialog.setCancelable(true);
                                            User.add("Showing Level Dialog to purchase.");
                                            levelDialog.show(fm, "level_dialog");
                                        }
                                    }else if(dialog.getActivity() != Levels.this) {
                                    }else {
                                    }
                                    //need to take away lock img
                                }
                            }).setCancelable(false);
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            if(alertDialog.isShowing()) {
                                int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                                View divider = alertDialog.findViewById(dividerId);
                                divider.setBackgroundColor(getResources().getColor(R.color.gold));
                            }
                        } else {
                            getSharedPreferences("Unlock", MODE_PRIVATE).edit().putString("Message", "").apply();
                            FragmentManager fm = getSupportFragmentManager();
                            fm.executePendingTransactions();
                            Fragment dialog = getFragmentManager().findFragmentByTag("level_dialog");
                            if(dialog == null) {
                                if(!levelDialog.isAdded()) {
                                    levelDialog.setCancelable(true);
                                    User.add("Showing Level Dialog to purchase.");
                                    levelDialog.show(fm, "level_dialog");
                                }
                            }else if(dialog.getActivity() != Levels.this) {
                            }else {
                            }
                        }
                    }
                }
            }else if(view.getId() == R.id.tutorial){
                Intent a = new Intent(context, TutorialPack.class);
                leaving = true;
                startActivity(a);
                Levels.this.finish();
            }else if(view.getId() == R.id.bubbles_moltens_paid_pack || view.getId() == R.id.portals_bubbles_paid_pack || view.getId() == R.id.bubbles_free_pack){
                Toast.makeText(context, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
            }
        }
    };
    public void onResume(){
        super.onResume();
        Chartboost.onResume(this);
        AppsFlyerLib.onActivityResume(this);
        if(!sd.isPlaying("homeBackground")) {
            sd.stop();
            sd.homeBackground();
        }

        leaving = false;
        setCounters();
    }
    public void setCounters(){
        SQL sql = new SQL(context);

        standardCounter.setVisibility(View.INVISIBLE);
        bonusCounter.setVisibility(View.INVISIBLE);
        fireCounter.setVisibility(View.INVISIBLE);
        bubblesFreeCounter.setVisibility(View.INVISIBLE);
        frozensFreeCounter.setVisibility(View.INVISIBLE);
        portalsFreeCounter.setVisibility(View.INVISIBLE);
        everythingOneFreeCounter.setVisibility(View.INVISIBLE);
        fireAndIceCounter.setVisibility(View.INVISIBLE);
        standardPaidCounter.setVisibility(View.INVISIBLE);
        breakablesFreeCounter.setVisibility(View.INVISIBLE);
        bubblesPaidCounter.setVisibility(View.INVISIBLE);
        breakablesPaidCounter.setVisibility(View.INVISIBLE);
        portalsPaidCounter.setVisibility(View.INVISIBLE);
        moltensBreakablesPaidCounter.setVisibility(View.INVISIBLE);
        everythingPaidCounter.setVisibility(View.INVISIBLE);
        bubblesPortalsPaidCounter.setVisibility(View.INVISIBLE);
        portalsBreakablesPaidCounter.setVisibility(View.INVISIBLE);
        portalsBubblesPaidCounter.setVisibility(View.INVISIBLE);
        bubblesMoltensPaidCounter.setVisibility(View.INVISIBLE);
        bubblesBreakablesFrozensPaidCounter.setVisibility(View.INVISIBLE);

        standardLock.setVisibility(View.INVISIBLE);
        bonusLock.setVisibility(View.INVISIBLE);
        fireLock.setVisibility(View.INVISIBLE);
        bubblesFreeLock.setVisibility(View.INVISIBLE);
        frozensFreeLock.setVisibility(View.INVISIBLE);
        portalsFreeLock.setVisibility(View.INVISIBLE);
        everythingOneFreeLock.setVisibility(View.INVISIBLE);
        fireAndIceLock.setVisibility(View.INVISIBLE);
        standardPaidLock.setVisibility(View.INVISIBLE);
        breakablesFreeLock.setVisibility(View.INVISIBLE);
        bubblesPaidLock.setVisibility(View.INVISIBLE);
        breakablesPaidLock.setVisibility(View.INVISIBLE);
        portalsPaidLock.setVisibility(View.INVISIBLE);
        moltensBreakablesPaidLock.setVisibility(View.INVISIBLE);
        everythingPaidLock.setVisibility(View.INVISIBLE);
        bubblesPortalsPaidLock.setVisibility(View.INVISIBLE);
        portalsBreakablesPaidLock.setVisibility(View.INVISIBLE);
        portalsBubblesPaidLock.setVisibility(View.INVISIBLE);
        bubblesMoltensPaidLock.setVisibility(View.INVISIBLE);
        bubblesBreakablesFrozensPaidLock.setVisibility(View.INVISIBLE);

        tutorialCounter.setText(sql.getPackCount("Tutorial") + "/6");
        standardCounter.setText(sql.getPackCount("Stout Temple")+"/30");
        bonusCounter.setText(sql.getPackCount("Earth Temple")+"/30");
        fireCounter.setText(sql.getPackCount("Flame Temple") + "/30");
        bubblesFreeCounter.setText(sql.getPackCount("Aqua Temple") + "/30");
        frozensFreeCounter.setText(sql.getPackCount("Chilled Temple") + "/30");
        portalsFreeCounter.setText(sql.getPackCount("Spirit Temple") + "/30");
        everythingOneFreeCounter.setText(sql.getPackCount("Light Temple") + "/30");
        fireAndIceCounter.setText(sql.getPackCount("Elemental Temple") + "/30");
        standardPaidCounter.setText(sql.getPackCount("Stalwart Temple") + "/30");
        breakablesFreeCounter.setText(sql.getPackCount("Brittle Temple") + "/30");
        bubblesPaidCounter.setText(sql.getPackCount("Tidal Temple") + "/30");
        breakablesPaidCounter.setText(sql.getPackCount("Crumbling Temple") + "/30");
        portalsPaidCounter.setText(sql.getPackCount("Mystic Temple") + "/30");
        everythingPaidCounter.setText(sql.getPackCount("Sun Temple") + "/30");
        moltensBreakablesPaidCounter.setText(sql.getPackCount("Volcano Temple") + "/30");
        bubblesPortalsPaidCounter.setText(sql.getPackCount("Sunken Temple") + "/30");
        portalsBreakablesPaidCounter.setText(sql.getPackCount("Cryptic Temple") + "/30");
        portalsBubblesPaidCounter.setText(sql.getPackCount("Lost Temple") + "/30");
        bubblesMoltensPaidCounter.setText(sql.getPackCount("Steam Temple") + "/30");
        bubblesBreakablesFrozensPaidCounter.setText(sql.getPackCount("Arctic Temple") + "/30");



        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Stout Temple' ").equals("1") || User.allPacks){
            standardCounter.setVisibility(View.VISIBLE);
        }else{
            standardLock.setVisibility(View.VISIBLE);
        }

        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Earth Temple' ").equals("1") || User.allPacks){
            bonusCounter.setVisibility(View.VISIBLE);
        }else{
            bonusLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Flame Temple' ").equals("1") || User.allPacks){
            fireCounter.setVisibility(View.VISIBLE);
        }else{
            fireLock.setVisibility(View.VISIBLE);
        }
        //if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Aqua Temple' ").equals("1") || User.allPacks){
        if(!true){
              bubblesFreeCounter.setVisibility(View.VISIBLE);
        }else{
            bubblesFreeLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Chilled Temple' ").equals("1") || User.allPacks){
            frozensFreeCounter.setVisibility(View.VISIBLE);
        }else{
            frozensFreeLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Spirit Temple' ").equals("1") || User.allPacks){
            portalsFreeCounter.setVisibility(View.VISIBLE);
        }else{
            portalsFreeLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Light Temple' ").equals("1") || User.allPacks){
            everythingOneFreeCounter.setVisibility(View.VISIBLE);
        }else{
            everythingOneFreeLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Sun Temple' ").equals("1") || User.allPacks){
            everythingPaidCounter.setVisibility(View.VISIBLE);
        }else{
            everythingPaidLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Sunken Temple' ").equals("1") || User.allPacks){
            bubblesPortalsPaidCounter.setVisibility(View.VISIBLE);
        }else{
            bubblesPortalsPaidLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Volcano Temple' ").equals("1") || User.allPacks){
            moltensBreakablesPaidCounter.setVisibility(View.VISIBLE);
        }else{
            moltensBreakablesPaidLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Mystic Temple' ").equals("1") || User.allPacks){
            portalsPaidCounter.setVisibility(View.VISIBLE);
        }else{
            portalsPaidLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Tidal Temple' ").equals("1") || User.allPacks){
            bubblesPaidCounter.setVisibility(View.VISIBLE);
        }else{
            bubblesPaidLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Elemental Temple' ").equals("1") || User.allPacks){
            fireAndIceCounter.setVisibility(View.VISIBLE);
        }else{
            fireAndIceLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Stalwart Temple' ").equals("1") || User.allPacks){
            standardPaidCounter.setVisibility(View.VISIBLE);
        }else{
            standardPaidLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Brittle Temple' ").equals("1") || User.allPacks){
            breakablesFreeCounter.setVisibility(View.VISIBLE);
        }else{
            breakablesFreeLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Crumbling Temple' ").equals("1") || User.allPacks){
            breakablesPaidCounter.setVisibility(View.VISIBLE);
        }else{
            breakablesPaidLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Cryptic Temple' ").equals("1") || User.allPacks){
            portalsBreakablesPaidCounter.setVisibility(View.VISIBLE);
        }else{
            portalsBreakablesPaidLock.setVisibility(View.VISIBLE);
        }
        //if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Lost Temple' ").equals("1") || User.allPacks){
        if(!true){
            portalsBubblesPaidCounter.setVisibility(View.VISIBLE);
        }else{
            portalsBubblesPaidLock.setVisibility(View.VISIBLE);
        }
        //if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Steam Temple' ").equals("1") || User.allPacks){
        if(!true){
            bubblesMoltensPaidCounter.setVisibility(View.VISIBLE);
        }else{
            bubblesMoltensPaidLock.setVisibility(View.VISIBLE);
        }
        if(sql.getSingleResult("permissions_packs", "permission", "where username = '" + User.getUser() + "' and pack = 'Arctic Temple' ").equals("1") || User.allPacks){
            bubblesBreakablesFrozensPaidCounter.setVisibility(View.VISIBLE);
        }else{
            bubblesBreakablesFrozensPaidLock.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sd.backPress();
        leaving = true;
        Intent a = new Intent(Levels.this, Home.class);
        startActivity(a);
        Levels.this.finish();
    }
    public void onPause(){
        super.onPause();
        Chartboost.onPause(this);
        AppsFlyerLib.onActivityPause(this);
        if(!leaving){
            sd.stop();
        }
    }
    public void onDestroy(){
        super.onDestroy();

        bp.release();
        Chartboost.onDestroy(this);

        /*
        context = null;
        sql = null;
        levelDialog = null;
        sd = null;
        coinsDialog = null;

        tutorial = null;
        standard = null;
        bonus = null;
        fire = null;
        bubblesFree = null;
        frozensFree = null;
        portalsFree = null;
        everythingOneFree = null;
        fireAndIce = null;
        standardPaid = null;
        breakablesFree = null;
        breakablesPaid = null;
        previous = null;
        bubblesPaid = null;
        portalsPaid = null;
        everythingPaid = null;
        moltensBreakablesPaid = null;
        bubblesPortalsPaid = null;
        portalsBreakablesPaid = null;
        portalsBubblesPaid = null;
        bubblesMoltensPaid = null;
        bubblesBreakablesFrozensPaid = null;

        tutorialCounter = null;
        standardCounter = null;
        bonusCounter = null;
        fireCounter = null;
        bubblesFreeCounter = null;
        frozensFreeCounter = null;
        portalsFreeCounter = null;
        everythingOneFreeCounter = null;
        fireAndIceCounter = null;
        standardPaidCounter = null;
        breakablesFreeCounter = null;
        bubblesPaidCounter = null;
        breakablesPaidCounter = null;
        portalsPaidCounter = null;
        moltensBreakablesPaidCounter = null;
        everythingPaidCounter = null;
        bubblesPortalsPaidCounter = null;
        portalsBreakablesPaidCounter = null;
        portalsBubblesPaidCounter = null;
        bubblesMoltensPaidCounter = null;
        bubblesBreakablesFrozensPaidCounter = null;

        standardLock = null;
        bonusLock = null;
        fireLock = null;
        bubblesFreeLock = null;
        frozensFreeLock = null;
        portalsFreeLock = null;
        everythingOneFreeLock = null;
        fireAndIceLock = null;
        standardPaidLock = null;
        breakablesFreeLock = null;
        bubblesPaidLock = null;
        breakablesPaidLock = null;
        portalsPaidLock = null;
        moltensBreakablesPaidLock = null;
        everythingPaidLock = null;
        bubblesPortalsPaidLock = null;
        portalsBreakablesPaidLock = null;
        portalsBubblesPaidLock = null;
        bubblesMoltensPaidLock = null;
        bubblesBreakablesFrozensPaidLock = null;

        freePacks = null;
*/
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
                        Intent userIntent = new Intent(Levels.this, ParseService.class);
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