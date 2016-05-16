package me.objecteditor.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectTreeUtil2 {

  private static Logger logger = LoggerFactory.getLogger(ObjectTreeUtil2.class);

  private static final String[] PARSE_PACKAGES = {"ru.yota", "me.objecteditor"};
  private static final String[] SYSTEM_PACKAGES = {"java.lang", "java.util"};

  private static final int MAX_LEVEL = 25;

  private static final String HTML_TEMPLATE_NAME = "<span class='field-name'>%s</span>";
  private static final String HTML_TEMPLATE_INDEX = "<span class='field-index'>[%s]</span>";
  private static final String HTML_TEMPLATE_SET_INDEX = "<span class='field-index'>*</span>";
  private static final String HTML_TEMPLATE_VALUE = " = <span class='field-value'>%s</span>";
  private static final String HTML_TEMPLATE_SET_VALUE = " <span class='field-value'>%s</span>";
  private static final String HTML_TEMPLATE_CLASSNAME = " <span class='field-class'>(%s)</span>";
  private static final String HTML_TEMPLATE_GENERIC = " <span class='field-class-generic'>%s</span>";
  private static final String HTML_TEMPLATE_COUNT = " <span class='field-count'>count=%s</span>";
  private static final String HTML_TEMPLATE_WRAPPER = "<span class='field-wrapper'>%s</span>";

  private static final String TEXT_TEMPLATE_NAME = "%s";
  private static final String TEXT_TEMPLATE_INDEX = "[%s]";
  private static final String TEXT_TEMPLATE_SET_INDEX = "*";
  private static final String TEXT_TEMPLATE_VALUE = " = %s";
  private static final String TEXT_TEMPLATE_SET_VALUE = " %s";
  private static final String TEXT_TEMPLATE_CLASSNAME = " (%s)";
  private static final String TEXT_TEMPLATE_GENERIC = " %s";
  private static final String TEXT_TEMPLATE_COUNT = " count=%s";

  private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

  private static ObjectMapper objectMapper = new ObjectMapper();
  private static SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

  public static ObjectNode getObjectTreeview(Object obj) {
    ObjectTreeElement tree = getObjectTree(obj.getClass(), null, null, null, obj, false, 0);
    return buildJsonTreeview(tree);
  }

  public static ObjectNode getFullObjectTreeview(Object obj) {
    ObjectTreeElement tree = getObjectTree(obj.getClass(), null, null, null, obj, true, 0);
    return buildJsonTreeview(tree);
  }

  // recursive
  private static ObjectTreeElement getObjectTree(Class<?> clazz, Type[] genericTypes, String name, Object key, Object value,
      boolean addNullValues, int level) {
    ObjectTreeElement currentElement = new ObjectTreeElement(clazz, genericTypes, name, key, value);

    // check is element value is null
    if (value == null) {
      if (clazz != null) {
        currentElement.setList(clazz.isAssignableFrom(List.class));
        currentElement.setMap(clazz.isAssignableFrom(Map.class));
        currentElement.setSet(clazz.isAssignableFrom(Set.class));
        currentElement.setArray(clazz.isArray());
        if (clazz.isEnum()) {
          currentElement.setEnum(true);
          currentElement.setEnumValues(clazz.getEnumConstants());
        }
      }
      return currentElement;
    }

    // check if we got maximum level of nesting (prevent infinite recursion)
    if (level >= MAX_LEVEL) {
      logger.warn("Get maximum nesting level: {}, current element: {}={}", level, name, value);
      return currentElement;
    }

    // if value is not null - set implementation class (instead of declared)
    if (clazz == null || !clazz.isPrimitive()) {
      clazz = value.getClass();
      currentElement.setClazz(clazz);
    }

    currentElement.setPrimitive(clazz.isPrimitive());

    // check whether element is Map
    if (value instanceof Map) {
      currentElement.setMap(true);

      // set count param
      Map<?, ?> map = (Map<?, ?>) value;
      currentElement.setCount(map.size());

      // get element's class (which was declared)
      Class<?> entryClass = null;
      Type[] entryGenericTypes = null;
      if (genericTypes != null && genericTypes.length > 0) {
        entryClass = getClassFromType(genericTypes[1]);
        entryGenericTypes = getGenericTypes(genericTypes[1]);
      }

      // recursively add map elements with key instead of name
      for (Entry<?, ?> entry : map.entrySet()) {
        // add entry to tree
        ObjectTreeElement child = getObjectTree(entryClass, entryGenericTypes, null, entry.getKey(), entry.getValue(),
            addNullValues, level + 1);
        child.setItemOf(clazz);
        currentElement.addChild(child);
      }
    }


    // check whether element is List
    if (value instanceof List) {
      currentElement.setList(true);

      // set count param
      List<?> list = (List<?>) value;
      currentElement.setCount(list.size());

      // get element's class (which was declared)
      Class<?> listElementClass = null;
      Type[] listElementGenericTypes = null;
      if (genericTypes != null && genericTypes.length > 0) {
        listElementClass = getClassFromType(genericTypes[0]);
        listElementGenericTypes = getGenericTypes(genericTypes[0]);
      }

      // recursively add list elements with index(as key) instead of name
      for (int i = 0; i < list.size(); i++) {
        ObjectTreeElement child = getObjectTree(listElementClass, listElementGenericTypes, null, i, list.get(i),
            addNullValues, level + 1);
        child.setItemOf(clazz);
        currentElement.addChild(child);
      }
    }


    // check whether element is Set
    if (value instanceof Set) {
      currentElement.setSet(true);

      // set count param
      Set<?> set = (Set<?>) value;
      currentElement.setCount(set.size());

      // get element's class (which was declared)
      Class<?> elementsClass = null;
      Type[] elementsGenericTypes = null;
      if (genericTypes != null && genericTypes.length > 0) {
        elementsClass = getClassFromType(genericTypes[0]);
        elementsGenericTypes = getGenericTypes(genericTypes[0]);
      }

      // recursively add set elements with index(as key) instead of name
      for (Object element : set) {
        ObjectTreeElement child = getObjectTree(elementsClass, elementsGenericTypes, null, null, element, addNullValues,
            level + 1);
        child.setItemOf(clazz);
        currentElement.addChild(child);
      }
    }

    // check whether element is Array
    if (value.getClass().isArray()) {
      currentElement.setArray(true);

      // set count param
      // Array set = (Array) value;
      currentElement.setCount(Array.getLength(value));

      // get element's class (which was declared)
      Class<?> elementsClass = null;
      Type[] elementsGenericTypes = null;
      elementsClass = value.getClass().getComponentType();

      // recursively add set elements with index(as key) instead of name
      int arrayCount = Array.getLength(value);
      for (int i = 0; i < arrayCount; i++) {
        Object element = Array.get(value, i);
        ObjectTreeElement child = getObjectTree(elementsClass, elementsGenericTypes, null, i, element, addNullValues,
            level + 1);
        child.setItemOf(Array.class);// TODO: item of Array.class?
        currentElement.addChild(child);
      }
    }


    // check whether element is Enum
    if (clazz.isEnum()) {
      currentElement.setEnum(true);
      currentElement.setEnumValues(clazz.getEnumConstants());
    }


    // check whether element is business object
    if (checkClassName(clazz.getName()) && !clazz.isEnum()) {
      Field[] fields = getAllFields(clazz);

      for (Field field : fields) {
        // serialUID is not needed - skip it
        if ("serialVersionUID".equals(field.getName())){
          continue;
        }
        field.setAccessible(true);

        try {
          // check whether field value is null
          if (field.get(value) == null && !addNullValues) {
            continue;
          }

          // get field generic types if exists
          Type[] fieldGenericTypes = getGenericTypes(field.getGenericType());

          // recursively add element
          currentElement.addChild(getObjectTree(field.getType(), fieldGenericTypes, field.getName(), null, field.get(value),
              addNullValues, level+1));

        } catch (IllegalArgumentException e) {
          logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
          logger.error(e.getMessage());
        }
      }
    }


    // if element type is primitive - just return it
    return currentElement;
  }

  private static boolean checkClassName(String name) {
    for (String packageName : PARSE_PACKAGES) {
      if (name.startsWith(packageName)) {
        return true;
      }
    }
    return false;
  }

  private static Field[] getAllFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<Field>();
    fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    if (clazz.getSuperclass() != null) {
        fields.addAll(Arrays.asList(getAllFields(clazz.getSuperclass())));
    }
    return fields.toArray(new Field[] {});
  }

  private static Type[] getGenericTypes(Type genericType) {
    Type[] fieldGenericTypes = null;
    if (genericType instanceof ParameterizedType) {
      fieldGenericTypes = ((ParameterizedType)genericType).getActualTypeArguments();
    }
    return fieldGenericTypes;
  }

  private static Class<?> getClassFromType(Type genericType) {
    Class<?> resultClass = null;
    try {
      if (genericType instanceof Class) {
        resultClass = (Class<?>) genericType;
      } else if (genericType instanceof ParameterizedType) {
        resultClass = (Class<?>) ((ParameterizedType)genericType).getRawType();
      } else {
        resultClass = Class.forName(genericType.getTypeName());
      }
    } catch (ClassNotFoundException e) {
      logger.error("Can't find generic class!", e);
    }
    return resultClass;
  }

  public static ObjectNode buildJsonTreeview(ObjectTreeElement tree) {
    Map<String, Object> treeMap = buildMapTreeview(tree);
    return objectMapper.valueToTree(treeMap);
  }

  public static Map<String, Object> buildMapTreeview(ObjectTreeElement tree) {
    return buildMapTreeview(tree, "");
  }

  // recursive
  private static Map<String, Object> buildMapTreeview(ObjectTreeElement tree, String path) {
    Map<String, Object> map = new HashMap<String, Object>();

    // setup common parameters
    map.put("isNull", tree.getValue() == null);
    map.put("isMap", tree.isMap());
    map.put("isList", tree.isList());
    map.put("isSet", tree.isSet());
    map.put("isEnum", tree.isEnum());
    map.put("path", path);
    map.put("className", tree.getClazzName());
    map.put("value", tree.getValue());
    map.put("key", tree.getKey());

    // set treeview options
    map.put("selectable", false);

    // add enum values
    if (tree.getEnumValues() != null) {
      map.put("enumValues", tree.getEnumValues());
    }

    // make text
    map.put("text", makeTreeviewText(tree));

    // make item icon
    if (tree.getItemOf() != null) {
      if (tree.isItemOf(Set.class))
        map.put("icon", "glyphicon glyphicon-menu-right");
    }

    // append childs
    if (tree.getChilds() != null) {
      List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
      for (ObjectTreeElement child : tree.getChilds()) {
        String childPath = String.valueOf(path);
        if (tree.isList()) {
          childPath += "[" + child.getKey() + "]";
        } else if (tree.isMap()) {
          childPath += "." + child.getKey();
        } else {
          if (childPath.length() != 0) {
            childPath += ".";
          }
          childPath += child.getName();
        }

        childList.add(buildMapTreeview(child, childPath));
      }
      map.put("nodes", childList);
    }

    return map;
  }

  private static String makeTreeviewText(ObjectTreeElement tree) {
    String result = "";

    if (tree.getKey() != null || tree.getItemOf() != null) {
      if (tree.isItemOf(Set.class)) {
        result += String.format(HTML_TEMPLATE_SET_INDEX, tree.getKey());
      } else {
        result += String.format(HTML_TEMPLATE_INDEX, tree.getKey());
      }
    } else if (tree.getName() != null) {
      result += String.format(HTML_TEMPLATE_NAME, tree.getName());
    }

    if (tree.getChilds() == null) {
      if (tree.isItemOf(Set.class)) {
        result += String.format(HTML_TEMPLATE_SET_VALUE, tree.getValue());
      } else {
        if (tree.getValue() instanceof Date) {
          result += String.format(HTML_TEMPLATE_VALUE, dateFormatter.format(tree.getValue()));
        } else if (tree.isItemOf(Set.class)) {
          result += String.format(HTML_TEMPLATE_SET_VALUE, tree.getValue());
        } else if (tree.isArray()){
          result += String.format(HTML_TEMPLATE_VALUE, Arrays.toString((Object[])tree.getValue()));
        } else {
          result += String.format(HTML_TEMPLATE_VALUE, tree.getValue());
        }
      }
    }

    String className = tree.getClazzName();
    if (tree.getGenericTypes() != null) {
      String genericNames = "<" + StringUtils.join(tree.getGenericTypeNames(), ", ") + ">";
      genericNames = genericNames.replace("<", "&lt;");
      genericNames = genericNames.replace(">", "&gt;");
      className += String.format(HTML_TEMPLATE_GENERIC, genericNames);
    }
    className = removeSystemPackage(className);
    result += String.format(HTML_TEMPLATE_CLASSNAME, className);

    if (tree.getValue() != null && (tree.isList() || tree.isMap() || tree.isSet() || tree.isArray())) {
      result += String.format(HTML_TEMPLATE_COUNT, tree.getCount());
    }

    result = String.format(HTML_TEMPLATE_WRAPPER, result);
    return result;
  }

  private static String removeSystemPackage(String className) {
    for (String sysPackage : SYSTEM_PACKAGES) {
      if (className.contains(sysPackage)) {
        className = className.replace(sysPackage + ".", "");
      }
    }
    return className;
  }

  public static void printObject(Object obj) {
    printObject("object tree:", obj);
  }

  public static void printObject(String message, Object obj) {
    ObjectTreeElement tree = getObjectTree(obj.getClass(), null, null, null, obj, true, 0);

    String resStr = buildTextTreeview(tree, 0);
    logger.info("{}\n{}", message, resStr);
  }

  // recursive
  private static String buildTextTreeview(ObjectTreeElement tree, int level) {
    String result = "";
    result += (level > 0) ? String.format("%" + level * 4 + "s", "") : "";

    if (tree.getKey() != null || tree.getItemOf() != null) {
      if (tree.isItemOf(Set.class)) {
        result += String.format(TEXT_TEMPLATE_SET_INDEX, tree.getKey());
      } else {
        result += String.format(TEXT_TEMPLATE_INDEX, tree.getKey());
      }
    } else if (tree.getName() != null) {
      result += String.format(TEXT_TEMPLATE_NAME, tree.getName());
    }

    if (tree.getChilds() == null) {
      if (tree.getValue() instanceof Date) {
        result += String.format(TEXT_TEMPLATE_VALUE, dateFormatter.format(tree.getValue()));
      } else if (tree.isItemOf(Set.class)) {
        result += String.format(TEXT_TEMPLATE_SET_VALUE, tree.getValue());
      } else if (tree.isArray()){
        result += String.format(TEXT_TEMPLATE_VALUE, Arrays.toString((Object[])tree.getValue()));
      } else {
        result += String.format(TEXT_TEMPLATE_VALUE, tree.getValue());
      }
    }

    String className = tree.getClazzName();
    if (tree.getGenericTypes() != null) {
      String genericNames = "<" + StringUtils.join(tree.getGenericTypeNames(), ", ") + ">";
      className += String.format(TEXT_TEMPLATE_GENERIC, genericNames);
    }
    className = removeSystemPackage(className);
    result += String.format(TEXT_TEMPLATE_CLASSNAME, className);

    if (tree.getValue() != null && (tree.isList() || tree.isMap() || tree.isSet() || tree.isArray())) {
      result += String.format(TEXT_TEMPLATE_COUNT, tree.getCount());
    }

    result += "\n";

    if (tree.getChilds() != null) {
      StringBuilder sb = new StringBuilder();
      for (ObjectTreeElement child : tree.getChilds()) {
        sb.append(buildTextTreeview(child, level + 1));
      }
      result += sb.toString();
    }

    return result;
  }

  public static String getParsePackagesJsonString() {
    try {
      return objectMapper.writeValueAsString(Arrays.asList(PARSE_PACKAGES));
    } catch (JsonProcessingException e) {
      return "";
    }
  }
}

