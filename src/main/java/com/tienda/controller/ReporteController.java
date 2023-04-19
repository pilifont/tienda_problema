package com.tienda.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;

import java.util.Map;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    DataSource datasource;

    @GetMapping("/principal")
    public String principal() {
        return "/reportes/principal";
    }

    
    @GetMapping("/clientes")
    public ResponseEntity<Resource> reporteClientes(@RequestParam String tipo) throws IOException {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("titulo", "Final Reporte");
        String reporte = "clientes";
        return reportePrivado(reporte, parametros, tipo);
    }
    
    @GetMapping("/ventas")
    public ResponseEntity<Resource> reporteVentas(@RequestParam String tipo) throws IOException {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("titulo", "Final Reporte");
        String reporte = "ventas";
        return reportePrivado(reporte, parametros, tipo);
    }

    private ResponseEntity<Resource> reportePrivado(String reporte, Map<String, Object> parametros, String tipo) throws IOException {
        try {
            String reportPath = "reportes";

            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            ClassPathResource fuente = new ClassPathResource(reportPath + File.separator + reporte + ".jasper");

            InputStream elReporte = fuente.getInputStream();

            // Se genera el reporte
            var reporteJasper = JasperFillManager.fillReport(elReporte, parametros, datasource.getConnection());

            //Se evalua el tipo
            byte[] data = null;
            MediaType mediaType = null;
            String archivoSalida = "";
            String estilo="attachment; ";
            if ("vPdf".equals(tipo)) {
                estilo="inline; ";
            }
            
            if ("Pdf".equals(tipo) || "vPdf".equals(tipo)) {
                JasperExportManager.exportReportToPdfStream(reporteJasper, salida);
                mediaType = MediaType.APPLICATION_PDF;
                archivoSalida = reporte + ".pdf";            
            } else if ("Xls".equals(tipo)) {
                JRXlsxExporter exportador = new JRXlsxExporter();
                exportador.setExporterInput(new SimpleExporterInput(reporteJasper));
                exportador.setExporterOutput(new SimpleOutputStreamExporterOutput(salida));
                SimpleXlsxReportConfiguration configuracion = new SimpleXlsxReportConfiguration();
                configuracion.setDetectCellType(true);
                configuracion.setCollapseRowSpan(true);
                exportador.setConfiguration(configuracion);
                exportador.exportReport();
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                archivoSalida = reporte + ".xlsx";
            } else if ("Csv".equals(tipo)) {
                JRCsvExporter exportador = new JRCsvExporter();
                exportador.setExporterInput(new SimpleExporterInput(reporteJasper));
                exportador.setExporterOutput(new SimpleWriterExporterOutput(salida));
                SimpleCsvExporterConfiguration configuracion = new SimpleCsvExporterConfiguration();                
                exportador.setConfiguration(configuracion);
                exportador.exportReport();
                mediaType = MediaType.TEXT_PLAIN;
                archivoSalida = reporte + ".csv";
            }
            data = salida.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Disposition", estilo+"filename=\"" + archivoSalida + "\"");
            return ResponseEntity.ok().headers(headers).contentLength(data.length).contentType(mediaType).body(new InputStreamResource(new ByteArrayInputStream(data)));

        } catch (SQLException | JRException e) {
            return null;
        }

    }

}
