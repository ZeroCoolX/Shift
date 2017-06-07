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
public class Pack extends Activity {

    Context context;
    SQL sql;
    SoundDriver sd;

    TextView packName;

    LinearLayout button_1;
    LinearLayout button_2;
    LinearLayout button_3;
    LinearLayout button_4;
    LinearLayout button_5;
    LinearLayout button_6;
    LinearLayout button_7;
    LinearLayout button_8;
    LinearLayout button_9;
    LinearLayout button_10;
    LinearLayout button_11;
    LinearLayout button_12;
    LinearLayout button_13;
    LinearLayout button_14;
    LinearLayout button_15;
    LinearLayout button_16;
    LinearLayout button_17;
    LinearLayout button_18;
    LinearLayout button_19;
    LinearLayout button_20;
    LinearLayout button_21;
    LinearLayout button_22;
    LinearLayout button_23;
    LinearLayout button_24;
    LinearLayout button_25;
    LinearLayout button_26;
    LinearLayout button_27;
    LinearLayout button_28;
    LinearLayout button_29;
    LinearLayout button_30;
    Button previous;


    HashMap<Integer, Integer> levels;

    boolean leaving;
    String pack = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pack);
        context = this;
        sql = new SQL(context);
        sd = MyApplication.getInstance().getSD();

        pack = getSharedPreferences("LEVELS", MODE_PRIVATE).getString("PACK", "Stout Temple");

        packName = (TextView) this.findViewById(R.id.pack_name);

        button_1 = (LinearLayout) this.findViewById(R.id.level_1);
        button_1.setOnClickListener(onClickListener);
        button_2 = (LinearLayout) this.findViewById(R.id.level_2);
        button_2.setOnClickListener(onClickListener);

        button_3 = (LinearLayout) this.findViewById(R.id.level_3);
        button_3.setOnClickListener(onClickListener);
        button_4 = (LinearLayout) this.findViewById(R.id.level_4);
        button_4.setOnClickListener(onClickListener);
        button_5 = (LinearLayout) this.findViewById(R.id.level_5);
        button_5.setOnClickListener(onClickListener);
        button_6 = (LinearLayout) this.findViewById(R.id.level_6);
        button_6.setOnClickListener(onClickListener);
        button_7 = (LinearLayout) this.findViewById(R.id.level_7);
        button_7.setOnClickListener(onClickListener);
        button_8 = (LinearLayout) this.findViewById(R.id.level_8);
        button_8.setOnClickListener(onClickListener);
        button_9 = (LinearLayout) this.findViewById(R.id.level_9);
        button_9.setOnClickListener(onClickListener);
        button_10 = (LinearLayout) this.findViewById(R.id.level_10);
        button_10.setOnClickListener(onClickListener);
        button_11 = (LinearLayout) this.findViewById(R.id.level_11);
        button_11.setOnClickListener(onClickListener);
        button_12 = (LinearLayout) this.findViewById(R.id.level_12);
        button_12.setOnClickListener(onClickListener);
        button_13 = (LinearLayout) this.findViewById(R.id.level_13);
        button_13.setOnClickListener(onClickListener);
        button_14 = (LinearLayout) this.findViewById(R.id.level_14);
        button_14.setOnClickListener(onClickListener);
        button_15 = (LinearLayout) this.findViewById(R.id.level_15);
        button_15.setOnClickListener(onClickListener);
        button_16 = (LinearLayout) this.findViewById(R.id.level_16);
        button_16.setOnClickListener(onClickListener);
        button_17 = (LinearLayout) this.findViewById(R.id.level_17);
        button_17.setOnClickListener(onClickListener);
        button_18 = (LinearLayout) this.findViewById(R.id.level_18);
        button_18.setOnClickListener(onClickListener);
        button_19 = (LinearLayout) this.findViewById(R.id.level_19);
        button_19.setOnClickListener(onClickListener);
        button_20 = (LinearLayout) this.findViewById(R.id.level_20);
        button_20.setOnClickListener(onClickListener);
        button_21 = (LinearLayout) this.findViewById(R.id.level_21);
        button_21.setOnClickListener(onClickListener);
        button_22 = (LinearLayout) this.findViewById(R.id.level_22);
        button_22.setOnClickListener(onClickListener);
        button_23 = (LinearLayout) this.findViewById(R.id.level_23);
        button_23.setOnClickListener(onClickListener);
        button_24 = (LinearLayout) this.findViewById(R.id.level_24);
        button_24.setOnClickListener(onClickListener);
        button_25 = (LinearLayout) this.findViewById(R.id.level_25);
        button_25.setOnClickListener(onClickListener);
        button_26 = (LinearLayout) this.findViewById(R.id.level_26);
        button_26.setOnClickListener(onClickListener);
        button_27 = (LinearLayout) this.findViewById(R.id.level_27);
        button_27.setOnClickListener(onClickListener);
        button_28 = (LinearLayout) this.findViewById(R.id.level_28);
        button_28.setOnClickListener(onClickListener);
        button_29 = (LinearLayout) this.findViewById(R.id.level_29);
        button_29.setOnClickListener(onClickListener);
        button_30 = (LinearLayout) this.findViewById(R.id.level_30);
        button_30.setOnClickListener(onClickListener);

        previous = (Button) this.findViewById(R.id.previous);
        previous.setOnClickListener(onClickListener);

        setLayout(pack);

        User.add("Went to "+pack);

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
            if(view instanceof Button || view instanceof LinearLayout){
                buttonClick(view);
            }
            switch (view.getId()) {
                case R.id.level_1:
                    level = 1;
                    break;
                case R.id.level_2:
                    level = 2;
                    break;
                case R.id.level_3:
                    level = 3;
                    break;
                case R.id.level_4:
                    level = 4;
                    break;
                case R.id.level_5:
                    level = 5;
                    break;
                case R.id.level_6:
                    level = 6;
                    break;
                case R.id.level_7:
                    level = 7;
                    break;
                case R.id.level_8:
                    level = 8;
                    break;
                case R.id.level_9:
                    level = 9;
                    break;
                case R.id.level_10:
                    level = 10;
                    break;
                case R.id.level_11:
                    level = 11;
                    break;
                case R.id.level_12:
                    level = 12;
                    break;
                case R.id.level_13:
                    level = 13;
                    break;
                case R.id.level_14:
                    level = 14;
                    break;
                case R.id.level_15:
                    level = 15;
                    break;
                case R.id.level_16:
                    level = 16;
                    break;
                case R.id.level_17:
                    level = 17;
                    break;
                case R.id.level_18:
                    level = 18;
                    break;
                case R.id.level_19:
                    level = 19;
                    break;
                case R.id.level_20:
                    level = 20;
                    break;
                case R.id.level_21:
                    level = 21;
                    break;
                case R.id.level_22:
                    level = 22;
                    break;
                case R.id.level_23:
                    level = 23;
                    break;
                case R.id.level_24:
                    level = 24;
                    break;
                case R.id.level_25:
                    level = 25;
                    break;
                case R.id.level_26:
                    level = 26;
                    break;
                case R.id.level_27:
                    level = 27;
                    break;
                case R.id.level_28:
                    level = 28;
                    break;
                case R.id.level_29:
                    level = 29;
                    break;
                case R.id.level_30:
                    level = 30;
                    break;
                case R.id.previous:
                    onBackPressed();
                    break;
            }
            if(view.getId() != R.id.previous) {
                sd.buttonPress();
                getSharedPreferences("LEVELS", MODE_PRIVATE)
                        .edit()
                        .putInt("LEVEL", level)
                        .apply();
                if (levels.get(level) == 1) {
                    Intent a = new Intent(context, Play.class);
                    leaving = true;
                    startActivity(a);
                    Pack.this.finish();
                } else {
                    Toast.makeText(context, getResources().getString(R.string.level_not_available), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    public String getLocaleName(String pack){
        switch (pack){
            case "Stout Temple":
                return getResources().getString(R.string.stout_temple);
            case "Earth Temple":
                return getResources().getString(R.string.earth_temple);
            case "Tutorial":
                return getResources().getString(R.string.tutorial);
            case "Flame Temple":
                return getResources().getString(R.string.flame_temple);
            case "Aqua Temple":
                return getResources().getString(R.string.aqua_temple);
            case "Chilled Temple":
                return getResources().getString(R.string.chilled_temple);
            case "Spirit Temple":
                return getResources().getString(R.string.spirit_temple);
            case "Light Temple":
                return getResources().getString(R.string.light_temple);
            case "Brittle Temple":
                return getResources().getString(R.string.brittle_temple);

            case "Tidal Temple":
                return getResources().getString(R.string.tidal_temple);
            case "Elemental Temple":
                return getResources().getString(R.string.elemental_temple);
            case "Stalwart Temple":
                return getResources().getString(R.string.stalwart_temple);
            case "Crumbling Temple":
                return getResources().getString(R.string.crumbling_temple);
            case "Mystic Temple":
                return getResources().getString(R.string.mystic_temple);
            case "Volcano Temple":
                return getResources().getString(R.string.volcano_temple);
            case "Sun Temple":
                return getResources().getString(R.string.sun_temple);
            case "Sunken Temple":
                return getResources().getString(R.string.sunken_temple);
            case "Cryptic Temple":
                return getResources().getString(R.string.cryptic_temple);
            case "Lost Temple":
                return getResources().getString(R.string.lost_temple);
            case "Steam Temple":
                return getResources().getString(R.string.steam_temple);
            case "Arctic Temple":
                return getResources().getString(R.string.arctic_temple);

            default: return getResources().getString(R.string.stout_temple);
        }
    }
    public void setLayout(String pack){
        levels = sql.getLevels("permissions_levels", " where username = '"+User.getUser()+"' and pack = '"+pack+"' ");
        HashMap<Integer, Integer> levelsScore;
        levelsScore = sql.getLevels("score_levels", " where username = '"+User.getUser()+"' and pack = '"+pack+"' ");
        packName.setText(getLocaleName(pack));
        Resources res = getResources();
        int id;
        for(int x=1; x <= 30; x++) {
            id = res.getIdentifier("button_" + Integer.toString(x), "id", context.getPackageName());
            Button view = (Button) this.findViewById(id);
            if (levels.get(x) == 1) {
                view.setTextColor(getResources().getColor(R.color.lt_tan));
            } else {
                view.setTextColor(Color.parseColor("#000000"));
            }
        }

        for(int x=1; x <=30; x++){
            for(int y = 1; y <= 3; y++) {
                View star;
                if(y == 1){
                    if(levelsScore.get(x) >= 30){
                        id = res.getIdentifier("star_" + Integer.toString(x) + "_" + Integer.toString(y), "id", context.getPackageName());
                        star = this.findViewById(id);
                        star.setVisibility(View.VISIBLE);
                    }
                }else if(y == 2){
                    if(levelsScore.get(x) >= 75){
                        id = res.getIdentifier("star_" + Integer.toString(x) + "_" + Integer.toString(y), "id", context.getPackageName());
                        star = this.findViewById(id);
                        star.setVisibility(View.VISIBLE);
                    }
                }else if(y == 3){
                    if(levelsScore.get(x) >= 95){
                        id = res.getIdentifier("star_" + Integer.toString(x) + "_" + Integer.toString(y), "id", context.getPackageName());
                        star = this.findViewById(id);
                        star.setVisibility(View.VISIBLE);
                    }
                }
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
        User.add("Pack Back Pressed");
        sd.backPress();
        leaving = true;
        Intent a = new Intent(Pack.this, Levels.class);
        startActivity(a);
        Pack.this.finish();
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

        button_1 = null;
        button_2 = null;
        button_3 = null;
        button_4 = null;
        button_5 = null;
        button_6 = null;
        button_7 = null;
        button_8 = null;
        button_9 = null;
        button_10 = null;
        button_11 = null;
        button_12 = null;
        button_13 = null;
        button_14 = null;
        button_15 = null;
        button_16 = null;
        button_17 = null;
        button_18 = null;
        button_19 = null;
        button_20 = null;
        button_21 = null;
        button_22 = null;
        button_23 = null;
        button_24 = null;
        button_25 = null;
        button_26 = null;
        button_27 = null;
        button_28 = null;
        button_29 = null;
        button_30 = null;

        previous = null;


        levels = null;
*/
        System.gc();

    }
}