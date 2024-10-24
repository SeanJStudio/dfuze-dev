/**
 * Project: Dfuze
 * File: ExcelCell.java
 * Date: Sep 9, 2020
 * Time: 6:20:29 PM
 */
package com.mom.dfuze.data;

import java.io.IOException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class ExcelCell {

  public enum format {
    DATE("m/d/yyy"), STRING("@"), GENERAL("General");

    String format;

    private format(String format) {
      this.format = format;
    }

    public String getFormat() {
      return format;
    }

  };

  private String cellValue;
  private CellType cellType;
  private XSSFCellStyle cellStyle;
  private Font cellFont;
  private boolean isFormula;

  public ExcelCell(String cellValue, CellType cellType, XSSFCellStyle cellStyle, Font cellFont, boolean isFormula) {
    setCellValue(cellValue);
    setCellType(cellType);
    setCellStyle(cellStyle);
    setCellFont(cellFont);
    setIsFormula(isFormula);
  }
  
  

  public Font getCellFont() {
	return cellFont;
}

public void setCellFont(Font cellFont) {
	this.cellFont = cellFont;
}

public void setCellValue(String cellValue) {
    this.cellValue = cellValue;
  }

  public void setCellType(CellType cellType) {
    this.cellType = cellType;
  }

  public void setIsFormula(boolean isFormula) {
    this.isFormula = isFormula;
  }

  public String getCellValue() {
    return cellValue;
  }

  public CellType getCellType() {
    return cellType;
  }

  public boolean getIsForumla() {
    return isFormula;
  }

public XSSFCellStyle getCellStyle() {
	return cellStyle;
}

public void setCellStyle(XSSFCellStyle cellStyle) {
	this.cellStyle = cellStyle;
}

public static XSSFCellStyle createCellStyle() {
	XSSFWorkbook wb = new XSSFWorkbook();
	XSSFCellStyle cellStyle = wb.createCellStyle();
	try {wb.close();} catch(IOException e) {}
	return cellStyle;
}
  
}
