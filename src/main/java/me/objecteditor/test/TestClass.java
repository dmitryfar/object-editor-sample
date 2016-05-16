package me.objecteditor.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestClass extends AbstractTestEntity {

  private int[] intVars;
  private Set<Integer> intSet;
  private int intVar;
  private Integer integerVar;
  private boolean boolVar;
  private Boolean booleanVar;
  private AbstractTestEntity self;
  private String emptyStringVar;
  private String stringVar;
  private Date dateVar;
  private TestEnum enumVar;
  private List<String> stringList;
  private List<String> emptyStringList;
  private List<List<List<List<String>>>> uglyList;
  private Map<String, Object> map;

  public TestClass() {
    super();
    this.intVar = 111;
    this.integerVar = 123;
    this.boolVar = false;
    this.booleanVar = false;
    this.emptyStringVar = null;
    this.stringVar = "s\"\"t''rin\"g valu'e";
    this.dateVar = new Date();
    this.enumVar = TestEnum.ENUM_VALUE1;
//    this.self = this;

    this.intSet = new HashSet<Integer>();
    this.intSet.add(22222);
    this.intSet.add(33333);

    this.emptyStringList = new ArrayList<String>();
    this.stringList = new ArrayList<String>();
    stringList.add("string1");
    stringList.add("string2");
    stringList.add("string3");

    this.uglyList = new ArrayList<List<List<List<String>>>>();
    List<List<List<String>>> list1 = new ArrayList<List<List<String>>>();
    List<List<String>> list2 = new ArrayList<List<String>>();
    list2.add(stringList);
    list2.add(null);
    list1.add(list2);
    this.uglyList.add(list1);
    this.uglyList.add(null);


    this.map = new HashMap<String, Object>();
    map.put("key1", false);
    map.put("key2", "map's string value");
    List<String> newList = new ArrayList<String>();
    newList.add("vvvvvvvvvalue");
    map.put("key3", newList);
//    map.put("key_xxx", this);
  }

  public int getIntVar() {
    return intVar;
  }

  public void setIntVar(int intVar) {
    this.intVar = intVar;
  }

  public Integer getIntegerVar() {
    return integerVar;
  }

  public void setIntegerVar(Integer integerVar) {
    this.integerVar = integerVar;
  }

  public Boolean getBooleanVar() {
    return booleanVar;
  }

  public void setBooleanVar(Boolean booleanVar) {
    this.booleanVar = booleanVar;
  }

  public String getEmptyStringVar() {
    return emptyStringVar;
  }

  public void setEmptyStringVar(String emptyStringVar) {
    this.emptyStringVar = emptyStringVar;
  }

  public String getStringVar() {
    return stringVar;
  }

  public void setStringVar(String stringVar) {
    this.stringVar = stringVar;
  }

  public List<String> getStringList() {
    return stringList;
  }

  public void setStringList(List<String> stringList) {
    this.stringList = stringList;
  }

  public List<String> getEmptyStringList() {
    return emptyStringList;
  }

  public void setEmptyStringList(List<String> emptyStringList) {
    this.emptyStringList = emptyStringList;
  }

  public Map<String, Object> getMap() {
    return map;
  }

  public void setMap(Map<String, Object> map) {
    this.map = map;
  }

  public int[] getIntVars() {
    return intVars;
  }

  public void setIntVars(int[] intVars) {
    this.intVars = intVars;
  }

  public Set<Integer> getIntSet() {
    return intSet;
  }

  public void setIntSet(Set<Integer> intSet) {
    this.intSet = intSet;
  }

  public boolean isBoolVar() {
    return boolVar;
  }

  public void setBoolVar(boolean boolVar) {
    this.boolVar = boolVar;
  }

  public AbstractTestEntity getSelf() {
    return self;
  }

  public void setSelf(AbstractTestEntity self) {
    this.self = self;
  }

  public Date getDateVar() {
    return dateVar;
  }

  public void setDateVar(Date dateVar) {
    this.dateVar = dateVar;
  }

  public TestEnum getEnumVar() {
    return enumVar;
  }

  public void setEnumVar(TestEnum enumVar) {
    this.enumVar = enumVar;
  }

  public List<List<List<List<String>>>> getUglyList() {
    return uglyList;
  }

  public void setUglyList(List<List<List<List<String>>>> uglyList) {
    this.uglyList = uglyList;
  }



  public enum TestEnum {
    ENUM_VALUE1,
    ENUM_VALUE2,
    ENUM_VALUE33
  }

}
