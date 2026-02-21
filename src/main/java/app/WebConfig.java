package app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{

        private final RateLimiter rateLimiter;

        public WebConfig(){
            this.rateLimiter = new RateLimiter();
        }
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            System.out.println("OH YEAH CORS ALLOWED");
            registry.addMapping("/**") // Allow all endpoints
                    .allowedOrigins("http://localhost:3000") // Replace with allowed domain(s)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specific HTTP methods
                    .allowedHeaders("*") // Allow all sheaders
                    .allowCredentials(true); // Allow cookies or credentials
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry){
            registry.addInterceptor(rateLimiter).addPathPatterns("/api/auth/login", "/api/cities", "/api/download");
        }
}