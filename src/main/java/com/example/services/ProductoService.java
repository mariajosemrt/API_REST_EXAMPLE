package com.example.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.entities.Producto;

public interface ProductoService {
    
    //Sort y Pageable vienen de springframework data domain!!

    //metodo para encontrar todos los productos pero ordenador x criterio, no el de siempre
    public List<Producto> findAll(Sort sort);

    //Metodo para paginar una lista de productos 
    public Page<Producto> findAll(Pageable pageable);

    public Producto findById(long id);

    public Producto save(Producto producto);

    public void delete(Producto producto);
}

