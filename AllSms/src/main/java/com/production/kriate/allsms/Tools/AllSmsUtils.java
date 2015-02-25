package com.production.kriate.allsms.Tools;


import android.content.Context;
import android.content.pm.PackageManager;

    public class AllSmsUtils {
    public static String getVersionName(Context ctx) {
        String versionName = null;
        try {
            versionName = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}

