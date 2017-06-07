package lucky8s.shift;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Christian on 5/19/2015.
 */
public class Settings extends Activity {

    Context context;
    SQL sql;
    ProgressDialog progress;
    SoundDriver sd;

    Button changePassword;
    Button previous;

    CheckBox muteSounds;
    CheckBox muteMusic;

    ParseObject user;

    ConnectivityManager cm;
    NetworkInfo netInfo;

    ParseObject permissions_packs;
    ParseObject permissions_levels;
    ParseObject score_levels;
    ParseObject noAdsArray;


    boolean leaving;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.add("In Settings");
        setContentView(R.layout.settings);

        context = Settings.this;
        sql = new SQL(this);
        sd = MyApplication.getInstance().getSD();

        changePassword = (Button) this.findViewById(R.id.change_password);
        changePassword.setOnClickListener(onClickListener);
        previous = (Button) this.findViewById(R.id.previous);
        previous.setOnClickListener(onClickListener);

        muteSounds = (CheckBox) this.findViewById(R.id.mute_sounds);
        muteSounds.setChecked(!getSharedPreferences("Music", MODE_PRIVATE).getBoolean("Sounds", true));
        muteSounds.setOnCheckedChangeListener(onChecked);

        muteMusic = (CheckBox) this.findViewById(R.id.mute_music);
        muteMusic.setChecked(!getSharedPreferences("Music", MODE_PRIVATE).getBoolean("Music", true));
        muteMusic.setOnCheckedChangeListener(onChecked);
    }
    CompoundButton.OnCheckedChangeListener onChecked = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()){
                case R.id.mute_sounds:
                    getSharedPreferences("Music", MODE_PRIVATE).edit().putBoolean("Sounds", !b).apply();
                    sd.resetSettings();
                    break;
                case R.id.mute_music:
                    getSharedPreferences("Music", MODE_PRIVATE).edit().putBoolean("Music", !b).apply();
                    sd.stop();
                    sd.resetSettings();
                    if(!b){
                        sd.homeBackground();
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
            cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
            switch (view.getId()) {
                case R.id.change_password:
                    User.add("Clicked Change Password");
                    if(User.username.equals("")){
                        Toast.makeText(context, getString(R.string.not_logged_in), Toast.LENGTH_SHORT).show();
                    }else if(netInfo == null){
                        Toast.makeText(context, getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
                    }else {
                        changePassword();
                    }
                    break;
                case R.id.previous:
                    onBackPressed();
                    break;
            }
            if(view.getId() != R.id.previous){
                sd.buttonPress();
            }
        }
    };
    @Override
    public void onBackPressed() {
        sd.backPress();
        User.add("Settings Back Pressed");
        Intent a = new Intent(Settings.this, Home.class);
        leaving = true;
        startActivity(a);
        Settings.this.finish();
    }

    public void changePassword(){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.password_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setView(promptsView);

        final EditText password = (EditText) promptsView
                .findViewById(R.id.password);
        password.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        final EditText newPassword = (EditText) promptsView
                .findViewById(R.id.new_password);
        newPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        final EditText confirmNewPassword = (EditText) promptsView
                .findViewById(R.id.confirm_new_password);
        confirmNewPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        final EditText hint = (EditText) promptsView
                .findViewById(R.id.hint);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.change),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
                                userQuery.orderByDescending("updatedAt");
                                userQuery.whereEqualTo("username", User.username);
                                userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                    public void done(ParseObject object, ParseException e) {
                                        user = object;

                                        if (user == null) {
                                            Toast.makeText(context, "An error has occurred.", Toast.LENGTH_SHORT).show();
                                        } else if (!user.get("password").equals((password.getText().toString().trim()))) {
                                            Toast.makeText(context, getString(R.string.password_not_correct), Toast.LENGTH_SHORT).show();
                                        } else if (!newPassword.getText().toString().trim().equals(confirmNewPassword.getText().toString().trim())) {
                                            Toast.makeText(context, getString(R.string.password_confirmation_not_match), Toast.LENGTH_SHORT).show();
                                        } else if (newPassword.getText().toString().equals(password.getText().toString())) {
                                            Toast.makeText(context, "!", Toast.LENGTH_SHORT).show();
                                        } else if (newPassword.getText().toString().length() < 8) {
                                            Toast.makeText(context, getString(R.string.new_password_length), Toast.LENGTH_SHORT).show();
                                        } else if (newPassword.getText().toString().contains(hint.getText().toString())) {
                                            Toast.makeText(context, getString(R.string.hint_cannot_contain), Toast.LENGTH_SHORT).show();
                                        }else {
                                            User.password = password.getText().toString();
                                            User.hint = hint.getText().toString();
                                            sql.update("user", " where username = '" + User.username + "' ", " set password = '" + password.getText().toString().trim() + "', hint = '" + hint.getText().toString() + "'");

                                            user.put("password", newPassword.getText().toString().trim());
                                            user.put("hint", hint.getText().toString().trim());
                                            try {
                                                user.save();
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                            Toast.makeText(context, getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(context.getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        if(alertDialog.isShowing()) {
            int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = alertDialog.findViewById(dividerId);
            divider.setBackgroundColor(getResources().getColor(R.color.gold));
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;
        alertDialog.getWindow().setLayout((width / 10) * 9, (height / 10) * 5);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
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
        sql = null;
        progress = null;
        sd = null;

        sync = null;
        changePassword = null;
        previous = null;

        muteSounds = null;
        muteMusic = null;

        user = null;
        purchases = null;

        cm = null;
        netInfo = null;

        permissions_packs = null;
        permissions_levels = null;
        score_levels = null;
        noAdsArray = null;
*/
        System.gc();

    }
}