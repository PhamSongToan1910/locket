package com.example.locket_clone.utils.fileUtils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Component
public class FileUtils {

    public static boolean validateFile(MultipartFile file) {
        return checkFileExtension(file.getOriginalFilename()) && checkFileSize(file.getSize());
    }

    private static boolean checkFileExtension(String fileName) {
        List<String> extensions = Arrays.asList(".jpg", ".png");
        return extensions.contains(fileName);
    }

    private static boolean checkFileSize(Long fileSize) {
        fileSize = fileSize / 1_048_576;
        return (fileSize > 0 && fileSize <= 5);
    }
}
