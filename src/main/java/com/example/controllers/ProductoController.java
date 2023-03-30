package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Producto;
import com.example.services.ProductoService;

import jakarta.validation.Valid;

//Anotacion para que nos devuelva datos en formato JSON
@RestController
@RequestMapping("/productos")

public class ProductoController {

    //Para inyectar dependencia 
    @Autowired
    private ProductoService productoService;

    /** El siguiente método va a responder a una peticion (request) del tipo:
     * http://localhost:8080/productos?page=1&size=4,
     * es decir, que tiene que ser capaz de devolver un listado de productos
     * paginados, o no, pero en cualquier caso ordenador por un criterio (nombre,
     * descripcion, etc)
     * Esto implica el uso de @RequestParam
     * 
     * Otra peticion de tipo 
     * /productos/3 => @PathVariable
     */
    @GetMapping //le voy a hacer la peticion por get
    public ResponseEntity<List<Producto>> findAll(@RequestParam( name = "page", required = false) Integer page,
                                                  @RequestParam(name = "size", required = false) Integer size) {
           
           ResponseEntity<List<Producto>> responseEntity = null;

           //Comprobamos si hemos recibido paginas o no
           List<Producto> productos = new ArrayList<>(); 
            
           //El criterio de ordenamiento lo sacamos del if para que nos sirva tanto si queremos paginacion como si no
           //El sort admite una lista de propiedades por las que sortear, enumeradas por comas ,
           Sort sortByNombre = Sort.by("nombre");

           if( page != null && size != null) {

                try {
                    Pageable pageable = PageRequest.of(page, size, sortByNombre);
                    Page<Producto> productosPaginados = productoService.findAll(pageable); 
                    productos = productosPaginados.getContent();
                    responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK); 
                   
                } catch (Exception e) {
                    // En el catch solo podemos mandar informacion de la peticion
                    responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
             } else {
                //Sin paginacion, pero con ordenamiento
                try {
                    productos = productoService.findAll(sortByNombre);
                    responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);

                } catch (Exception e) {
                    // TODO: handle exception
                    responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }

                }
           
           return responseEntity;

    } 

    /**
     * Recupera un producto por el id
     * Va a responder a una peticion del tipo, por ejemplo:
     * http://localhost:8080/productos/2
     */

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable(name = "id") Integer id) {

        ResponseEntity<Map<String, Object>> responseEntity = null;
        Map<String, Object> responseAsMap = new HashMap<>();

        try {
            Producto producto = productoService.findById(id);

            if(producto != null) {

            String successMessage = "Se ha encontrado el producto con id: " + id;
            responseAsMap.put("mensaje", successMessage);
            responseAsMap.put("producto", producto);
              
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK); 
        
        } else {

            String errorMessage = "No se ha encontrado el producto con id: " + id;
            responseAsMap.put("error", errorMessage);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.NOT_FOUND);
        }
           
        } catch (Exception e) {
            // TODO: handle exception
            String errorGrave = "Error grave";
            responseAsMap.put("error", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;
    }

    /** El siguiente método persiste un producto en la base de datos */
   
    //Un mapa para poder mandar un mensaje con el string y un object lo que necesitamos y otro 
    //mensaje o lo que sea, simplemente pa q no sea otra vez string creo
    //Usamos Hash map pq si no necesitamos ordenamiento pues este es mas rapido que el que si
    //permite ordenamiento (treemap)
    //Se anota con postmapping pq va a recibir los datos del formulario. Con post lo que manda
    //va DENTRO. En get va en la CABEZERA.

    @PostMapping
    @Transactional //spring
    public ResponseEntity<Map<String, Object>> insert(@Valid @RequestBody Producto producto,
                                                                 BindingResult result) {

        Map<String, Object> resopnseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        /** Primero: Comprobar si hay errores en el producto recibido */
        if(result.hasErrors()) {

            //Aquí guardamos los errores
            List<String> errorMessages = new ArrayList<>();

            //un for mejorado para recorrerlos creamos la coleccion, despues de los puntos estan
            //donde estan (result)y luego .getAllErrors, ahi ya nos pide que a la izq haya ObjectError
            for(ObjectError error : result.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }

            resopnseAsMap.put("errores", errorMessages);

            responseEntity = new ResponseEntity<Map<String,Object>>(resopnseAsMap, HttpStatus.BAD_REQUEST);
            //Return para no salir del if y no guardar. Solo devolvemos mensaje error
            return responseEntity; 

        }
        //Si no hay errores, entonces se persiste el producto (se guarda mi hermano)
        Producto productoDB = productoService.save(producto);

        try {
            if(productoDB != null) {
                String mensaje = "El producto se ha creado correctamente";
                resopnseAsMap.put("mensaje", mensaje);
                resopnseAsMap.put("producto", productoDB);
                responseEntity = new ResponseEntity<Map<String, Object>>(resopnseAsMap, HttpStatus.CREATED);
    
            } else {
                //No se ha creado el producto
            }
            
        } catch (DataAccessException e) {
            String errorGrave = "Se ha producido un error grave" 
                                 + ", y la causa más probable puede ser" 
                                    + e.getMostSpecificCause();

            resopnseAsMap.put("errorGrave", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(resopnseAsMap,
                                                                 HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    /** Actualiza un producto en la base de datos */
    //Es basicamente igual que el de crear uno de arriba
    @PutMapping("/{id}")
    @Transactional //spring
    public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody Producto producto,
                                    BindingResult result,
                                    @PathVariable(name = "id") Integer id) {

        Map<String, Object> resopnseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        /** Primero: Comprobar si hay eerores en el producto recibido */
        if(result.hasErrors()) {

            //Aquí guardamos los errores
            List<String> errorMessages = new ArrayList<>();

            //un for mejorado para recorrerlos creamos la coleccion, despues de los puntos estan
            //donde estan (result)y luego .getAllErrors, ahi ya nos pide que a la izq haya ObjectError
            for(ObjectError error : result.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }

            resopnseAsMap.put("errores", errorMessages);

            responseEntity = new ResponseEntity<Map<String,Object>>(resopnseAsMap, HttpStatus.BAD_REQUEST);
            //Return para no salir del if y no guardar. Solo devolvemos mensaje error
            return responseEntity; 

        }
        //Si no hay errores, entonces se persiste el producto (se guarda mi hermano)
        //al producto que vas a persistir lo vinculamos con el id que se recibe con el producto
        producto.setId(id);
        Producto productoDB = productoService.save(producto);

        try {
            if(productoDB != null) {
                String mensaje = "El producto se ha actualizado correctamente";
                resopnseAsMap.put("mensaje", mensaje);
                resopnseAsMap.put("producto", productoDB);
                responseEntity = new ResponseEntity<Map<String, Object>>(resopnseAsMap, HttpStatus.OK);
    
            } else {
                //No se ha actualizado el producto
            }
            
        } catch (DataAccessException e) {
            String errorGrave = "Se ha producido un error grave" 
                                 + ", y la causa más probable puede ser" 
                                    + e.getMostSpecificCause();

            resopnseAsMap.put("errorGrave", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(resopnseAsMap,
                                                                 HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }


    /**Método para borrar un producto de la base de datos */
    //Si tuviesemos metodo para borrar por id sería más rapido, pero con lo que tenemos
    //Es así:
    @DeleteMapping("/{id}")
    @Transactional //spring
    public ResponseEntity<String> delete(@PathVariable(name = "id") Integer id) {

            ResponseEntity<String> responseEntity = null;

        try {
            //Primero lo recuperamos
            Producto producto = productoService.findById(id);

            if(producto != null) {
               productoService.delete(producto);
               responseEntity = new ResponseEntity<String>("Borrado exitosamente", HttpStatus.OK);

            } else {

                responseEntity = new ResponseEntity<String>("No existe el producto buscado", HttpStatus.NOT_FOUND);
            }
            
        } catch (DataAccessException e) {
            e.getMostSpecificCause();
            responseEntity = new ResponseEntity<String>("Error fatal", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }


    /** El método siguiente es de ejemplo para entender el formato JSON,
     * no tiene que ver en sí con el proyecto */

    //  @GetMapping
    //  public List<String> nombres() {
    //     List<String> nombres = Arrays.asList(
    //         "Salma", "Judith", "Elisabet"
    //     ); 
    //     return nombres;
    //  }
    
}
