package me.objecteditor.groovy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.objecteditor.test.SomeInterfase;

public class SimpleEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  private String fieldString;
  private String nullFieldString = null;
  private String emptyFieldString = "";
  private long fieldPrimitiveLong;
  private Long fieldLong;
  private boolean fieldSimpleBoolean;
  private Boolean fieldBoolean;
  private SimpleEntity child;
  private List<String> fieldList = new ArrayList<String>();
  private List<List<List<String>>> fieldList2;
  private Map<String, Object> simpleMap;
  private Map<String, SimpleEntity> simpleEntityMap;

  private SomeInterfase someInterfacedField;

  private List<SimpleEntity> simpleEntities = null;

  public SimpleEntity() {
    fieldString = UUID.randomUUID().toString();
  }

  public String getFieldString() {
    return fieldString;
  }

  public void addFieldListElement(String val) {
    fieldList.add(val);
  }

  public List<String> getFieldList() {
    return fieldList;
  }

  public void setFieldListToNull() {
    fieldList = null;
  }

  public void setFieldString(String fieldString) {
    this.fieldString = fieldString;
  }

  public List<List<List<String>>> getFieldList2() {
    return fieldList2;
  };

  public void setFieldList2(List<List<List<String>>> fieldList2) {
    this.fieldList2 = fieldList2;
  }

  public long getFieldPrimitiveLong() {
    return fieldPrimitiveLong;
  }

  public void setFieldPrimitiveLong(long fieldPrimitiveLong) {
    this.fieldPrimitiveLong = fieldPrimitiveLong;
  }

  public Long getFieldLong() {
    return fieldLong;
  }

  public void setFieldLong(Long fieldLong) {
    this.fieldLong = fieldLong;
  }

  public Boolean getFieldBoolean() {
    return fieldBoolean;
  }

  public void setFieldBoolean(Boolean fieldBoolean) {
    this.fieldBoolean = fieldBoolean;
  }

  public boolean getFieldSimpleBoolean() {
    return fieldSimpleBoolean;
  }

  public void setFieldSimpleBoolean(boolean fieldSimpleBoolean) {
    this.fieldSimpleBoolean = fieldSimpleBoolean;
  }

  public void setChild(SimpleEntity child) {
    this.child = child;
  }

  public SimpleEntity getChild() {
    return child;
  }

  public void setSimpleEntities(List<SimpleEntity> simpleEntities) {
    this.simpleEntities = simpleEntities;
  }

  public List<SimpleEntity> getSimpleEntities() {
//    if (simpleEntities == null) {
//      simpleEntities = new ArrayList<SimpleEntity>();
//    }
    return simpleEntities;
  }

  public Map<String, Object> getSimpleMap() {
    return simpleMap;
  }

  public void setSimpleMap(Map<String, Object> simpleMap) {
    this.simpleMap = simpleMap;
  }

  public Map<String, SimpleEntity> getSimpleEntityMap() {
    return simpleEntityMap;
  }

  public void setSimpleEntityMap(Map<String, SimpleEntity> simpleEntityMap) {
    this.simpleEntityMap = simpleEntityMap;
  }

  public SomeInterfase getSomeInterfacedField() {
    return someInterfacedField;
  }

  public void setSomeInterfacedField(SomeInterfase someInterfacedField) {
    this.someInterfacedField = someInterfacedField;
  }

  @Override
  public String toString() {
    return "SimpleEntity [fieldString=" + fieldString + ", nullFieldString=" + nullFieldString + ", emptyFieldString="
        + emptyFieldString + ", fieldLong=" + fieldLong + ", child=" + child + ", fieldList=" + fieldList + "]";
  }

}
