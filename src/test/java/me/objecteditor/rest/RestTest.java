package me.objecteditor.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedList;

import org.activiti.pm.filter.variable.RestVariable;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.TestCase;
import me.objecteditor.groovy.SimpleEntity;
import me.objecteditor.test.CoolTestEntity;
import me.objecteditor.test.OtherTestClass;
import me.objecteditor.util.ObjectTreeUtil;
import me.objecteditor.util.XStreamUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class RestTest extends TestCase {
  private static final Logger logger = LoggerFactory.getLogger(RestTest.class);

  @Configuration
  @ComponentScan(basePackages = { "me.objecteditor" }, excludeFilters = {
      @Filter(type = FilterType.ANNOTATION, value = Configuration.class) })
  public static class SpringConfig {

  }

  private MockMvc mockMvc;

  private static CloseableHttpClient client;
  protected static LinkedList<CloseableHttpResponse> httpResponses = new LinkedList<CloseableHttpResponse>();

  @Autowired
  private ObjectEditorRestController objectEditorRestController;

  @Autowired
  XStreamUtil xStreamUtil;

  ObjectMapper mapper = new ObjectMapper();

  @Before
  public void setUp() {
    // MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(objectEditorRestController).build();
  }

  @Test
  public void testUpdateObjectValue() throws Exception {
    SimpleEntity entity = new SimpleEntity();
    String xmlEntity = xStreamUtil.toXML(entity);

    UpdateObjectValueRequest updateObjectValueRequest = new UpdateObjectValueRequest();
    updateObjectValueRequest.setPath("fieldLong");
    updateObjectValueRequest.setXmlEntity(xmlEntity);
    RestVariable variable = new RestVariable();
    variable.setName("someVariableName");
    variable.setType("long");
    variable.setValue(123123L);
    updateObjectValueRequest.setVariable(variable);
    // String jsonString = mapper.writeValueAsString(updateObjectValueRequest);

    MvcResult mvcResult = mockMvc
        .perform(post("/value/update").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateObjectValueRequest)))
        .andExpect(status().isOk()).andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    ObjectValueResponse response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);
    // JsonNode responseNode = mapper.readTree(httpResponse.getContentAsString());

    // logger.info("response: {}", response);
    // logger.info("responseNode: {}", responseNode);

    SimpleEntity updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);
    assertEquals((Long)123123L, updatedEntity.getFieldLong());
  }

  @Test
  public void testSetNullObjectValue() throws Exception {
    SimpleEntity entity = new SimpleEntity();
    entity.setFieldLong(123L);
    String xmlEntity = xStreamUtil.toXML(entity);

    UpdateObjectValueRequest updateObjectValueRequest = new UpdateObjectValueRequest();
    updateObjectValueRequest.setPath("fieldLong");
    updateObjectValueRequest.setXmlEntity(xmlEntity);
    RestVariable variable = new RestVariable();
    variable.setName("someVariableName");
    // variable.setType("long");
    variable.setValue(null);
    updateObjectValueRequest.setVariable(variable);

    MvcResult mvcResult = mockMvc
        .perform(post("/value/update").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateObjectValueRequest)))
        .andExpect(status().isOk()).andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    ObjectValueResponse response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);

    SimpleEntity updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);
    assertNull(updatedEntity.getFieldLong());
  }

  @Test
  public void testDeleteObjectValue() throws Exception {
    SimpleEntity entity = new SimpleEntity();
    entity.setFieldPrimitiveLong(123L);
    String xmlEntity = xStreamUtil.toXML(entity);

    UpdateObjectValueRequest updateObjectValueRequest = new UpdateObjectValueRequest();
    updateObjectValueRequest.setPath("fieldPrimitiveLong");
    updateObjectValueRequest.setXmlEntity(xmlEntity);
    RestVariable variable = new RestVariable();
//    variable.setName("someVariableName");
    // variable.setType("long");
    variable.setValue(null);
    updateObjectValueRequest.setVariable(variable);

    MvcResult mvcResult = mockMvc
        .perform(post("/value/delete").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateObjectValueRequest)))
        .andExpect(status().isOk()).andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    ObjectValueResponse response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);

    SimpleEntity updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);
    assertEquals(0, updatedEntity.getFieldPrimitiveLong());
  }

  @Test
  public void testCreateObjectValue() throws Exception {
    SimpleEntity entity = new SimpleEntity();
    String xmlEntity = xStreamUtil.toXML(entity);

    CreateObjectRequest createObjectRequest = new CreateObjectRequest();
    createObjectRequest.setPath("someInterfacedField");
    createObjectRequest.setXmlEntity(xmlEntity);
    createObjectRequest.setClassName("me.objecteditor.test.OtherTestClass");

    MvcResult mvcResult = mockMvc
        .perform(post("/value/create").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createObjectRequest)))
        .andExpect(status().isOk()).andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    ObjectValueResponse response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);

    SimpleEntity updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);
    assertEquals(OtherTestClass.class, updatedEntity.getSomeInterfacedField().getClass());

    // create new CoolTestEntity

    xmlEntity = xStreamUtil.toXML(updatedEntity);
    createObjectRequest = new CreateObjectRequest();
    createObjectRequest.setPath("someInterfacedField");
    createObjectRequest.setXmlEntity(xmlEntity);
    createObjectRequest.setClassName("me.objecteditor.test.CoolTestEntity");

    mvcResult = mockMvc
        .perform(post("/value/create").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createObjectRequest)))
        .andExpect(status().isOk()).andReturn();

    httpResponse = mvcResult.getResponse();

    response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);

    updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);
    assertEquals(CoolTestEntity.class, updatedEntity.getSomeInterfacedField().getClass());
  }

  @Test
  public void testCreateList() throws Exception {
    SimpleEntity entity = new SimpleEntity();
    String xmlEntity = xStreamUtil.toXML(entity);

    CreateObjectRequest createObjectRequest = new CreateObjectRequest();
    createObjectRequest.setPath("simpleEntities");
    createObjectRequest.setXmlEntity(xmlEntity);

    MvcResult mvcResult = mockMvc
        .perform(post("/value/create/list").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createObjectRequest)))
        .andExpect(status().isOk()).andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    ObjectValueResponse response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);

    SimpleEntity updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);

    // add element to list

    xmlEntity = xStreamUtil.toXML(updatedEntity);

    AddElementRequest addObjectValueRequest = new AddElementRequest();
    addObjectValueRequest.setPath("fieldList");
    addObjectValueRequest.setXmlEntity(xmlEntity);
    RestVariable variable = new RestVariable();
    variable.setType("string");
    variable.setValue("@@@@@@@@@@@@@@@@@@@@@@@@");
    addObjectValueRequest.setVariable(variable );

    mvcResult = mockMvc
        .perform(post("/value/list/add").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(addObjectValueRequest)))
        .andExpect(status().isOk()).andReturn();

    httpResponse = mvcResult.getResponse();

    response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);

    updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);

    // add complicated element to list

    xmlEntity = xStreamUtil.toXML(updatedEntity);

    addObjectValueRequest = new AddElementRequest();
    addObjectValueRequest.setPath("simpleEntities");
    addObjectValueRequest.setXmlEntity(xmlEntity);
    addObjectValueRequest.setClassName(SimpleEntity.class.getName());

    mvcResult = mockMvc
        .perform(post("/value/list/add").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(addObjectValueRequest)))
        .andExpect(status().isOk()).andReturn();

    httpResponse = mvcResult.getResponse();

    response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);

    updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);
  }

  @Test
  public void testRemoveListElement() throws Exception {
    SimpleEntity entity = new SimpleEntity();
    entity.addFieldListElement("a1a1a1a1a1a1a1");
    entity.addFieldListElement("b2b2b2b2b2b2b2");
    entity.addFieldListElement("c3c3c3c3c3c3c3");
    String xmlEntity = xStreamUtil.toXML(entity);

    RemoveElementRequest removeElementRequest = new RemoveElementRequest();
    removeElementRequest.setXmlEntity(xmlEntity);
    removeElementRequest.setPath("fieldList");
    removeElementRequest.setIndex(1);

    MvcResult mvcResult = mockMvc
        .perform(post("/value/list/remove").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(removeElementRequest)))
        .andExpect(status().isOk()).andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    ObjectValueResponse response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);

    SimpleEntity updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);
  }
}
