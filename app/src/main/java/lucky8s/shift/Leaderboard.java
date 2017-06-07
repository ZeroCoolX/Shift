package lucky8s.shift;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import com.vungle.publisher.VunglePub;

import org.json.JSONArray;
import org.json.JSONException;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Christian on 5/30/2015.
 */
public class Leaderboard extends Activity{
    Context context;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    Locale locale;
    ProgressDialog progress;
    SoundDriver sd;
    PopupMenu popup;


    LinearLayout position_container;
    LinearLayout user_container;
    LinearLayout score_container;

    Button world;
    Button national;
    Button personal;
    Button addFriend;
    Button previous;

    EditText friend;

    ArrayList<Leader> leaders;

    ParseObject friendsArray;

    boolean leaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.add("In Leaderboard");
        setContentView(R.layout.leaderboard);
        context = this;

        sd = MyApplication.getInstance().getSD();

        locale = Locale.getDefault();

        friend = (EditText) this.findViewById(R.id.friends_username);

        world = (Button) this.findViewById(R.id.world);
        national = (Button) this.findViewById(R.id.national);
        personal = (Button) this.findViewById(R.id.personal);
        addFriend = (Button) this.findViewById(R.id.add_friend);
        previous = (Button) this.findViewById(R.id.previous);

        world.setOnClickListener(onClickListener);
        national.setOnClickListener(onClickListener);
        national.setText(locale.getDisplayCountry());
        personal.setOnClickListener(onClickListener);
        addFriend.setOnClickListener(onClickListener);
        previous.setOnClickListener(onClickListener);

        user_container = (LinearLayout) findViewById(R.id.user_container);
        position_container = (LinearLayout) findViewById(R.id.position_container);
        score_container = (LinearLayout) findViewById(R.id.score_container);

        addFriend.setVisibility(View.INVISIBLE);
        friend.setVisibility(View.INVISIBLE);
        world.performClick();

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
        @Override
        public void onClick(View view) {
            if(view instanceof Button){
                buttonClick(view);
            }
            switch (view.getId()){
                case R.id.world:
                    User.add("Clicked on World");
                    sd.buttonPress();
                    user_container.removeAllViews();
                    position_container.removeAllViews();
                    score_container.removeAllViews();
                    addFriend.setVisibility(View.INVISIBLE);
                    friend.setVisibility(View.INVISIBLE);
                    cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    netInfo = cm.getActiveNetworkInfo();
                    if(netInfo != null) {
                        setLeaderboard(getResources().getString(R.string.world));
                    }else{
                        Toast.makeText(context, getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.national:
                    User.add("Clicked on National");
                    sd.buttonPress();
                    user_container.removeAllViews();
                    position_container.removeAllViews();
                    score_container.removeAllViews();
                    addFriend.setVisibility(View.INVISIBLE);
                    friend.setVisibility(View.INVISIBLE);
                    cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    netInfo = cm.getActiveNetworkInfo();
                    if(netInfo != null) {
                        setLeaderboard(getResources().getString(R.string.national));
                    }else{
                        Toast.makeText(context, getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.personal:
                    User.add("Clicked on Personal");
                    sd.buttonPress();
                    user_container.removeAllViews();
                    position_container.removeAllViews();
                    score_container.removeAllViews();
                    addFriend.setVisibility(View.VISIBLE);
                    friend.setVisibility(View.VISIBLE);
                    cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    netInfo = cm.getActiveNetworkInfo();
                    if(netInfo != null) {
                        if(User.username.equals("")){
                            Toast.makeText(context, getResources().getString(R.string.sign_in_view), Toast.LENGTH_SHORT).show();
                        }else {
                            setLeaderboard(getResources().getString(R.string.personal));
                        }
                    }else{
                        Toast.makeText(context, getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.add_friend:
                    User.add("Clicked to add friend.");
                    sd.buttonPress();
                    cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    netInfo = cm.getActiveNetworkInfo();
                    if(netInfo != null) {
                        if(User.username.equals("")){
                            Toast.makeText(context, getResources().getString(R.string.sign_in_personal), Toast.LENGTH_SHORT).show();
                        }else {
                            addFriend();
                        }
                    }else{
                        Toast.makeText(context, getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
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
        User.add("Leaderboard Back Press");
        sd.backPress();
        leaving = true;
        Intent a = new Intent(Leaderboard.this, Home.class);
        startActivity(a);
        Leaderboard.this.finish();
    }
    public void setLeaderboard(String thisboard){
        leaders = new ArrayList<Leader>();
        final String board = thisboard;
        progress = new ProgressDialog(context, R.style.MyTheme);
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.show();
        new Thread() {
            public void run(){
                if(board.equals(getResources().getString(R.string.world))) {
                    ParseQuery query = new ParseQuery("score");
                    query.orderByDescending("score");
                    int count = 0;
                    try {
                        count = query.count();
                    } catch (ParseException pe) {
                    }
                    query.setLimit(count);
                    List<ParseObject> list = new ArrayList<ParseObject>();
                    try {
                        list = query.find();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                            for (int x = 0; x < list.size(); x++) {
                                Leader leader = new Leader();
                                ParseObject temp = list.get(x);
                                leader.setPosition(x + 1);
                                leader.setUsername(temp.getString("username"));
                                leader.setScore(temp.getInt("score"));
                                if(leaders == null){
                                    return;
                                }else {
                                    leaders.add(leader);
                                }
                            }
                            leaders = filter(leaders);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < (leaders.size() > 100 ? 100 : leaders.size()); i++) {
                                Leader user = leaders.get(i);
                                String username = user.getUsername();
                                String score = Integer.toString(user.getScore());
                                String position = Integer.toString(user.getPosition());
                                TextView tempPosition = new TextView(position_container.getContext());
                                TextView tempUser = new TextView(user_container.getContext());
                                TextView tempScore = new TextView(score_container.getContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
                                layoutParams.weight=1;
                                layoutParams.gravity = Gravity.CENTER;
                                tempPosition .setText(position);
                                tempPosition.setTypeface(null, Typeface.BOLD);
                                tempPosition.setLayoutParams(layoutParams);
                                tempPosition.setTextColor(getResources().getColor(R.color.lt_tan));
                                tempPosition.setGravity(Gravity.CENTER);
                                position_container.addView(tempPosition);
                                tempUser.setText(username);
                                tempUser.setTypeface(null, Typeface.BOLD);
                                tempUser.setTextColor(getResources().getColor(R.color.lt_tan));
                                if(username.equals(User.username)){
                                    tempUser.setTextColor(getResources().getColor(R.color.gold));
                                }
                                tempUser.setLayoutParams(layoutParams);
                                tempUser.setGravity(Gravity.CENTER);
                                user_container.addView(tempUser);
                                tempScore.setText(score);
                                tempScore.setTypeface(null, Typeface.BOLD);
                                tempScore.setLayoutParams(layoutParams);
                                tempScore.setGravity(Gravity.CENTER);
                                tempScore.setTextColor(getResources().getColor(R.color.lt_tan));
                                score_container.addView(tempScore);
                            }
                        }
                    });
                            if(progress.isShowing()) {
                                progress.dismiss();
                            }
                }else if(board.equals(getResources().getString(R.string.personal))){
                    ParseQuery query = new ParseQuery("score");
                    query.whereContainedIn("username", getFriends(User.username));
                    query.orderByDescending("score");
                    int count = 0;
                    try {
                        count = query.count();
                    } catch (ParseException pe) {
                    }
                    query.setLimit(count);
                    List<ParseObject> list = new ArrayList<ParseObject>();
                    try {
                        list = query.find();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                            for (int x = 0; x < list.size(); x++) {
                                Leader leader = new Leader();
                                ParseObject temp = list.get(x);
                                leader.setPosition(x + 1);
                                leader.setUsername(temp.getString("username"));
                                leader.setScore(temp.getInt("score"));
                                if(leaders == null){
                                    return;
                                }else {
                                    leaders.add(leader);
                                }
                            }
                            leaders = filter(leaders);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < (leaders.size() > 100 ? 100 : leaders.size()); i++) {
                                Leader user = leaders.get(i);
                                String username = user.getUsername();
                                String score = Integer.toString(user.getScore());
                                String position = Integer.toString(user.getPosition());
                                TextView tempPosition = new TextView(position_container.getContext());
                                TextView tempUser = new TextView(user_container.getContext());
                                TextView tempScore = new TextView(score_container.getContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
                                layoutParams.weight=1;
                                layoutParams.gravity = Gravity.CENTER;
                                tempPosition .setText(position);
                                tempPosition.setTypeface(null, Typeface.BOLD);
                                tempPosition.setLayoutParams(layoutParams);
                                tempPosition.setTextColor(getResources().getColor(R.color.lt_tan));
                                tempPosition.setGravity(Gravity.CENTER);
                                position_container.addView(tempPosition);
                                tempUser.setText(username);
                                tempUser.setTypeface(null, Typeface.BOLD);
                                tempUser.setTextColor(getResources().getColor(R.color.lt_tan));
                                if(username.equals(User.username)){
                                    tempUser.setTextColor(getResources().getColor(R.color.gold));
                                }
                                tempUser.setClickable(true);
                                tempUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final TextView temp = (TextView) view;
                                        popup = new PopupMenu(Leaderboard.this, view);
                                        popup.getMenu().add(Menu.NONE, 1, Menu.NONE, getResources().getString(R.string.remove));
                                        popup.show();

                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem menuItem) {
                                                switch (menuItem.getItemId()) {
                                                    case 1:
                                                        Toast.makeText(context, getResources().getString(R.string.removing) + " " + temp.getText().toString(), Toast.LENGTH_SHORT).show();
                                                        removeFriend(temp.getText().toString());
                                                        break;
                                                }
                                                return false;
                                            }
                                        });
                                    }
                                });
                                tempUser.setLayoutParams(layoutParams);
                                tempUser.setGravity(Gravity.CENTER);
                                user_container.addView(tempUser);
                                tempScore.setText(score);
                                tempScore.setTypeface(null, Typeface.BOLD);
                                tempScore.setLayoutParams(layoutParams);
                                tempScore.setGravity(Gravity.CENTER);
                                tempScore.setTextColor(getResources().getColor(R.color.lt_tan));
                                score_container.addView(tempScore);
                            }
                        }
                    });
                            if(progress.isShowing()) {
                                progress.dismiss();
                            }
                }else if(board.equals(getResources().getString(R.string.national))){
                    ParseQuery query = new ParseQuery("score");
                    query.whereContainedIn("username", getCountryUsers());
                    query.orderByDescending("score");
                    int count = 0;
                    try {
                        count = query.count();
                    } catch (ParseException pe) {
                    }
                    query.setLimit(count);
                    List<ParseObject> list = new ArrayList<ParseObject>();
                    try {
                        list = query.find();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                            for (int x = 0; x < list.size(); x++) {
                                Leader leader = new Leader();
                                ParseObject temp = list.get(x);
                                leader.setPosition(x + 1);
                                leader.setUsername(temp.getString("username"));
                                leader.setScore(temp.getInt("score"));
                                if(leaders == null){
                                    return;
                                }else {
                                    leaders.add(leader);
                                }
                            }
                            leaders = filter(leaders);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < (leaders.size() > 100 ? 100 : leaders.size()); i++) {
                                Leader user = leaders.get(i);
                                String username = user.getUsername();
                                String score = Integer.toString(user.getScore());
                                String position = Integer.toString(user.getPosition());
                                TextView tempPosition = new TextView(position_container.getContext());
                                TextView tempUser = new TextView(user_container.getContext());
                                TextView tempScore = new TextView(score_container.getContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
                                layoutParams.weight=1;
                                layoutParams.gravity = Gravity.CENTER;
                                tempPosition .setText(position);
                                tempPosition.setTypeface(null, Typeface.BOLD);
                                tempPosition.setLayoutParams(layoutParams);
                                tempPosition.setTextColor(getResources().getColor(R.color.lt_tan));
                                tempPosition.setGravity(Gravity.CENTER);
                                position_container.addView(tempPosition);
                                tempUser.setText(username);
                                tempUser.setTypeface(null, Typeface.BOLD);
                                tempUser.setTextColor(getResources().getColor(R.color.lt_tan));
                                if(username.equals(User.username)){
                                    tempUser.setTextColor(getResources().getColor(R.color.gold));
                                }
                                tempUser.setLayoutParams(layoutParams);
                                tempUser.setGravity(Gravity.CENTER);
                                user_container.addView(tempUser);
                                tempScore.setText(score);
                                tempScore.setTypeface(null, Typeface.BOLD);
                                tempScore.setLayoutParams(layoutParams);
                                tempScore.setGravity(Gravity.CENTER);
                                tempScore.setTextColor(getResources().getColor(R.color.lt_tan));
                                score_container.addView(tempScore);
                            }
                        }
                    });
                            if(progress.isShowing()) {
                                progress.dismiss();
                            }
                }
            }
        }.start();
    }
    public ArrayList<Leader> filter(ArrayList<Leader> leaders){
        ArrayList<Leader> distinctUsers = new ArrayList<>();
        ArrayList<String> distinctNames = new ArrayList<>();
        for(int i = 0; i < leaders.size();i++){
            if(distinctNames.contains(leaders.get(i).getUsername())){
                int index = distinctNames.indexOf(leaders.get(i).getUsername());
               if(distinctUsers.get(index).getScore() < leaders.get(i).getScore() && leaders.get(i).getScore() > 0){
                   distinctUsers.remove(index);
                   distinctNames.remove(index);
                   distinctUsers.add(leaders.get(i));
                   distinctNames.add(leaders.get(i).getUsername());
               }else{
               }
            }else{
                if(leaders.get(i).getScore() > 0) {
                    distinctNames.add(leaders.get(i).getUsername());
                    distinctUsers.add(leaders.get(i));
                }
            }
        }
        for(int i = 0; i < distinctUsers.size(); i++){
            distinctUsers.get(i).setPosition(i + 1);
        }
        return distinctUsers;
    }
    public ArrayList<String> getFriends(String username) {
        final ArrayList<String> friendsUsername = new ArrayList<>();
        ParseQuery<ParseObject> friendsQuery = new ParseQuery<ParseObject>("user");
        friendsQuery.whereEqualTo("username", username);
        try {
            friendsArray = friendsQuery.getFirst();
        }catch (ParseException pe){}
                if (friendsArray != null) {
                    JSONArray temp = friendsArray.getJSONArray("friends");
                    if (temp != null) {
                        for (int i=0;i<temp.length();i++){
                            try {
                                friendsUsername.add(temp.get(i).toString());
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
        friendsUsername.add(User.username);
        return friendsUsername;
    }
    public ArrayList<String> getCountryUsers(){
        locale = Locale.getDefault();
        final ArrayList<String> usernames = new ArrayList<>();
        ParseQuery<ParseObject> usersQuery = new ParseQuery<ParseObject>("user");
        usersQuery.whereEqualTo("country", locale.getCountry());
        int count = 0;
        try {
            count = usersQuery.count();
        } catch (ParseException pe) {
        }
        usersQuery.setLimit(count);
        List<ParseObject> parseObjects = new ArrayList<>();
        try {
            parseObjects = usersQuery.find();
        }catch (ParseException pe){}
        for(int x = 0; x < parseObjects.size();x++){
            usernames.add(parseObjects.get(x).getString("username"));
        }
        return usernames;
    }
    public void addFriend(){
            final String username = friend.getText().toString();
        if(username.equals("")){
            Toast.makeText(context, getResources().getString(R.string.username_blank), Toast.LENGTH_SHORT).show();
            return;
        }else{
            if(username.length() <= 5){
                Toast.makeText(context, getResources().getString(R.string.username_not_valid), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
        userQuery.whereEqualTo("username", username);
        userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                final ParseObject friendObject = object;
                if (object != null) {
                    final ParseQuery<ParseObject> friendsQuery = ParseQuery.getQuery("user");
                    friendsQuery.whereEqualTo("username", User.username);
                    friendsQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                object = new ParseObject("user");
                            }
                            object.add("friends", username);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(context, username +" "+ getResources().getString(R.string.added_user), Toast.LENGTH_SHORT).show();
                                    personal.performClick();
                                    friend.setText("");
                                    getSharedPreferences("email", MODE_PRIVATE).edit().putString("recipient", friendObject.getString("email")).apply();
                                    getSharedPreferences("email", MODE_PRIVATE).edit().putString("recipientUser", friendObject.getString("username")).apply();
                                    getSharedPreferences("email", MODE_PRIVATE).edit().putString("subject", getResources().getString(R.string.someone_new)).apply();
                                    getSharedPreferences("email", MODE_PRIVATE).edit().putString("user", User.username).apply();
                                    SendEmailASyncTask task = new SendEmailASyncTask(getApplicationContext());
                                    task.execute();
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(context, getResources().getString(R.string.cannot_find_user), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void removeFriend(String username){
        User.add("Removing friend.");
        final String friendUsername = username;
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
        userQuery.whereEqualTo("username", User.username);
        userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object != null) {
                    final ArrayList<String> tempFriends = new ArrayList<>();
                    tempFriends.add(friendUsername);
                    final ParseQuery<ParseObject> friendsQuery = ParseQuery.getQuery("user");
                    friendsQuery.whereEqualTo("username", User.username);
                    friendsQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            object.removeAll("friends", tempFriends);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    personal.performClick();
                                }
                            });
                        }
                    });
                }
            }
        });
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
                email.setSubject(getSharedPreferences("email", MODE_PRIVATE).getString("subject", getString(R.string.someone_new)));
                email.setHtml(getSharedPreferences("email", MODE_PRIVATE).getString("recipientUser", ""));
                email.setTemplateId(Config.FRIEND_ADDED_YOU_EMAIL);
                email.replaceTag(":friend", User.username);
                email.replaceTag(":tag1", getString(R.string.user));
                email.replaceTag(":tag2", getString(R.string.added_you));
                email.replaceTag(":tag3", getString(R.string.to_their_personal));
                email.replaceTag(":tag4", getString(R.string.see_how_close));
                email.replaceTag(":tag5", getString(R.string.add_back));
                email.addCategory("Friended");

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
        AppsFlyerLib.onActivityResume(this);
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
        locale = null;
        progress = null;
        sd = null;
        popup = null;


        position_container = null;
        user_container = null;
        score_container = null;

        world = null;
        national = null;
        personal = null;
        addFriend = null;
        previous = null;

        friend = null;

        leaders = null;

        friendsArray = null;
*/

        System.gc();

    }
}
