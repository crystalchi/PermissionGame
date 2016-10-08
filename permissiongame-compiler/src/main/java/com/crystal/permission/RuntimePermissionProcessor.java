package com.crystal.permission;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * 编译时解析注解并生成我们所需要的java code，运行时直接调用。
 * 速度相对于运行时解析注解的性能效率高
 * Created by crystalchi on 2016/9/22 0022.
 */
@AutoService(Processor.class)
public class RuntimePermissionProcessor extends AbstractProcessor{

    private Map<String, ProxyInfo> proxyInfoMap = new HashMap<String, ProxyInfo>(); //存储需要生成的代理信息
    private Messager messager;
    private Filer filer;
    private Elements elementUtils;

    /**
     * 要解析的注解
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportedAnnotationTypes = new LinkedHashSet<String>();
        supportedAnnotationTypes.add(PermissionGranted.class.getCanonicalName());
        supportedAnnotationTypes.add(PermissionDenied.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    /**
     * 支持JDK版本
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.elementUtils = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "process...");
        proxyInfoMap.clear(); //防止重复添加

        if(!processAnnotations(roundEnv, PermissionGranted.class)){
            return false;
        }
        if(!processAnnotations(roundEnv, PermissionDenied.class)){
            return false;
        }

        //print...
        for(Map.Entry m : proxyInfoMap.entrySet()){
            ProxyInfo proxyInfo = (ProxyInfo) m.getValue();
            try {
                JavaFileObject jfo = filer.createSourceFile(proxyInfo.getProxyFullName(), proxyInfo.getClassElement());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.NOTE, "writer' exception is "+ e.getMessage());
            }
        }
        return true;
    }

    public boolean processAnnotations(RoundEnvironment roundEnv, Class<? extends Annotation> clz){
        Set<Element> grantedElements = (Set<Element>) roundEnv.getElementsAnnotatedWith(clz);
        for(Element annotationElement : grantedElements){
            //过滤此需要解析的注解是针对方法上注解的,得到是方法element
            if(!validateAnnotations(annotationElement, clz)){
                return false;
            }
            //Represents a method
            ExecutableElement methodElement = (ExecutableElement) annotationElement;
            //return a class
            TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
            //get PackageElement of the class
            PackageElement packageElement = elementUtils.getPackageOf(classElement);

            //完整包名、类名(代理类名拼接)、类名对象作为参数、requestCode作为参数(从注解上获取)
            String keyFullClassName = classElement.getQualifiedName().toString();
            ProxyInfo proxyInfo = proxyInfoMap.get(keyFullClassName);
            if(proxyInfo == null){
                proxyInfo = new ProxyInfo(packageElement, classElement, methodElement);
                proxyInfoMap.put(keyFullClassName, proxyInfo);
            }

            Annotation annotation = methodElement.getAnnotation(clz); //获取此方法上的注解
            if(annotation instanceof PermissionGranted){ //已授权
                int requestCode = ((PermissionGranted) annotation).value();
                proxyInfo.getGrantedMap().put(requestCode, methodElement.getSimpleName().toString()); //存储已授权的方法名
            }else if(annotation instanceof PermissionDenied){
                int requestCode = ((PermissionDenied) annotation).value();
                proxyInfo.getDeniedMap().put(requestCode, methodElement.getSimpleName().toString()); //存储拒绝授权的方法名
            }else{
                messager.printMessage(Diagnostic.Kind.ERROR, "not supported...", methodElement);
                return false;
            }
        }
        return true;
    }

    private boolean validateAnnotations(Element annotationElement, Class<? extends Annotation> clz){
        if(annotationElement.getKind() != ElementKind.METHOD){
            messager.printMessage(Diagnostic.Kind.ERROR, clz.getSimpleName() + " is only support method", annotationElement);
            return false;
        }
        return true;
    }
}
