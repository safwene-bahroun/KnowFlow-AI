package tn.knowflowai.backend.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImageStorageService {

    @Value("${app.upload.dir:images}")
    private String uploadDir;

    /**
     * Receives a base64 data URL (or null / already a path)
     * Saves the file under the images folder
     * Returns the public path that should be stored in the database
     * e.g. "/images/a1b2c3d4-e5f6-....jpg"
     */
    public String saveBase64Image(String base64DataUrl) throws IOException {

        if (base64DataUrl == null || base64DataUrl.isBlank()) {
            return null;
        }

        // If it is already a normal path/URL, just return it
        if (!base64DataUrl.startsWith("data:image")) {
            return base64DataUrl;
        }

        // data:image/jpeg;base64,/9j/4AAQ...
        String[] parts = base64DataUrl.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid base64 image data");
        }

        String header = parts[0];   // data:image/jpeg;base64
        String base64 = parts[1];

        // Detect file extension
        String extension = "jpg";
        if (header.contains("png")) {
            extension = "png";
        } else if (header.contains("gif")) {
            extension = "gif";
        } else if (header.contains("webp")) {
            extension = "webp";
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        // Create the folder if it does not exist
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // Unique file name
        String fileName = UUID.randomUUID() + "." + extension;
        Path target = uploadPath.resolve(fileName);

        Files.write(target, imageBytes);

        // Path that will be stored in the database and served by WebConfig
        return "/images/" + fileName;
    }
}