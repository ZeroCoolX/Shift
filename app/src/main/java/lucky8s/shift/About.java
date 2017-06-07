package lucky8s.shift;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.jirbo.adcolony.AdColony;

/**
 * Created by Christian on 5/19/2015.
 */
public class About extends Activity {

    Context context;
    SoundDriver sd;

    Button rateApp;
    Button previous;

    boolean leaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.add("In About");
        setContentView(R.layout.about);

        context = this;
        sd = MyApplication.getInstance().getSD();

        rateApp = (Button) this.findViewById(R.id.rate_app);
        rateApp.setOnClickListener(onClickListener);

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
                case R.id.rate_app:
                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        User.add("Went to Rate App");
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
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
        User.add("Back Pressed in About");
        leaving = true;
        Intent a = new Intent(About.this, Home.class);
        startActivity(a);
        About.this.finish();
    }
    public void onPause(){
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
        if(!leaving){
            sd.stop();
        }
        sd.stop();
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