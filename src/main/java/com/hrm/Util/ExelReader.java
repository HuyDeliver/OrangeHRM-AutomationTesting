package com.hrm.Util;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExelReader {

    public static Object[][] getDataFromExel(String filePath, String sheetName) {
        Object data[][] = null;
        FileInputStream file = null;
        Workbook workbook = null;

        try {
            file = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet(sheetName);

            int toltalRow = sheet.getLastRowNum();
            int totalCol = sheet.getRow(0).getLastCellNum();

            data = new Object[toltalRow][totalCol];
            DataFormatter dataFormatter = new DataFormatter();

            for (int i = 1; i <= toltalRow; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < totalCol; j++) {
                    if (row == null) {
                        data[i - 1][0] = "";
                    } else {
                        Cell cell = row.getCell(j);
                        data[i - 1][j] = (cell == null) ? "" : dataFormatter.formatCellValue(cell);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file Excel: " + e.getMessage());
        } finally {
            // Đóng file để giải phóng bộ nhớ
            try {
                if (workbook != null)
                    workbook.close();
                if (file != null)
                    file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
