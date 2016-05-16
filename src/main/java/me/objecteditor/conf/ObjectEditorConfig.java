package me.objecteditor.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import me.objecteditor.util.ObjectEditorMetrics;

@Configuration
@ComponentScan(basePackages = "me.objecteditor")
/*@PropertySources(value = {
		@org.springframework.context.annotation.PropertySource("classpath:application-default.properties"),
		@org.springframework.context.annotation.PropertySource("file:${catalina.home}/application.properties")
})*/
@EnableWebMvc
public class ObjectEditorConfig extends WebMvcConfigurerAdapter {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/bootstrap/**").addResourceLocations("/bootstrap/");
    registry.addResourceHandler("/css/**").addResourceLocations("/css/");
    registry.addResourceHandler("/jquery/**").addResourceLocations("/jquery/");
    registry.addResourceHandler("/js/**").addResourceLocations("/js/");
  }

  @Bean
  ObjectEditorMetrics objectEditorMetrics() {
    return ObjectEditorMetrics.getInstance();
  }
}
