package com.shopsphere.productservice.service.impl;

import com.shopsphere.productservice.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements IFileService {

    @Override
    public String uploadImage(final MultipartFile image, final String imageDirectoryName) throws Exception {
        final String originalFilename = image.getOriginalFilename();

        final String fileName = UUID.randomUUID().toString()
                .concat(Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".")));

        final File newDir = new File(imageDirectoryName);
        if (!newDir.exists()) newDir.mkdir();

        final String filePath = imageDirectoryName + File.separator + fileName;
        Files.copy(image.getInputStream(), Paths.get(filePath));

        return filePath.substring("images/".length());
    }
}
