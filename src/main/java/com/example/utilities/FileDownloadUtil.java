package com.example.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
//Este es el componenre que vamos a inyectar en controller pa recoger la imagen del servidor
public class FileDownloadUtil {

    private Path foundFile;

    public Resource getFileAsResource(String fileCode) throws IOException {

        Path dirPath = Paths.get("Files-Upload");

        //por cada elemento que hay en esa ruta dirPath ve analizandomelo
        //Suponienfo q todo son ficheros(file) dame el nombre, cambialo a string y si empieza con
        //
        //el return es de la lamda para parar la iteracion pq he encontrado el fichero
        //Bueno mira esto está grabado miratelo maquina
        Files.list(dirPath).forEach(file -> {
            if(file.getFileName().toString().startsWith(fileCode)) {
                foundFile = file;

                return ;
            }
        });

        if(foundFile != null) {
            //Si encuentra el fichero nos dice: vale lo tengo!
            //pero aun no nos va a devolver el fichero del servidor, eso lo va a hacer
            //un metodo en controller que va a hacer uso de esta clase q estamos creando

            return new UrlResource(foundFile.toUri());
        }
        //si no lo encuentra devuelve null
        return null;
    }
    
}
