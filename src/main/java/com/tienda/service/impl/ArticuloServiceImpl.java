package com.tienda.service.impl;

import com.tienda.dao.ArticuloDao;
import com.tienda.domain.Articulo;
import com.tienda.service.ArticuloService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticuloServiceImpl implements ArticuloService {
   @Autowired
    private ArticuloDao articuloDao;
    @Override
    @Transactional(readOnly=true)
    public List<Articulo> getArticulos(boolean activos) {
        var lista = (List<Articulo>) articuloDao.findAll();
        if (activos) {
            lista.removeIf(e -> !e.isActivo());
        }
        return lista;
        
    }
    @Override
    @Transactional(readOnly=true)
    public Articulo getArticulo(Articulo articulo) {
        return articuloDao.findById(articulo.getIdArticulo()).orElse(null);
    }
    @Override
    @Transactional(readOnly=false)
    public void deleteArticulo(Articulo articulo) {
        articuloDao.delete(articulo);
    }
    @Override
    @Transactional(readOnly=false)
    public void saveArticulo(Articulo articulo) {
        articuloDao.save(articulo);
    }

    @Override
    @Transactional(readOnly=true)
    public List<Articulo> getArticulosMetodoQuery(double precioInf, double precioSup) {
        return articuloDao.findByPrecioBetweenOrderByDescripcion(precioInf, precioSup);
    }

    @Override
    @Transactional(readOnly=true)
    public List<Articulo> getArticulosMetodoJPQA(double precioInf, double precioSup) {
        return articuloDao.metodoJPQL(precioInf, precioSup);
    }

    @Override
    @Transactional(readOnly=true)
    public List<Articulo> getArticulosMetodoNativo(double precioInf, double precioSup) {
        return articuloDao.metodoNativo(precioInf, precioSup);
    }
}
