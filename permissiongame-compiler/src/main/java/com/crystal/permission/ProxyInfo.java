package com.crystal.permission;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * 生成的代理类信息
 * Created by crystalchi on 2016/9/22 0022.
 */
public class ProxyInfo {

    private Map<Integer, String> grantedMap = new HashMap<Integer, String>(); //存储已授权的requestCode及对应的方法
    private Map<Integer, String> deniedMap = new HashMap<Integer, String>(); //存储拒绝授权的requestCode及对应的方法
    public static final String PROXY = "PermissionProxy"; //代理类后缀
    private PackageElement packageElement;
    private TypeElement classElement;
    private ExecutableElement methodElement;
    private String packageName; //包名
    private String proxyClassName; //代理类名

    public ProxyInfo(PackageElement packageElement, TypeElement classElement, ExecutableElement methodElement){
        this.packageElement = packageElement;
        this.classElement = classElement;
        this.methodElement = methodElement;
        this.packageName = packageElement.getQualifiedName().toString();
        this.proxyClassName = classElement.getSimpleName().toString() + "$$" + PROXY;
    }

    /**
     * 生成java code
     * @return
     */
    public String generateJavaCode(){
        StringBuilder sb = new StringBuilder();
        sb.append("//Generated code from PermissionGame. Do not modify!\n");
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import ").append("com.crystal.permission.*;\n\n");
        sb.append("public class ").append(proxyClassName).append(" implements ")
                .append(PROXY + "<" + classElement.getSimpleName() +">");
        sb.append("{\n");

        //生成方法
        generateMethod(sb);

        sb.append("}\n");
        return sb.toString();
    }

    public void generateMethod(StringBuilder sb){
        generateGrantedMethod(sb);
        generateDeniedMethod(sb);
    }

    /**
     * 生成已授权方法
     * @param sb
     */
    public void generateGrantedMethod(StringBuilder sb){
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append("public void granted(").append(classElement.getSimpleName().toString()).append(" obj, int requestCode){\n");
        sb.append("\t\t").append("switch(requestCode){\n");
        for(Integer key : grantedMap.keySet()){
            sb.append("\t\t\t").append("case ").append(key).append(":\n");
            sb.append("\t\t\t").append("obj." + grantedMap.get(key) + "();\n");
            sb.append("\t\t\t").append("break;\n");
        }
        sb.append("\t\t").append("}\n");
        sb.append("\t").append("}\n");
    }

    /**
     * 生成拒绝授权方法
     * @param sb
     */
    public void generateDeniedMethod(StringBuilder sb){
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append("public void denied(").append(classElement.getSimpleName().toString() + " obj, int requestCode){\n");
        sb.append("\t\t").append("switch(requestCode){\n");
        for(Integer key : deniedMap.keySet()){
            sb.append("\t\t\t").append("case ").append(key).append(":\n");
            sb.append("\t\t\t").append("obj." + deniedMap.get(key) + "();\n");
            sb.append("\t\t\t").append("break;\n");
        }
        sb.append("\t\t").append("}\n");
        sb.append("\t").append("}\n");
    }

    /**
     * 生成的代理类完全包名
     * @return
     */
    public String getProxyFullName(){
        StringBuffer sb = new StringBuffer(128);
        sb.append(packageName).append(".").append(proxyClassName);
        return sb.toString();
    }

    public Map<Integer, String> getGrantedMap() {
        return grantedMap;
    }

    public Map<Integer, String> getDeniedMap() {
        return deniedMap;
    }

    public TypeElement getClassElement() {
        return classElement;
    }
}
