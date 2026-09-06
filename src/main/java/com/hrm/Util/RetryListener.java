package com.hrm.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

public class RetryListener implements IAnnotationTransformer {
    @Override
    @SuppressWarnings("rawtypes")
    public void transform(ITestAnnotation testannotation, Class testClass, Constructor testConstructor,
            Method testMethod) {
        testannotation.setRetryAnalyzer(Retry.class);
    }
}
