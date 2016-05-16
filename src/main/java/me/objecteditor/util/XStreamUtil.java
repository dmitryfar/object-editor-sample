package me.objecteditor.util;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

@Component
public class XStreamUtil {
  private static final Logger logger = LoggerFactory.getLogger(XStreamUtil.class);

  private XStream xStream;

  @PostConstruct
  private void name() {
    logger.info("there");

    XmlFriendlyNameCoder nameCoder = new XmlFriendlyNameCoder("ddd", "_");
    xStream = new XStream(new DomDriver("UTF-8", nameCoder));
  }

  public <T> String toXML(T obj) {
    return xStream.toXML(obj);
  }

  @SuppressWarnings("unchecked")
  public <T> T fromXML(String xml) {
    return (T) xStream.fromXML(xml);
  }
}
