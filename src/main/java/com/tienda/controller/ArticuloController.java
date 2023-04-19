package com.tienda.controller;

import com.tienda.domain.Articulo;
import com.tienda.service.ArticuloService;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/articulo")
public class ArticuloController {
    @Autowired
    ArticuloService articuloService;   
     
     
    @GetMapping("/listado")
    public String inicio(Model model) {
     //   var articulos = articuloService.getArticulos(false);
     var articulos = articuloService.getArticulos(false);
        model.addAttribute("articulos", articulos);
        model.addAttribute("totalArticulos",articulos.size());        
        return "/articulo/listado";
    }

    @GetMapping("/eliminar/{idArticulo}")
    public String eliminaArticulo(Articulo articulo) {
        articuloService.deleteArticulo(articulo);
        return "redirect:/articulo/listado";
    }

    @GetMapping("/nuevo")
    public String nuevoArticulo(Articulo articulo) {
        return "/articulo/modifica";
    }
    @PostMapping("/guardar")
    public String guardaArticulo(Articulo articulo) {
        articuloService.saveArticulo(articulo);
        return "redirect:/articulo/listado";
    }

    @GetMapping("/modificar/{idArticulo}")
    public String modificaArticulo(Articulo articulo, Model model) {
        articulo = articuloService.getArticulo(articulo);
        model.addAttribute("articulo", articulo);
        return "/articulo/modifica";
    }
    
     @GetMapping("/reporte")
    public ResponseEntity<byte[]> generateReport() {
        try {
            
            String reportPath = "src/main/resources";

            // Compile the Jasper report from .jrxml to .japser
            JasperReport jasperReport = JasperCompileManager.compileReport(reportPath + "/articulos.jrxml");

            // Get your data source
            JRBeanCollectionDataSource datos = new JRBeanCollectionDataSource(articuloService.getArticulos(true));

            // Add parameters
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("titulo", "Final Reporte");

            // Fill the report
            JasperPrint reporte = JasperFillManager.fillReport(jasperReport, parametros,datos);

            // Export the report to a PDF file
            byte[] data=JasperExportManager.exportReportToPdf(reporte);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION,"inline;filename=clientes.pdf");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
 

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
