package com.crystal.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义已授权注解
 * Created by crystalchi on 2016/9/22 0022.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface PermissionGranted {

    int value();
}
