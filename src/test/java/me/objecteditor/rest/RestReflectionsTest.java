package me.objecteditor.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import me.objecteditor.test.AbstractTestEntity;
import me.objecteditor.util.ObjectTreeUtil;
import me.objecteditor.util.XStreamUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class RestReflectionsTest extends TestCase {
  private static final Logger logger = LoggerFactory.getLogger(RestReflectionsTest.class);

  @Configuration
  @ComponentScan(basePackages = { "me.objecteditor" }, excludeFilters = {
      @Filter(type = FilterType.ANNOTATION, value = Configuration.class) })
  public static class SpringConfig {

  }

  private MockMvc mockMvc;

  @Autowired
  private ObjectEditorRestController objectEditorRestController;

  @Autowired
  XStreamUtil xStreamUtil;

  ObjectMapper mapper = new ObjectMapper();

  @Override
  @Before
  public void setUp() {
    // MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(objectEditorRestController).build();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetSubtypes() throws Exception {
    String className = AbstractTestEntity.class.getName();
    String basePackage = "me.objecteditor";

    MvcResult mvcResult = mockMvc
        .perform(
            get("/subtypes/" + className + "/" + basePackage + "/list").contentType(TestUtil.APPLICATION_JSON_UTF8)
        )
        .andExpect(status().isOk())
        .andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    List<String> response = mapper.readValue(httpResponse.getContentAsByteArray(), List.class);

    ObjectTreeUtil.printObject(response);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetMapSubtypes() throws Exception {
    String className = Map.class.getName();
    String basePackage = "java";

    MvcResult mvcResult = mockMvc
        .perform(
            get("/subtypes/" + className + "/" + basePackage + "/list").contentType(TestUtil.APPLICATION_JSON_UTF8)
        )
        .andExpect(status().isOk())
        .andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    List<String> response = mapper.readValue(httpResponse.getContentAsByteArray(), List.class);

    ObjectTreeUtil.printObject(response);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetMapSubtypes2() throws Exception {
    String className = Map.class.getName();

    List<String> packages = new ArrayList<String>();
    packages.add("org.spring");
    packages.add("java");

    MvcResult mvcResult = mockMvc
        .perform(
            post("/subtypes/" + className + "/list").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(packages))
            )
        .andExpect(status().isOk())
        .andReturn();

    MockHttpServletResponse httpResponse = mvcResult.getResponse();

    List<String> response = mapper.readValue(httpResponse.getContentAsByteArray(), List.class);

    ObjectTreeUtil.printObject(response);
  }
}
