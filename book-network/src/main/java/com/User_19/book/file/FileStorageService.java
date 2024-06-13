package com.User_19.book.file;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@Slf4j
public class FileStorageService {
    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;
    public String saveFile(@Nonnull  MultipartFile sourceFile,
                           @Nonnull  Integer id) {
        final String fileUploadSubPath= "users"+ separator +id;
        return uploadFile(sourceFile,fileUploadSubPath);


    }

    private String uploadFile(@Nonnull MultipartFile sourceFile,
                              @Nonnull String fileUploadSubPath) {
        final String finalUploadPath= this.fileUploadPath + separator +fileUploadSubPath;
        File targetFolder= new File(finalUploadPath);
        if (!targetFolder.exists()){
            boolean folderCreated=targetFolder.mkdirs();
            if(!folderCreated){
                log.warn("Failed to create the target folder ");
                return null;

            }
        }
        final String fileExtension=getFileExtenstion(sourceFile.getOriginalFilename());
        String targetFilePath=finalUploadPath+separator+System.currentTimeMillis()+"."+fileExtension;
        Path targetPath= Paths.get(targetFilePath);
        try {
            Files.write(targetPath,sourceFile.getBytes());
            log.info("file saved succsufully");
            return targetFilePath;
        } catch (IOException e) {
            log.error("File was not saved",e);


        }
        return null;


    }

    private String getFileExtenstion(String fileName) {
        if (fileName.equals("") || fileName.isEmpty()){
            return "";
        }
        int LastIndexPt=fileName.lastIndexOf(".");
        return fileName.substring(LastIndexPt+1).toLowerCase();
    }
}
