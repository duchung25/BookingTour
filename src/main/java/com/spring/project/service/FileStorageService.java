package com.spring.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Service lưu trữ file ảnh trên thư mục local.
 * Ảnh được lưu tại thư mục cấu hình bởi app.upload.dir (mặc định: uploads/tours).
 */
@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads/tours}")
    private String uploadDir;

    private Path uploadPath;

    /** Các định dạng ảnh được phép upload */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp", "gif"
    );

    /**
     * Tạo thư mục upload khi ứng dụng khởi động (nếu chưa tồn tại).
     */
    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload: " + uploadPath, e);
        }
    }

    /**
     * Lưu file ảnh vào thư mục upload.
     *
     * @param file MultipartFile từ form upload
     * @return tên file đã lưu (VD: "a1b2c3d4.jpg")
     * @throws IllegalArgumentException nếu file rỗng hoặc không phải ảnh
     * @throws RuntimeException nếu không thể lưu file
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được để trống");
        }

        // Lấy extension và validate
        String originalName = file.getOriginalFilename();
        String extension = getFileExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Định dạng file không hợp lệ. Chỉ chấp nhận: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // Sinh tên file duy nhất bằng UUID
        String fileName = UUID.randomUUID().toString() + "." + extension.toLowerCase();
        Path targetPath = uploadPath.resolve(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file: " + fileName, e);
        }

        return fileName;
    }

    /**
     * Xóa file ảnh khỏi thư mục upload.
     *
     * @param fileName tên file cần xóa (VD: "a1b2c3d4.jpg")
     */
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        try {
            Path filePath = uploadPath.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log lỗi nhưng không throw — việc xóa file không nên block luồng chính
            System.err.println("Không thể xóa file: " + fileName + " - " + e.getMessage());
        }
    }

    /**
     * Lấy extension từ tên file.
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
