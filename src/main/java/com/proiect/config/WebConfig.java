/** Clasa de configurare pentru maparea resurselor statice (imagini).
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */

package com.proiect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer { // <--- ACUM NUMELE E CORECT

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Asta face ca pozele sa se incarce instantaneu din folderul "uploads"
        // fara sa mai fie nevoie de restart
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}