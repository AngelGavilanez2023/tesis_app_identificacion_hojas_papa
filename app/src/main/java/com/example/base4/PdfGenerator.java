package com.example.base4;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.example.base4.modelo.Resultados;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfGenerator {

    public static void generatePDF(List<Resultados> listaResultados, Context context) {
        try {
            // Obtener el directorio de almacenamiento externo específico de la aplicación
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }

            // Crear el archivo PDF en el directorio
            File file = new File(dir, "InformeResultados.pdf");

            // Inicializar PdfWriter y Document
            PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            // Agregar contenido al PDF (aquí puedes personalizar según tus necesidades)
            for (Resultados resultado : listaResultados) {
                document.add(new Paragraph("ID: " + resultado.getId()));
                document.add(new Paragraph("Enfermedad: " + resultado.getEnfermedad()));
                document.add(new Paragraph("Accuracy: " + resultado.getAccuracy()));
                document.add(new Paragraph("Fecha y Hora: " + resultado.getFecha_hora()));
                document.add(new Paragraph("Tratamiento: " + resultado.getTratamiento()));
                document.add(new Paragraph("Dosis: " + resultado.getDosis()));
                document.add(new Paragraph("-----------------------------------"));
            }

            document.close();
            Log.d("PDF Generator", "PDF generado correctamente en " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
