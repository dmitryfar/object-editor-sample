package me.objecteditor.rest;

import java.util.ArrayList;
import java.util.List;

import org.activiti.pm.filter.variable.RestVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.objecteditor.groovy.GroovyObjectUtil;
import me.objecteditor.util.ObjectTreeUtil2;
import me.objecteditor.util.ReflectionUtil;
import me.objecteditor.util.XStreamUtil;

@RestController
@RequestMapping(value = "/")
public class ObjectEditorRestController {

  ObjectMapper mapper = new ObjectMapper();

  @Autowired
  Environment env;

  @Autowired
  RequestFactory requestFactory;

  @Autowired
  XStreamUtil xStreamUtil;

  private static final Logger logger = LoggerFactory.getLogger(ObjectEditorRestController.class);

  @RequestMapping(value = "/")
  public ModelAndView test() {
    logger.info("aaaa");
    return new ModelAndView("view.jsp");
  }

  @RequestMapping(value = "/edit"/* , method = RequestMethod.POST */)
  public ModelAndView editObject() {
    return new ModelAndView("edit.jsp");
  }

  @RequestMapping(value = "/value/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE/*, produces = MediaType.APPLICATION_JSON_VALUE*/)
  public ObjectValueResponse updateObjectValue(@RequestBody UpdateObjectValueRequest request) {

    // ObjectNode result = mapper.createObjectNode();
    // result.put("hello", "WORLD");

    RestVariable restVariable = request.getVariable();

    Object value = requestFactory.getVariableValue(restVariable);
    logger.debug("name: {}, type: {}, value: {}", restVariable.getName(), restVariable.getType(), value);

    Object entity = xStreamUtil.fromXML(request.getXmlEntity());
    logger.debug("entity: {}", entity);

    GroovyObjectUtil.setObjectValue(entity, request.getPath(), value);

    String xmlEntity = xStreamUtil.toXML(entity);

    ObjectValueResponse response = new ObjectValueResponse();
    response.setPath(request.getPath());
    response.setXmlEntity(xmlEntity);
    response.setObjectTree(ObjectTreeUtil2.getFullObjectTreeview(entity));

    return response;
  }

  @RequestMapping(value = "/value/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ObjectValueResponse deleteObjectValue(@RequestBody DeleteObjectValueRequest request) {
    Object entity = xStreamUtil.fromXML(request.getXmlEntity());
    logger.debug("entity: {}", entity);

    GroovyObjectUtil.setObjectValue(entity, request.getPath(), null);

    String xmlEntity = xStreamUtil.toXML(entity);

    ObjectValueResponse response = new ObjectValueResponse();
    response.setPath(request.getPath());
    response.setXmlEntity(xmlEntity);
    response.setObjectTree(ObjectTreeUtil2.getFullObjectTreeview(entity));

    return response;
  }

  @RequestMapping(value = "/value/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ObjectValueResponse createObjectValue(@RequestBody CreateObjectRequest request) {
    Object entity = xStreamUtil.fromXML(request.getXmlEntity());
    logger.debug("entity: {}", entity);

    GroovyObjectUtil.createNewObject(entity, request.getPath(), request.getClassName());

    String xmlEntity = xStreamUtil.toXML(entity);

    ObjectValueResponse response = new ObjectValueResponse();
    response.setPath(request.getPath());
    response.setXmlEntity(xmlEntity);
    response.setObjectTree(ObjectTreeUtil2.getFullObjectTreeview(entity));

    return response;
  }

  @RequestMapping(value = "/value/create/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ObjectValueResponse createList(@RequestBody CreateObjectRequest request) {
    Object entity = xStreamUtil.fromXML(request.getXmlEntity());
    logger.debug("entity: {}", entity);

    if (request.getClassName() == null) {
      request.setClassName(ArrayList.class.getName());
    }

    // GroovyObjectUtil.createNewObject(entity, request.getPath(), request.getClassName());
    GroovyObjectUtil.createNewList(entity, request.getPath());

    String xmlEntity = xStreamUtil.toXML(entity);

    ObjectValueResponse response = new ObjectValueResponse();
    response.setPath(request.getPath());
    response.setXmlEntity(xmlEntity);
    response.setObjectTree(ObjectTreeUtil2.getFullObjectTreeview(entity));

    return response;
  }

  @RequestMapping(value = "/value/list/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ObjectValueResponse addListElement(@RequestBody AddElementRequest request) {
    Object entity = xStreamUtil.fromXML(request.getXmlEntity());
    logger.debug("entity: {}", entity);

    RestVariable restVariable = request.getVariable();

    Object value = null;
    if (restVariable != null) {
      value = requestFactory.getVariableValue(restVariable);
      logger.debug("restVariable> name: {}, type: {}, value: {}", restVariable.getName(), restVariable.getType(), value);
    } else {
      // GroovyObjectUtil.createNewObject(entity, request.getPath(), request.getClassName());
      value = GroovyObjectUtil.createNewInstance(request.getClassName());
    }

    GroovyObjectUtil.addListElement(entity, request.getPath(), value);

    String xmlEntity = xStreamUtil.toXML(entity);

    ObjectValueResponse response = new ObjectValueResponse();
    response.setPath(request.getPath());
    response.setXmlEntity(xmlEntity);
    response.setObjectTree(ObjectTreeUtil2.getFullObjectTreeview(entity));

    return response;
  }

  @RequestMapping(value = "/value/list/remove", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ObjectValueResponse removeListElement(@RequestBody RemoveElementRequest request) {
    Object entity = xStreamUtil.fromXML(request.getXmlEntity());
    logger.debug("entity: {}", entity);

    GroovyObjectUtil.removeListElement(entity, request.getPath(), request.getIndex());

    String xmlEntity = xStreamUtil.toXML(entity);

    ObjectValueResponse response = new ObjectValueResponse();
    response.setPath(request.getPath());
    response.setXmlEntity(xmlEntity);
    response.setObjectTree(ObjectTreeUtil2.getFullObjectTreeview(entity));

    return response;
  }

  @RequestMapping(value = "/value/map/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE/*, produces = MediaType.APPLICATION_JSON_VALUE*/)
  public ObjectValueResponse addMapElement(@RequestBody AddElementRequest request) {
    Object entity = xStreamUtil.fromXML(request.getXmlEntity());
    logger.debug("entity: {}", entity);

    RestVariable restVariable = request.getVariable();

    Object value = null;
    if (restVariable != null) {
      value = requestFactory.getVariableValue(restVariable);
      logger.debug("restVariable> name: {}, type: {}, value: {}", restVariable.getName(), restVariable.getType(), value);
    } else {
      // GroovyObjectUtil.createNewObject(entity, request.getPath(), request.getClassName());
      value = GroovyObjectUtil.createNewInstance(request.getClassName());
    }

    GroovyObjectUtil.putMapElement(entity, request.getPath(), request.getKey(), value);

    String xmlEntity = xStreamUtil.toXML(entity);

    ObjectValueResponse response = new ObjectValueResponse();
    response.setPath(request.getPath());
    response.setXmlEntity(xmlEntity);
    response.setObjectTree(ObjectTreeUtil2.getFullObjectTreeview(entity));

    return response;
  }

  @RequestMapping(value = "/value/map/remove", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ObjectValueResponse removeMapElement(@RequestBody RemoveElementRequest request) {
    Object entity = xStreamUtil.fromXML(request.getXmlEntity());
    logger.debug("entity: {}", entity);

    GroovyObjectUtil.removeMapElement(entity, request.getPath(), request.getKey());

    String xmlEntity = xStreamUtil.toXML(entity);

    ObjectValueResponse response = new ObjectValueResponse();
    response.setPath(request.getPath());
    response.setXmlEntity(xmlEntity);
    response.setObjectTree(ObjectTreeUtil2.getFullObjectTreeview(entity));

    return response;
  }

  @RequestMapping(value = "/subtypes/{className}/{basePackage}/list", method = RequestMethod.GET)
  public List<String> getSubtypes(@PathVariable String className, @PathVariable String basePackage) {
    logger.info("className: {}, basePackage: {}", className, basePackage);

    List<String> subClasses = ReflectionUtil.getSubTypeNamesOf(className, true, false, basePackage);

    return subClasses;
  }

  @RequestMapping(value = "/subtypes/{className}/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getSubtypesFromPackages(@PathVariable String className, @RequestBody List<String> basePackages) {
    logger.info("className: {}, basePackage: {}", className, basePackages);

    String[] array = new String[basePackages.size()];
    basePackages.toArray(array);
    List<String> subClasses = ReflectionUtil.getSubTypeNamesOf(className, true, false, array);

    return subClasses;
  }
}
