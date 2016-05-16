package me.objecteditor.rest;

import java.io.Serializable;

public class RemoveElementRequest extends AbstractObjectValueRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  private int index;
  private String key;

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
