package com.tienda.service;

import com.tienda.domain.Categoria;
import java.util.List;

public interface CategoriaService {
    
     //Se recupera una lista con todos los registros de la tabla categoria
    public List<Categoria> getCategorias(boolean activos);
    
    //Se recupera el registro que tiene el idCategoria pasado por parámetro
    //si no existe en la tabla se retorna null
    public Categoria getCategoria(Categoria categoria);
    
    //Se elimina el registro que tiene el idCategoria pasado por parámetro
    public void deleteCategoria(Categoria categoria);
    
    //Si el objeto categoria tiene un idCategoria que existe en la tabla categoria
    //El registro de actualiza con la nueva información
    //Si el idCategoria NO existe en la tabla, se crea el registro con esa información
    public void saveCategoria(Categoria categoria);

}
