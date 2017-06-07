package lucky8s.shift;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.*;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;

/**
 * Created by Christian on 5/19/2015.
 */
public class Profile extends Activity {

    Context context;
    SoundDriver sd;
    SQL sql;

    Button previous;

    TextView user;
    TextView speed;
    TextView portalSpeed;
    TextView pro;
    TextView allPacks;
    TextView noAds;
    TextView perfectBonusMod;
    TextView perfectBonusAdd;
    TextView frozenMod;

    boolean leaving;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.add("In Profile");
        setContentView(R.layout.profile);

        context = this;
        sd = MyApplication.getInstance().getSD();
        sql = new SQL(this);

        previous = (Button) this.findViewById(R.id.previous);
        previous.setOnClickListener(onClickListener);

        user = (TextView) this.findViewById(R.id.user);
        speed = (TextView) this.findViewById(R.id.move_speed);
        portalSpeed = (TextView) this.findViewById(R.id.portal_speed);
        pro = (TextView) this.findViewById(R.id.pro);
        allPacks = (TextView) this.findViewById(R.id.all_packs);
        noAds = (TextView) this.findViewById(R.id.no_ads);
        perfectBonusMod = (TextView) this.findViewById(R.id.perfect_modifier);
        perfectBonusAdd = (TextView) this.findViewById(R.id.perfect_add);
        frozenMod = (TextView) this.findViewById(R.id.frozen_mod);

        setProfile();

    }
    public void setProfile(){
        user.setText(User.getUser());
        speed.setText(String.format("%.2f", User.speed*100.0)+"%");
        portalSpeed.setText((User.portalSpeed == 750) ? getResources().getString(R.string.instant) : String.format("%.2f", User.portalSpeed*100.0)+"%");
        pro.setText(User.pro ? getString(R.string.yes) : getString(R.string.no));
        allPacks.setText(User.allPacks ? getString(R.string.yes) : getString(R.string.no));
        noAds.setText(sql.getSingleResult("user", "no_ads", " where username = '"+User.getUser()+"' ").contains("all") ? getString(R.string.yes) : getString(R.string.no));
        perfectBonusMod.setText(String.format("%.2f", User.perfectBonusModifier * 100.0)+"%");
        perfectBonusAdd.setText(String.format("%.2f", User.perfectBonusAdd * 100.0)+"%");
        frozenMod.setText(String.format("%.2f", User.frozenModifier * 100.0) + "%");

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
                case R.id.previous:
                    onBackPressed();
                    break;
            }
        }
    };
    @Override
    public void onBackPressed() {
        sd.backPress();
        User.add("Profile Back Pressed");
        leaving = true;
        Intent a = new Intent(Profile.this, Home.class);
        startActivity(a);
        Profile.this.finish();
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
    @Override
    public void onDestroy(){
        super.onDestroy();
        System.gc();
    }
}