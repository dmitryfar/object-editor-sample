package me.objecteditor.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectTreeElement {

  private Class<?> clazz;

  private String name;

  private Object key;

  private Object value;

  private int count;

  private boolean isMap;

  private boolean isList;

  private boolean isSet;

  private boolean isArray;

  private boolean isEnum;

  private Class<?> itemOf;

  private boolean isPrimitive;

  private Object[] enumValues;

  private Type[] genericTypes;

  private List<ObjectTreeElement> childs;

  public ObjectTreeElement(Class<?> clazz, Type[] genericTypes, String name, Object key, Object value) {
    this.clazz = clazz;
    this.genericTypes = genericTypes;
    this.name = name;
    this.key = key;
    this.value = value;
    this.count = 0;
    this.isList = false;
    this.isSet = false;
    this.isArray = false;
    this.isMap = false;
    this.isEnum = false;
    this.isPrimitive = false;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  public String getClazzName() {
    if (clazz != null) {
      if (clazz.isArray()) {
        return clazz.getComponentType().getName() + "[]";
      }
      return clazz.getName();
    } else {
      return "";
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getKey() {
    return key;
  }

  public void setKey(Object key) {
    this.key = key;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public boolean isMap() {
    return isMap;
  }

  public void setMap(boolean isMap) {
    this.isMap = isMap;
  }

  public boolean isList() {
    return isList;
  }

  public void setList(boolean isList) {
    this.isList = isList;
  }

  public boolean isSet() {
    return isSet;
  }

  public void setSet(boolean isSet) {
    this.isSet = isSet;
  }

  public boolean isArray() {
    return isArray;
  }

  public void setArray(boolean isArray) {
    this.isArray = isArray;
  }

  public Class<?> getItemOf() {
    return itemOf;
  }

  public void setItemOf(Class<?> itemOf) {
    this.itemOf = itemOf;
  }

  public boolean isItemOf(Class<?> clazz) {
    return itemOf != null && (clazz.isAssignableFrom(itemOf));
  }

  public boolean isPrimitive() {
    return isPrimitive;
  }

  public void setPrimitive(boolean isPrimitive) {
    this.isPrimitive = isPrimitive;
  }

  public boolean isEnum() {
    return isEnum;
  }

  public void setEnum(boolean isEnum) {
    this.isEnum = isEnum;
  }

  public Object[] getEnumValues() {
    return enumValues;
  }

  public void setEnumValues(Object[] enumValues) {
    this.enumValues = enumValues;
  }

  public Type[] getGenericTypes() {
    return genericTypes;
  }

  public List<String> getGenericTypeNames() {
    if (genericTypes == null) {
      return null;
    }
    List<String> result = new ArrayList<String>();
    for (Type type : genericTypes) {
      result.add(type.getTypeName());
    }
    return result;
  }

  public void setGenericTypes(Type[] genericTypes) {
    this.genericTypes = genericTypes;
  }

  public List<ObjectTreeElement> getChilds() {
    return childs;
  }

  public void setChilds(List<ObjectTreeElement> childs) {
    this.childs = childs;
  }

  public void addChild(ObjectTreeElement child) {
    if (this.childs == null) {
      this.childs = new ArrayList<ObjectTreeElement>();
    }
    this.childs.add(child);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("(");
    builder.append(clazz);
    if (genericTypes != null) {
      builder.append(" <");
      builder.append(Arrays.toString(genericTypes));
      builder.append(">");
    }
    builder.append(") ");
    builder.append(value);
    return builder.toString();
  }


}
