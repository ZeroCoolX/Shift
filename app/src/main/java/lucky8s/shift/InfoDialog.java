package lucky8s.shift;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Christian on 4/5/2015.
 */
public class InfoDialog extends DialogFragment{

    LinearLayout column1;
    LinearLayout column2;
    LinearLayout column3;

    TextView pack;

    public InfoDialog() {
        // Empty constructor required for DialogFragment
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pack_info, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        column1 = (LinearLayout) view.findViewById(R.id.column1);
        column2 = (LinearLayout) view.findViewById(R.id.column2);
        column3 = (LinearLayout) view.findViewById(R.id.column3);

        pack = (TextView) view.findViewById(R.id.pack);

        String packName = getDialog().getContext().getSharedPreferences("LEVELS", Context.MODE_PRIVATE).getString("INFOPACK", "Stout Temple");
        pack.setText(packName);
        addBlockTypes(packName);

        return view;
    }
    public void addBlockTypes(String pack){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.obstacle_info_width), (int)getResources().getDimension(R.dimen.obstacle_info_width));
        View obstacle = new View(getDialog().getContext());
        obstacle.setLayoutParams(layoutParams);
        obstacle.setBackground(getResources().getDrawable(R.drawable.play_obstacle));

        View molten = new View(getDialog().getContext());
        molten.setLayoutParams(layoutParams);
        molten.setBackground(getResources().getDrawable(R.drawable.play_molten));

        View poppable = new View(getDialog().getContext());
        poppable.setLayoutParams(layoutParams);
        poppable.setBackground(getResources().getDrawable(R.drawable.play_bubble));

        View breakable = new View(getDialog().getContext());
        breakable.setLayoutParams(layoutParams);
        breakable.setBackground(getResources().getDrawable(R.drawable.play_breakable));

        View portal = new View(getDialog().getContext());
        portal.setLayoutParams(layoutParams);
        portal.setBackground(getResources().getDrawable(R.drawable.play_portal));

        View frozen = new View(getDialog().getContext());
        frozen.setLayoutParams(layoutParams);
        frozen.setBackground(getResources().getDrawable(R.drawable.play_frozen));

        switch (pack){
            case "Tutorial":
                column1.addView(obstacle);
                column1.addView(molten);
                column2.addView(poppable);
                column2.addView(breakable);
                column3.addView(portal);
                column3.addView(frozen);
                break;
            case "Stout Temple":
                column2.addView(obstacle);
                break;
            case "Earth Temple":
                column1.addView(obstacle);
                column2.addView(poppable);
                column2.addView(breakable);
                column2.addView(molten);
                column3.addView(portal);
                break;
            case "Flame Temple":
                column2.addView(obstacle);
                column2.addView(molten);
                break;
            case "Aqua Temple":
                column2.addView(obstacle);
                column2.addView(poppable);
                break;
            case "Chilled Temple":
                column2.addView(obstacle);
                column2.addView(frozen);
                break;
            case "Spirit Temple":
                column2.addView(obstacle);
                column2.addView(portal);
                break;
            case "Light Temple":
                column1.addView(obstacle);
                column1.addView(molten);
                column2.addView(poppable);
                column3.addView(portal);
                column3.addView(frozen);
                break;
            case "Sunken Temple":
                column1.addView(obstacle);
                column2.addView(poppable);
                column3.addView(portal);
                break;
            case "Sun Temple":
                column1.addView(obstacle);
                column1.addView(molten);
                column2.addView(poppable);
                column2.addView(breakable);
                column3.addView(portal);
                column3.addView(frozen);
                break;
            case "Volcano Temple":
                column2.addView(obstacle);
                column1.addView(molten);
                column3.addView(breakable);
                break;
            case "Tidal Temple":
                column2.addView(obstacle);
                column2.addView(poppable);
                break;
            case "Brittle Temple":
                column2.addView(obstacle);
                column2.addView(breakable);
                break;
            case "Elemental Temple":
                column1.addView(obstacle);
                column2.addView(molten);
                column3.addView(frozen);
                break;
            case "Crumbling Temple":
                column2.addView(obstacle);
                column2.addView(breakable);
                break;
            case "Stalwart Temple":
                column2.addView(obstacle);
                break;
            case "Mystic Temple":
                column2.addView(obstacle);
                column2.addView(portal);
                break;
            case "Cryptic Temple":
                column1.addView(obstacle);
                column2.addView(portal);
                column3.addView(breakable);
                break;
            case "Lost Temple":
                column1.addView(obstacle);
                column2.addView(portal);
                column3.addView(poppable);
                break;
            case "Steam Temple":
                column1.addView(obstacle);
                column2.addView(poppable);
                column3.addView(molten);
                break;
            case "Arctic Temple":
                column1.addView(obstacle);
                column2.addView(breakable);
                column2.addView(frozen);
                column3.addView(poppable);
                break;
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
        getDialog().getWindow().setLayout((width / 10) * 6, (int)Math.round((height / 10) * 4.5));
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
    public void onDestroy(){
        super.onDestroy();
        System.gc();
    }
}
