package com.github.yash777.myworld.api;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

//@EnableWebMvc
//@Configuration
//@PropertySource(ignoreResourceNotFound = true, value = {"file:${CONFIG_HOME}/application.properties", "file:${CONFIG_HOME}/application_override.properties"})
@Slf4j
public class WebConfig extends WebMvcConfigurerAdapter {

	@Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(jaksonMessageConverter());
        converters.add(stringConverter());
        super.configureMessageConverters(converters);
    }
    @Bean
    public MappingJackson2HttpMessageConverter jaksonMessageConverter() {
        final MappingJackson2HttpMessageConverter jaksonMessageConverter = new MappingJackson2HttpMessageConverter();
        jaksonMessageConverter.setObjectMapper(objectMapper());
        return jaksonMessageConverter;
    }
    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        //mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false); // don't sort alphabetically
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // since 2.10 recommended to use ALLOW_JAVA_COMMENTS - Java/C++ style comments (both '/'+'*' and '//' varieties)
        mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true);
        //objectMapper.configure(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_JAVA_COMMENTS, true);
        return mapper;
    }
    @Bean
    public StringHttpMessageConverter stringConverter() {
        final StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.TEXT_HTML, MediaType.APPLICATION_JSON));
        return stringConverter;
    }
    
    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        super.addCorsMappings(registry);
        // https://en.wikipedia.org/wiki/Cross-origin_resource_sharing
        registry.addMapping("/*/**").allowedMethods("PUT", "DELETE", "POST", "GET", "OPTIONS").allowedOrigins("*");
        registry.addMapping("/*").allowedMethods("PUT", "DELETE", "POST", "GET", "OPTIONS").allowedOrigins("*");
    }
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/");

    }
    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        registry.addViewController("/docs").setViewName("documentation");
    }
    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        super.extendMessageConverters(converters);
    }


//    @Bean
//    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> embeddedServletContainerCustomizer() {
//
//        return new WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>() {
//            private static final String CONTEXT_PATH = "/weather-api";
//
//            @Override
//            public void customize(final ConfigurableServletWebServerFactory factory) {
//                int port = 8084;
//                try {
//                    final String portString = System.getProperty("server.port");
//                    if (portString != null) {
//                        port = Integer.parseInt(portString);
//                    }
//                    log.info("Server running on the port {} ", port);
//                } catch (final NumberFormatException e) {
//                    log.debug("Error while reading server.port ", e);
//                }
//                log.trace("Customizing embeddedServlet container  using port :{} and contextPath :{}", port, CONTEXT_PATH);
//                factory.setPort(port);
//            }
//        };
//    }

//    @Bean(name = "messageSource")
//    public ReloadableResourceBundleMessageSource messageSource() {
//        final ReloadableResourceBundleMessageSource messageBundle = new ReloadableResourceBundleMessageSource();
//        messageBundle.setBasename("classpath:messages/messages");
//        messageBundle.setUseCodeAsDefaultMessage(true);
//        messageBundle.setDefaultEncoding("UTF-8");
//        return messageBundle;
//    }
//
//    @Bean
//    public BufferingClientHttpRequestFactory requestFactory() {
//        return new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory());
//    }
//    @Bean
//    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
//        final SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
//        simpleClientHttpRequestFactory.setReadTimeout(3000);
//        simpleClientHttpRequestFactory.setConnectTimeout(3000);
//        simpleClientHttpRequestFactory.setBufferRequestBody(true);
//        simpleClientHttpRequestFactory.setOutputStreaming(true);
//        return simpleClientHttpRequestFactory;
//    }
//
//    /*
//server.session.cookie.secure=true
//server.session.cookie.http-only=true
//     */
//    @Bean
//    public ServletContextInitializer servletContextInitializer(
//    		@Value("${server.session.cookie.secure}") boolean secure,
//            @Value("${server.session.cookie.http-only}") boolean httpOnly) {
//        return new ServletContextInitializer() {
//
//            @Override
//            public void onStartup(ServletContext servletContext) throws ServletException {
//                servletContext.getSessionCookieConfig().setSecure(secure);
//                servletContext.getSessionCookieConfig().setHttpOnly(httpOnly);
//            }
//        };
//    }
//
//    @PostConstruct
//    private void init() {
//
//    }
}