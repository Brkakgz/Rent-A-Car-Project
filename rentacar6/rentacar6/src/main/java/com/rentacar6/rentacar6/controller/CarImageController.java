package com.rentacar6.rentacar6.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/cars")
public class CarImageController {

    @Value("${upload.path}")
    private String uploadPath; // Görsellerin kaydedileceği dizin

    // Araç görseli yükleme endpoint'i
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadCarImage(@RequestParam("file") MultipartFile file) {
        try {
            // Benzersiz dosya adı oluştur
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadPath).resolve(fileName);

            // Dosyayı kaydet
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Görselin URL'sini döndür
            return ResponseEntity.ok("/uploads/cars/" + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }

    // Görseli silme endpoint'i
    @DeleteMapping("/delete-image/{fileName}")
    public ResponseEntity<String> deleteCarImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileName);
            Files.deleteIfExists(filePath);
            return ResponseEntity.ok("Image deleted successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete image: " + e.getMessage());
        }
    }

    @PostMapping("/save-file-path")
    public ResponseEntity<String> saveFilePath(@RequestParam("filePath") String filePath) {
        // Dosya yolunu alıp işleyin
        String fullPath = "/uploads/cars/" + Paths.get(filePath).getFileName().toString();
        // Burada veritabanına kaydetme işlemini yapabilirsiniz (isteğe bağlı)
        return ResponseEntity.ok("Dosya yolu başarıyla kaydedildi: " + fullPath);
    }

}
