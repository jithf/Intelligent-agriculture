package jit.hf.agriculture.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * Author: zj
 * Description ：springMVC的配置
 */
@Configuration
public class MyWebAppConfigurer extends WebMvcConfigurerAdapter {
    /**
     * 自定义静态资源文件目录实现，让项目能找到与之对应的资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/src/main/webapp/**").addResourceLocations("classpath:/webapp/");
        registry.addResourceHandler( "/static/**" ).addResourceLocations( "classpath:/static/" );//
        super.addResourceHandlers(registry);
    }

    /**
     * 后端 cors解决跨域问题
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");//允许所有路径可以跨域
    }
}
