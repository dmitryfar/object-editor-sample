package me.objecteditor.reflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.objecteditor.test.AbstractTestEntity;
import me.objecteditor.test.CoolTestEntity;
import me.objecteditor.test.SomeInterfase;
import me.objecteditor.util.ObjectTreeUtil;
import me.objecteditor.util.ReflectionUtil;

public class ReflectionTest {
  private static final Logger logger = LoggerFactory.getLogger(ReflectionTest.class);

  @Test
  public void testReflection() {

    List<Class<?>> subClasses = ReflectionUtil.getSubClasses(AbstractTestEntity.class, "me.objecteditor");
    ObjectTreeUtil.printObject(subClasses);

    List<Class<?>> mapSubClasses = ReflectionUtil.getSubClasses(Map.class, "org");
    ObjectTreeUtil.printObject(mapSubClasses);

  }

  @Test
  public void testReflections() {

    List<Class<?>> subClasses = ReflectionUtil.getSubTypesOf(AbstractTestEntity.class, "me.objecteditor");
    ObjectTreeUtil.printObject(subClasses);

    subClasses = ReflectionUtil.getSubTypesOf(SomeInterfase.class, "me.objecteditor");
    ObjectTreeUtil.printObject(subClasses);

    List<String> subClassNames = ReflectionUtil.getSubTypeNamesOf(Map.class, "org.spring");
    ObjectTreeUtil.printObject(subClassNames);

  }

  @Test
  public void testObjects() {
    CoolTestEntity coolTestEntity = new CoolTestEntity();

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("keyA", "ValueA");
    map.put("keyB", "ValueB");
    coolTestEntity.setMap(map);
    ObjectTreeUtil.printObject(map);
  }
}
