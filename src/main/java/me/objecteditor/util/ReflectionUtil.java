package me.objecteditor.util;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class ReflectionUtil {
  private static final Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

  public static List<Class<?>> getSubClasses(Class<?> clazz, String basePackage) {
    List<Class<?>> classes = new ArrayList<Class<?>>();
    List<String> classNames = getSubClassNames(clazz, basePackage);
    for (String className : classNames) {
      try {
        Class<?> subClazz = Class.forName(className);
        classes.add(subClazz);
      } catch (ClassNotFoundException e) {
        // skip because of we found this class by scan
      }
    }
    return classes;
  }

  public static List<String> getSubClassNames(Class<?> clazz, String basePackage) {
    ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AssignableTypeFilter(clazz));

    List<String> classNames = new ArrayList<String>();

    // scan in basePackage
    Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
    for (BeanDefinition component : components) {
      classNames.add(component.getBeanClassName());
    }

    return classNames;
  }
  public static <T> List<String> getSubTypeNamesOf(String className, String... packages) {
    return getSubTypeNamesOf(className, false, true, packages);
  }

  public static <T> List<String> getSubTypeNamesOf(String className, boolean includeCurrent, String... packages) {
    return getSubTypeNamesOf(className, includeCurrent, true, packages);
  }

  public static <T> List<String> getSubTypeNamesOf(String className, boolean includeCurrent, boolean includeAbstract, String... packages) {
    try {
      Class<?> clazz = Class.forName(className);
      return getSubTypeNamesOf(clazz, includeCurrent, includeAbstract, packages);
    } catch (ClassNotFoundException e) {
      return Collections.emptyList();
    }
  }

  public static <T> List<String> getSubTypeNamesOf(Class<T> clazz, String... packages) {
    return getSubTypeNamesOf(clazz, false, true, packages);
  }

  public static <T> List<String> getSubTypeNamesOf(Class<T> clazz, boolean includeCurrent, String... packages) {
    return getSubTypeNamesOf(clazz, includeCurrent, true, packages);
  }

  public static <T> List<String> getSubTypeNamesOf(Class<T> clazz, boolean includeCurrent, boolean includeAbstract, String... packages) {
    List<String> names = new ArrayList<String>();
    for (String aPackage : packages) {
      Reflections reflections = new Reflections(aPackage, new SubTypesScanner(false));
      Set<Class<? extends T>> cc = reflections.getSubTypesOf(clazz);
      for (Class<?> aClass : cc) {
        if (includeAbstract || !Modifier.isAbstract(aClass.getModifiers())) {
          names.add(aClass.getName());
        }
      }
    }
    if (includeCurrent && (includeAbstract || !Modifier.isAbstract(clazz.getModifiers())) ) {
      names.add(clazz.getName());
    }
    return names;
  }

  public static <T> List<Class<?>> getSubTypesOf(String className, String... packages) {
    try {
      Class<?> clazz = Class.forName(className);
      return getSubTypesOf(clazz, packages);
    } catch (ClassNotFoundException e) {
      return Collections.emptyList();
    }
  }

  public static <T> List<Class<?>> getSubTypesOf(Class<T> clazz, String... packages) {
    List<Class<?>> classes = new ArrayList<Class<?>>();
    for (String aPackage : packages) {
      Reflections reflections = new Reflections(aPackage, new SubTypesScanner(false));
      Set<Class<? extends T>> cc = reflections.getSubTypesOf(clazz);
      for (Class<?> aClass : cc) {
        /*if (!aClass.isInterface() && xmlAnnotated) {
          classes.add(aClass);
        }*/
        classes.add(aClass);
      }
    }
    return classes;
  }
}
