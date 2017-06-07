package lucky8s.shift;

import android.content.Context;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Christian on 5/20/2015.
 */
public class User {

    public static String username = "";
    public static String email = "";
    public static String password = "";
    public static String hint = "";
    public static String country = "";
    public static Double speed = 1.0;
    public static boolean pro = false;
    public static boolean allPacks = false;
    public static Double portalSpeed = 1.0;
    public static Double perfectBonusAdd= 0.00;
    public static Double perfectBonusModifier = 0.1;
    public static Double frozenModifier = 1.0;
    public static String debug = "";

    public static String getUser(){
        if(username.equals("")){
            return MyApplication.getInstance().getString(R.string.guest);
        }else{
            return username;
        }
    }
    public static void add(String append){
        debug += append+"\n\n";
    }

}
