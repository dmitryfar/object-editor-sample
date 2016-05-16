package me.objecteditor.rest;

import java.io.Serializable;

public class CreateObjectRequest extends AbstractObjectValueRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  private String className;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }
}
