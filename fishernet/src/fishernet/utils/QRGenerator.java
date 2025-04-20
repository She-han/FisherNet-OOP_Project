/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet.utils;

 import com.google.zxing.*;
 import com.google.zxing.client.j2se.MatrixToImageWriter;
 import com.google.zxing.common.BitMatrix;
 import java.nio.file.*;
 import java.util.HashMap;
 public class QRGenerator {
    public static String generateQR(String data, String fileName) {
        try {
            String path = "qr/" + fileName + ".png";
            BitMatrix matrix = new MultiFormatWriter().encode(
                    data, BarcodeFormat.QR_CODE, 200, 200,
                    new HashMap<>()
            );
            Path outputPath = Paths.get(path);
            Files.createDirectories(outputPath.getParent());
            MatrixToImageWriter.writeToPath(matrix, "PNG", outputPath);
            return path;
        } catch (Exception e) {
            System.out.println("Failed"+e.getMessage());
            return null;
        }
    }
 }
