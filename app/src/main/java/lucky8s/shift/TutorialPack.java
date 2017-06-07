package lucky8s.shift;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;

import java.util.HashMap;

/**
 * Created by Christian on 5/19/2015.
 */
public class TutorialPack extends Activity {

    Context context;
    SQL sql;
    SoundDriver sd;

    TextView packName;

    LinearLayout level_1;
    LinearLayout level_2;
    LinearLayout level_3;
    LinearLayout level_4;
    LinearLayout level_5;
    LinearLayout level_6;
    Button previous;

    HashMap<Integer, Integer> levels;

    String pack = "";

    boolean leaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.add("In TutorialPack");
        setContentView(R.layout.tutorial_pack);
        context = this;
        sql = new SQL(context);

        sd = MyApplication.getInstance().getSD();

        pack = getSharedPreferences("LEVELS", MODE_PRIVATE).getString("PACK", "Tutorial");

        packName = (TextView) this.findViewById(R.id.pack_name);

        level_1 = (LinearLayout) this.findViewById(R.id.level_1);
        level_1.setOnClickListener(onClickListener);
        level_2 = (LinearLayout) this.findViewById(R.id.level_2);
        level_2.setOnClickListener(onClickListener);
        level_3 = (LinearLayout) this.findViewById(R.id.level_3);
        level_3.setOnClickListener(onClickListener);
        level_4 = (LinearLayout) this.findViewById(R.id.level_4);
        level_4.setOnClickListener(onClickListener);
        level_5 = (LinearLayout) this.findViewById(R.id.level_5);
        level_5.setOnClickListener(onClickListener);
        level_6 = (LinearLayout) this.findViewById(R.id.level_6);
        level_6.setOnClickListener(onClickListener);

        previous = (Button) this.findViewById(R.id.previous);
        previous.setOnClickListener(onClickListener);

        setLayout(pack);

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
            int level = 1;
            if(view instanceof Button){
                buttonClick(view);
            }
            switch (view.getId()) {
                case R.id.level_1:
                    level = 1;
                    User.add("Clicked Tutorial Level 1");
                    break;
                case R.id.level_2:
                    level = 2;
                    User.add("Clicked Tutorial Level 2");
                    break;
                case R.id.level_3:
                    level = 3;
                    User.add("Clicked Tutorial Level 3");
                    break;
                case R.id.level_4:
                    level = 4;
                    User.add("Clicked Tutorial Level 4");
                    break;
                case R.id.level_5:
                    level = 5;
                    User.add("Clicked Tutorial Level 5");
                    break;
                case R.id.level_6:
                    level = 6;
                    User.add("Clicked Tutorial Level 6");
                    break;
                case R.id.previous:
                    onBackPressed();
            }
            if(view.getId() != R.id.previous) {
                sd.buttonPress();
                getSharedPreferences("LEVELS", MODE_PRIVATE).edit().putInt("LEVEL", level).apply();
                if (levels.get(level) == 1) {
                    Intent a = new Intent(context, Play.class);
                    leaving = true;
                    User.add("Going to level "+Integer.toString(level));
                    startActivity(a);
                    TutorialPack.this.finish();
                } else {
                    Toast.makeText(context, getResources().getString(R.string.level_not_available), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    public void setLayout(String pack){
        levels = sql.getLevels("permissions_levels", " where username = '"+User.getUser()+"' and pack = '"+pack+"' ");
        HashMap<Integer, Integer> levelsScore = new HashMap<>();
        levelsScore = sql.getLevels("score_levels", " where username = '"+User.getUser()+"' and pack = '"+pack+"' ");
        packName.setText(pack);
        Resources res = getResources();
        int id;
        for(int x=1; x <= 6; x++) {
            id = res.getIdentifier("button_" + Integer.toString(x), "id", context.getPackageName());
            Button view = (Button) this.findViewById(id);
            if (levels.get(x) == 1) {
                view.setTextColor(getResources().getColor(R.color.lt_tan));
            } else {
                view.setTextColor(Color.parseColor("#000000"));
            }
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
        setLayout(pack);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        User.add("TutorialPack Back Pressed");
        sd.backPress();
        Intent a = new Intent(TutorialPack.this, Levels.class);
        leaving = true;
        startActivity(a);
        TutorialPack.this.finish();
    }
    public void onPause(){
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
        if(!leaving){
            sd.stop();
        }
    }
    public void onDestroy(){
        super.onDestroy();
/*
        context = null;
        sql = null;
        sd = null;

        packName = null;

        level_1 = null;
        level_2 = null;
        level_3 = null;
        level_4 = null;
        level_5 = null;
        level_6 = null;
        previous = null;

        levels = null;
*/
        System.gc();

    }
}