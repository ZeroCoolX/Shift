package lucky8s.shift;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Christian on 6/1/2015.
 */
public class ParseService extends IntentService {

    public static final String Object = "permissions_packs";

    Random random = new Random();
    ParseObject noAdsArray;
    public static boolean syncUser = false;
    public static boolean syncPurchases = false;
    public static boolean syncScore = false;
    public static boolean syncScoreLevels = false;
    public static boolean syncPermissionsLevels = false;
    public static boolean syncPermissionsPacks = false;
    public static boolean synced = false;

    public ParseService() {
        super(ParseService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final SQL sql = new SQL(this);
        final ArrayList<String> packs = sql.getPacks(User.username);

        final String option = intent.getStringExtra(Object);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (option.equals("user")) {
                    final String num_hints = sql.getSingleResult("user", "hints", " where username = '" + User.username + "' ");
                    final String no_ads = sql.getSingleResult("user", "no_ads", " where username = '" + User.username + "' ");
                    final String rated = sql.getSingleResult("user", "rated", " where username = '" + User.username + "' ");
                    final String coins = sql.getSingleResult("user", "coins", " where username = '" + User.username + "' ");
                    final String gold = sql.getSingleResult("user", "gold", " where username = '" + User.username + "' ");
                    final ArrayList<String> noAdsArray = new ArrayList<>();
                    noAdsArray.addAll(Arrays.asList(no_ads.split("\\s*,\\s*")));
                    ArrayList<String> noAdsArrayParse = getNoAdsParse(User.username);
                    final ArrayList<String> differences = getDifference(noAdsArrayParse, noAdsArray);
                    final Locale locale = Locale.getDefault();
                    final ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
                    userQuery.whereEqualTo("username", User.username);
                    userQuery.orderByDescending("updatedAt");
                    userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                object = new ParseObject("user");
                            }
                            object.put("username", User.username);
                            object.put("password", User.password);
                            object.put("country", locale.getCountry());
                            object.put("hint", User.hint);
                            object.put("email", User.email);
                            object.put("hints", num_hints.equals("") ? 0 : Integer.valueOf(num_hints));
                            object.put("coins", coins.equals("") ? 0 : Integer.valueOf(coins));
                            object.put("gold", gold.equals("") ? 0 : Integer.valueOf(gold));
                            object.put("perfect_bonus_mod", User.perfectBonusModifier);
                            for (int i = 0; i < differences.size(); i++) {
                                object.add("no_ads", differences.get(i));
                            }
                            object.put("rated", rated);
                            object.saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        updateSync("user", true);
                                    }
                                }
                            });
                        }
                    });
                } else if (option.equals("purchases")) {
                    final ParseQuery<ParseObject> purchasesQuery = ParseQuery.getQuery("purchases");
                    purchasesQuery.whereEqualTo("username", User.username);
                    purchasesQuery.orderByDescending("updatedAt");
                    purchasesQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                object = new ParseObject("purchases");
                            }
                            object.put("username", User.username);
                            object.put("pro", User.pro ? 1 : 0);
                            object.put("all_packs", User.allPacks ? 1 : 0);
                            object.put("speed", User.speed);
                            object.put("portal_speed", User.portalSpeed);
                            object.put("frozen_mod", User.frozenModifier);
                            object.put("perfect_bonus_add", User.perfectBonusAdd);
                            object.saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        updateSync("purchases", true);
                                    }
                                }
                            });
                        }
                    });
                } else if (option.equals("score")) {
                    final String overallScoreStr = sql.getSingleResult("score_levels", "sum(score)", " where username = '" + User.username + "' and pack != 'Tutorial' group by username ");
                    final int overallScore = Integer.valueOf(overallScoreStr.equals("") ? "0" : overallScoreStr);
                    final String overallLevelsString = sql.getSingleResult("score_levels", "sum(levels)", " where username = '" + User.username + "' and pack != 'Tutorial' group by username ");
                    final int overallLevels = Integer.valueOf(overallLevelsString.equals("") ? "0" : overallLevelsString);
                    final ParseQuery<ParseObject> scoreQuery = ParseQuery.getQuery("score");
                    scoreQuery.whereEqualTo("username", User.username);
                    scoreQuery.orderByDescending("updatedAt");
                    scoreQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                object = new ParseObject("score");
                            }
                            object.put("username", User.username);
                            object.put("score", overallScore);
                            object.put("levels", overallLevels);
                            object.saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        updateSync("score", true);
                                    }
                                }
                            });
                        }
                    });
                } else if (option.equals("permissions_packs")) {
                    final SQL sql2 = sql;
                    final ParseQuery<ParseObject> packsQuery = ParseQuery.getQuery("permissions_packs");
                    final ArrayList<String> packsList = packs;
                    packsQuery.whereEqualTo("username", User.username);
                    packsQuery.orderByDescending("updatedAt");
                    packsQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (list != null && list.size() > 0) {
                                final int listSize = list.size();
                                for (int x = 0; x < list.size(); x++) {
                                    String sqlResult = sql2.getSingleResult("permissions_packs", "permission", " where username = '" + User.username + "' and pack = '" + list.get(x).getString("pack") + "' ");
                                    if(!sqlResult.equals("")) {
                                        final int fx = x;
                                        list.get(x).put("permission", Integer.valueOf(sqlResult));
                                        list.get(x).saveEventually(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e == null) {
                                                    if(fx == listSize-1){
                                                       updateSync("permissions_packs", true);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            } else {
                                final int packsSize = packsList.size();
                                for (int i = 0; i < packsList.size(); i++) {
                                    final int fx = i;
                                    ParseObject tempObject = new ParseObject("permissions_packs");
                                    tempObject.put("permission", Integer.valueOf(sql2.getSingleResult("permissions_packs", "permission", " where username = '" + User.username + "' and pack = '" + packsList.get(i) + "' ")));
                                    tempObject.put("username", User.username);
                                    tempObject.put("pack", packsList.get(i));
                                    tempObject.saveEventually(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e == null) {
                                                if(fx == packsSize-1){
                                                    updateSync("permissions_packs", true);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else if (option.equals("score_levels")) {
                    final int packsSize = packs.size();
                    for (int i = 0; i < packs.size(); i++) {
                        final int fx = i;
                        final String pack = packs.get(i);
                        final HashMap<Integer, Integer> temp = sql.getLevels("score_levels", " where pack = '" + pack + "' and username = '" + User.username + "' ");

                        final ParseQuery<ParseObject> levelsScoreQuery = ParseQuery.getQuery("score_levels");
                        levelsScoreQuery.whereEqualTo("username", User.username);
                        levelsScoreQuery.whereEqualTo("pack", pack);
                        levelsScoreQuery.orderByDescending("updatedAt");
                        levelsScoreQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e) {
                                if (object == null) {
                                    object = new ParseObject("score_levels");
                                }
                                object.put("username", User.username);
                                object.put("pack", pack);
                                for (int x = 1; x <= 30; x++) {
                                    object.put("level_" + Integer.toString(x), temp.get(x));
                                }
                                object.put("levels", temp.get(31));
                                object.put("score", temp.get(32));
                                object.saveEventually(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            if(fx == packsSize-1) {
                                                updateSync("score_levels", true);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                } else if (option.equals("permissions_levels")) {
                    final int packsSize = packs.size();
                    for (int i = 0; i < packs.size(); i++) {
                        final int fx = i;
                        final String pack = packs.get(i);
                        final HashMap<Integer, Integer> temp = sql.getLevels("permissions_levels", " where pack = '" + pack + "' and username = '" + User.username + "' ");

                        final ParseQuery<ParseObject> levelsStandardQuery = ParseQuery.getQuery("permissions_levels");
                        levelsStandardQuery.whereEqualTo("username", User.username);
                        levelsStandardQuery.whereEqualTo("pack", pack);
                        levelsStandardQuery.orderByDescending("updatedAt");
                        levelsStandardQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e) {
                                if (object == null) {
                                    object = new ParseObject("permissions_levels");
                                }
                                object.put("username", User.username);
                                object.put("pack", pack);
                                for (int x = 1; x <= 30; x++) {
                                    object.put("level_" + Integer.toString(x), temp.get(x));
                                }
                                object.saveEventually(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            if(fx == packsSize-1) {
                                                updateSync("permissions_levels", true);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                } if (option.equals("user_force")) {
                    final String num_hints = sql.getSingleResult("user", "hints", " where username = '" + User.username + "' ");
                    final String no_ads = sql.getSingleResult("user", "no_ads", " where username = '" + User.username + "' ");
                    final String rated = sql.getSingleResult("user", "rated", " where username = '" + User.username + "' ");
                    final String coins = sql.getSingleResult("user", "coins", " where username = '" + User.username + "' ");
                    final String gold = sql.getSingleResult("user", "gold", " where username = '" + User.username + "' ");
                    final ArrayList<String> noAdsArray = new ArrayList<>();
                    noAdsArray.addAll(Arrays.asList(no_ads.split("\\s*,\\s*")));
                    ArrayList<String> noAdsArrayParse = getNoAdsParse(User.username);
                    final ArrayList<String> differences = getDifference(noAdsArrayParse, noAdsArray);
                    final Locale locale = Locale.getDefault();
                    final ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
                    userQuery.whereEqualTo("username", User.username);
                    userQuery.orderByDescending("updatedAt");
                    ParseObject object = null;
                    try {
                        object = userQuery.getFirst();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (object == null) {
                                object = new ParseObject("user");
                            }
                            object.put("username", User.username);
                            object.put("password", User.password);
                            object.put("country", locale.getCountry());
                            object.put("hint", User.hint);
                            object.put("email", User.email);
                            object.put("hints", num_hints.equals("") ? 0 : Integer.valueOf(num_hints));
                            object.put("coins", coins.equals("") ? 0 : Integer.valueOf(coins));
                            object.put("gold", gold.equals("") ? 0 : Integer.valueOf(gold));
                            object.put("perfect_bonus_mod", User.perfectBonusModifier);
                            for (int i = 0; i < differences.size(); i++) {
                                object.add("no_ads", differences.get(i));
                            }
                            object.put("rated", rated);
                    try {
                        object.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    updateSync("user", true);
                } else if (option.equals("purchases_force")) {
                    final ParseQuery<ParseObject> purchasesQuery = ParseQuery.getQuery("purchases");
                    purchasesQuery.whereEqualTo("username", User.username);
                    purchasesQuery.orderByDescending("updatedAt");
                    ParseObject object = null;
                    try {
                        object = purchasesQuery.getFirst();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                            if (object == null) {
                                object = new ParseObject("purchases");
                            }
                            object.put("username", User.username);
                            object.put("pro", User.pro ? 1 : 0);
                            object.put("all_packs", User.allPacks ? 1 : 0);
                            object.put("speed", User.speed);
                            object.put("portal_speed", User.portalSpeed);
                            object.put("frozen_mod", User.frozenModifier);
                            object.put("perfect_bonus_add", User.perfectBonusAdd);
                    try {
                        object.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    updateSync("purchases", true);
                } else if (option.equals("score_force")) {
                    final String overallScoreStr = sql.getSingleResult("score_levels", "sum(score)", " where username = '" + User.username + "' and pack != 'Tutorial' group by username ");
                    final int overallScore = Integer.valueOf(overallScoreStr.equals("") ? "0" : overallScoreStr);
                    final String overallLevelsString = sql.getSingleResult("score_levels", "sum(levels)", " where username = '" + User.username + "' and pack != 'Tutorial' group by username ");
                    final int overallLevels = Integer.valueOf(overallLevelsString.equals("") ? "0" : overallLevelsString);
                    final ParseQuery<ParseObject> scoreQuery = ParseQuery.getQuery("score");
                    scoreQuery.whereEqualTo("username", User.username);
                    scoreQuery.orderByDescending("updatedAt");
                    ParseObject object = null;
                    try {
                        object = scoreQuery.getFirst();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                            if (object == null) {
                                object = new ParseObject("score");
                            }
                            object.put("username", User.username);
                            object.put("score", overallScore);
                            object.put("levels", overallLevels);
                    try {
                        object.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    updateSync("score",true);
                } else if (option.equals("permissions_packs_force")) {
                    final SQL sql2 = sql;
                    final ParseQuery<ParseObject> packsQuery = ParseQuery.getQuery("permissions_packs");
                    final ArrayList<String> packsList = packs;
                    packsQuery.whereEqualTo("username", User.username);
                    packsQuery.orderByDescending("updatedAt");
                    List<ParseObject> list = null;
                    try {
                        list = packsQuery.find();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                            if (list != null && list.size() > 0) {
                                for (int x = 0; x < list.size(); x++) {
                                    String sqlResult = sql2.getSingleResult("permissions_packs", "permission", " where username = '" + User.username + "' and pack = '" + list.get(x).getString("pack") + "' ");
                                    if(!sqlResult.equals("")) {
                                        list.get(x).put("permission", Integer.valueOf(sqlResult));
                                        try {
                                            list.get(x).save();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } else {
                                for (int i = 0; i < packsList.size(); i++) {
                                    ParseObject tempObject = new ParseObject("permissions_packs");
                                    tempObject.put("permission", Integer.valueOf(sql2.getSingleResult("permissions_packs", "permission", " where username = '" + User.username + "' and pack = '" + packsList.get(i) + "' ")));
                                    tempObject.put("username", User.username);
                                    tempObject.put("pack", packsList.get(i));
                                    try {
                                        tempObject.save();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    updateSync("permissions_packs", true);
                } else if (option.equals("score_levels_force")) {
                    for (int i = 0; i < packs.size(); i++) {
                        final String pack = packs.get(i);
                        final HashMap<Integer, Integer> temp = sql.getLevels("score_levels", " where pack = '" + pack + "' and username = '" + User.username + "' ");

                        final ParseQuery<ParseObject> levelsScoreQuery = ParseQuery.getQuery("score_levels");
                        levelsScoreQuery.whereEqualTo("username", User.username);
                        levelsScoreQuery.whereEqualTo("pack", pack);
                        levelsScoreQuery.orderByDescending("updatedAt");
                        ParseObject object = null;
                        try {
                            object = levelsScoreQuery.getFirst();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                                if (object == null) {
                                    object = new ParseObject("score_levels");
                                }
                                object.put("username", User.username);
                                object.put("pack", pack);
                                for (int x = 1; x <= 30; x++) {
                                    object.put("level_" + Integer.toString(x), temp.get(x));
                                }
                                object.put("levels", temp.get(31));
                                object.put("score", temp.get(32));
                        try {
                            object.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    updateSync("score_levels", true);
                } else if (option.equals("permissions_levels_force")) {
                    for (int i = 0; i < packs.size(); i++) {
                        final String pack = packs.get(i);
                        final HashMap<Integer, Integer> temp = sql.getLevels("permissions_levels", " where pack = '" + pack + "' and username = '" + User.username + "' ");

                        final ParseQuery<ParseObject> levelsStandardQuery = ParseQuery.getQuery("permissions_levels");
                        levelsStandardQuery.whereEqualTo("username", User.username);
                        levelsStandardQuery.whereEqualTo("pack", pack);
                        levelsStandardQuery.orderByDescending("updatedAt");
                        ParseObject object = null;
                        try {
                            object = levelsStandardQuery.getFirst();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                                if (object == null) {
                                    object = new ParseObject("permissions_levels");
                                }
                                object.put("username", User.username);
                                object.put("pack", pack);
                                for (int x = 1; x <= 30; x++) {
                                    object.put("level_" + Integer.toString(x), temp.get(x));
                                }
                        try {
                            object.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    updateSync("permissions_levels", true);
                }
            }
        }).start();
    }
    public ArrayList<String> getNoAdsParse(String username) {
        final ArrayList<String> noAds = new ArrayList<>();
        ParseQuery<ParseObject> noAdsQuery = new ParseQuery<ParseObject>("user");
        noAdsQuery.whereEqualTo("username", username);
        noAdsQuery.orderByDescending("updatedAt");
        try {
            noAdsArray = noAdsQuery.getFirst();
        }catch (ParseException pe){}
        if (noAdsArray != null) {
            JSONArray temp = noAdsArray.getJSONArray("no_ads");
            if (temp != null) {
                for (int i=0;i<temp.length();i++){
                    try {
                        noAds.add(temp.get(i).toString());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        noAds.add(User.username);
        return noAds;
    }

    public ArrayList<String> getDifference(ArrayList<String> parseList, ArrayList<String> localList){
        ArrayList<String> difference = new ArrayList<>();
        for(int i = 0; i < localList.size(); i++){
            if(!parseList.contains(localList.get(i)) && !difference.contains(localList.get(i))){
                difference.add(localList.get(i));
            }
        }
        return difference;
    }
    public static void updateSync(String table, boolean syncedBoolean){
        switch (table){
            case "user":
                syncUser = syncedBoolean;
                if(syncUser && syncPurchases && syncScore && syncScore && syncScoreLevels && syncPermissionsPacks && syncPermissionsLevels){
                    synced = true;
                }else{
                    synced = false;
                }
                break;
            case "purchases":
                syncPurchases = syncedBoolean;
                if(syncUser && syncPurchases && syncScore && syncScore && syncScoreLevels && syncPermissionsPacks && syncPermissionsLevels){
                    synced = true;
                }else{
                    synced = false;
                }
                break;
            case "score":
                syncScore = syncedBoolean;
                if(syncUser && syncPurchases && syncScore && syncScore && syncScoreLevels && syncPermissionsPacks && syncPermissionsLevels){
                    synced = true;
                }else{
                    synced = false;
                }
                break;
            case "score_levels":
                syncScoreLevels = syncedBoolean;
                if(syncUser && syncPurchases && syncScore && syncScore && syncScoreLevels && syncPermissionsPacks && syncPermissionsLevels){
                    synced = true;
                }else{
                    synced = false;
                }
                break;
            case "permissions_packs":
                syncPermissionsPacks = syncedBoolean;
                if(syncUser && syncPurchases && syncScore && syncScore && syncScoreLevels && syncPermissionsPacks && syncPermissionsLevels){
                    synced = true;
                }else{
                    synced = false;
                }
                break;
            case "permissions_levels":
                syncPermissionsLevels = syncedBoolean;
                if(syncUser && syncPurchases && syncScore && syncScore && syncScoreLevels && syncPermissionsPacks && syncPermissionsLevels){
                    synced = true;
                }else{
                    synced = false;
                }
                break;
            case "All":
                if(syncUser && syncPurchases && syncScore && syncScore && syncScoreLevels && syncPermissionsPacks && syncPermissionsLevels){
                    synced = true;
                }else{
                    synced = false;
                }
                break;
        }
    }
}