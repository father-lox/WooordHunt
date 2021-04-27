package com.excel;

import com.wooordhunt.Word;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelManager {
    public ExcelManager(String locationFullPath, String fileName) {
        workbook = new HSSFWorkbook();
        this.locationFullPath = locationFullPath;
        this.fileName = fileName;
    }
    public void exportWords(Word[] words) {
        HSSFSheet sheet = workbook.createSheet("EN");
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
        String tag = date.format(new Date());
        setTitles(sheet);
        for (int i = 0; i < words.length; i++) {
            Row wordRow = sheet.createRow(i + 1);
            wordRow.createCell(0).setCellValue("0%");
            wordRow.createCell(1).setCellValue(tag + "; " + setTag(words[i].getRang()));
            wordRow.createCell(2).setCellValue(words[i].getWord());
            wordRow.createCell(3).setCellValue(words[i].getUkTranscription());
            wordRow.createCell(4).setCellValue(words[i].getTranlations(" ; "));
            if (words[i].examplesIsSet())
                wordRow.createCell(6).setCellValue(words[i].getExamples(" — ","\n"));
        }
        saveFile();
    }

    private void setTitles(HSSFSheet sheet) {
        Row title = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            title.createCell(i).setCellValue(titles[i]);
        }
    }

    private void saveFile() {
        try {
            FileOutputStream out = new FileOutputStream(new File(locationFullPath + "\\" + fileName + ".xls"));
            workbook.write(out);
            out.close();
            System.out.println("Excel written successfully..");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String setTag(int rank) {
        if (rank <= 100) {
            return "TOP 100";
        }
        else if (rank <= 1000) {
            return "TOP 1000";
        }
        else if (rank <= 2000) {
            return "TOP 2000";
        }
        else if (rank <= 5000) {
            return "TOP 5000";
        }
        else if (rank <= 10000) {
            return "TOP 10000";
        }
        else if (rank <= 15000){
            return "TOP 15000";
        }
        else {
            return "TOP > 15000";
        }
    }

    private final HSSFWorkbook workbook;
    private final String locationFullPath;
    private final String fileName;
    private final String[] titles = {"Выучено", "Теги", "Слово", "Транскрипция", "Перевод", "Дополнительный перевод", "Примеры"};
}
