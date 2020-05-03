/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package universal.servico;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author leandro.silva
 */
public class Excel {

    private XSSFWorkbook workbook;
    private Iterator<Row> rowIterator;

    public Excel(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        rowIterator = sheet.iterator();
    }

    public String buscarProximaLinha() {
        String linha = null;
        if (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            linha = "";
            //For each row, iterate through all the columns
            Iterator<Cell> cellIterator = row.cellIterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                //Check the cell type and format accordingly
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date data = cell.getDateCellValue();
                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                           linha += df.format(data) + ";";
                        } else {
                            linha += cell.getNumericCellValue() + ";";
                        }
                        break;
                    case Cell.CELL_TYPE_STRING:
                        if (!cell.getStringCellValue().equals(" ")) {
                            linha += cell.getStringCellValue() + ";";
                        }
                        break;
                }
            }
        }
        if (linha != null && linha.replaceAll(";", "").equals("")) {
            return buscarProximaLinha();
        }
        return linha;
    }

}
