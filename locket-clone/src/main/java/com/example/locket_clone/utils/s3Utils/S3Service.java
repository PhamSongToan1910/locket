package com.example.locket_clone.utils.s3Utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    }

    public void deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    public String getFileNameFromURl(String URl) {
        if(StringUtils.hasLength(URl)) {
            String[] url = URl.split("/");
            return url[url.length - 1];
        }
        return null;
    }

}
