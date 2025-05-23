package com.shopsphere.porductservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    String uploadImage(final MultipartFile image, final String imageDirectoryName) throws Exception;
}
