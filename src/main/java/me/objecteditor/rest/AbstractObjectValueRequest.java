package me.objecteditor.rest;

public abstract class AbstractObjectValueRequest {
  private String xmlEntity;
  private String path;

  public String getXmlEntity() {
    return xmlEntity;
  }

  public void setXmlEntity(String xmlEntity) {
    this.xmlEntity = xmlEntity;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
