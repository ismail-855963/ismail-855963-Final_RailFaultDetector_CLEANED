package com.example.railfalldetector.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.railfalldetector.data.model.FaultEvent
import com.example.railfalldetector.data.model.SensorData
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder

object FileExportHelper {
    fun exportCsv(context: Context, data: List<SensorData>, fileName: String): Uri? {
        val csvBuilder = StringBuilder().apply {
            append("id,timestamp,type,x,y,z\n")
            data.forEach { append("${it.id},${it.timestamp},${it.type},${it.x},${it.y},${it.z}\n") }
        }
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "$fileName.csv")
        FileOutputStream(file).use { it.write(csvBuilder.toString().toByteArray()) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
    fun exportPdf(context: Context, data: List<SensorData>, fileName: String): Uri? {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "$fileName.pdf")
        val writer = PdfWriter(file)
        val pdf = com.itextpdf.kernel.pdf.PdfDocument(writer)
        val document = Document(pdf).apply { add(Paragraph("Sensor Data Report")) }
        data.forEach {
            document.add(Paragraph("ID: ${it.id}, Time: ${it.timestamp}, x=${it.x}, y=${it.y}, z=${it.z}"))
        }
        document.close()
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
    fun exportExcel(context: Context, data: List<SensorData>, fileName: String): Uri? {
        val workbook = XSSFWorkbook().apply {
            createSheet("SensorData").also { sheet ->
                sheet.createRow(0).apply {
                    createCell(0).setCellValue("ID")
                    createCell(1).setCellValue("Timestamp")
                    createCell(2).setCellValue("Type")
                    createCell(3).setCellValue("X")
                    createCell(4).setCellValue("Y")
                    createCell(5).setCellValue("Z")
                }
                data.forEachIndexed { idx, d ->
                    sheet.createRow(idx + 1).apply {
                        createCell(0).setCellValue(d.id.toDouble())
                        createCell(1).setCellValue(d.timestamp.toDouble())
                        createCell(2).setCellValue(d.type.toDouble())
                        createCell(3).setCellValue(d.x.toDouble())
                        createCell(4).setCellValue(d.y.toDouble())
                        createCell(5).setCellValue(d.z.toDouble())
                    }
                }
            }
        }
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "$fileName.xlsx")
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
    fun exportKml(context: Context, faults: List<FaultEvent>, fileName: String): Uri? {
        val kml = StringBuilder().apply {
            append("<?xml version="1.0" encoding="UTF-8"?>\n<kml xmlns="http://www.opengis.net/kml/2.2">\n<Document>\n")
            faults.forEach {
                append("<Placemark>\n<name>${it.type}</name>\n<Point><coordinates>${it.longitude},${it.latitude},0</coordinates></Point>\n</Placemark>\n")
            }
            append("</Document>\n</kml>")
        }
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "$fileName.kml")
        FileOutputStream(file).use { it.write(kml.toString().toByteArray()) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
