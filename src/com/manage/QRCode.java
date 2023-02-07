package com.manage;

import com.barcodelib.barcode.Linear;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.image.BufferedImage;
import java.io.*;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Windows Amirzan Fikri Prasetyo
 */
public class QRCode {

    private final FileManager fManage = new FileManager();

    public boolean createQr(String data) {
        try {
            String path = System.getProperty("user.dir");
            Linear barcode = new Linear();
            barcode.setType(Linear.CODE128B);
            barcode.setData(data);
            barcode.setI(11.0f);
            String fname = data;
            if (barcode.renderBarcode(path + "\\src\\qr code\\" + data + ".png")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public String scanQr(String data) throws Exception {
        try {
            String dir = System.getProperty("user.dir");
            File path = new File(dir + "\\src\\qr code\\");
            File gambar = fManage.getImage(path, data);
            InputStream barInputStream = new FileInputStream(gambar);
            BufferedImage barBufferedImage = ImageIO.read(barInputStream);
            barInputStream.close();
            if(fManage.deleteFile(gambar.toString())){
                System.out.println("qr code dihapus");   
            }else{
                System.out.println("qr code tidak bisa dihapus");   
            }
            LuminanceSource source = new BufferedImageLuminanceSource(barBufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Reader reader = new MultiFormatReader();
            return reader.decode(bitmap).getText();
        } catch (ChecksumException | FormatException | NotFoundException | IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            Message.showException(this, "Terjadi Kesalahan!\n\nError message : " + e.getMessage(), e, true);
        } catch (NullPointerException n) {
            Message.showException(this, "Terjadi Kesalahan!\n\nQR Code tidak ditemukan", n, true);
            n.printStackTrace();
            System.out.println("error qr code");
        }
        return "";
    }
}
