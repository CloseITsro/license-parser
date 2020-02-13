package co.closeit.licenseparser.writer;

import co.closeit.licenseparser.model.Dependency;
import co.closeit.licenseparser.model.ReplacementTag;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@Log4j2
public class ExcelWriter {
    
    public static final List<ReplacementTag> DEFAULT_COLUMNS = Arrays.asList(
            ReplacementTag.NAME, 
            ReplacementTag.VERSION, 
            ReplacementTag.PROJECT_NAME, 
            ReplacementTag.PACKAGE_MANAGER, 
            ReplacementTag.LICENSES_NAMES, 
            ReplacementTag.SOURCE);
    
    @Setter
    private List<ReplacementTag> providedColumns;

    public void writeExcel(List<Dependency> dependencies, String fileName) {
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
                HSSFWorkbook workbook = new HSSFWorkbook()) {

            List<ReplacementTag> columns = providedColumns == null ? DEFAULT_COLUMNS : providedColumns;
            
            HSSFSheet sheet = workbook.createSheet("Dependencies");
            HSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);

            HSSFRow rowhead = sheet.createRow(0);
            
            int columnIndex = 0;
            for (ReplacementTag column : columns) {
                rowhead.createCell(columnIndex).setCellValue(column.getName());
                rowhead.getCell(columnIndex).setCellStyle(headerStyle);
                columnIndex++;
            }         

            int rowIndex = 1;
            columnIndex = 0;
            for (Dependency dependency : dependencies) {
                HSSFRow row = sheet.createRow(rowIndex);
                
                for (ReplacementTag column : columns) {
                    row.createCell(columnIndex).setCellValue(column.getValue(dependency));
                    columnIndex++;
                }
                columnIndex = 0;
                rowIndex++;
            }

            columnIndex = 0;
            for (ReplacementTag column : columns) {
                sheet.autoSizeColumn(columnIndex);
                columnIndex++;
            }
                
            workbook.write(fileOut);
        } catch (FileNotFoundException ex) {
            log.error(ex.getMessage());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

}
