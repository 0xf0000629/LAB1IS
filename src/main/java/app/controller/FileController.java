package app.controller;

import app.MinioService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private MinioService minioService;

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam("filename") String fileName
    ) {
        try {
            // Fetch the file from MinIO
            InputStream fileStream = minioService.minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("files")
                            .object(fileName)
                            .build()
            );

            // Build the response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            System.out.println("download of " + fileName + " FAILED");
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}