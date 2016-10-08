package com.crystal.permission;

import android.annotation.TargetApi;
import android.os.Build;

import com.crystal.permission.utils.Utils;

import java.util.List;

/**
 * 权限请求
 * Created by crystalchi on 2016/9/19 0019.
 */
public class PermissionRequest {

    private static final String PROXY = "$$PermissionProxy";

    public static void requestPermission(Object object, int requestCode, String permission){
        requestPermission(object, requestCode, new String[]{permission});
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    public static void requestPermission(Object object, int requestCode, String[] permissions){
        if(Utils.isBeforeOfAndroidM()){
            doExecuteSuccess(object, requestCode); //Android6.0以下版本无需请求dangerous permissions，可直接成功执行其业务逻辑
            return;
        }

        List<String> deniedPermissionList = Utils.getPermissionOfDenied(Utils.getActivity(object), permissions);
        if(deniedPermissionList.size() > 0){
            Utils.getActivity(object).requestPermissions(deniedPermissionList.toArray(new String[deniedPermissionList.size()]), requestCode);
        }else{
            doExecuteSuccess(object, requestCode);
        }
    }


    public static void requestPermissionResult(Object object, int requestCode, String[] permissions, int[] grantResults){
        List<String> grantFailPermissionList = Utils.getPermissionOfGrantFail(permissions, grantResults);
        if(grantFailPermissionList.size() > 0){
            doExecuteFailure(object, requestCode);
        }else{
            doExecuteSuccess(object, requestCode);
        }
    }


    public static void doExecuteSuccess(Object object, int requestCode){
        PermissionProxy permissionProxy = getPermissionProxy(object);
        if(permissionProxy != null){
            permissionProxy.granted(object, requestCode);
        }
    }

    public static void doExecuteFailure(Object object, int requestCode){
        PermissionProxy permissionProxy = getPermissionProxy(object);
        if(permissionProxy != null){
            permissionProxy.denied(object, requestCode);
        }
    }


    private static PermissionProxy getPermissionProxy(Object activity){
        try {
            Class clz = activity.getClass();
            Class clazz = Class.forName(clz.getName() + PROXY);
            PermissionProxy permissionProxy = (PermissionProxy) clazz.newInstance();
            return permissionProxy;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


}
