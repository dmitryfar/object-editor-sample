package me.objecteditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import me.objecteditor.test.CoolTestEntity;
import me.objecteditor.test.TestArrayEntity;
import me.objecteditor.test.TestClass;
import me.objecteditor.test.TestListEntity;
import me.objecteditor.test.TestSetEntity;
import me.objecteditor.util.ObjectTreeUtil;
import me.objecteditor.util.ObjectTreeUtil2;

public class ObjectTreeTest {
  private static final Logger logger = LoggerFactory.getLogger(ObjectTreeTest.class);

  @Test
  public void testObjects() {
    CoolTestEntity coolTestEntity = new CoolTestEntity();

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("keyA", "ValueA");
    map.put("keyB", new ArrayList());
    List<String> list = new ArrayList<String>();
    list.add("ItemA");
    list.add("ItemB");
    map.put("keyC", list);
    coolTestEntity.setMap(map);
    ObjectTreeUtil.printObject(map);
  }


  @Test
  public void testReflection() {

  }

  @Test
  public void testObjectTree() {
    TestClass testObject = new TestClass();

    JSONObject json = ObjectTreeUtil.getFullObjectTree(testObject);

    logger.info(json.toString());

    ObjectNode result = ObjectTreeUtil2.getFullObjectTreeview(testObject);

    logger.info(result.toString());

    ObjectTreeUtil2.printObject(testObject);
  }

  @Test
  public void testSetTree() {
    Set<String> set = new HashSet<String>();
    Set<Long> setLongs = new HashSet<Long>();
    Set<Long> emptySetLongs = new HashSet<Long>();
    Set<String> emptySet = new HashSet<String>();
    set.add("abc");
    set.add("cde");
    setLongs.add(111L);
    setLongs.add(222L);

    List<String> list = new ArrayList<String>();
    list.add("abc");
    list.add("cde");

    TestSetEntity testSetEntity = new TestSetEntity();
    ObjectTreeUtil2.printObject(testSetEntity);
    testSetEntity.setSet(emptySet);
    testSetEntity.setSetLongs(emptySetLongs);
    ObjectTreeUtil2.printObject(testSetEntity);
    testSetEntity.setSet(set);
    testSetEntity.setSetLongs(setLongs);
    ObjectTreeUtil2.printObject(testSetEntity);

    ObjectTreeUtil2.printObject(set);
    ObjectTreeUtil2.printObject(list);

    ObjectNode treeview = ObjectTreeUtil2.getObjectTreeview(testSetEntity);
    logger.info("treeview: {}", treeview);
  }

  @Test
  public void testListTree() {
    TestListEntity testListEntity = new TestListEntity();
    List list = new ArrayList();
    logger.info("list: {}" + list);
    testListEntity.setList(list );
    ObjectTreeUtil2.printObject(testListEntity);
  }

  @Test
  public void testArrayTree() {
    String[] array = new String[]{};
    TestClass[] arrayTC = new TestClass[]{};

    TestArrayEntity testArrayEntity = new TestArrayEntity();
    ObjectTreeUtil2.printObject(testArrayEntity);

    Long[] arrayLongs = new Long[]{};
    testArrayEntity.setArray(arrayLongs );
    ObjectTreeUtil2.printObject(testArrayEntity);

    arrayLongs = ArrayUtils.add(arrayLongs, 123L);
    testArrayEntity.setArray(arrayLongs );
    ObjectTreeUtil2.printObject(testArrayEntity);

    logger.info("is array: {}, {}", testArrayEntity.getArray().getClass().isArray(), (arrayTC.getClass().getComponentType()));
    logger.info("is array: {}", array.getClass().isArray());
    logger.info("is array: {}", arrayTC.getClass().isArray());
  }

}
