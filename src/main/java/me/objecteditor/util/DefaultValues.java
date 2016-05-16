package me.objecteditor.util;

import org.apache.commons.lang3.ArrayUtils;

public final class DefaultValues {

  private static Class<?>[] primitives = {int.class, boolean.class, byte.class, char.class, double.class, float.class, long.class, short.class};
  private static Class<?>[] potentialPrimitives = {Integer.class, Boolean.class, Byte.class, Character.class, Double.class, Float.class, Long.class, Short.class};

  public static boolean isPrimitive(Class<?> clazz) {
    return ArrayUtils.contains(primitives, clazz);
  }

  public static boolean isPotentialPrimitive(Class<?> clazz) {
    return ArrayUtils.contains(potentialPrimitives, clazz);
  }

  /**
   * <P>
   * The default value for a boolean is {@code false}.
   * </P>
   *
   * <P>
   * Viewed 1/21/2014 <BR>
   * <CODE><A HREF="http://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">http://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html</A></CODE>
   * :
   * </P>
   *
   * <P>
   * <B>Default Values:</B>
   * </P>
   *
   * <P>
   * It's not always necessary to assign a value when a field is declared.
   * Fields that are declared but not initialized will be set to a reasonable
   * default by the compiler. Generally speaking, this default will be zero or
   * null, depending on the data type. Relying on such default values, however,
   * is generally considered bad programming style. The following chart
   * summarizes the default values for the above data types.
   * </P>
   *
   * <PRE>
   * {@literal
  Data Type   Default Value (for fields)
  --------------------------------------
  byte                       0
  short                      0
  int                        0
  long                       0L
  float                      0.0f
  double                     0.0d
  char                       '\u0000'
  String (or any object)     null
  boolean                    false}
   * </PRE>
   *
   * @see #getForClass(String) getForClass(s)
   * @see #getForClassName(String) getForClassName(s)
   * @see #DEFAULT_CHAR
   * @see #DEFAULT_BYTE
   * @see #DEFAULT_SHORT
   * @see #DEFAULT_INT
   * @see #DEFAULT_LONG
   * @see #DEFAULT_FLOAT
   * @see #DEFAULT_DOUBLE
   **/
  public static final boolean DEFAULT_BOOLEAN = false;
  /**
   * <P>
   * The default value for a char {@code '\u0000'}.
   * </P>
   *
   * @see #DEFAULT_BOOLEAN
   **/
  public static final char DEFAULT_CHAR = '\u0000';
  /**
   * <P>
   * The default value for a byte is {@code 0}.
   * </P>
   *
   * @see #DEFAULT_BOOLEAN
   **/
  public static final byte DEFAULT_BYTE = 0;
  /**
   * <P>
   * The default value for a short is {@code 0}.
   * </P>
   *
   * @see #DEFAULT_BOOLEAN
   **/
  public static final short DEFAULT_SHORT = 0;
  /**
   * <P>
   * The default value for a int is {@code 0}.
   * </P>
   *
   * @see #DEFAULT_BOOLEAN
   **/
  public static final int DEFAULT_INT = 0;
  /**
   * <P>
   * The default value for a long is {@code 0L}.
   * </P>
   *
   * @see #DEFAULT_BOOLEAN
   **/
  public static final long DEFAULT_LONG = 0L;
  /**
   * <P>
   * The default value for a float {@code 0.0f}.
   * </P>
   *
   * @see #DEFAULT_BOOLEAN
   **/
  public static final float DEFAULT_FLOAT = 0.0f;
  /**
   * <P>
   * The default value for a double {@code 0.0d}.
   * </P>
   *
   * @see #DEFAULT_BOOLEAN
   **/
  public static final double DEFAULT_DOUBLE = 0.0d;

  /**
   * <P>
   * Get an object containing an initialized value for the static class-type.
   * </P>
   *
   * @param clazz
   *          May not be {@code null}.
   * @return <CODE>
   *         {@link getForClassName(String) getForClassName}(cls_static.getName())</CODE>
   **/
  public static final Object getForClass(Class clazz) {
    try {
      return getForClassName(clazz.getName());
    } catch (RuntimeException rtx) {
      throw new NullPointerException("getForClass: clazz");
    }
  }

  /**
   * <P>
   * Get an object containing an initialized value for the type whose name is in
   * a string.
   * </P>
   *
   * <P>
   * Idea from (viewed 1/2/2014) <BR>
   * &nbsp; &nbsp; {@code <A HREF=
   * "http://stackoverflow.com/questions/2891970/getting-default-value-for-java-primitive-types/2892067#2892067">
   * http://stackoverflow.com/questions/2891970/getting-default-value-for-java-primitive-types/2892067#2892067
   * </A>}
   * </P>
   *
   * @param className
   *          May not be {@code null}.
   * @return If {@code s_type} is equal to
   *         <UL>
   *         <LI>{@code "boolean"}: {@link #DEFAULT_BOOLEAN}</LI>
   *         <LI>{@code "char"}: {@link #DEFAULT_CHAR}</LI>
   *         <LI>{@code "byte"}: {@link #DEFAULT_BYTE}</LI>
   *         <LI>{@code "short"}: {@link #DEFAULT_SHORT}</LI>
   *         <LI>{@code "int"}: {@link #DEFAULT_INT}</LI>
   *         <LI>{@code "long"}: {@link #DEFAULT_LONG}</LI>
   *         <LI>{@code "float"}: {@link #DEFAULT_FLOAT}</LI>
   *         <LI>{@code "double"}: {@link #DEFAULT_DOUBLE}</LI>
   *         <LI><I>anything else</I>: {@code null}</LI>
   *         </UL>
   * @see #getForClass(Class) getForClass(cls)
   **/
  public static final Object getForClassName(String className) {
    try {
      if (className.equals("boolean")) {
        return DEFAULT_BOOLEAN;
      }
    } catch (NullPointerException npx) {
      throw new NullPointerException("getForClassName: s_type");
    }
    if (className.equals("char")) {
      return DEFAULT_CHAR;
    }
    if (className.equals("byte")) {
      return DEFAULT_BYTE;
    }
    if (className.equals("short")) {
      return DEFAULT_SHORT;
    }
    if (className.equals("int")) {
      return DEFAULT_INT;
    }
    if (className.equals("long")) {
      return DEFAULT_LONG;
    }
    if (className.equals("float")) {
      return DEFAULT_FLOAT;
    }
    if (className.equals("double")) {
      return DEFAULT_DOUBLE;
    }

    // Non-primitive type
    return null;
  }
}