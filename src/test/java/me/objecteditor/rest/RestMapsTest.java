package me.objecteditor.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
import me.objecteditor.util.ObjectTreeUtil;
import me.objecteditor.util.XStreamUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class RestMapsTest extends TestCase {
  private static final Logger logger = LoggerFactory.getLogger(RestMapsTest.class);

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
  public void testAddMapElement() throws Exception {
    SimpleEntity entity = new SimpleEntity();
    ObjectTreeUtil.printObject(entity);
    String xmlEntity = xStreamUtil.toXML(entity);

    AddElementRequest addElementRequest = new AddElementRequest();
    addElementRequest.setXmlEntity(xmlEntity);
    addElementRequest.setPath("simpleMap");
    addElementRequest.setKey("KeyB");
    RestVariable variable = new RestVariable();
    variable.setName("nothing");
    variable.setType("string");
    variable.setValue("BBBBBBBBBBBBBBBB");
    addElementRequest.setVariable(variable );

    MvcResult mvcResult = mockMvc
        .perform(post("/value/map/add").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(addElementRequest)))
        .andExpect(status().isOk()).andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    ObjectValueResponse response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);

    SimpleEntity updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);
  }

  @Test
  public void testRemoveMapElement() throws Exception {
    SimpleEntity entity = new SimpleEntity();
    Map<String, Object> simpleMap = new HashMap<String, Object>();
    simpleMap.put("KeyA", "ValueA");
    simpleMap.put("KeyB", "ValueB");
    simpleMap.put("KeyC", "ValueC");
    entity.setSimpleMap(simpleMap);
    String xmlEntity = xStreamUtil.toXML(entity);

    RemoveElementRequest removeElementRequest = new RemoveElementRequest();
    removeElementRequest.setXmlEntity(xmlEntity);
    removeElementRequest.setPath("simpleMap");
    removeElementRequest.setKey("KeyB");

    MvcResult mvcResult = mockMvc
        .perform(post("/value/map/remove").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(removeElementRequest)))
        .andExpect(status().isOk()).andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    ObjectValueResponse response = mapper.readValue(httpResponse.getContentAsByteArray(), ObjectValueResponse.class);

    SimpleEntity updatedEntity = xStreamUtil.fromXML(response.getXmlEntity());
    ObjectTreeUtil.printObject(updatedEntity);
  }
}
