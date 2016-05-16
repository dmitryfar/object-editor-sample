package me.objecteditor.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("ObjectTreeUtil")
public class ObjectTreeUtil {
  private static Logger logger = LoggerFactory.getLogger(ObjectTreeUtil.class);

  private static final String[] PARSE_PACKAGES = {"ru.yota", "me.objecteditor"};

  public static JSONObject getObjectTree(Object obj) {
    return getObjectTree(obj, 0, false);
  }

  public static JSONObject getFullObjectTree(Object obj) {
    return getObjectTree(obj, 0, true);
  }

  private static JSONObject getObjectTree(Object obj, int level, boolean addNullValues) {
    if (level > 25) {
      throw new IllegalArgumentException("level = " + level + ", obj=" + obj);
    }
    JSONObject jsonObject = new JSONObject();


    if (obj != null) {
      if (level == 0) {
        jsonObject.put("text", obj.getClass().getName());

        if (obj instanceof List) {
          return getNodeWithChilds(obj.getClass(), "list", (List) obj, level, true);
        } else if (obj instanceof Map) {
          return getNode(obj.getClass(), "map", (Map) obj, level, true);
        }
      }
      jsonObject.put("level", level);

      Class<?> clazz = obj.getClass();

      Field[] fields = getAllFields(clazz );
      JSONArray nodes = new JSONArray();
      for (Field field : fields) {
        if ("serialVersionUID".equals(field.getName())){
          continue;
        }
        field.setAccessible(true);
        Object fieldValue = null;
        try {
          fieldValue = field.get(obj);
          if (fieldValue == null && !addNullValues) {
            continue;
          }
          Class<?> fieldClass = field.getType();
          if (fieldValue != null) {
            fieldClass = fieldValue.getClass();
          }
          JSONObject node = null;
          if (fieldValue instanceof List) {
            node = getNodeWithChilds(fieldClass, field.getName(), (List) fieldValue, level+1, addNullValues);
          } else {
            node = getNode(fieldClass, field.getName(), fieldValue, level+1, addNullValues);
          }
          nodes.put(node);
        } catch (IllegalArgumentException e) {
          logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
          logger.error(e.getMessage());
        }
      }
      jsonObject.put("nodes", nodes);
    }
    return jsonObject;
  }

  private static Field[] getAllFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<Field>();
    fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    if (clazz.getSuperclass() != null) {
        fields.addAll(Arrays.asList(getAllFields(clazz.getSuperclass())));
    }
    return fields.toArray(new Field[] {});
  }

  private static JSONObject getNodeWithChilds(Class<?> fieldType, String fieldName, List fieldValue, int level, boolean addNullValues) {
    JSONObject node = new JSONObject();
    JSONObject textNode = new JSONObject();

    String name = "<span class='field-name'>" + fieldName + "</span>";
    textNode.put("name", fieldName);
    String className = "<span class='field-class'>" + fieldType.getName() + "</span>";
    textNode.put("className", fieldType.getName());

    Integer count = (fieldValue != null) ? ((List<?>)fieldValue).size() : null;
    textNode.put("count", count);
    String text = name + " (" + className + ")" + ((count != null) ? " count=" +count : "");

    node.put("textNode", textNode);
    node.put("text", text);
    node.put("isList", true);
    JSONArray nodeList = new JSONArray();
    int i = 0;
    for (Object valueItem : (List<?>)fieldValue) {
      JSONObject item = null;
      if (valueItem != null) {
        JSONObject itemTextNode = new JSONObject();
        // String itemName = "<span class='field-name'>" + fieldName + "</span>";
        String itemClassName = "<span class='field-class'>" + valueItem.getClass().getName() + "</span>";
        itemTextNode.put("itemClassName", valueItem.getClass().getName());
        itemTextNode.put("itemIndex", i);

        String itemText = "[" + i + "] (" + itemClassName + ")";
        if (valueItem instanceof List) {
          item = getObjectTree(valueItem, level+1, addNullValues);
          item.put("itemTextNode", itemTextNode);
          item.put("text", itemText);
          item.put("level", level + 1);
        } else if (checkClassName(valueItem.getClass().getName())) {
          item = getObjectTree(valueItem, level+1, addNullValues);
          item.put("itemTextNode", itemTextNode);
          item.put("text", itemText);
          item.put("level", level + 1);
        } else {

          if (valueItem instanceof String) {
            itemText = "[" + i + "] = \"" + valueItem + "\" (" + itemClassName + ")";
          } else {
            itemText = "[" + i + "] = " + valueItem + " (" + itemClassName + ")";
          }
          item = new JSONObject();
          itemTextNode.put("itemValue", valueItem);
          item.put("itemTextNode", itemTextNode);
          item.put("text", itemText);
          item.put("level", level + 1);
        }
        nodeList.put(item);
      } else if (addNullValues){
        nodeList.put(item);
      }
      i++;
    }
    node.put("nodes", nodeList);
    node.put("level", level);
    return node;
  }

  @SuppressWarnings("rawtypes")
  private static JSONObject getMapNodeWithChilds(Class<?> fieldType, String fieldName, Map fieldValue, int level, boolean addNullValues) {
    JSONObject node = new JSONObject();
    JSONObject textNode = new JSONObject();

    String name = "<span class='field-name'>" + fieldName + "</span>";
    textNode.put("name", fieldName);
    String className = "<span class='field-class'>" + fieldType.getName() + "</span>";
    textNode.put("className", fieldType.getName());

    Integer count = (fieldValue != null) ? fieldValue.size() : null;
    textNode.put("count", count);
    String text = name + " (" + className + ")" + ((count != null) ? " count=" +count : "");

    node.put("textNode", textNode);
    node.put("text", text);
    node.put("isMap", true);
    JSONArray nodeList = new JSONArray();
    for (Object key : fieldValue.keySet()) {
      Object valueItem = fieldValue.get(key);
      JSONObject item = null;
      if (valueItem != null) {
        JSONObject itemTextNode = new JSONObject();
        String itemKey = "<span class='field-name'>" + key + "</span>";
        String itemClassName = "<span class='field-class'>" + valueItem.getClass().getName() + "</span>";
        itemTextNode.put("itemClassName", valueItem.getClass().getName());
        itemTextNode.put("itemIndex", key);

        String itemText = "[" + key + "] (" + itemClassName + ")";
        if (valueItem instanceof List) {
          // item = getObjectTree(valueItem, level+1, addNullValues);
          item = getNodeWithChilds(valueItem.getClass(), "[" + String.valueOf(key) + "]", (List)valueItem, level+1, addNullValues);
          if (item.has("textNode")) {
            // itemTextNode = item.getJSONObject("textNode");
          }
          itemText = "[" + itemKey + "] (" + itemClassName + ") count=" + ((List)valueItem).size();
          item.put("text", itemText);
//          item.put("level", level + 1);
        } else if (checkClassName(valueItem.getClass().getName())) {
          item = getObjectTree(valueItem, level+1, addNullValues);
          item.put("itemTextNode", itemTextNode);
          item.put("text", itemText);
          item.put("level", level + 1);
        } else {

          if (valueItem instanceof String) {
            itemText = "[" + key + "] = \"" + valueItem + "\" (" + itemClassName + ")";
          } else {
            itemText = "[" + key + "] = " + valueItem + " (" + itemClassName + ")";
          }
          item = new JSONObject();
          itemTextNode.put("itemValue", valueItem);
          item.put("itemTextNode", itemTextNode);
          item.put("text", itemText);
          item.put("level", level + 1);
        }
        nodeList.put(item);
      } else if (addNullValues){
        nodeList.put(item);
      }
    }
    node.put("nodes", nodeList);
    node.put("level", level);
    return node;
  }


  private static boolean checkClassName(String name) {
    for (String packageName : PARSE_PACKAGES) {
      if (name.startsWith(packageName)) {
        return true;
      }
    }
    return false;
  }

  private static JSONObject getNode(Class<?> fieldType, String fieldName, Object fieldValue, int level, boolean addNullValues) {
    JSONObject node = new JSONObject();
    JSONObject textNode = new JSONObject();
    String text = null;

    String name = "<span class='field-name'>" + fieldName + "</span>";
    textNode.put("name", fieldName);
    String className = "<span class='field-class'>" + fieldType.getName() + "</span>";
    textNode.put("className", fieldType.getName());

    if (fieldValue != null) {
      if (fieldValue instanceof Enum) {
        String value = "<span class='field-value'>" + fieldValue + "</span>";
        text = name + " = " + value + " (" + className + ")";
        textNode.put("value", fieldValue);
      } else if (fieldValue instanceof Map) {
        text = name + " (" + className + ")";
        JSONObject innerNode = getMapNodeWithChilds(fieldType, fieldName, (Map) fieldValue, level, addNullValues);
        text = innerNode.getString("text");
        textNode = innerNode.getJSONObject("textNode");
        node = innerNode;
      } else if (checkClassName(fieldValue.getClass().getName())) {
        text = name + " (" + className + ")";
        JSONObject innerNode = getObjectTree(fieldValue, level, addNullValues);
        node.put("nodes", innerNode.get("nodes"));
      } else {
        String value = null;
        if (String.class.equals(fieldType)) {
          value = "<span class='field-value'>\"" + fieldValue + "\"</span>";
          textNode.put("value", "\"" + fieldValue + "\"");
        } else {
          value = "<span class='field-value'>" + fieldValue + "</span>";
          textNode.put("value", fieldValue);
        }
        text = name + " = " + value + " (" + className + ")";
      }
    } else {
      text = name + " = null" + " (" + className + ")";
      textNode.put("value", "null");
      node.put("isNull", true);
    }
    node.put("textNode", textNode);
    node.put("text", text);
    node.put("level", level);
    return node;
  }

  public static void printObject(Object obj) {
    printObject("object tree:", obj);
  }
  public static void printObject(String message, Object obj) {
    JSONObject object = getFullObjectTree(obj);

    String resStr = printNode(object);
    logger.info("{}\n{}", message, resStr);
  }

  private static String printNode(JSONObject node) {
    int level = node.getInt("level");
    String levelSpaces = (level > 0) ? String.format("%" + level * 4 + "s", "") : "";

    String text = "";
    if (node.has("textNode")) {
      JSONObject textNode = node.getJSONObject("textNode");
      String name = textNode.has("name") ? textNode.getString("name") : null;
      Object value = textNode.has("value") ? textNode.get("value") : null;
      String className = textNode.has("className") ? textNode.getString("className") : null;
      Integer count = textNode.has("count") ? textNode.getInt("count") : null;


      if (count == null) {
        text += name;
        if (value != null) {
          text += " = ";
          text += value;
        }

        if (className != null) {
          text += " (";
          text += className;
          text += ")";
        }
      } else {
        text += name;
        if (className != null) {
          text += " (";
          text += className;
          text += ")";
        }
        text +=  " count=" + count;
      }
    } else if (node.has("itemTextNode")) {
      JSONObject itemTextNode = node.getJSONObject("itemTextNode");
      // [0] = aaa (<span class='field-class'>java.lang.String<\/span>)
      text +=  "[";
      text +=  itemTextNode.get("itemIndex");
      text += "]";
      if (itemTextNode.has("itemValue")) {
        text += " " + itemTextNode.get("itemValue");
      }
      text += " (";
      text += itemTextNode.getString("itemClassName");
      text += ")";
    } else {
      text = node.getString("text");
    }

    String resStr = levelSpaces + text + "\n";
    try {
      JSONArray nodes = (JSONArray) node.get("nodes");
      if (nodes == null) {
        return "";
      }
      resStr += printNodes(nodes);
    } catch (JSONException e) {
      // skip
    }

    return resStr;
  }

  private static String printNodes(JSONArray nodes) {
    String resStr = "";
    for (int i = 0; i < nodes.length(); i++) {
      JSONObject node = (JSONObject) nodes.get(i);
      resStr += printNode(node);
    }
    return resStr;
  }
}
