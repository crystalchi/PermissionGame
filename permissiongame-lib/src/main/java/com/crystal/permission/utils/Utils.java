package com.crystal.permission.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crystalchi on 2016/9/22 0022.
 */
public class Utils {

    /**
     * Android M(android6.0)之前
     * @return
     */
    public static boolean isBeforeOfAndroidM(){
       if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
           return true;
       }
       return false;
    }

    /**
     * 获得未授权的权限集合
     * Android6.0及以上版本
     * @param activity
     * @param permissions
     * @return
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String> getPermissionOfDenied(Activity activity, String[] permissions){
        List<String> deniedPermissionList = new ArrayList<String>();
        for(String permission : permissions){
            if(activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                deniedPermissionList.add(permission);
            }
        }
        return deniedPermissionList;
    }

    /**
     * 获取授权失败或未授权的权限集合
     * @param permissions
     * @param grantResults
     * @return
     */
    public static List<String> getPermissionOfGrantFail(String[] permissions, int[] grantResults){
        List<String> grantFailPermissionList = new ArrayList<String>();
        for(int i = 0; i < grantResults.length; i++){
            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                grantFailPermissionList.add(permissions[i]);
            }
        }
        return grantFailPermissionList;
    }

    public static Activity getActivity(Object object){
        if(object instanceof Activity){
            return (Activity) object;
        }else if(object instanceof Fragment){
            return ((Fragment) object).getActivity();
        }else if(object instanceof android.app.Fragment){
            return ((android.app.Fragment) object).getActivity();
        }else{
            return null;
        }
    }
}
