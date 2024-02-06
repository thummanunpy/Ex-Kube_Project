//package io.learnk8s.knotejava.configuration;
//
//import io.learnk8s.knotejava.properties.KnoteProperties;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.resource.PathResourceResolver;
//
//
//@Configuration
//@EnableConfigurationProperties(KnoteProperties.class)
//public class KnoteConfig implements WebMvcConfigurer {
//
//    @Autowired
//    private KnoteProperties knoteProperties;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry
//                .addResourceHandler("/uploads/**")
//                .addResourceLocations("file:" + knoteProperties.getUploadDir())
//                .setCachePeriod(3600)
//                .resourceChain(true)
//                .addResolver(new PathResourceResolver());
//    }
//
//
//}
