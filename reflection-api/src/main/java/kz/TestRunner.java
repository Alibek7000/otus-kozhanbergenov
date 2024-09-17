package kz;

import kz.annotation.AfterSuite;
import kz.annotation.BeforeSuite;
import kz.annotation.Disabled;
import kz.annotation.Test;
import kz.exception.PriorityOutOfBoundException;
import kz.exception.TooManyAfterSuiteAnnotationsException;
import kz.exception.TooManyAnnotations;
import kz.exception.TooManyBeforeSuiteAnnotationsException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TestRunner {

    public static final int MIN_PRIORITY_VALUE = 1;
    public static final int MAX_PRIORITY_VALUE = 10;

    public void run(Class testSuiteClass) throws Exception {
        if (isClassDisabledByAnnotation(testSuiteClass)) {
            return;
        }
        List<Method> allMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;
        for (Method method : testSuiteClass.getDeclaredMethods()) {
            allMethods.add(method);
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                beforeSuiteMethod = method;
            }
            if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                afterSuiteMethod = method;
            }
        }
        checkAnnotationsValidity(allMethods);
        Object instance = testSuiteClass.getDeclaredConstructor().newInstance();
        runMethod(instance, beforeSuiteMethod);
        System.out.println();
        testMethods.sort(Comparator.comparingInt((Method m) -> m.getAnnotation(Test.class).priority()).reversed());
        System.out.println();
        runTests(instance, testMethods);
        runMethod(instance, afterSuiteMethod);
    }

    private boolean isClassDisabledByAnnotation(Class testSuiteClass) {
        if (testSuiteClass.isAnnotationPresent(Disabled.class)) {
            Disabled disabledAnnotation = (Disabled) testSuiteClass.getAnnotation(Disabled.class);
            System.out.printf("%s is disabled ", testSuiteClass.getName());
            if (!disabledAnnotation.cause().isBlank()) {
                System.out.printf(" cause %s", disabledAnnotation.cause());
            }
            System.out.println();
            return true;
        }
        return false;
    }

    private void checkAnnotationsValidity(List<Method> methods) throws Exception {
        int countOfBeforeTestAnnotation = 0;
        int countOfAfterSuiteTestAnnotation = 0;
        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                countOfBeforeTestAnnotation++;
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                countOfAfterSuiteTestAnnotation++;
            }
            if (countOfBeforeTestAnnotation > 1) {
                throw new TooManyBeforeSuiteAnnotationsException();
            }
            if (countOfAfterSuiteTestAnnotation > 1) {
                throw new TooManyAfterSuiteAnnotationsException();
            }
            checkTooManyAnnotations(method);

            if (method.isAnnotationPresent(Test.class)) {
                Test annotation = method.getAnnotation(Test.class);
                if (annotation.priority() < MIN_PRIORITY_VALUE || annotation.priority() > MAX_PRIORITY_VALUE) {
                    throw new PriorityOutOfBoundException();
                }
            }
        }
    }

    private void checkTooManyAnnotations(Method method) throws TooManyAnnotations {
        int limitedAnnotationCount = 0;
        List<Class> limitedAnnotations = Arrays.asList(Test.class, BeforeSuite.class, AfterSuite.class);
        for (Class annotation : limitedAnnotations) {
            if (method.isAnnotationPresent(annotation)) {
                limitedAnnotationCount++;
            }
        }
        if (limitedAnnotationCount > 1) {
            throw new TooManyAnnotations();
        }
    }

    private void runTests(Object instance, List<Method> testMethods) {
        int successRuns = 0;
        int failedRuns = 0;
        for (Method method : testMethods) {
            try {
                Test annotation = method.getAnnotation(Test.class);
                System.out.println("Method: " + method.getName());
                System.out.println("Priority: " + annotation.priority());
                runMethod(instance, method);
                System.out.println();
                successRuns++;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                failedRuns++;
            }
        }
        System.out.println("Success runs: " + successRuns);
        System.out.println("Failed runs: " + failedRuns);
    }

    private void runMethod(Object instance, Method method) throws InvocationTargetException, IllegalAccessException {
        if (method == null) {
            return;
        }
        if (method.isAnnotationPresent(Disabled.class)) {
            Disabled disabledAnnotation = method.getAnnotation(Disabled.class);
            System.out.println();
            System.out.printf("%s is disabled ", method.getName());
            if (!disabledAnnotation.cause().isBlank()) {
                System.out.printf(" cause %s", disabledAnnotation.cause());
            }
            System.out.println();
            return;
        }
        method.invoke(instance);
    }
}
