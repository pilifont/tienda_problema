package com.tienda.dao;

import com.tienda.domain.Articulo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloDao extends JpaRepository<Articulo, Long>{
   
    //Ejemplo de método utilizando Métodos de Query
    public List<Articulo> findByPrecioBetweenOrderByDescripcion(double precioInf, double precioSup);
    
    //Ejemplo de método utilizando Consultas con JPQL
    @Query(value="SELECT a FROM Articulo a where a.precio BETWEEN :precioInf AND :precioSup ORDER BY a.descripcion ASC")
    public List<Articulo> metodoJPQL(@Param("precioInf") double precioInf, @Param("precioSup") double precioSup);
    
    //Ejemplo de método utilizando Consultas con Native Query SQL
    @Query(nativeQuery=true,
            value="SELECT * FROM articulo where articulo.precio BETWEEN :precioInf AND :precioSup ORDER BY articulo.descripcion ASC")
    public List<Articulo> metodoNativo(@Param("precioInf") double precioInf, @Param("precioSup") double precioSup);
}