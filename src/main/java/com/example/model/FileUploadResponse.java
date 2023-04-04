package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FileUploadResponse {
    
    private String fileName;
    //Creacion un link para descargarte la imagen del producto directamente
    private String downloadURI;
    private long size;
}
