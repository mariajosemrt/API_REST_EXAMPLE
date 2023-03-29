package com.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.dao.ProductoDao;
import com.example.entities.Producto;

@Service
public class ProductoServiceImpl implements ProductoService {

    //Para cuando necesitemos un objeto de tipo dao lo inyectamos creando la variable y
    //con la anotacion que inyecta @autowired
    @Autowired
    private ProductoDao productoDao;

    @Override
    public List<Producto> findAll(Sort sort) {
        return productoDao.findAll(sort);
    }

    @Override
    public Page<Producto> findAll(Pageable pageable) {
        return productoDao.findAll(pageable);
    }

    @Override
    public Producto findById(long id) {
        return productoDao.findById(id);
    }

    @Override
    public Producto save(Producto producto) {
        return productoDao.save(producto); 
    }

    @Override
    public void delete(Producto producto) {
        productoDao.delete(producto);
    }
    
}
