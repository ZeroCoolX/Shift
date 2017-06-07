package lucky8s.shift;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.apptracker.android.listener.AppModuleListener;
import com.apptracker.android.track.AppTracker;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError;
import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.IncentivizedAd;
import com.heyzap.sdk.ads.InterstitialAd;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyInterstitialAd;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.adbuddiz.sdk.AdBuddizDelegate;
import com.purplebrain.adbuddiz.sdk.AdBuddizError;
import com.purplebrain.adbuddiz.sdk.AdBuddizRewardedVideoDelegate;
import com.purplebrain.adbuddiz.sdk.AdBuddizRewardedVideoError;
import com.vungle.publisher.EventListener;
import com.vungle.publisher.VunglePub;

import java.util.ArrayList;

/**
 * Created by Christian on 1/3/2016.
 */
public class Ad{
    public static Activity act;
    public static AdInterface adInterface;
    public static VunglePub vungle;
    public static AdColonyInterstitialAd adColonyInterstitialAd;
    public static boolean initialized;
    public static String lastPlayedIncentive = "";
    public static String lastPlayedInterstitial = "";
    public static ConnectivityManager cm;
    public static NetworkInfo netInfo;
    public static void init(Activity activity){
        act = activity;
        HeyzapAds.start("a0b7fa4653e075aef579cf948709e77c", act);
        AdBuddiz.setPublisherKey("2f90c31e-012f-4668-82a4-66fbefd19ebf");
        vungle = VunglePub.getInstance();
        vungle.init(act, Config.APP_ID);
        AdColony.configure(act,null,Config.ADCOLONY_APPID,"vz57d4406ba3324044a5");
        adColonyInterstitialAd = new AdColonyInterstitialAd("vz57d4406ba3324044a5");
        Chartboost.startWithAppId(act, "56822860f78982257a5b9d8e", "76286ec46a9d7f5da8e24bfbd4985638b73eca13");
        Chartboost.setDelegate(chartboostDelegate);
        AppTracker.startSession(act.getApplicationContext(),"bpTlGG7qjK7qoB1i9qGAAoPpKhZyi2IN");
        AppTracker.setModuleListener(leadboltListener);
        fetch("All");

        AdBuddiz.RewardedVideo.setDelegate(adBuddizRewardedVideoDelegate);
        AdBuddiz.setDelegate(adBuddizDelegate);

        IncentivizedAd.setOnIncentiveResultListener(heyzapIncentiveListener);
        InterstitialAd.setOnStatusListener(heyzapInterstitialListener);

        vungle.addEventListeners(vungleEventListener);
        if(adColonyInterstitialAd != null) {
            adColonyInterstitialAd.withListener(adColonyAdListener);
        }

        initialized = true;
    }
    public static void setAdInterface(AdInterface adInterfacePassed){
        adInterface = adInterfacePassed;
    }
    static AppModuleListener leadboltListener = new AppModuleListener() {
        @Override
        public void onModuleLoaded(String s) {
            if(act != null) {
                act.getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putInt("levels_completed", 0).apply();
                int numImpressions = act.getSharedPreferences("ADS", Context.MODE_PRIVATE).getInt("interstitial_impressions",0);
                act.getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putInt("interstitial_impressions",numImpressions+1).apply();
            }
            lastPlayedInterstitial = "Leadbolt";
            cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
            if(netInfo != null) {
                fetch("Leadbolt");
            }
        }

        @Override
        public void onModuleClosed(String s) {
            if(adInterface != null) {
                adInterface.showFinished();
            }
        }

        @Override
        public void onModuleClicked(String s) {

        }

        @Override
        public void onModuleCached(String s) {

        }

        @Override
        public void onModuleFailed(String s, String s1, boolean b) {
            if(adInterface != null) {
                adInterface.showFinished();
            }
            fetch("Leadbolt");
        }

        @Override
        public void onMediaFinished(boolean b) {
            if(b){
                //adInterface.getReward();
            }
        }
    };
    static ChartboostDelegate chartboostDelegate = new ChartboostDelegate() {
        @Override
        public void didDismissInterstitial(String location) {
            super.didDismissInterstitial(location);
        }
        @Override
        public void didFailToLoadInterstitial(String location, CBError.CBImpressionError error) {
            super.didFailToLoadInterstitial(location, error);
            if(adInterface != null) {
                adInterface.showFinished();
            }
            fetch("Chartboost");
        }
        @Override
        public void didCloseInterstitial(String location) {
            super.didCloseInterstitial(location);
            if(adInterface != null) {
                adInterface.showFinished();
            }
            fetch("Chartboost");
        }

        @Override
        public void didClickInterstitial(String location) {
            super.didClickInterstitial(location);
            Log.i("debugger", "didClickInterstitial");
        }

        @Override
        public void didDisplayInterstitial(String location) {
            super.didDisplayInterstitial(location);
            if(act != null) {
                act.getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putInt("levels_completed", 0).apply();
                int numImpressions = act.getSharedPreferences("ADS", Context.MODE_PRIVATE).getInt("interstitial_impressions",0);
                act.getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putInt("interstitial_impressions",numImpressions+1).apply();
            }
            lastPlayedInterstitial = "Chartboost";
        }

        @Override
        public void didDismissRewardedVideo(String location) {
            super.didDismissRewardedVideo(location);
        }

        @Override
        public void didCloseRewardedVideo(String location) {
            super.didCloseRewardedVideo(location);
        }

        @Override
        public void didClickRewardedVideo(String location) {
            super.didClickRewardedVideo(location);
            Log.i("debugger", "didClickRewardedVideo");
        }

        @Override
        public void didCompleteRewardedVideo(String location, int reward) {
            super.didCompleteRewardedVideo(location, reward);
            if(adInterface != null) {
                adInterface.getReward();
            }
            lastPlayedIncentive = "Chartboost";
        }

        @Override
        public void didDisplayRewardedVideo(String location) {
            super.didDisplayRewardedVideo(location);
            fetch("Chartboost");
        }
    };
    static AdColonyAdListener adColonyAdListener = new AdColonyAdListener() {
        @Override
        public void onAdColonyAdAttemptFinished(AdColonyAd adColonyAd) {
            if(adInterface != null) {
                adInterface.showFinished();
            }
                if(adColonyAd.shown()) {
                    if(act != null) {
                        act.getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putInt("levels_completed", 0).apply();
                        int numImpressions = act.getSharedPreferences("ADS", Context.MODE_PRIVATE).getInt("interstitial_impressions",0);
                        act.getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putInt("interstitial_impressions",numImpressions+1).apply();
                    }
                    fetch("AdColony");
                    lastPlayedInterstitial = "AdColony";
                }
        }

        @Override
        public void onAdColonyAdStarted(AdColonyAd adColonyAd) {
            if((adColonyAd.noFill() || adColonyAd.notShown())){
                if(adInterface != null) {
                    adInterface.showFinished();
                }
                fetch("AdColony");
            }
        }
    };
    static EventListener vungleEventListener = new EventListener() {
        @Override
        public void onAdEnd(boolean b) {
        }

        @Override
        public void onAdStart() {
        }

        @Override
        public void onAdUnavailable(String s) {
        }

        @Override
        public void onAdPlayableChanged(boolean b) {

        }

        @Override
        public void onVideoView(boolean b, int i, int i1) {
            if(b){
                if(adInterface != null) {
                    adInterface.getReward();
                }
                lastPlayedIncentive = "Vungle";
            }
        }
    };
    static HeyzapAds.OnStatusListener heyzapInterstitialListener = new HeyzapAds.OnStatusListener() {
        @Override
        public void onShow(String s) {
            if(act != null) {
                act.getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putInt("levels_completed", 0).apply();
                int numImpressions = act.getSharedPreferences("ADS", Context.MODE_PRIVATE).getInt("interstitial_impressions",0);
                act.getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putInt("interstitial_impressions",numImpressions+1).apply();
            }
            lastPlayedInterstitial = "Heyzap";
        }

        @Override
        public void onClick(String s) {

        }

        @Override
        public void onHide(String s) {
            if(adInterface != null) {
                adInterface.showFinished();
            }
        }

        @Override
        public void onFailedToShow(String s) {
            if(adInterface != null) {
                adInterface.showFinished();
            }
        }

        @Override
        public void onAvailable(String s) {

        }

        @Override
        public void onFailedToFetch(String s) {

        }

        @Override
        public void onAudioStarted() {

        }

        @Override
        public void onAudioFinished() {

        }
    };
    static HeyzapAds.OnIncentiveResultListener heyzapIncentiveListener = new HeyzapAds.OnIncentiveResultListener() {
        @Override
        public void onComplete(String tag) {
            if(adInterface != null) {
                adInterface.getReward();
            }
            lastPlayedIncentive = "Heyzap";
        }

        @Override
        public void onIncomplete(String tag) {
            //tell them why they don't get reward
        }
    };
    static AdBuddizDelegate adBuddizDelegate = new AdBuddizDelegate() {
        @Override
        public void didCacheAd() {

        }

        @Override
        public void didShowAd() {
            if(act != null) {
                act.getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putInt("levels_completed", 0).apply();
                int numImpressions = act.getSharedPreferences("ADS", Context.MODE_PRIVATE).getInt("interstitial_impressions",0);
                act.getSharedPreferences("ADS", Context.MODE_PRIVATE).edit().putInt("interstitial_impressions",numImpressions+1).apply();
            }
            lastPlayedInterstitial = "AdBuddiz";
        }

        @Override
        public void didFailToShowAd(AdBuddizError adBuddizError) {
            if(adInterface != null) {
                adInterface.showFinished();
            }
            fetch("AdBuddiz");
        }

        @Override
        public void didClick() {

        }

        @Override
        public void didHideAd() {
            if(adInterface != null) {
                adInterface.showFinished();
            }
            fetch("AdBuddiz");
        }
    };
    static AdBuddizRewardedVideoDelegate adBuddizRewardedVideoDelegate = new AdBuddizRewardedVideoDelegate() {
        @Override
        public void didFetch() {

        }

        @Override
        public void didFail(AdBuddizRewardedVideoError adBuddizRewardedVideoError) {

        }

        @Override
        public void didComplete() {
            if(adInterface != null) {
                adInterface.getReward();
            }
            lastPlayedIncentive = "AdBuddiz";
            fetch("AdBuddiz");
        }

        @Override
        public void didNotComplete() {

        }
    };
    public static boolean isIncentiveReady(){
            ArrayList<String> priorities = getPriorities("Incentive");
            for (int i = 0; i < priorities.size(); i++) {
                if (isIncentiveReady(priorities.get(i))) {
                    return true;
                }
            }
            return false;
    }
    public static boolean isIncentiveReady(String network){
        if(network == null || (Config.SKIP_LAST && network.equals(lastPlayedIncentive))){
            return false;
        }
        try {
            switch (network) {
                case "AdBuddiz":
                    boolean adBuddizReady;
                    try {
                        adBuddizReady = AdBuddiz.RewardedVideo.isReadyToShow(act);
                    } catch (Exception e2) {
                        adBuddizReady = false;
                    }
                    if (adBuddizReady) {
                        return true;
                    } else {
                        return false;
                    }
                case "Heyzap":
                    if (IncentivizedAd.isAvailable()) {
                        return true;
                    } else {
                        return false;
                    }
                case "Vungle":
                    if (vungle != null && vungle.isAdPlayable()) {
                        return true;
                    } else {
                        return false;
                    }
                case "Chartboost":
                    if (Chartboost.hasRewardedVideo(CBLocation.LOCATION_HOME_SCREEN)) {
                        return true;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        }catch (NullPointerException np){
            User.add(Log.getStackTraceString(np));
            return false;
        }
    }
    public static boolean isInterstitialReady(){
        ArrayList<String> priorities = getPriorities("Interstitial");
        for(int i = 0; i < priorities.size(); i++){
            if(isInterstitialReady(priorities.get(i))){
                return true;
            }
        }
        return false;
    }
    public static boolean isInterstitialReady(String network){
        if(Config.SKIP_LAST && network.equals(lastPlayedInterstitial)){
            return false;
        }
        try {
            switch (network) {
                case "AdBuddiz":
                    boolean adBuddizReady;
                    try {
                        adBuddizReady = AdBuddiz.isReadyToShowAd(act);
                    } catch (Exception e2) {
                        adBuddizReady = false;
                    }
                    if (adBuddizReady) {
                        return true;
                    } else {
                        return false;
                    }
                case "Heyzap":
                    if (InterstitialAd.isAvailable()) {
                        return true;
                    } else {
                        return false;
                    }
                case "AdColony":
                    if (adColonyInterstitialAd != null && adColonyInterstitialAd.isReady()) {
                        return true;
                    } else {
                        return false;
                    }
                case "Chartboost":
                    if (Chartboost.hasInterstitial(CBLocation.LOCATION_LEVEL_COMPLETE)) {
                        return true;
                    } else {
                        return false;
                    }
                case "Leadbolt":
                    if (AppTracker.isAdReady("inapp")) {
                        return true;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        }catch(NullPointerException np){
            User.add(Log.getStackTraceString(np));
            return false;
        }
    }
    public static void fetch(String param){
        cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if(netInfo == null){
            return;
        }
        switch (param){
            case "AdBuddiz":
                AdBuddiz.RewardedVideo.fetch(act);
                AdBuddiz.cacheAds(act);
                break;
            case "Heyzap":
                IncentivizedAd.fetch();
                InterstitialAd.fetch();
                break;
            case "AdColony":
                adColonyInterstitialAd = new AdColonyInterstitialAd("vz57d4406ba3324044a5");
                adColonyInterstitialAd.withListener(adColonyAdListener);
                break;
            case "Chartboost":
                Chartboost.cacheInterstitial(CBLocation.LOCATION_LEVEL_COMPLETE);
                Chartboost.cacheRewardedVideo(CBLocation.LOCATION_HOME_SCREEN);
                Chartboost.setDelegate(chartboostDelegate);
                break;
            case "Leadbolt":
                AppTracker.loadModuleToCache(act.getApplicationContext(),"inapp");
                break;
            case "All":
                ArrayList<String> priorities = getPriorities("All");
                for(int i = 0; i < priorities.size(); i++){
                    fetch(priorities.get(i));
                }
                break;
            default:
        }
    }
    public static void showInterstitial(){
        ArrayList<String> priorities = getPriorities("Interstitial");
        for(int i = 0; i < priorities.size(); i++){
            if(isInterstitialReady(priorities.get(i))){
                showInterstitial(priorities.get(i));
                return;
            }
        }
    }
    public static void showInterstitial(String param){
        switch (param){
            case "AdBuddiz":
                AdBuddiz.showAd(act);
                break;
            case "Heyzap":
                InterstitialAd.display(act);
                break;
            case "AdColony":
                adColonyInterstitialAd.show();
                break;
            case "Chartboost":
                Chartboost.showInterstitial(CBLocation.LOCATION_LEVEL_COMPLETE);
                Chartboost.setDelegate(chartboostDelegate);
                break;
            case "Leadbolt":
                AppTracker.loadModule(act.getApplicationContext(),"inapp");
                break;
            default:
                InterstitialAd.display(act);
                break;
        }
    }
    public static void showIncentive(){
        ArrayList<String> priorities = getPriorities("Incentive");
        for(int i = 0; i < priorities.size(); i++){
            if(isIncentiveReady(priorities.get(i))){
                showIncentive(priorities.get(i));
                return;
            }
        }
    }
    public static void showIncentive(String param){
        switch (param){
            case "AdBuddiz":
                AdBuddiz.RewardedVideo.show(act);
                break;
            case "Heyzap":
                IncentivizedAd.display(act);
                break;
            case "Vungle":
                vungle.playAd();
                break;
            case "Chartboost":
                Chartboost.showRewardedVideo(CBLocation.LOCATION_HOME_SCREEN);
                Chartboost.setDelegate(chartboostDelegate);
                break;
            default:
                IncentivizedAd.display(act);
                break;
        }
    }
    public static ArrayList<String> getPriorities(String param){
        ArrayList<String> priorities = new ArrayList<String>();
        switch (param){
            case "Incentive":
                priorities.add(Config.INCENTIVE_1);
                priorities.add(Config.INCENTIVE_2);
                priorities.add(Config.INCENTIVE_3);
                priorities.add(Config.INCENTIVE_4);
                break;
            case "Interstitial":
                priorities.add(Config.INTERSTITIAL_1);
                priorities.add(Config.INTERSTITIAL_2);
                priorities.add(Config.INTERSTITIAL_3);
                priorities.add(Config.INTERSTITIAL_4);
                priorities.add(Config.INTERSTITIAL_5);
                break;
            case "All":
                priorities.add(Config.INCENTIVE_1);
                priorities.add(Config.INCENTIVE_2);
                priorities.add(Config.INCENTIVE_3);
                priorities.add(Config.INCENTIVE_4);
                if(!priorities.contains(Config.INTERSTITIAL_1)){
                    priorities.add(Config.INTERSTITIAL_1);
                }
                if(!priorities.contains(Config.INTERSTITIAL_2)){
                    priorities.add(Config.INTERSTITIAL_2);
                }
                if(!priorities.contains(Config.INTERSTITIAL_3)){
                    priorities.add(Config.INTERSTITIAL_3);
                }
                if(!priorities.contains(Config.INTERSTITIAL_4)){
                    priorities.add(Config.INTERSTITIAL_4);
                }
                if(!priorities.contains(Config.INTERSTITIAL_5)){
                    priorities.add(Config.INTERSTITIAL_5);
                }
                break;
        }
        return priorities;
    }
    public static void setAct(Activity activity){
        act=activity;
    }
}
