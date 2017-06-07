package lucky8s.shift;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import java.util.Locale;

/**
 * Created by Christian on 5/19/2015.
 */
public class SignUp extends Activity {

    Context context;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    SoundDriver sd;
    SQL sql;
    Locale locale;
    ProgressDialog progress;
    AlertDialog alertDialog;

    CheckBox transferGuest;

    TextView emailError;
    TextView usernameError;
    TextView passwordError;
    TextView confirmError;
    TextView hintError;
    TextView refError;

    EditText email;
    EditText username;
    EditText password;
    EditText confirmPassword;
    EditText hint;
    EditText ref;

    Button signUp;
    Button previous;

    ParseObject user;

    boolean leaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        User.add("In SignUp");

        context = this;
        sql = new SQL(this);
        sd = MyApplication.getInstance().getSD();
        locale = Locale.getDefault();

        email = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        password.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        confirmPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        hint = (EditText) findViewById(R.id.hint);
        ref = (EditText) findViewById(R.id.ref);

        emailError = (TextView) findViewById(R.id.email_error);
        usernameError = (TextView) findViewById(R.id.username_error);
        passwordError = (TextView) findViewById(R.id.password_error);
        confirmError = (TextView) findViewById(R.id.confirm_password_error);
        hintError = (TextView) findViewById(R.id.hint_error);
        refError = (TextView) findViewById(R.id.ref_error);

        emailError.setVisibility(View.INVISIBLE);
        usernameError.setVisibility(View.INVISIBLE);
        passwordError.setVisibility(View.INVISIBLE);
        confirmError.setVisibility(View.INVISIBLE);
        hintError.setVisibility(View.INVISIBLE);
        refError.setVisibility(View.INVISIBLE);

        signUp = (Button) findViewById(R.id.sign_up);
        signUp.setOnClickListener(onClickListener);
        previous = (Button) findViewById(R.id.previous);
        previous.setOnClickListener(onClickListener);

        transferGuest = (CheckBox) this.findViewById(R.id.transfer_guest);
        transferGuest.setChecked(true);
        String possibleUser = sql.getSingleResult("user", "username", " where username != 'Guest'");
        if(possibleUser.equals("")) {//this means there is NOT a user in the local DB other than a guest and they can transfer the data from guest to another account
            transferGuest.setVisibility(View.VISIBLE);
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false).setTitle(getResources().getString(R.string.signup_successful)).setMessage(getResources().getString(R.string.check_email)).
                setPositiveButton(context.getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent b = new Intent(SignUp.this, Login.class);
                        leaving = true;
                        startActivity(b);
                        SignUp.this.finish();
                    }
                });
        alertDialog = alertDialogBuilder.create();
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
                case R.id.sign_up:
                    User.add("Clicked SignUp");
                    sd.buttonPress();
                    signUp();
                    break;
                case R.id.previous:
                    onBackPressed();
                    break;
            }
        }
    };

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
//transfer any data from guest to new user
    public void signUp() {
        final String userEmail = email.getText().toString();
        final String userName = username.getText().toString();
        final String pw = password.getText().toString();
        final String pwConfirm = confirmPassword.getText().toString();
        final String userHint = hint.getText().toString();
        final String referral = ref.getText().toString();

        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(context, getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
        } else if (verifyClean(userEmail, userName, pw, pwConfirm, userHint, referral)) {
            progress = new ProgressDialog(context, R.style.MyTheme);
            progress.setIndeterminate(true);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(false);
            progress.show();
            new Thread() {
                public void run() {
                    ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
                    userQuery.orderByDescending("updatedAt");
                    userQuery.whereEqualTo("username", userName);
                    try {
                        user = null;
                        user = userQuery.getFirst();
                    } catch (ParseException pe) {
                    }
                    if (user != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showError(usernameError, getString(R.string.username_already_registered));
                            }
                        });
                    } else {
                        sql.insert("user", " email,username,password,hint,hints,country,rated, no_ads,coins,gold ", "'" + userEmail + "','" + userName + "','" + pw + "','" + userHint + "'," + "3" + ",'" + locale.getCountry() + "', '', '', 0, 0 ");
                        getSharedPreferences("USER", MODE_PRIVATE).edit().putString("remember_me_username", username.getText().toString()).apply();
                        getSharedPreferences("USER", MODE_PRIVATE).edit().putBoolean("remember_me", true).apply();
                        getSharedPreferences("email", MODE_PRIVATE).edit().putString("recipient", email.getText().toString()).apply();
                        getSharedPreferences("email", MODE_PRIVATE).edit().putString("subject", getResources().getString(R.string.welcome_to_shift_email)).apply();
                        getSharedPreferences("email", MODE_PRIVATE).edit().putString("message", getResources().getString(R.string.shift_user) + " " + username.getText().toString() + " " + getResources().getString(R.string.user_created)).apply();
                        SendEmailASyncTask task = new SendEmailASyncTask(getApplicationContext());

                        if (user == null) {
                            user = new ParseObject("user");
                        }
                        user.put("username", userName);
                        user.put("password", pw);
                        user.put("country", Locale.getDefault().getCountry());
                        user.put("hint", userHint);
                        user.put("email", userEmail);
                        String currentHints = sql.getSingleResult("user", "hints", " where username = 'Guest' ");
                        user.put("hints", Integer.valueOf((currentHints.equals("") || currentHints.equals("nul")) ? "0" : currentHints));
                        String rated = sql.getSingleResult("user", "rated", " where username = 'Guest' ");
                        user.put("rated", rated);
                        String coins = sql.getSingleResult("user", "coins", " where username = 'Guest' ");
                        user.put("coins", Integer.valueOf((coins.equals("") || coins.equals("nul")) ? "0" : coins));
                        String gold = sql.getSingleResult("user", "gold", " where username = 'Guest' ");
                        user.put("gold", Integer.valueOf((gold.equals("") || gold.equals("nul")) ? "0" : gold));
                        String perfect_bonus_mod = sql.getSingleResult("user", "perfect_bonus_mod", " where username = 'Guest' ");
                        user.put("perfect_bonus_mod", Double.valueOf((perfect_bonus_mod.equals("") || perfect_bonus_mod.equals("nul")) ? "0.1" : forceDecimal(perfect_bonus_mod, "0.1")));
                        user.put("no_ads", new JSONArray());
                        if(transferGuest.isChecked()) {
                            sql.duplicateAll("Guest", userName);
                        }
                        try {
                            user.save();
                        } catch (ParseException pe) {
                            Log.i("debugger", "PE: "+pe.getMessage());
                        }
                        User.add("Signed up user "+userName);
                        task.execute();
                        if(!referral.equals("")) {
                            rewardRef(referral);
                        }
                        ParseService.updateSync("permissions_packs", false);
                        Intent packsIntent = new Intent(SignUp.this, ParseService.class);
                        packsIntent.putExtra(ParseService.Object, "permissions_packs");
                        startService(packsIntent);
                        ParseService.updateSync("permissions_levels", false);
                        Intent levelsIntent = new Intent(SignUp.this, ParseService.class);
                        levelsIntent.putExtra(ParseService.Object, "permissions_levels");
                        startService(levelsIntent);
                        ParseService.updateSync("purchases", false);
                        Intent purchasesIntent = new Intent(SignUp.this, ParseService.class);
                        purchasesIntent.putExtra(ParseService.Object, "purchases");
                        startService(purchasesIntent);
                        ParseService.updateSync("score_levels", false);
                        Intent scoreLevelsIntent = new Intent(SignUp.this, ParseService.class);
                        scoreLevelsIntent.putExtra(ParseService.Object, "score_levels");
                        startService(scoreLevelsIntent);
                        ParseService.updateSync("score", false);
                        Intent scoreIntent = new Intent(SignUp.this, ParseService.class);
                        scoreIntent.putExtra(ParseService.Object, "score");
                        startService(scoreIntent);
                        if(progress.isShowing()){
                            progress.dismiss();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    alertDialog.show();
                                    if (alertDialog.isShowing()) {
                                        int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                                        View divider = alertDialog.findViewById(dividerId);
                                        divider.setBackgroundColor(getResources().getColor(R.color.gold));
                                    }
                                }catch (Exception e){
                                    User.add("Hit exception showing AlertDialog after SignUp");
                                    Intent b = new Intent(SignUp.this, Login.class);
                                    leaving = true;
                                    startActivity(b);
                                    SignUp.this.finish();
                                }
                            }
                        });
                    }
                }
            }.start();
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
    //below will be removed after January 10th 2016 at 12:00 midnight.  (2016-01-10)
    public void rewardMissed(String username){
        User.add("Rewarded Missed.");
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
        userQuery.whereEqualTo("username", username);
        userQuery.orderByDescending("updatedAt");
        ParseObject user = null;
        try {
            user = userQuery.getFirst();
        } catch (ParseException pe) {
        }
        if (user != null) {
            user.put("gold", user.getInt("gold") + 2);
            try {
                user.save();
            } catch (ParseException pe) {}
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, getString(R.string.you_get_two), Toast.LENGTH_LONG).show();
            }
        });
    }
    //above will be removed after January 10th 2016 at 12:00 midnight.  (2016-01-10)
    public void rewardRef(String objectId){
        User.add("Rewarded Referal.");
        String friendUsername = "";
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
        userQuery.orderByDescending("updatedAt");
        userQuery.whereEqualTo("objectId", objectId);
        ParseObject user = null;
        try {
            user = userQuery.getFirst();
        } catch (ParseException pe) {
        }
        if (user != null) {
            user.put("gold", user.getInt("gold") + 1);
            user.add("friends", username.getText().toString());
            friendUsername = user.getString("username");
            try {
                user.save();
            } catch (ParseException pe) {}
        }

        ParseQuery<ParseObject> meQuery = ParseQuery.getQuery("user");
        meQuery.whereEqualTo("username", username.getText().toString());
        meQuery.orderByDescending("updatedAt");
        ParseObject me = null;
        try {
            me = meQuery.getFirst();
        } catch (ParseException pe) {
        }
        if (me != null) {
            me.put("coins", me.getInt("coins") + 500);
            if(!friendUsername.equals("")) {
                me.add("friends", friendUsername);
            }
            try {
                me.save();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getString(R.string.you_rewarded), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (ParseException pe) {}
        }

    }

    @Override
    public void onBackPressed(){
        sd.backPress();
        User.add("SignUp Back Pressed");
        Intent a = new Intent(SignUp.this, Login.class);
        leaving = true;
        startActivity(a);
        SignUp.this.finish();
    }
    public boolean isGoodString(String arg){
        if(arg.contains("'")||arg.contains("\"")||arg.contains("+")||arg.contains("\\")||arg.contains("/")||arg.contains("}")||arg.contains("{")||arg.contains("]")||arg.contains("[")||arg.contains(";")||arg.contains(":")||arg.contains(",")||arg.contains("*")||arg.contains("(")||arg.contains(")")||arg.contains("-")){
            return false;
        }else{
            return true;
        }
    }
    public boolean verifyClean(String userEmail, String userName, String userPw, String userPwConfirm, String userHint, String userRef){
        boolean clean = true;
        emailError.setVisibility(View.INVISIBLE);
        usernameError.setVisibility(View.INVISIBLE);
        passwordError.setVisibility(View.INVISIBLE);
        confirmError.setVisibility(View.INVISIBLE);
        hintError.setVisibility(View.INVISIBLE);
        refError.setVisibility(View.INVISIBLE);


        if(userEmail.equals("") || userEmail.trim().length() <= 0){
            showError(emailError, getResources().getString(R.string.email_blank));
            clean = false;
        }else{
            if(!userEmail.contains(".") || !userEmail.contains("@")){
                showError(emailError, getResources().getString(R.string.must_contain_symbols));
                clean = false;
            }else if(!isGoodString(userEmail)) {
                    showError(emailError, getString(R.string.invalid_chars));
                    clean = false;
            }else if (userEmail.trim().contains(" ")){
                showError(emailError, getResources().getString(R.string.no_spaces));
                clean = false;
            }else{
                if(userEmail.contains("@")){
                    String newEmail = userEmail.replaceFirst("\\@", "~");
                    if(newEmail.contains("@")) {
                        showError(emailError, getResources().getString(R.string.too_many_ats));
                        clean = false;
                    }
                }
            }
        }

        if(userName.equals("") || userName.trim().length() <= 0){
            showError(usernameError, getResources().getString(R.string.username_blank));
            clean = false;
        }else if(!isGoodString(userName)) {
                showError(usernameError, getString(R.string.invalid_chars));
                clean = false;
        }else if (userName.trim().contains(" ")){
            showError(usernameError, getResources().getString(R.string.no_spaces));
            clean = false;
        }else{
            if(userName.length() <= 5){
                showError(usernameError, getResources().getString(R.string.username_length));
                clean = false;
            }
        }

        if(userPw.equals("")){
            showError(passwordError, getResources().getString(R.string.password_blank));
            clean = false;
        }else if(!isGoodString(userPw)) {
            showError(passwordError, getString(R.string.invalid_chars));
            clean = false;
        }else{
            if(userPw.length() < 8){
                showError(passwordError, getResources().getString(R.string.new_password_length));
                clean = false;
            }
        }

        if(userPwConfirm.equals("")){
            showError(confirmError, getResources().getString(R.string.confirmation_blank));
            clean = false;
        }else{
            if(!userPw.equals(userPwConfirm)){
                showError(confirmError, getResources().getString(R.string.password_confirmation_not_match));
                clean = false;
            }
        }

        if(userHint.equals("")){
            showError(hintError, getResources().getString(R.string.hint_blank));
            clean = false;
        }else if(!isGoodString(userHint)) {
            showError(hintError, getString(R.string.invalid_chars));
            clean = false;
        }else{
            if(userPw.contains(userHint)){
                showError(hintError, getResources().getString(R.string.hint_cannot_contain));
                clean = false;
            }
        }
        if(!userRef.equals("")){
            if(!verifyRef(userRef)){
                showError(refError, getResources().getString(R.string.ref_code_invalid));
                clean = false;
            }
        }
        return clean;
    }
    public boolean verifyRef(String refCode){
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
        userQuery.whereEqualTo("objectId", refCode);
        ParseObject user = null;
        try {
            user = userQuery.getFirst();
        } catch (ParseException pe) {
        }
        if (user != null) {
            return true;
        }else{
            return false;
        }
    }
    public void showError(TextView field, String message){
        field.setVisibility(View.VISIBLE);
        field.setText(message);
    }
    /**
     * ASyncTask that composes and sends email
     */
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

                final SendGrid.Email email = new SendGrid.Email();

                // Get values from edit text to compose email
                // TODO: Validate edit texts

                String promoCode = PromoCodes.getUnusedCode(getSharedPreferences("PROMOCODES", MODE_PRIVATE).getStringSet("USED", null), "coins", "silver_500");

                email.addTo(getSharedPreferences("email", MODE_PRIVATE).getString("recipient", "noone@noone.com"));
                email.setFrom(Config.SENDGRID_EMAIL);
                email.setFromName(Config.SENDGRID_NAME);
                email.setSubject(getString(R.string.welcome_to_shift_email));
                email.setHtml("-");
                email.setTemplateId(promoCode.equals("") ? Config.WELCOME_EMAIL_NO_PROMO : Config.WELCOME_EMAIL);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            email.replaceTag(":user", username == null ? "" : username.getText().toString());
                        } catch (JSONException je) {
                        }
                    }
                });
                email.replaceTag(":welcome", getString(R.string.welcome_to_shift_email));
                email.replaceTag(":promoCode", promoCode.equals("") ? " " : promoCode);
                email.replaceTag(":tag1", getString(R.string.welcome_1));
                if(!promoCode.equals("")) {
                    email.replaceTag(":tag2", getString(R.string.welcome_2));
                    email.replaceTag(":tag3", getString(R.string.welcome_3));
                    email.replaceTag(":tag4", getString(R.string.explain_coins1));
                }
                email.replaceTag(":tag5", getString(R.string.what_are_coins));
                email.replaceTag(":tag6", getString(R.string.explain_coins2));
                email.addCategory("Welcome");

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
    public void onDestroy(){
        super.onDestroy();
/*
        context = null;
        cm = null;
        netInfo = null;
        sd = null;
        sql = null;
        locale = null;
        progress = null;

        emailError = null;
        usernameError = null;
        passwordError = null;
        confirmError = null;
        hintError = null;

        email = null;
        username = null;
        password = null;
        confirmPassword = null;
        hint = null;

        transferGuest = null;

        signUp = null;
        previous = null;

        user = null;
*/
        System.gc();

    }
}