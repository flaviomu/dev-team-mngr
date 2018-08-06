package com.flaviomu.devteammngr.swagger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;


/**
 * Defines the Swagger configurations
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Builds the Swagger API for the application
     *
     * @return the builder for the SpringFox framework
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.flaviomu.devteammngr.web"))
                .paths(paths())
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                /*.globalResponseMessage()*/;

    }

    /*
        Provides the general info regarding the API
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("DEV TEAM MANAGER API")
                .version("1.0.0")
                .description("Below is a detailed description of the REST API of the DEV TEAM MANAGER application")
                .contact(new Contact("FM", "", ""))
                .license("GPL v3")
                .licenseUrl("https://www.gnu.org/licenses/gpl-3.0.html")
                .build();
    }

    /*
        Defines the paths for the API builder
     */
    private Predicate<String> paths() {
        return Predicates.or(regex("/users.*"));
    }

}
