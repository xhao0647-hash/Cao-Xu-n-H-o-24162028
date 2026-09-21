package vn.iotstar.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Luu file upload (anh san pham, avatar) vao thu muc "uploads/<subFolder>/"
 * o ngoai classpath, tra ve duong dan public dang "/uploads/<subFolder>/<ten-file>".
 */
@Service
public class FileStorageService {

    public String store(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                ext = original.substring(dot);
            }
            String fileName = UUID.randomUUID() + ext;

            Path dir = Paths.get("uploads", subFolder);
            Files.createDirectories(dir);

            Path target = dir.resolve(fileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + subFolder + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Khong the luu file: " + e.getMessage(), e);
        }
    }
}
