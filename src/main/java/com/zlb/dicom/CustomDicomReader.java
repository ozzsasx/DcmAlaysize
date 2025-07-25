package com.zlb.dicom;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import java.io.File;
import java.io.IOException;

public class CustomDicomReader {
    /*
     * 修改文件的内容
     * */
    public static void main(String[] args) {
        String filePath = "src/main/resources/1.3.6.1.4.1.5962.99.1.666032443.202847392.1654228474171.6826.0.dcm"; // 替换为你的文件路径
        File file = new File(filePath);

        try (DicomInputStream dis = new DicomInputStream(file)) {
            Attributes dataset = dis.readDataset(-1, -1);

            System.out.println("=== 提取的 DICOM 字段 ===");
            System.out.println("患者姓名: " + getString(dataset, Tag.PatientName));
            System.out.println("患者 ID: " + getString(dataset, Tag.PatientID));
            System.out.println("出生日期: " + getString(dataset, Tag.PatientBirthDate));
            System.out.println("性别: " + getString(dataset, Tag.PatientSex));

            System.out.println("检查日期(StudyDate): " + getString(dataset, Tag.StudyDate));
            System.out.println("系列日期(SeriesDate): " + getString(dataset, Tag.SeriesDate));
            System.out.println("内容日期(ContentDate): " + getString(dataset, Tag.ContentDate));

            System.out.println("检查时间(StudyTime): " + getString(dataset, Tag.StudyTime));
            System.out.println("系列时间(SeriesTime): " + getString(dataset, Tag.SeriesTime));
            System.out.println("内容时间(ContentTime): " + getString(dataset, Tag.ContentTime));

            System.out.println("设备类型(Modality): " + getString(dataset, Tag.Modality));
            System.out.println("设备序列号(DeviceSerialNumber): " + getString(dataset, Tag.DeviceSerialNumber));
            System.out.println("软件版本(SoftwareVersions): " + getString(dataset, Tag.SoftwareVersions));
            System.out.println("研究ID(StudyID): " + getString(dataset, Tag.StudyID));
            System.out.println("系列编号(SeriesNumber): " + getInt(dataset, Tag.SeriesNumber));
            System.out.println("实例编号(InstanceNumber): " + getInt(dataset, Tag.InstanceNumber));
            System.out.println("图像行数(Rows): " + getInt(dataset, Tag.Rows));
            System.out.println("图像列数(Columns): " + getInt(dataset, Tag.Columns));

        } catch (IOException e) {
            System.err.println("读取 DICOM 文件失败！");
            e.printStackTrace();
        }
    }

    // 封装安全获取字符串的方法
    private static String getString(Attributes ds, int tag) {
        return ds.getString(tag, null);
    }

    // 封装安全获取整型的方法
    private static Integer getInt(Attributes ds, int tag) {
        return ds.getInt(tag, 0); // 返回 -1 表示未定义
    }
}
