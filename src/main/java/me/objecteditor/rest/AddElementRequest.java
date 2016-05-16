package me.objecteditor.rest;

import java.io.Serializable;

import org.activiti.pm.filter.variable.RestVariable;

public class AddElementRequest extends AbstractObjectValueRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  private String className;
  private RestVariable variable;

  /**
   * Used for maps
   */
  private String key;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public RestVariable getVariable() {
    return variable;
  }

  public void setVariable(RestVariable variable) {
    this.variable = variable;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
