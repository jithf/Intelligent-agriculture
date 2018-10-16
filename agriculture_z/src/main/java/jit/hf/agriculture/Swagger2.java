package jit.hf.agriculture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Auther: zj
 * @Date: 2018/6/24 22:07
 * @Description:添加Swagger2配置类
 */
@Configuration
@EnableSwagger2 //@EnableSwagger2来启动swagger注解
public class Swagger2 {

    @Bean
    public Docket createRestApi() {
        return new Docket( DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any()) // 对所有api进行监控
                .paths(PathSelectors.any()) // 对所有路径进行监控
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot中使用Swagger2构建RESTful APIs")
               // .description("更多Spring Boot相关文章请关注：http://blog.didispace.com/")
                //.termsOfServiceUrl("http://blog.didispace.com/")
                .contact("Zj")
                .version("1.0")
                .build();
    }

}
