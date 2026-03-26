package com.github.yash777.myworld.api.swagger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * 
🧪 Test It

✅ Access Secure APIs from Swagger UI
Once the user is logged in via the form, Swagger UI uses the existing session cookie (JSESSIONID) to authenticate requests.

🔁 Try These in Same Browser:
✅ Login form: http://localhost:8080/public/mylogin.html → Login
✅ Open Swagger UI in same browser tab: http://localhost:8080/swagger-ui/index.html
🔐 Access protected endpoint like /api/users/me → should succeed

 * @author Yash
 *
 */
/**
 * 🔧 Swagger/OpenAPI configuration.
 *
 * Adds metadata for the API and defines server URLs.
 *
 * ⚠️ Server URLs are **informational** and help Swagger UI know what base URL to use.
 *     They do NOT enforce redirection or authentication on their own.
 *
 * 💡 You can include your form login page path as a UI note in the description,
 *     but servers are meant for API base URLs, not HTML pages.
 */
@OpenAPIDefinition(
    info = @Info(
        title = "My World API",
        version = "1.0",
        description = """
        📘 My World API Documentation.

        <ul>🔐 Note: To access secured endpoints.
        <li> Click here to access <a href="/myworld/public/mylogin.html"> 👉 Login Page</a> 
        on success Redirects to <a href="/myworld/swagger-ui/index.html">🔁 Swagger UI</a>
        <li>To access Swagger UI URLs in doc style of metadata, OpenAPI Specs (YAML/JSON) of all services: 
        <a href="/myworld/v3/api-docs">JSON 🧾 /v3/api-docs</a>, <a href="/myworld/v3/api-docs.yaml">YAML 🧾 /v3/api-docs.yaml</a>
        <li> Access an <a href="/myworld/public/mylogin.html">🌐 Unauthorized resource</a> which redirects to Login Page.
        <li> Production-ready endpoints via Spring Boot Actuator are included under the <a href="/myworld/actuator">🖋️ /actuator</a> group.
        </ul>
        """
    ),
    servers = { // Include context-path=/myworld
        @Server(url = "http://localhost:8080/myworld", description = "Local server"),
        @Server(url = "https://myworld.example.com/myworld", description = "Production server")
    }
)
@Configuration
public class SwaggerConfig {
	
    /**
     * 🧩 Filter to detect Swagger UI requests based on Referer header.
     * If a request comes from Swagger UI, adds a cookie `X-Swagger-UI=true`
     */
    @Bean
    @Order(1) // Apply early in the filter chain /myworld/v3/api-docs
    public Filter swaggerRefererFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {

                HttpServletRequest req = (HttpServletRequest) request;
                HttpServletResponse res = (HttpServletResponse) response;

                String referer = req.getHeader("Referer");
                if (referer != null && referer.contains("/swagger-ui")) {
                    // Set a cookie so future requests are marked as from Swagger UI
                    res.addHeader("Set-Cookie", "X-Swagger-UI=true; Path=/; HttpOnly");
                }

                chain.doFilter(request, response);
            }
        };
    }
    
    //http://localhost:8080/myworld/actuator/info
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//            .info(new io.swagger.v3.oas.models.info.Info()
//                .title("My API with Actuator")
//                .version("1.0")
//                .description("This Swagger UI includes actuator endpoints like /actuator/health, /metrics, etc."));
//    }
    
//    @Bean // /myworld/v3/api-docs/actuator
//    public GroupedOpenApi actuatorGroup() {
//        return GroupedOpenApi.builder()
//            .group("actuator")
//            .pathsToMatch("/actuator/**")
//            .build();
//    }
    
//    @Bean
//    public GroupedOpenApi onlineModule() {
//        return GroupedOpenApi.builder()
//                .group("Online Module")
//                .pathsToMatch("/xml/**", "/json/**", "/text/**")
//                .build();
//    }
}