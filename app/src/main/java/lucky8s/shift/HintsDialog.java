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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christian on 4/5/2015.
 */
public class HintsDialog extends DialogFragment implements View.OnClickListener, DialogListener{

    ConnectivityManager cm;
    NetworkInfo netInfo;
    AlertDialog alertDialog;
    CoinsDialog coinsDialog = new CoinsDialog(this);
    DialogListener listener;
    SQL sql;

    Button purchase5;
    Button purchase20;
    Button purchaseInfinite;
    Button toStore;
    Button back;

    View arrow1;
    View arrow2;
    View arrow3;


    String PURCHASE_CODE = "";
    @SuppressLint("ValidFragment")
    public HintsDialog(DialogListener activityDialogListener) {
        this.listener = activityDialogListener;
    }
    public HintsDialog() {
        // Empty constructor required for DialogFragment
    }
    public void onCloseDialog() {}
    public void showCoinsStore(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buy_hints, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        sql = new SQL(getDialog().getContext());
        User.add("In HintsDialog");

        purchase5 = (Button) view.findViewById(R.id.buy_hints_five);
        purchase20 = (Button) view.findViewById(R.id.buy_hints_twenty);
        purchaseInfinite = (Button) view.findViewById(R.id.buy_hints_infinite);
        toStore = (Button) view.findViewById(R.id.to_store);
        back = (Button) view.findViewById(R.id.back);

        arrow1 = view.findViewById(R.id.arrow1);
        arrow2 = view.findViewById(R.id.arrow2);
        arrow3 = view.findViewById(R.id.arrow3);

        cm = (ConnectivityManager) getDialog().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        purchase5.setOnClickListener(this);
        purchase20.setOnClickListener(this);
        purchaseInfinite.setOnClickListener(this);
        toStore.setOnClickListener(this);
        back.setOnClickListener(this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getDialog().getContext());
        alertDialogBuilder.setCancelable(false)
                .setTitle(getResources().getString(R.string.not_enough_coins))
                .setMessage(getResources().getString(R.string.get_more_coins))
                .setPositiveButton(getDialog().getContext().getResources().getString(R.string.buy_coins),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                User.add("Going to CoinsDialog from HintsDialog");
                                listener.showCoinsStore();
                            }
                        })
                .setNegativeButton(getDialog().getContext().getResources().getString(R.string.okay),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        alertDialog = alertDialogBuilder.create();

        return view;
    }
    public void purchaseHints(String code){
            int hints;
            int currentHints;
            boolean enoughCoins=false;

            hints = (code.equals("five") ? 5 : (code.equals("twenty") ? 20 : (code.equals("infinite") ? -1 : 0)));
            currentHints = Integer.valueOf(sql.getSingleResult("user", "hints", " where username = '"+User.getUser()+"' "));
            int cost = 0;
            String costType = "";
            switch (hints) {
                case 5:
                    if (sql.useCoins(Integer.valueOf(getString(R.string.five_hints_cost)), "silver")) {
                        sql.update("user", " where username = '" + User.getUser() + "' ", " set hints = " + Integer.toString(hints + currentHints) + " ");
                        cost = Integer.valueOf(getString(R.string.five_hints_cost));
                        costType = "silver";
                        enoughCoins = true;
                    }
                    break;
                case 20:
                    if (sql.useCoins(Integer.valueOf(getString(R.string.twenty_hints_cost)), "silver")) {
                        sql.update("user", " where username = '" + User.getUser() + "' ", " set hints = " + Integer.toString(hints + currentHints) + " ");
                        cost = Integer.valueOf(getString(R.string.twenty_hints_cost));
                        costType = "silver";
                        enoughCoins = true;
                    }
                    break;
                case -1:
                    if (sql.useCoins(Integer.valueOf(getString(R.string.infinite_hints_cost)), "gold")) {
                        sql.update("user", " where username = '" + User.getUser() + "' ", " set hints = " + Integer.toString(-1) + " ");
                        cost = Integer.valueOf(getString(R.string.infinite_hints_cost));
                        costType = "gold";
                        enoughCoins = true;
                    }
                    break;
            }
        if(getDialog()!= null && enoughCoins) {
            User.add("Purchased hints "+ Integer.toString(hints));
            Map<String, Object> eventValue = new HashMap<String, Object>();
            eventValue.put(AFInAppEventParameterName.PRICE,cost);
            eventValue.put(AFInAppEventParameterName.CONTENT_TYPE,code+"_hints");
            eventValue.put(AFInAppEventParameterName.CURRENCY, costType);
            AppsFlyerLib.trackEvent(getDialog().getOwnerActivity().getApplicationContext(), "SPENDING", eventValue);
        }
            if(!User.username.equals("")) {
                ParseService.updateSync("user", false);
                Intent userIntent = new Intent(getDialog().getOwnerActivity(), ParseService.class);
                userIntent.putExtra(ParseService.Object, "user");
                getDialog().getOwnerActivity().startService(userIntent);
            }
            if(!enoughCoins){
                User.add("Tried to Purchase hints, but not enough gold!");
                if(alertDialog != null){
                    User.add("alertDialog was null. Recreating and proceeding");
                    recreateCoinAlert();
                }
                alertDialog.show();
                if(alertDialog.isShowing()) {
                    int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                    View divider = alertDialog.findViewById(dividerId);
                    divider.setBackgroundColor(getResources().getColor(R.color.gold));
                }
            }else {
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
    public void onClick(View v) {
        if(v instanceof Button){
            buttonClick(v);
        }
        switch (v.getId()) {
            case R.id.buy_hints_five:
                PURCHASE_CODE = "five";
                break;
            case R.id.buy_hints_twenty:
                PURCHASE_CODE = "twenty";
                break;
            case R.id.buy_hints_infinite:
                PURCHASE_CODE = "infinite";
                break;
            case R.id.to_store:
                Intent a = new Intent(getDialog().getOwnerActivity(), Store.class);
                User.add("Going to Store from HintsDialog");
                startActivity(a);
                getDialog().dismiss();
                getDialog().getOwnerActivity().finish();
                break;
            case R.id.back:
                getDialog().dismiss();
                break;
            default:
                PURCHASE_CODE = "";
                break;
        }
        v.getBackground().clearColorFilter();
        if(!PURCHASE_CODE.equals("") && (v.getId() != R.id.to_store || v.getId() != R.id.back)) {
            purchaseHints(PURCHASE_CODE);
        }else if(PURCHASE_CODE.equals("") || (v.getId() == R.id.to_store) || (v.getId() == R.id.back)) {
        }
    }
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onCloseDialog();
    }
    public void onDestroy(){
        super.onDestroy();

        System.gc();

    }
    private void recreateCoinAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getDialog().getOwnerActivity());
        alertDialogBuilder.setCancelable(false)
                .setTitle(getResources().getString(R.string.not_enough_coins))
                .setMessage(getResources().getString(R.string.get_more_coins))
                .setPositiveButton(getDialog().getContext().getResources().getString(R.string.buy_coins),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                User.add("Going to CoinsDialog from HintsDialog");
                                listener.showCoinsStore();
                            }
                        })
                .setNegativeButton(getDialog().getContext().getResources().getString(R.string.okay),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        alertDialog = alertDialogBuilder.create();
    }
}
