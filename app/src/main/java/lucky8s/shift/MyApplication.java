package lucky8s.shift;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.parse.Parse;
import com.vungle.publisher.AdConfig;
import com.vungle.publisher.Orientation;
import com.vungle.publisher.VunglePub;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Created by Christian on 5/20/2015.
 */
public class MyApplication extends Application {
    private static MyApplication singleton;
    private static SoundDriver mSd;
    private static Thread.UncaughtExceptionHandler defaultUEH;

    @Override
    public void onCreate(){
        super.onCreate();
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
        singleton = this;
        mSd = new SoundDriver(this);
        int versionCode = 0;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(versionCode != 0){
            User.add("Version code = "+Integer.toString(versionCode));
        }
        User.add("Version code = "+Integer.toString(versionCode));
        Parse.initialize(this, "xuOdc3GaoLx0Qpi5oca93h3OHWf2ObStx5TwCQOo", "MQu7QUKiZOzrWzvcUiJOQPN79j5Fj4b9BUWFYsH5");
    }
    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
               public void uncaughtException(Thread thread, Throwable ex) {
                   logActivity(ex);
                   startNewActivity();
                   System.exit(1);
               }
    };

    public static MyApplication getInstance() {
        return singleton;
    }

    public SoundDriver getSD() {
        return mSd;
    }
    public void logActivity(Throwable ex){
        User.add(Log.getStackTraceString(ex));
        User.add(getStackTraceString(ex.getStackTrace()));
        getSharedPreferences("Exception", MODE_PRIVATE).edit().putString("Uncaught", User.debug).apply();
    }
    public void startNewActivity(){
        Intent intent = new Intent ();
        intent.setAction ("lucky8s.shift.ERROR_HOME"); // see step 5.
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity (intent);
    }
    public String getStackTraceString(StackTraceElement[] stackTraceElements){
        if (stackTraceElements == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (StackTraceElement element : stackTraceElements)
            stringBuilder.append(element.toString()).append("\n");
        return stringBuilder.toString();
    }
}
