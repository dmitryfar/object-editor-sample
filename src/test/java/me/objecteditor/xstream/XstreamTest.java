package me.objecteditor.xstream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import me.objecteditor.groovy.SimpleEntity;
import me.objecteditor.util.XStreamUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class XstreamTest {
  private static final Logger logger = LoggerFactory.getLogger(XstreamTest.class);

  @Configuration
  @ComponentScan(basePackages = { "me.objecteditor" }, excludeFilters = {
      @Filter(type = FilterType.ANNOTATION, value = Configuration.class) })
  public static class SpringConfig {

  }

  @Autowired
  XStreamUtil xStreamUtil;

  @Test
  public void testXstream() {
    SimpleEntity entity = new SimpleEntity();
    SimpleEntity child = new SimpleEntity();
    entity.setChild(child);

    String xml = xStreamUtil.toXML(entity);

    logger.info("xml: {}", xml);

    SimpleEntity parsedObject = xStreamUtil.fromXML(xml);
    logger.info("parsedObject: {}", parsedObject);

  }
}
