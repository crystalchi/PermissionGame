package com.crystal.permission;

/**
 * Created by xpchi on 2016/9/22.
 */
public interface PermissionProxy<T> {

    public void granted(T obj, int requestCode);

    public void denied(T obj, int requestCode);
}
