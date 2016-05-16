package me.objecteditor.rest;

import java.util.ArrayList;
import java.util.List;

import org.activiti.pm.filter.variable.BooleanRestVariableConverter;
import org.activiti.pm.filter.variable.DateRestVariableConverter;
import org.activiti.pm.filter.variable.DoubleRestVariableConverter;
import org.activiti.pm.filter.variable.EnumRestVariableConverter;
import org.activiti.pm.filter.variable.IntegerRestVariableConverter;
import org.activiti.pm.filter.variable.LongRestVariableConverter;
import org.activiti.pm.filter.variable.RestVariable;
import org.activiti.pm.filter.variable.RestVariable.RestVariableScope;
import org.activiti.pm.filter.variable.RestVariableConverter;
import org.activiti.pm.filter.variable.ShortRestVariableConverter;
import org.activiti.pm.filter.variable.StringRestVariableConverter;
import org.springframework.stereotype.Component;

@Component
public class RequestFactory {

  public static final String BYTE_ARRAY_VARIABLE_TYPE = "binary";
  public static final String SERIALIZABLE_VARIABLE_TYPE = "serializable";

  protected List<RestVariableConverter> variableConverters = new ArrayList<RestVariableConverter>();

  public RequestFactory() {
    initializeVariableConverters();
  }

  /**
   * @return list of {@link RestVariableConverter} which are used by this
   *         factory. Additional converters can be added and existing ones
   *         replaced ore removed.
   */
  public List<RestVariableConverter> getVariableConverters() {
    return variableConverters;
  }

  /**
   * Called once when the converters need to be initialized. Override of custom
   * conversion needs to be done between java and rest.
   */
  protected void initializeVariableConverters() {
    variableConverters.add(new StringRestVariableConverter());
    variableConverters.add(new IntegerRestVariableConverter());
    variableConverters.add(new LongRestVariableConverter());
    variableConverters.add(new ShortRestVariableConverter());
    variableConverters.add(new DoubleRestVariableConverter());
    variableConverters.add(new BooleanRestVariableConverter());
    variableConverters.add(new DateRestVariableConverter());
    variableConverters.add(new EnumRestVariableConverter());
  }

  public RestVariable createRestVariable(String name, Object value, RestVariableScope scope, String id,
      int variableType, boolean includeBinaryValue) {

    RestVariableConverter converter = null;
    RestVariable restVar = new RestVariable();
    restVar.setVariableScope(scope);
    restVar.setName(name);

    if (value != null) {
      // Try converting the value
      for (RestVariableConverter c : variableConverters) {
        if (c.getVariableType().isAssignableFrom(value.getClass())) {
          converter = c;
          break;
        }
      }

      if (converter != null) {
        converter.convertVariableValue(value, restVar);
        restVar.setType(converter.getRestTypeName());
      } else {
        // Revert to default conversion, which is the serializable/byte-array
        // form
        if (value instanceof Byte[] || value instanceof byte[]) {
          restVar.setType(BYTE_ARRAY_VARIABLE_TYPE);
        } else {
          restVar.setType(SERIALIZABLE_VARIABLE_TYPE);
        }

        if (includeBinaryValue) {
          restVar.setValue(value);
        }
      }
    }
    return restVar;
  }

  public Object getVariableValue(RestVariable restVariable) {
    Object value = null;

    if (restVariable.getType() != null) {
      // Try locating a converter if the type has been specified
      RestVariableConverter converter = null;
      for (RestVariableConverter conv : variableConverters) {
        if (conv.getRestTypeName().equals(restVariable.getType())) {
          converter = conv;
          break;
        }
      }
      if (converter == null) {
        throw new IllegalArgumentException(
            "Variable '" + restVariable.getName() + "' has unsupported type: '" + restVariable.getType() + "'.");
      }
      value = converter.getVariableValue(restVariable);

    } else {
      // Revert to type determined by REST-to-Java mapping when no explicit type
      // has been provided
      value = restVariable.getValue();
    }
    return value;
  }

  public RestVariable createBinaryRestVariable(String name, RestVariableScope scope, String type, String taskId,
      String executionId, String processInstanceId) {
    RestVariable restVar = new RestVariable();
    restVar.setVariableScope(scope);
    restVar.setName(name);
    restVar.setType(type);

    return restVar;
  }
}
