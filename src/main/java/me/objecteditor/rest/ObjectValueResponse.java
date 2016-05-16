package me.objecteditor.rest;

import java.io.Serializable;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectValueResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  private String xmlEntity;
  private String path;
  private ObjectNode objectTree;

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

  public ObjectNode getObjectTree() {
    return objectTree;
  }

  public void setObjectTree(ObjectNode objectTree) {
    this.objectTree = objectTree;
  }

  @Override
  public String toString() {
    return "ObjectValueResponse [xmlEntity=" + xmlEntity + ", path=" + path + "]";
  }

}
