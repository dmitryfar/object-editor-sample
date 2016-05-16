package me.objecteditor.groovy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import me.objecteditor.util.DefaultValues;
import me.objecteditor.util.ObjectTreeUtil;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class GroovySpringTest {

  private static final Logger logger = LoggerFactory.getLogger(GroovySpringTest.class);

  @Configuration
  @ComponentScan(basePackages = { "me.objecteditor" }, excludeFilters = {
      @Filter(type = FilterType.ANNOTATION, value = Configuration.class) })
  public static class SpringConfig {

  }

  @Test
  public void testNewList() {
    SimpleEntity entity = new SimpleEntity();
    SimpleEntity child = new SimpleEntity();
    child.setFieldListToNull();
    entity.setChild(child);

    ObjectTreeUtil.printObject(entity);

    // create list

    GroovyObjectUtil.createNewList(entity, "fieldList");
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.createNewList(entity, "child.fieldList");
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.createNewList(entity, "child.simpleEntities");
    ObjectTreeUtil.printObject(entity);

    // add elements

    SimpleEntity element1 = new SimpleEntity();
    element1.setFieldString("**************");
    GroovyObjectUtil.addListElement(entity, "simpleEntities", element1);
    ObjectTreeUtil.printObject(entity);

    SimpleEntity element2 = new SimpleEntity();
    element2.setFieldString("@@@@@@@@@@@@@@");
    GroovyObjectUtil.addListElement(entity, "simpleEntities", element2);
    ObjectTreeUtil.printObject(entity);

    // set list element value

    GroovyObjectUtil.setObjectValue(entity, "simpleEntities[0].fieldString", "$$$$$$$$$$$$$$$$$$");
    GroovyObjectUtil.setObjectValue(entity, "simpleEntities[0].fieldString", 11111.1f);
    GroovyObjectUtil.setObjectValue(entity, "simpleEntities[0].fieldString", false);
    GroovyObjectUtil.setObjectValue(entity, "fieldSimpleBoolean", true);
    GroovyObjectUtil.setObjectValue(entity, "fieldBoolean", true);
    GroovyObjectUtil.setObjectValue(entity, "fieldSimpleBoolean", 0);
    GroovyObjectUtil.setObjectValue(entity, "fieldBoolean", 0);
    ObjectTreeUtil.printObject(entity);
    String fieldString = GroovyObjectUtil.getObjectValue(entity, "simpleEntities[0].fieldString");
    logger.info("fieldString: " + fieldString);

    // remove element

    int index = 0;
    // firstly will added list to null field - it creates new array and adds element
    GroovyObjectUtil.removeListElement(entity, "simpleEntities", index);
    ObjectTreeUtil.printObject(entity);

    index = 0;
    GroovyObjectUtil.removeListElement(entity, "simpleEntities", index);
    ObjectTreeUtil.printObject(entity);

    index = 0;
    GroovyObjectUtil.removeListElement(entity, "simpleEntities", index);
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.setObjectNullValue(entity, "simpleEntities");
    ObjectTreeUtil.printObject(entity);

    index = 0;
    GroovyObjectUtil.removeListElement(entity, "simpleEntities", index);
    ObjectTreeUtil.printObject(entity);
  }

  @Test
  public void testMap() {
    SimpleEntity entity = new SimpleEntity();
    // Map<String, Object> simpleMap;
    // entity.setSimpleMap(simpleMap);

    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.putMapElement(entity, "simpleMap", "keyA", "valueA");
    GroovyObjectUtil.putMapElement(entity, "simpleMap", "keyB", "valueB");
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.putMapElement(entity, "simpleEntityMap", "simpleEntityA", new SimpleEntity());
    GroovyObjectUtil.setObjectValue(entity, "simpleEntityMap.simpleEntityA.fieldString", "#################");
    ObjectTreeUtil.printObject(entity);

    SimpleEntity innerEntity = new SimpleEntity();
    GroovyObjectUtil.putMapElement(entity, "simpleMap", "entity", innerEntity);
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.setObjectValue(entity, "simpleMap.entity.fieldString", "$$$$$$$$$$$$$$$$$$");
    ObjectTreeUtil.printObject(entity);
  }

  @Test
  public void testNUllPrimitives() throws NoSuchFieldException, SecurityException {
    SimpleEntity entity = new SimpleEntity();

    GroovyObjectUtil.setObjectValue(entity, "fieldPrimitiveLong", 12312313123123L);
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.setObjectValue(entity, "fieldPrimitiveLong", null);
    ObjectTreeUtil.printObject(entity);

    entity.setFieldLong(123L);
    GroovyObjectUtil.setObjectValue(entity, "fieldLong", null);
    ObjectTreeUtil.printObject(entity);
  }

  @Test
  public void testNUllMaps() throws NoSuchFieldException, SecurityException {
    SimpleEntity entity = new SimpleEntity();

    GroovyObjectUtil.putMapElement(entity, "simpleMap", "keyA", "valueA");
    GroovyObjectUtil.putMapElement(entity, "simpleMap", "keyB", "valueB");
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.setObjectValue(entity, "simpleMap.keyA", "@@@@@@@@@@@@@@@@@@");
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.setObjectValue(entity, "simpleMap.keyA", null);
    ObjectTreeUtil.printObject(entity);

    SimpleEntity element1 = new SimpleEntity();
    element1.setFieldString("**************");
    GroovyObjectUtil.addListElement(entity, "simpleEntities", element1);

    GroovyObjectUtil.setObjectValue(entity, "simpleEntities[0].fieldString", null);
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.setObjectValue(entity, "simpleEntities[0]", null);
    ObjectTreeUtil.printObject(entity);
  }

  @Test
  public void testPrimitives() throws NoSuchFieldException, SecurityException {

    SimpleEntity entity = new SimpleEntity();

    List<List<List<String>>> list = new ArrayList<List<List<String>>>();
    List list1 = new LinkedList<List>();
    List list2 = new ArrayList();
    list2.add("item1");
    list1.add(list2);
    list.add(list1);
    entity.setFieldList2(list);
    //GroovyObjectUtil.addListElement(entity, "fieldList2", list);
    ObjectTreeUtil.printObject(entity);

    Class<?> type = GroovyObjectUtil.getType(123);
    logger.info("type: {}", type);



    type = GroovyObjectUtil.getDeclaredType(entity, "fieldList2");
    logger.info("type: {}", type);

    // GroovyObjectUtil.addListElement(entity, "fieldList", "element1");

    type = GroovyObjectUtil.getDeclaredType(entity, "fieldList2[0]");
    logger.info("type: {}", type);

    type = GroovyObjectUtil.getDeclaredType(entity, "fieldList2[0][0]");
    logger.info("type: {}", type);

    type = GroovyObjectUtil.getDeclaredType(entity, "fieldList2[0][0][0]");
    logger.info("type: {}", type);

    if (type != null) {
      return;
    }

    type = GroovyObjectUtil.getDeclaredType(entity, "");
    logger.info("type: {}", type);

    type = GroovyObjectUtil.getDeclaredType(entity, "fieldLong");
    logger.info("type: {}", type);
    Object defaultValue = DefaultValues.getForClass(type);
    logger.info("defaultValue: {}", defaultValue);

    type = GroovyObjectUtil.getDeclaredType(entity, "fieldPrimitiveLong");
    logger.info("type: {}", type);
    defaultValue = DefaultValues.getForClass(type);
    logger.info("defaultValue: {}", defaultValue);
  }
}
