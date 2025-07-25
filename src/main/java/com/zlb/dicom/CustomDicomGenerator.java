package com.zlb.dicom;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CustomDicomGenerator {

    public static void main(String[] args) {
        String outputFilePath = "src/main/resources/custom_output.dcm";

        try {
            // 创建 DICOM 数据集
            Attributes dcm = new Attributes();
            Attributes fmi = new Attributes();

            // 文件元信息
            fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.UltrasoundMultiFrameImageStorage);
            fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, UIDUtils.createUID());
            fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
            fmi.setString(Tag.ImplementationClassUID, VR.UI, UIDUtils.createUID());

            // 设置患者信息（使用 GB18030 编码）
            dcm.setString(Tag.PatientName, VR.PN, "小红");
            dcm.setString(Tag.PatientID, VR.LO, "26154642");
            dcm.setString(Tag.PatientSex, VR.CS, "女");
            dcm.setString(Tag.PatientBirthDate, VR.DA, "20250424");

            // 设置检查时间
            LocalDateTime scanTime = LocalDateTime.of(2025, 4, 14, 14, 43, 5);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            dcm.setString(Tag.StudyTime, VR.TM, scanTime.format(formatter));
            dcm.setString(Tag.StudyDate, VR.DA, scanTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            // 设置设备和图像参数
            dcm.setString(Tag.Modality, VR.CS, "US"); // 超声
            dcm.setString(Tag.SpecificCharacterSet, VR.CS, "GB18030"); // 显式指定字符集
            dcm.setString(Tag.SeriesDescription, VR.LO, "测试描述");

            // SOP Class & Instance UID
            dcm.setString(Tag.SOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
            dcm.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID());

            // 图像尺寸（模拟灰度图）
            int width = 512;
            int height = 512;
            dcm.setInt(Tag.Rows, VR.US, height);
            dcm.setInt(Tag.Columns, VR.US, width);
            dcm.setInt(Tag.BitsAllocated, VR.US, 8);
            dcm.setInt(Tag.BitsStored, VR.US, 8);
            dcm.setInt(Tag.HighBit, VR.US, 7);
            dcm.setInt(Tag.PixelRepresentation, VR.US, 0);

            // 生成模拟图像数据
            BufferedImage image = generateSimulatedUltrasoundImage(width, height);
            byte[] pixelData = convertToByteArray(image);

            // 写入 Pixel Data
            dcm.setBytes(Tag.PixelData, VR.OB, pixelData);

            // 写入 DICOM 文件
            try (DicomOutputStream dos = new DicomOutputStream(new File(outputFilePath))) {
                dos.writeDataset(fmi, dcm);
            }

            System.out.println("DICOM 文件已生成: " + outputFilePath);

            // 可选：保存为 PNG 查看效果
            ImageIO.write(image, "jpg", new File("src/main/resources/simulated_ultrasound.jpg"));
            System.out.println("模拟图像已保存为 simulated_ultrasound.jpg");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 生成模拟图像数据
    private static BufferedImage generateSimulatedUltrasoundImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte[] data = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Random rand = new Random();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;

                // 基础噪声
                int noise = (int) (Math.abs(rand.nextGaussian()) * 32); // 使用高斯分布

                // 中心圆形高亮区域（模拟乳腺）
                double centerX = width / generateRandomNumber();
                double centerY = height / generateRandomNumber();
                double radius = 100 + 20 * Math.sin(x / 50.0 + rand.nextDouble() * 5); // 添加随机偏移
                double dist = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
                int intensity = 0;

                if (dist < radius) {
                    intensity = 180 + (int)(20 * Math.sin(dist / 10));
                } else if (dist < radius + 20) {
                    intensity = 120 + (int)(40 * Math.sin(dist / 10));
                } else {
                    intensity = noise;
                }

                data[index] = (byte) intensity;
            }
        }

        return image;
    }

    // 1.5 到2.5 之间的随机数
    private static double generateRandomNumber() {
        Random random = new Random();
        return 1.5 + random.nextDouble();
    }

    private static byte[] convertToByteArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] pixels = new byte[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int gray = (rgb & 0xFF); // 提取灰度值
                pixels[y * width + x] = (byte) gray;
            }
        }

        return pixels;
    }
}
