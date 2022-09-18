package com.spring.boot.upload.example.concurrent.service;

import com.spring.boot.upload.example.concurrent.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileService {

    @Value("${app.upload.dir:D:\\uploads}")
    public String uploadDir;

    private final ExecutorService executorService = Executors
            .newWorkStealingPool(Runtime.getRuntime().availableProcessors());

    private void uploadFile(MultipartFile file)
    {
        try {
            Path copyLocation = Paths.get(uploadDir + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
            Files.copy(file.getInputStream(),copyLocation, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("File Not Found");
        }

    }

    public void uploadFilesSequentially(List<MultipartFile> files) {

        files.forEach(this::uploadFile);

    }

    public void uploadFilesConcurrently(List<MultipartFile> multipartFiles) {

        if(Files.notExists(Paths.get(uploadDir))) {
            try {
                Files.createDirectory(Paths.get(uploadDir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            multipartFiles
                    .parallelStream()
                    .forEach(file -> {
                        try {
                            CompletableFuture.runAsync(() -> retrieveFutureObject(file)).get();
                        }  catch (InterruptedException | ExecutionException exception) {
                            System.out.println(exception.getMessage());
                        }
                    });

    }

    private void retrieveFutureObject(MultipartFile file) throws FileStorageException {

        CompletableFuture.supplyAsync(() -> Paths.get(uploadDir + File.separator +
                        StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()))), executorService)
                .thenApplyAsync(copyLocation -> copyFileToDestination(copyLocation, file), executorService);

    }

    private long copyFileToDestination(Path copyLocation,MultipartFile file)  {
        try {
            return Files.copy(file.getInputStream(),copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException(e.getMessage());
        }
    }
}
