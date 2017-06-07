package lucky8s.shift;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import static lucky8s.shift.User.add;

/**
 * Created by Christian on 5/19/2015.
 */
public class Login extends FragmentActivity {

    SQL sql;
    Context context;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    ProgressDialog progress;
    ForgotDialog forgotDialog = new ForgotDialog();
    SoundDriver sd;

    EditText username;
    EditText password;

    Button login;
    Button hint;
    Button previous;

    TextView signUp;
    TextView newHere;
    CheckBox rememberMe;

    ParseObject user;
    ParseObject permissions_packs;
    ParseObject permissions_levels;
    ParseObject score_levels;
    ParseObject purchases;

    boolean leaving;
    String generatedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.add("In Login");
        setContentView(R.layout.login);

        context = this;
        sd = MyApplication.getInstance().getSD();
        sql = new SQL(context);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        password.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(onClickListener);
        signUp = (TextView) findViewById(R.id.sign_up);
        signUp.setOnClickListener(onClickListener);
        newHere = (TextView) findViewById(R.id.new_here);
        newHere.setOnClickListener(onClickListener);
        hint = (Button) findViewById(R.id.hint);
        hint.setOnClickListener(onClickListener);
        previous = (Button) findViewById(R.id.previous);
        previous.setOnClickListener(onClickListener);

        rememberMe = (CheckBox) this.findViewById(R.id.remember_me);
        rememberMe.setChecked(getSharedPreferences("USER", MODE_PRIVATE)
                .getBoolean("remember_me", false));
        if(rememberMe.isChecked()){
            username.setText(getSharedPreferences("USER", MODE_PRIVATE).getString("remember_me_username", ""));
        }
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getSharedPreferences("USER", MODE_PRIVATE).edit().putBoolean("remember_me", b).apply();
            }
        });



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
            switch (view.getId()) {
                case R.id.login:
                    User.add("Clicked on Login");
                    sd.buttonPress();
                    verifyLogin();
                    break;
                case R.id.previous:
                    onBackPressed();
                    break;
                case R.id.new_here:
                case R.id.sign_up:
                    User.add("Clicked on SignUp");
                    sd.buttonPress();
                    Intent a = new Intent(Login.this, SignUp.class);
                    leaving = true;
                    User.add("Going to SignUp");
                    startActivity(a);
                    Login.this.finish();
                    break;
                case R.id.hint:
                    User.add("Clicked on Hint");
                    sd.buttonPress();
                    cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    netInfo = cm.getActiveNetworkInfo();
                    if(username.getText().toString().equals("")){
                        Toast.makeText(context, getResources().getString(R.string.username_blank), Toast.LENGTH_SHORT).show();
                    }else {
                        if (netInfo != null) {
                            ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
                            userQuery.whereEqualTo("username", username.getText().toString().trim());
                            userQuery.orderByDescending("updatedAt");
                            userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    user = object;
                                    if (user == null) {
                                        User.hint = username.getText().toString() + getResources().getString(R.string.not_registered);
                                        try {
                                            new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.hint)).setMessage(getResources().getString(R.string.hint_not_available)).setPositiveButton(context.getResources().getString(R.string.ok), null).
                                                    show();
                                        }catch (WindowManager.BadTokenException bt){
                                            User.add(Log.getStackTraceString(bt));
                                        }
                                    } else {
                                        TextView message = new TextView(context);
                                        message.setText("\n"+(user.containsKey("hint") ? user.get("hint").toString() : context.getResources().getString(R.string.hint_not_available)));
                                        message.setGravity(Gravity.CENTER);
                                        AlertDialog.Builder alertDialogBuider = new AlertDialog.Builder(context);
                                        alertDialogBuider.setView(message).setPositiveButton(context.getResources().getString(R.string.ok), null).
                                                setNegativeButton(context.getResources().getString(R.string.forgot_password), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(final DialogInterface dialogInterface, int i) {
                                                        netInfo = cm.getActiveNetworkInfo();
                                                        if (netInfo != null) {
                                                            getSharedPreferences("FORGOT", MODE_PRIVATE).edit().putString("username", user.getString("username")).apply();
                                                            getSharedPreferences("FORGOT", MODE_PRIVATE).edit().putString("email", user.getString("email")).apply();
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
                                                            Fragment dialog = getFragmentManager().findFragmentByTag("forgot_dialog");
                                                            if (dialog == null) {
                                                                if (!forgotDialog.isAdded()) {
                                                                    add("Showing ForgotDialog");
                                                                    forgotDialog.setCancelable(true);
                                                                    forgotDialog.show(fm, "forgot_dialog");
                                                                }
                                                            } else if (dialog.getActivity() != Login.this) {
                                                            } else {
                                                            }
                                                        } else {
                                                            Toast.makeText(context, getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        AlertDialog alertDialog = alertDialogBuider.create();
                                        alertDialog.show();
                                        if(alertDialog.isShowing()) {
                                            int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                                            View divider = alertDialog.findViewById(dividerId);
                                            divider.setBackgroundColor(getResources().getColor(R.color.gold));
                                        }
                                    }
                                }
                            });
                            break;
                        } else {
                            Toast.makeText(context, getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        }
    };
    public void verifyLogin(){
        User.add("Verifying Login");
        String userName = username.getText().toString();
        String userPw = password.getText().toString();

        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if(netInfo == null){
            Toast.makeText(this, getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
            password.setText("");
        }else if(userName.equals("")){
            Toast.makeText(this, getResources().getString(R.string.username_blank), Toast.LENGTH_SHORT).show();
        }else if(userPw.equals("")){
            Toast.makeText(this, getResources().getString(R.string.password_blank), Toast.LENGTH_SHORT).show();
        }else {
            ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
            userQuery.whereEqualTo("username", userName);
            userQuery.orderByDescending("updatedAt");
            userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    user = object;

                    if(user == null || Config.BLACKLIST.contains(user.get("username"))){
                        Toast.makeText(context, getResources().getString(R.string.username_not_registered), Toast.LENGTH_SHORT).show();
                    }else if (user.get("password").equals((password.getText().toString().trim()))) {

                        User.email = user.get("email").toString();
                        User.username = user.get("username").toString();
                        User.password = user.get("password").toString();
                        User.hint = user.get("hint").toString();
                        User.country = user.get("country").toString();
                        User.perfectBonusModifier = (Double) user.get("perfect_bonus_mod");

                        Calendar cal = Calendar.getInstance();

                        getSharedPreferences("Login", MODE_PRIVATE).edit().putLong("Time", cal.getTimeInMillis()).apply();
                        getSharedPreferences("Login", MODE_PRIVATE).edit().putBoolean("LoggedIn", true).apply();
                        getSharedPreferences("Login", MODE_PRIVATE).edit().putString("Username", User.username).apply();

                        JSONArray temp = user.getJSONArray("no_ads");
                        String no_ads = "";
                        if(temp != null){
                            for (int i=0;i<temp.length();i++){
                                try {
                                    no_ads += temp.get(i);
                                    no_ads += (i == temp.length()-1 ? "" : " , ");
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        if(sql.checkEntry("user", " where username = '"+ user.get("username").toString() +"' ")){
                            sql.update("user", " where username = '"+user.get("username").toString()+"' ", " set password = '"+user.get("password").toString()+"', hint = '"+user.get("hint").toString()+"', country = '"+User.country+"', hints = "+Integer.toString(user.getInt("hints"))+", no_ads = '"+no_ads+"', rated = '"+user.getString("rated")+"', coins = "+Integer.toString(user.getInt("coins"))+", gold = "+Integer.toString(user.getInt("gold"))+", perfect_bonus_mod = '"+String.format("%.3f",(Double)user.get("perfect_bonus_mod"))+"' ");
                        }else {
                            sql.insert("user", "'" + User.email + "','" + User.username + "','" + User.password + "','" + User.hint + "','"+User.country+"', "+Integer.toString(user.getInt("hints"))+",'"+no_ads+"' , '"+user.getString("rated")+"',"+Integer.toString(user.getInt("coins"))+","+Integer.toString(user.getInt("gold"))+",'"+String.format("%.3f",(Double)user.get("perfect_bonus_mod"))+"' ");
                        }

                        if(rememberMe.isChecked()){
                            getSharedPreferences("USER", MODE_PRIVATE)
                                    .edit()
                                    .putString("remember_me_username", User.username)
                                    .apply();
                            getSharedPreferences("USER", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("remember_me", true)
                                    .apply();
                        }else{
                            getSharedPreferences("USER", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("remember_me", false)
                                    .apply();
                        }
                        progress = new ProgressDialog(context, R.style.MyTheme);
                        progress.setIndeterminate(true);
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setCancelable(false);
                        progress.show();
                        new Thread() {
                            public void run(){
                                createTables();
                                updateTables();
                                progress.dismiss();
                            }
                        }.start();
                        User.add("Logged into user "+User.username);
                        Intent a = new Intent(Login.this, Home.class);
                        leaving = true;
                        startActivity(a);
                        Login.this.finish();
                    }else{
                        User.email = "";
                        User.username = "";
                        User.password = "";
                        Toast.makeText(context, getResources().getString(R.string.password_not_correct), Toast.LENGTH_SHORT).show();
                        password.setText("");
                    }
                }
            });
        }
    }
    public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;

            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }

            public char charAt(int index) {
                return '*'; // This is the important part
            }

            public int length() {
                return mSource.length(); // Return default
            }

            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    }
    public void createTables(){
        HashMap<String, Integer> levelPacks = new HashMap<>();
        System.out.println("creating tables from login");
        ArrayList<String> fPaks = new ArrayList<>();
        fPaks.add("Brittle Temple");
        fPaks.add("Flame Temple");
        fPaks.add("Chilled Temple");
        fPaks.add("Tidal Temple");
        fPaks.add("Spirit Temple");
        fPaks.add("Earth Temple");
        fPaks.add("Light Temple");
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

        if(!sql.checkEntry("purchases", " where username = '"+User.username+"' ")){
            sql.insert("purchases","username, speed,portal_speed, pro, all_packs, perfect_bonus_add, frozen_mod ","'"+User.getUser()+"', '1.0','1.0', 0, 0, '0', '1.0' ");
        }
        for(String key : levelPacks.keySet()){
            if(!sql.checkEntry("permissions_packs", " where username = '"+User.username+"' and pack = '"+key+"' ")){
                sql.insert("permissions_packs", " '" + User.username + "', '"+key+"'," +
                                levelPacks.get(key)
                );
            }else{
                //if the user HAS the pack entry in the db, but its locked UNLOCK IT. but only if it is one of the free packs
                if(fPaks.contains(key) && (sql.getSingleResult("permissions_packs", "permission", " where username = '" + User.getUser() + "' and pack = '" + key + "' ")+"").equals("0")) {//if its already unlocked who cares.
                    sql.update("permissions_packs", " where username ='" + User.getUser() + "' and pack='" + key + "' ", "set permission = " + 1);
                }
            }
            if(!sql.checkEntry("permissions_levels", " where username = '"+User.username+"' and pack = '"+key+"' ")){
                sql.insert("permissions_levels", "'" + User.username + "'," +
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
            if(!sql.checkEntry("score_levels", " where username = '"+User.username+"' and pack = '"+key+"' ")) {
                sql.insert("score_levels", "'" + User.username + "'," +
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

    public String forceDecimal(String str, String failsafe){
        if(str == null || str.equals("nul")){
            return failsafe;//failsafe so no null pointers
        }
        if(str.contains(",")){
            str = str.replace(",", ".");
        }
        return str;

    }

    public void updateTables(){
        ArrayList<String> packs = sql.getPacks(User.username);

        final ParseQuery<ParseObject> purchasesQuery = ParseQuery.getQuery("purchases");
        purchasesQuery.whereEqualTo("username", User.username);
        purchasesQuery.orderByDescending("updatedAt");
        purchasesQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                purchases = object;
                if (purchases != null) {
                    sql.update("purchases", " where username ='" + User.username + "' ", "set all_packs = '" + (purchases.get("all_packs") == null ? "" : purchases.get("all_packs").toString()) + "' , pro = " + (purchases.get("pro") == null ? "" : purchases.get("pro").toString()) + ", speed = '" + (purchases.get("speed") == null ? "" : purchases.get("speed").toString()) + "', portal_speed = '" + (purchases.get("portal_speed") == null ? "" : purchases.get("portal_speed").toString()) + "' ");
                    User.pro = ((purchases.get("pro") == null) ? false : (purchases.get("pro").equals("1")));
                    User.allPacks = ((purchases.get("all_packs") == null) ? false : (purchases.get("all_packs").equals("1")));
                    User.speed = purchases.get("speed") == null ? 1.0 : Double.valueOf(forceDecimal(""+purchases.get("speed"), "1.0"));
                    User.portalSpeed = purchases.get("portal_speed") == null ? 1.0 : Double.valueOf(forceDecimal(""+purchases.get("portal_speed"), "1.0"));
                    User.frozenModifier = purchases.get("frozen_mod") == null ? 1.0 : Double.valueOf(forceDecimal(""+purchases.get("frozen_mod"), "1.0"));
                    User.perfectBonusAdd = purchases.get("perfect_bonus_add") == null ? 0.0 : Double.valueOf(forceDecimal(""+purchases.get("perfect_bonus_add"), "0.0"));
                }
            }
        });

        for(int i = 0; i < packs.size(); i++) {
            final ParseQuery<ParseObject> packsStandardQuery = ParseQuery.getQuery("permissions_packs");
            packsStandardQuery.whereEqualTo("username", User.username);
            packsStandardQuery.whereEqualTo("pack", packs.get(i));
            packsStandardQuery.orderByDescending("updatedAt");
            packsStandardQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    permissions_packs = object;
                    if (permissions_packs != null) {
                        sql.update("permissions_packs", " where username ='" + User.username + "' and pack='" + permissions_packs.get("pack") + "' ", "set permission = " + permissions_packs.get("permission"));
                    }
                }
            });
            final ParseQuery<ParseObject> levelsStandardQuery = ParseQuery.getQuery("permissions_levels");
            levelsStandardQuery.whereEqualTo("username", User.username);
            levelsStandardQuery.whereEqualTo("pack", packs.get(i));
            levelsStandardQuery.orderByDescending("updatedAt");
            levelsStandardQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    permissions_levels = object;
                    if (permissions_levels != null) {
                        for(int x = 1; x<=30;x++) {
                            sql.update("permissions_levels", " where username ='" + User.username + "' and pack='" + permissions_levels.get("pack") + "' ", "set level_"+Integer.toString(x)+" = " + permissions_levels.get("level_"+Integer.toString(x)));
                        }
                    }
                }
            });
            final ParseQuery<ParseObject> levelsStandardScoreQuery = ParseQuery.getQuery("score_levels");
            levelsStandardScoreQuery.whereEqualTo("username", User.username);
            levelsStandardScoreQuery.whereEqualTo("pack", packs.get(i));
            levelsStandardScoreQuery.orderByDescending("updatedAt");
            levelsStandardScoreQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    score_levels = object;
                    if (score_levels != null) {
                        for(int x = 1; x<=(score_levels.get("pack").equals("Tutorial") ? 5 :30);x++) {
                            sql.update("score_levels", " where username ='" + User.username + "' and pack='" + score_levels.get("pack") + "' ", "set level_"+Integer.toString(x)+" = " + score_levels.get("level_"+Integer.toString(x)));
                        }
                        sql.update("score_levels", " where username ='" + User.username + "' and pack='" + score_levels.get("pack") + "' ", "set levels = " + score_levels.get("levels"));
                        sql.update("score_levels", " where username ='" + User.username + "' and pack='" + score_levels.get("pack") + "' ", "set levels = " + score_levels.get("score"));
                    }
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
                 sd.backPress();
        leaving = true;
        Intent a = new Intent(Login.this, Home.class);
        startActivity(a);
        Login.this.finish();
    }
    private class SendEmailASyncTask extends AsyncTask<Void, Void, Void> {

        private Context context;
        private String msgResponse;

        private SendEmailASyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                SendGrid sendgrid = new SendGrid(Config.SENDGRID_USERNAME, Config.SENDGRID_PASSWORD);

                SendGrid.Email email = new SendGrid.Email();

                // Get values from edit text to compose email
                // TODO: Validate edit texts

                email.addTo(getSharedPreferences("email", MODE_PRIVATE).getString("recipient", "noone@noone.com"));
                email.setFrom(Config.SENDGRID_EMAIL);
                email.setFromName(Config.SENDGRID_NAME);
                email.setHtml(getSharedPreferences("email", MODE_PRIVATE).getString("code", ""));
                email.setSubject(getSharedPreferences("email", MODE_PRIVATE).getString("subject", "Shift"));
                email.setTemplateId(Config.RESET_PASSWORD);
                //email.setText(getSharedPreferences("email", MODE_PRIVATE).getString("code", ""));
                email.replaceTag(":user", getSharedPreferences("email", MODE_PRIVATE).getString("user", ""));
                email.replaceTag(":tag1", getString(R.string.forgot_your_password));
                email.replaceTag(":tag2", getString(R.string.no_problem));
                email.replaceTag(":tag3", getString(R.string.user_semi));
                email.replaceTag(":tag4", getString(R.string.code_semi));
                email.addCategory("Password");

                // Send email, execute http request
                SendGrid.Response response = sendgrid.send(email);
                msgResponse = response.getMessage();

                Log.i("SendAppExample", msgResponse);

            } catch (SendGridException e) {
                Log.e("SendAppExample", e.toString());
            } catch (JSONException e) {
                Log.e("SendAppExample", e.toString());
            }

            return null;
        }
    }
    public void sendCode(String username, String email){
        generatedCode = generateCode();
        getSharedPreferences("email", MODE_PRIVATE).edit().putString("recipient", email).apply();
        getSharedPreferences("email", MODE_PRIVATE).edit().putString("subject", "Password Reset Code").apply();
        getSharedPreferences("email", MODE_PRIVATE).edit().putString("code", generatedCode).apply();
        getSharedPreferences("email", MODE_PRIVATE).edit().putString("user", username).apply();
        SendEmailASyncTask task = new SendEmailASyncTask(getApplicationContext());
        task.execute();
    }
    public String generateCode(){
        String genCode = "";
        Random r = new Random();
        int r1 = r.nextInt(52);
        int r2 = r.nextInt(10);
        int r3 = r.nextInt(52);
        int r4 = r.nextInt(10);
        int r5 = r.nextInt(52);
        int r6 = r.nextInt(10);
        int r7 = r.nextInt(52);
        int r8 = r.nextInt(10);

        genCode = mapInt(r1)+
                Integer.toString(r2)+
                mapInt(r3)+
                Integer.toString(r4)+
                mapInt(r5)+
                Integer.toString(r6)+
                mapInt(r7)+
                Integer.toString(r8);

        return genCode;
    }
    public String mapInt(int rand){
        String result = "";
        switch (rand){
            case 0:
                return "a";
            case 1:
                return "A";
            case 2:
                return "b";
            case 3:
                return "B";
            case 4:
                return "c";
            case 5:
                return "C";
            case 6:
                return "d";
            case 7:
                return "D";
            case 8:
                return "e";
            case 9:
                return "E";
            case 10:
                return "f";
            case 11:
                return "F";
            case 12:
                return "g";
            case 13:
                return "G";
            case 14:
                return "h";
            case 15:
                return "H";
            case 16:
                return "i";
            case 17:
                return "I";
            case 18:
                return "j";
            case 19:
                return "J";
            case 20:
                return "k";
            case 21:
                return "K";
            case 22:
                return "l";
            case 23:
                return "L";
            case 24:
                return "m";
            case 25:
                return "M";
            case 26:
                return "n";
            case 27:
                return "N";
            case 28:
                return "o";
            case 29:
                return "O";
            case 30:
                return "p";
            case 31:
                return "P";
            case 32:
                return "q";
            case 33:
                return "Q";
            case 34:
                return "r";
            case 35:
                return "R";
            case 36:
                return "s";
            case 37:
                return "S";
            case 38:
                return "t";
            case 39:
                return "T";
            case 40:
                return "u";
            case 41:
                return "U";
            case 42:
                return "v";
            case 43:
                return "V";
            case 44:
                return "w";
            case 45:
                return "W";
            case 46:
                return "x";
            case 47:
                return "X";
            case 48:
                return "y";
            case 49:
                return "Y";
            case 50:
                return "z";
            case 51:
                return "Z";
        }
        if(result.equals("")){
            result="q";
        }
        return result;
    }
    public void onPause(){
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
        if(!leaving){
            sd.stop();
        }
    }
    public void onResume(){
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
        if(!sd.isPlaying("homeBackground")) {
            sd.stop();
            sd.homeBackground();
        }
        leaving = false;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        System.gc();
    }
}
