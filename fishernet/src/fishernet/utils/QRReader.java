/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 package fishernet.utils;
 import com.google.zxing.*;
 import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
 import com.google.zxing.common.HybridBinarizer;
 import javax.imageio.ImageIO;
 import java.awt.image.BufferedImage;
 import java.io.File;
 public class QRReader {
    public static String readQR(String filePath) {
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (Exception e) {
            System.out.println("QR read failed :"+e.getMessage());
            return null;
        }
    }
 }
