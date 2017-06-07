package lucky8s.shift;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import org.json.JSONException;

/**
 * Created by Christian on 5/19/2015.
 */
public class Refer extends Activity {

    Context context;
    SoundDriver sd;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    ProgressDialog progress;

    EditText friendsEmail;

    Button refer;
    Button previous;

    boolean leaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.add("In Refer");
        setContentView(R.layout.refer);

        context = this;
        sd = MyApplication.getInstance().getSD();

        friendsEmail = (EditText) this.findViewById(R.id.friends_email);

        refer = (Button) this.findViewById(R.id.refer);
        refer.setOnClickListener(onClickListener);

        previous = (Button) this.findViewById(R.id.previous);
        previous.setOnClickListener(onClickListener);

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
                case R.id.refer:
                    User.add("Clicked Refer");
                    cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    netInfo = cm.getActiveNetworkInfo();
                    if (netInfo == null) {
                        Toast.makeText(context, getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
                    }else if(User.getUser().equals(getString(R.string.guest))) {
                        Toast.makeText(context, getResources().getString(R.string.not_logged_in), Toast.LENGTH_SHORT).show();
                    }else{
                        refer(friendsEmail.getText().toString());
                    }
                    break;
                case R.id.previous:
                    onBackPressed();
                    break;
            }
        }
    };
    @Override
    public void onBackPressed() {
        sd.backPress();
        User.add("Refer Back Pressed");
        leaving = true;
        Intent a = new Intent(Refer.this, Home.class);
        startActivity(a);
        Refer.this.finish();
    }
    public void onPause(){
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
        if(!leaving){
            sd.stop();
        }
        sd.stop();
    }
    public void refer(String email){
        progress = new ProgressDialog(context, R.style.MyTheme);
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.show();
        final String finalEmail = email;
        new Thread() {
            public void run() {
                if (verifyEmail(finalEmail)) {
                    getSharedPreferences("REFER", MODE_PRIVATE).edit().putString("FRIEND", finalEmail).apply();
                    SendEmailASyncTask task = new SendEmailASyncTask(getApplicationContext());
                    User.add("Referred a friend.");
                    task.execute();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, getString(R.string.email_invalid), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                progress.dismiss();
            }
        }.start();
    }
    public boolean verifyEmail(String email){
        boolean clean = true;
        if(email.equals("")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, getResources().getString(R.string.email_blank), Toast.LENGTH_SHORT).show();
                }
            });
            clean = false;
        }else{
            if(!email.contains(".") || !email.contains("@")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.must_contain_symbols), Toast.LENGTH_SHORT).show();
                    }
                });
                clean = false;
            }else{
                if(email.contains("@")){
                    String newEmail = email.replaceFirst("\\@", "~");
                    if(newEmail.contains("@")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, getResources().getString(R.string.too_many_ats), Toast.LENGTH_SHORT).show();
                            }
                        });
                        clean = false;
                    }
                }
            }
        }
        return clean;
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

                email.addTo(getSharedPreferences("REFER", MODE_PRIVATE).getString("FRIEND", "someone@hotmail.com"));
                email.setFrom(Config.SENDGRID_EMAIL);
                email.setFromName(Config.SENDGRID_NAME);
                email.setSubject(User.username + " " + getString(R.string.wants_you_to_join));
                email.setHtml("-");
                email.setTemplateId(Config.REFER_A_FRIEND);
                email.replaceTag(":user", User.username);
                email.replaceTag(":referralCode", getReferalCode(User.username));
                email.replaceTag(":downloadLink", Config.DOWNLOAD);
                email.replaceTag(":tag1", getString(R.string.wants_to_compete));
                    email.replaceTag(":tag2", getString(R.string.download_shift));
                    email.replaceTag(":tag3", getString(R.string.sign_up_for_new_account));
                    email.replaceTag(":tag4", getString(R.string.to_get_a_reward));
                email.replaceTag(":tag5", getString(R.string.once_you_sign_up));
                email.replaceTag(":tag6", getString(R.string.hurry_friends));
                email.addCategory("Refer");

                // Send email, execute http request
                SendGrid.Response response = sendgrid.send(email);
                msgResponse = response.getMessage();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        friendsEmail.setText("");
                        Toast.makeText(context, getString(R.string.sent_out_ref), Toast.LENGTH_SHORT).show();
                    }
                });

                Log.i("SendAppExample", msgResponse);

            } catch (SendGridException e) {
                Log.e("SendAppExample", e.toString());
            } catch (JSONException e) {
                Log.e("SendAppExample", e.toString());
            }

            return null;
        }
    }
    public String getReferalCode(String username){
        String refCode = "";

        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
        userQuery.orderByDescending("updatedAt");
        userQuery.whereEqualTo("username", username);
        ParseObject user = null;
        try {
            user = userQuery.getFirst();
        } catch (ParseException pe) {
        }
        if (user != null) {
            refCode = user.getObjectId();
        }
        return refCode;
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
        System.gc();

    }
}