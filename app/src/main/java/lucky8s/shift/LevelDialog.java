package lucky8s.shift;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.TextView;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christian on 4/5/2015.
 */
public class LevelDialog extends DialogFragment implements View.OnClickListener,DialogListener{

    SQL sql;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    DialogListener listener;
    AlertDialog alertDialog;

    Button unlock;
    Button unlockNoAds;
    Button toStore;
    Button back;

    TextView message;

    ParseObject noAdsArray;

    String PURCHASE_CODE = "";
    String pack = "";

    @SuppressLint("ValidFragment")
    public LevelDialog(DialogListener activityDialogListener) {
        this.listener = activityDialogListener;
    }
    public LevelDialog(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unlock_pack, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        User.add("In LevelDialog");
        sql = new SQL(getDialog().getContext());

        pack = getDialog().getContext().getSharedPreferences("LEVELS", Context.MODE_PRIVATE).getString("PACK", "Stout Temple");

        unlock = (Button) view.findViewById(R.id.unlock_pack);
        unlockNoAds = (Button) view.findViewById(R.id.unlock_pack_no_ads);
        toStore = (Button) view.findViewById(R.id.to_store);
        back = (Button) view.findViewById(R.id.back);

        message = (TextView) view.findViewById(R.id.message);
        message.setText(getDialog().getContext().getSharedPreferences("Unlock", Context.MODE_PRIVATE).getString("Message", "Stout Temple"));

        cm = (ConnectivityManager) getDialog().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        unlock.setOnClickListener(this);
        unlockNoAds.setOnClickListener(this);
        toStore.setOnClickListener(this);
        back.setOnClickListener(this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getDialog().getContext());
        alertDialogBuilder.setCancelable(false).setTitle(getDialog().getContext().getResources().getString(R.string.not_enough_coins)).setMessage(getDialog().getContext().getResources().getString(R.string.get_more_coins)).
                setPositiveButton(getDialog().getContext().getResources().getString(R.string.buy_coins), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.showCoinsStore();
                    }
                }).
                setNegativeButton(getDialog().getContext().getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        return view;
    }
    public void onCloseDialog() {}
    public void showCoinsStore() {}
    public void unlockPacks(String PURCHASE_CODE){
        User.add("Unlocking pack "+PURCHASE_CODE);
        boolean enoughCoins = false;
        int cost = 0;
        String costType = "";
            switch (PURCHASE_CODE){
                case "unlock":
                    if(sql.useCoins(Integer.valueOf(getResources().getString(R.string.unlock_cost)), "gold")) {
                        sql.update("permissions_packs", " where username = '" + User.getUser() + "' and pack = '" + pack + "' ", " set permission = 1 ");
                        sql.update("permissions_levels", " where username = '" + User.getUser() + "' and pack = '" + pack + "' ", " set level_1 = 1 ");
                        cost = Integer.valueOf(getString(R.string.unlock_cost));
                        costType = "gold";
                        enoughCoins = true;
                    }
                    break;
                case "unlock_no_ads":
                    if(sql.useCoins(Integer.valueOf(getResources().getString(R.string.unlock_remove_ads_cost)),"gold")) {
                        sql.update("permissions_packs", " where username = '" + User.getUser() + "' and pack = '" + pack + "' ", " set permission = 1 ");
                        sql.update("permissions_levels", " where username = '" + User.getUser() + "' and pack = '" + pack + "' ", " set level_1 = 1 ");
                        sql.update("user", " where username = '" + User.getUser() + "' ", " set no_ads = '" + getNoAdsString(User.username) + "," + pack + "' ");
                        cost = Integer.valueOf(getString(R.string.unlock_remove_ads_cost));
                        costType = "gold";
                        enoughCoins = true;
                    }
                    break;
                default:
            }
        Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.PRICE,cost);
        eventValue.put(AFInAppEventParameterName.CONTENT_TYPE,PURCHASE_CODE);
        eventValue.put(AFInAppEventParameterName.CURRENCY, costType);
        AppsFlyerLib.trackEvent(getDialog().getOwnerActivity().getApplicationContext(), "SPENDING", eventValue);
            if(!User.username.equals("")) {
                ParseService.updateSync("permissions_packs", false);
                Intent permissionsIntent = new Intent(getDialog().getContext(), ParseService.class);
                permissionsIntent.putExtra(ParseService.Object, "permissions_packs");
                getDialog().getContext().startService(permissionsIntent);
                ParseService.updateSync("permissions_levels", false);
                Intent levelsIntent = new Intent(getDialog().getContext(), ParseService.class);
                levelsIntent.putExtra(ParseService.Object, "permissions_levels");
                getDialog().getContext().startService(levelsIntent);
                ParseService.updateSync("user", false);
                Intent userIntent = new Intent(getDialog().getContext(), ParseService.class);
                userIntent.putExtra(ParseService.Object, "user");
                getDialog().getContext().startService(userIntent);
            }
            if(!enoughCoins){
                alertDialog.show();
                if(alertDialog.isShowing()) {
                    int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                    View divider = alertDialog.findViewById(dividerId);
                    divider.setBackgroundColor(getResources().getColor(R.color.gold));
                }
            }else {
                listener.onCloseDialog();
                getDialog().dismiss();
            }
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
        getDialog().getWindow().setLayout((width / 10) * 9, (height / 10) * 8);
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
        if(v instanceof Button){
            buttonClick(v);
        }
        switch (v.getId()) {
            case R.id.unlock_pack:
                PURCHASE_CODE = "unlock";
                break;
            case R.id.unlock_pack_no_ads:
                PURCHASE_CODE = "unlock_no_ads";
                break;
            case R.id.to_store:
                Intent a = new Intent(getDialog().getOwnerActivity(), Store.class);
                User.add("Going to Store from LevelDialog");
                startActivity(a);
                getDialog().dismiss();
                getDialog().getOwnerActivity().finish();
                break;
            case R.id.back:
                onBackPressed();
            default:
                PURCHASE_CODE = "";
                break;
        }
        v.getBackground().clearColorFilter();
        if(!PURCHASE_CODE.equals("") && (v.getId() != R.id.to_store)){
            unlockPacks(PURCHASE_CODE);
        }else if(PURCHASE_CODE.equals("")) {}
    }
    public void onBackPressed(){
        User.add("LevelDialog Back Pressed");
        this.dismiss();
    }
    public String getNoAdsString(String username) {
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
        String noAdsString = "";
        for(int i = 0; i < noAds.size() ;i++){
            noAdsString += noAds.get(i)+ (i == (noAds.size()-1) ? "" : ",");
        }
        return noAdsString;
    }
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
    public void onDestroy(){
        super.onDestroy();

        System.gc();

    }
}
