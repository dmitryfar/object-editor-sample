package me.objecteditor.groovy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.objecteditor.test.CoolTestEntity;
import me.objecteditor.test.OtherTestClass;
import me.objecteditor.test.TestArrayEntity;
import me.objecteditor.test.TestArrayEntityWithObject;
import me.objecteditor.test.TestSetEntity;
import me.objecteditor.util.DefaultValues;
import me.objecteditor.util.ObjectTreeUtil;
import me.objecteditor.util.ObjectTreeUtil2;

public class GroovyTest {

  private static final Logger logger = LoggerFactory.getLogger(GroovyTest.class);

  @Test
  public void testParse() {
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("abc", "Hello");

    String filename = "scripts/script1.groovy";
    Object result = ScriptEngineUtil.evaluateFile(filename, ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE, bindings,
        false);
    logger.info("bindings: {}", bindings);
    logger.info("result: {}", result);

    String filename2 = "scripts/script2.groovy";
    Object result2 = ScriptEngineUtil.evaluateFile(filename2, ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE, bindings,
        true);
    logger.info("bindings: {}", bindings);
    logger.info("result2: {}", result2);
  }

  @Test
  public void testWithObject() {
    SimpleEntity child = new SimpleEntity();

    SimpleEntity entity = new SimpleEntity();
    entity.setChild(child);

    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("entity", entity);
    bindings.put("someStringValue", "some string value");

    ScriptEngineUtil.evaluateFile("scripts/entity-test.groovy", ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE, bindings,
        false);
    logger.info("bindings: {}", bindings);
  }

  @Test
  public void testCreateChildObject() {
    SimpleEntity entity = new SimpleEntity();
    SimpleEntity child = new SimpleEntity();
    child.setFieldString("++++++++++++++++++++");

    GroovyObjectUtil.setObjectValue(entity, "child", child);
    ObjectTreeUtil.printObject(entity);
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
  }

  @Test
  public void testRemoveListElement() {
    SimpleEntity entity = new SimpleEntity();
    List<SimpleEntity> simpleEntities = new ArrayList<SimpleEntity>();
    simpleEntities.add(new SimpleEntity());
    simpleEntities.add(new SimpleEntity());
    entity.setSimpleEntities(simpleEntities);
    ObjectTreeUtil.printObject(entity);

    int index = 0;
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
  public void testRemoveMapElement() {
    SimpleEntity entity = new SimpleEntity();
    Map<String, Object> simpleMap = new HashMap<String, Object>();
    simpleMap.put("KeyA", "ValueA");
    simpleMap.put("KeyB", "ValueB");
    simpleMap.put("KeyC", "ValueC");
    entity.setSimpleMap(simpleMap);


    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.removeMapElement(entity, "simpleMap", "KeyB");
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.removeMapElement(entity, "simpleMap", "KeyZ");
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

  @Test
  public void testSet() {
    Set<String> s = new HashSet<String>();
    s.add("123");
    s.add("abc");
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("s", s);
    String script = "";
    ScriptEngineUtil.evaluateFile("scripts/test-set.groovy" , ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE, bindings );
  }

  @Test
  public void testNParentField() {
    CoolTestEntity entity = new CoolTestEntity();

    entity.setBooleanVar(true);
    ObjectTreeUtil.printObject(entity);

    GroovyObjectUtil.setObjectValue(entity, "booleanVar", false);
    ObjectTreeUtil.printObject(entity);
  }

  @Test
  public void testArrays() {
    TestArrayEntity entity = new TestArrayEntity();

    Long[] arrayLongs = new Long[]{111L};
    entity.setArray(arrayLongs);
    arrayLongs = ArrayUtils.add(arrayLongs, 222L);
    arrayLongs = ArrayUtils.add(arrayLongs, 333L);
    entity.setArray(arrayLongs );

    TestSetEntity[] setEntities = new TestSetEntity[] {};
    setEntities = ArrayUtils.add(setEntities, new TestSetEntity());
    setEntities = ArrayUtils.add(setEntities, new TestSetEntity());
    entity.setTestSetEntities(setEntities);

    ObjectTreeUtil2.printObject(entity);

    // ArrayUtils.remove(arrayLongs, 1);

    GroovyObjectUtil.setObjectValue(entity, "testSetEntities[1]", null);
    ObjectTreeUtil2.printObject(entity);

    GroovyObjectUtil.removeListElement(entity, "testSetEntities", 1);
    ObjectTreeUtil2.printObject(entity);

    GroovyObjectUtil.removeListElement(entity, "testSetEntities", 0);
    ObjectTreeUtil2.printObject(entity);
  }

  @Test
  public void testSetArrayDeeeepValue() {
    TestArrayEntityWithObject entity = new TestArrayEntityWithObject();


    OtherTestClass[] otherTestClassArray = new OtherTestClass[]{};

    OtherTestClass otherTestEntity = new OtherTestClass();
    otherTestEntity.setFieldA("AaAaAaAaAa");
    OtherTestClass otherTestEntity2 = new OtherTestClass();
    otherTestClassArray = ArrayUtils.add(otherTestClassArray, otherTestEntity );
    otherTestClassArray = ArrayUtils.add(otherTestClassArray, otherTestEntity2 );

    entity.setOtherTestClassArray(otherTestClassArray);
    ObjectTreeUtil2.printObject(entity);

    GroovyObjectUtil.setObjectValue(entity, "otherTestClassArray[1].fieldB", "BbBbBbBb");
    ObjectTreeUtil2.printObject(entity);

    GroovyObjectUtil.setObjectNullValue(entity, "otherTestClassArray[1].fieldB");
    ObjectTreeUtil2.printObject(entity);

    GroovyObjectUtil.setObjectNullValue(entity, "otherTestClassArray[0]");
    ObjectTreeUtil2.printObject(entity);

    GroovyObjectUtil.removeListElement(entity, "otherTestClassArray", 1);
    ObjectTreeUtil2.printObject(entity);

    GroovyObjectUtil.setObjectNullValue(entity, "otherTestClassArray");
    ObjectTreeUtil2.printObject(entity);

    otherTestEntity2.setFieldB("BBBBBBBBB");
    GroovyObjectUtil.addListElement(entity, "otherTestClassArray", otherTestEntity2);
    ObjectTreeUtil2.printObject(entity);
  }
}
