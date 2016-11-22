package gluu.oxd.org.badgemanager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Environment;


import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by lcom15 on 17/3/16.
 */
public class Application extends android.app.Application {
    public static volatile Context applicationContext;


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();

        try {
            final File path = new File(
                    Environment.getExternalStorageDirectory(), "Qapp_logs");
            if (!path.exists()) {
                path.mkdir();
            }
            Runtime.getRuntime().exec(
                    "logcat  -d -f " + path + File.separator
                            + "dbo_logcat" + System.currentTimeMillis()
                            + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Fresco.initialize(this);

    }


    public static boolean isAppIsInBackground() {
        boolean isInBackground = true;
        try {
            ActivityManager am = (ActivityManager) Application.applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(Application.applicationContext.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(Application.applicationContext.getPackageName())) {
                    isInBackground = false;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return isInBackground;
    }

}
