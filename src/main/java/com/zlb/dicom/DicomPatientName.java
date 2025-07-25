package com.zlb.dicom;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
 * 修改DICOM文件中的PatientName和PatientID
 * */
public class DicomPatientName {

    public static void main(String[] args) {
        String inputPath = "src/main/resources/custom_output.dcm";      // 输入DICOM文件路径
        String outputPath = "src/main/resources/custom_output.dcm";    // 输出DICOM文件路径
        String newPatientName = "张三"; // 新的患者姓名
        String patientID = "8630718"; // 患者ID

        try {
            // 读取DICOM文件
            File inputFile = new File(inputPath);
            DicomInputStream dis = new DicomInputStream(new FileInputStream(inputFile));

            // 读取 FMI 和 数据集
            Attributes fmi = dis.readFileMetaInformation();  // 读取 FMI
            Attributes dataset = dis.readDataset();          // 读取数据集

            // 修改
            dataset.setString(Tag.PatientName, VR.PN, newPatientName);
            dataset.setString(Tag.PatientID, VR.PN, patientID);

            // 写回新的DICOM文件
            try (DicomOutputStream dos = new DicomOutputStream(new File(outputPath))) {
                dos.writeDataset(fmi, dataset);  // 同时写入 FMI 和 数据集
            }

            System.out.println("PatientName 修改成功，新文件保存为: " + outputPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
