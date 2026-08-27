package tn.knowflowai.backend.Config;   // or any package you prefer

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:images}")
    private String imageUploadDir;

    @Value("${app.file.upload.dir:files}")
    private String fileUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path imagePath = Paths.get(imageUploadDir).toAbsolutePath().normalize();
        Path filePath = Paths.get(fileUploadDir).toAbsolutePath().normalize();

        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:" + imagePath + "/");

        registry.addResourceHandler("/files/**")
            .addResourceLocations("file:" + filePath + "/");
    }
}