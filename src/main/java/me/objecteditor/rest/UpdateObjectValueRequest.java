package me.objecteditor.rest;

import java.io.Serializable;

import org.activiti.pm.filter.variable.RestVariable;

public class UpdateObjectValueRequest extends AbstractObjectValueRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  private RestVariable variable;

  public RestVariable getVariable() {
    return variable;
  }

  public void setVariable(RestVariable variable) {
    this.variable = variable;
  }

}
