package me.objecteditor.groovy;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.objecteditor.util.DefaultValues;

public class GroovyObjectUtil {
  private static final Logger logger = LoggerFactory.getLogger(GroovyObjectUtil.class);

  /**
   * Set entity field value to null.
   *
   * @param entity
   * @param fieldPath
   */
  public static <T> void setObjectNullValue(T entity, String fieldPath) {
    setObjectValue(entity, fieldPath, null);
  }

  /**
   * Set entity field value.
   *
   * @param entity
   * @param fieldPath
   * @param value
   */
  public static <T> void setObjectValue(T entity, String fieldPath, Object value) {
    Object currentValue = getObjectValue(entity, fieldPath);
    if (value == null && currentValue != null) {
      // TODO: check if is potential primitive or is primitive
      //if (DefaultValues.isPotentialPrimitive(currentValue.getClass())) {
        // check primitive types to set default values
        Class<?> type = getDeclaredType(entity, fieldPath);
        // Check if is primitive to rewrite value
        if (DefaultValues.isPrimitive(type)) {
          value = DefaultValues.getForClass(type);
        }
      // }
    }
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("entity", entity);
    bindings.put("fieldPath", getEntityFieldPath("entity", fieldPath));
    bindings.put("value", value);

//    try {
    ScriptEngineUtil.evaluateFile("scripts/set-object-value.groovy", ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE,
        bindings, false);
//    } catch(Exception e) {
//      logger.error(e.getMessage(), e);
//    }

    // ObjectTreeUtil.printObject(entity);

    // ScriptEngineUtil.evaluate(fieldPath + " = value",
    // ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE, bindings);
    // ObjectTreeUtil.printObject(bindings.get("entity"));
    // logger.debug("entity: " + entity);
  }

  @SuppressWarnings("unchecked")
  public static <T, E> T getObjectValue(E entity, String fieldPath) {
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("entity", entity);
    bindings.put("fieldPath", getEntityFieldPath("entity", fieldPath));

    return (T) ScriptEngineUtil.evaluateFile("scripts/get-object-value.groovy",
        ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE, bindings, false);
  }

  public static <T> void createNewObject(T entity, String fieldPath, String className) {
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("entity", entity);
    bindings.put("fieldPath", getEntityFieldPath("entity", fieldPath));
    bindings.put("className", className);

    ScriptEngineUtil.evaluateFile("scripts/create-new-object.groovy", ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE,
        bindings, false);
  }

  public static <T> T createNewInstance(String className) {
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("className", className);

    ScriptEngineUtil.evaluateFile("scripts/create-new-class-instance.groovy", ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE,
        bindings, true);

    return (T) bindings.get("result");
  }

  /**
   * Create new list object. It is possible to use addListElement without create
   * new list if the field value is null.
   *
   * @param entity
   * @param fieldPath
   */
  public static <T> void createNewList(T entity, String fieldPath) {
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("entity", entity);
    bindings.put("fieldPath", getEntityFieldPath("entity", fieldPath));

    ScriptEngineUtil.evaluateFile("scripts/create-new-list.groovy", ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE,
        bindings, false);
    // ObjectTreeUtil.printObject(entity);
  }

  /**
   * Add element to array list. If field value has no list and is null then new
   * list will be created before.
   *
   * @param entity
   * @param fieldPath
   * @param value
   */
  public static <T> void addListElement(T entity, String fieldPath, Object value) {
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("entity", entity);
    bindings.put("fieldPath", getEntityFieldPath("entity", fieldPath));
    bindings.put("value", value);

    ScriptEngineUtil.evaluateFile("scripts/add-list-element.groovy", ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE,
        bindings, false);
  }

  /**
   * Remove element from list. Will be skipped if there is no elements in the
   * list.
   *
   * @param entity
   * @param fieldPath
   * @param index
   */
  public static <T> void removeListElement(T entity, String fieldPath, int index) {
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("entity", entity);
    bindings.put("fieldPath", getEntityFieldPath("entity", fieldPath));
    bindings.put("index", index);

    ScriptEngineUtil.evaluateFile("scripts/remove-list-element.groovy", ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE,
        bindings, false);
  }

  public static <T> void putMapElement(T entity, String fieldPath, String key, Object value) {
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("entity", entity);
    bindings.put("fieldPath", getEntityFieldPath("entity", fieldPath));
    bindings.put("key", key);
    bindings.put("value", value);

    ScriptEngineUtil.evaluateFile("scripts/add-map-element.groovy", ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE,
        bindings, false);
  }

  /**
   * Remove element from map. Will be skipped if there is no key in the map.
   *
   * @param entity
   * @param fieldPath
   * @param key
   */
  public static <T> void removeMapElement(T entity, String fieldPath, String key) {
    Map<String, Object> bindings = new HashMap<String, Object>();
    bindings.put("entity", entity);
    bindings.put("fieldPath", getEntityFieldPath("entity", fieldPath));
    bindings.put("key", key);

    ScriptEngineUtil.evaluateFile("scripts/remove-map-element.groovy", ScriptEngineUtil.GROOVY_SCRIPTING_LANGUAGE,
        bindings, false);
  }


  /**
   * Returns real entity field type. Works for primitives too.
   *
   * @param entity
   * @return Class
   */
  public static <T> Class<?> getType(T entity) {
    return getObjectType(entity);
  }

  /**
   * Returns real entity field type. Will return correct type for primitives.
   * Returns entity class if path is empty.
   *
   * @param entity
   * @param fieldPath
   * @return Class
   */
  public static <T> Class<?> getDeclaredType(T entity, String fieldPath) {
    if (entity == null) {
      return null;
    }

    Object currentFieldValue = getObjectValue(entity, fieldPath);
    if (currentFieldValue == null) {
      //return;
    }

    Class<?> type = null;

    if (StringUtils.isNoneEmpty(fieldPath)) {
      String fieldName = null;
      String objectPath = null;
      // check path on array item: someField.fieldList[2]
      if (checkIsArrayElement(fieldPath)) {
        String[] pair = splitListIndex(fieldPath);
        objectPath = pair[0]; // someField.fieldList
        fieldName = pair[1]; // looks like array: "[2]"


        String[] pair2 = splitAllListIndexes(fieldPath);
        logger.info("pair2: {}", Arrays.asList(pair2));

//        String[] parts = pair2[0].split("\\.");
//        fieldName = parts[parts.length - 1];
//        String fullIndex = pair2[1];
//        parts = ArrayUtils.remove(parts, parts.length - 1);
//        objectPath = StringUtils.join(parts, ".");
//        logger.info("({})({})({})", objectPath, fieldName, fullIndex);

        // TODO: get generic names set and return last generic
      } else {
        String[] parts = fieldPath.split("\\.");
        fieldName = parts[parts.length - 1];
        parts = ArrayUtils.remove(parts, parts.length - 1);
        objectPath = StringUtils.join(parts, ".");
      }




      // parts = ArrayUtils.remove(parts, parts.length - 1);

      // object is the object who contains field with fieldName
      Object object = getObjectValue(entity, objectPath);

      // check map generic type
      if (object instanceof Map) {
        logger.info("object is map: {}", object);
        return getType(currentFieldValue);
      }

      // check list generic type
      if (object instanceof List) {
        logger.info("object is list: {} with class {}", object, object.getClass().getName());
        // Class<?> clazz = object.getClass();
        // getObjectValue(entity, "").getClass().getDeclaredField("fieldList2").getGenericType()
        return getType(currentFieldValue);
      }

      // check set generic type
      if (object instanceof Set) {
        logger.info("object is set: {}", object);
        return getType(currentFieldValue);
      }

      // check array
      if (object instanceof Array) {
        logger.info("object is array: {}", object);
        return getType(currentFieldValue);
      }

      Class<?> clazz = object.getClass();
      try {
        Field field = object.getClass().getDeclaredField(fieldName);
        type = field.getType();
      } catch (NoSuchFieldException e) {
        type = clazz;
      } catch (Exception e) {
        logger.error(e.getLocalizedMessage());
      }
    } else {
      // this is an entity object
      type = getObjectType(entity);
    }

    logger.debug("type: {}", type);

    return type;
  }

  private static Class<?> getObjectType(Object object) {
    Class<?> clazz = object.getClass();
    try {
      Field typeField = clazz.getDeclaredField("TYPE");
      return (Class<?>) typeField.get(object);
    } catch (NoSuchFieldException e) {
      // skip and return clazz
    } catch (Exception e) {
      logger.error(e.getLocalizedMessage());
    }
    return clazz;
  }

  private static boolean checkIsArrayElement(String fieldPath) {
    return fieldPath.matches(".*\\[[^\\[]*\\]");
  }

  private static String[] splitListIndex(String fieldPath) {
    String pattern = "(.*)(\\[[^\\[]*\\])";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(fieldPath);
    if (m.find( )) {
      return new String[]{m.group(1), m.group(2)};
    }
    return new String[]{fieldPath};
  }

  private static String[] splitAllListIndexes(String fieldPath) {
    Pattern pattern = Pattern.compile("(.*?)((\\[[^\\[]*\\])+)$");
    Matcher matcher = pattern.matcher(fieldPath);
    if (matcher.find()) {
      return new String[]{matcher.group(1), matcher.group(2)};
    }
    return new String[]{fieldPath, null};
  }

  private static String getEntityFieldPath(String entityName, String fieldPath) {
    String entityFieldPath = entityName + ((StringUtils.isNotEmpty(fieldPath)) ? "." + fieldPath : "");
    return entityFieldPath;
  }
}
