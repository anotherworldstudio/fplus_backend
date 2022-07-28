package com.kbeauty.gbt.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.kbeauty.gbt.entity.domain.Content;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelUtil {
	
	public static void main(String[] args)  throws Exception{
		ExcelUtil util = new ExcelUtil();
		String path = "E:\\beautage\\DEV\\sandbox\\excel\\피부진단초기적재데이터.xlsx";
		List<Content> firstSheetData = ExcelUtil.getFirstSheetData(path, "com.kbeauty.gbt.entity.domain.Content");
	}

	public static <T> List<T> getFirstSheetData(String filePath, String className) throws Exception{		
		XSSFWorkbook wb = null;		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
			// HSSFWorkbook은 엑셀파일 전체 내용을 담고 있는 객체
			wb = new XSSFWorkbook(fis);
			for (Sheet sheet : wb) {
				return getData(className, sheet);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb != null)
					wb.close();
				if (fis != null)
					fis.close();
			} catch (IOException e){
				
			}
		}
		
		return null;
	}
	
	public static <T> List<T> getFirstSheetData(InputStream is, String className) throws Exception{		
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(is);
			for (Sheet sheet : wb) {
				return getData(className, sheet);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb != null)
					wb.close();		
			} catch (IOException e) {}
		}
		
		return null;
	}

	private static <T extends Object> List<T> getData(String className, Sheet sheet) throws Exception{
		Class<?> cls = Class.forName(className);
		Constructor<?> constructor = cls.getConstructor();
		
		List<T> list = new ArrayList<T>();
		Map<Integer, String> columnMap = new HashMap<>();
		
		T t;
		boolean isFirst = true;
		for (Row row : sheet) {
			if(isFirst) { // 첫줄은 항상 field명이 들어와야 한다. 
				isFirst = false;
				int i = 0;
				for (Cell cell : row) {					
					columnMap.put(i, getCellValue(cell));
					i++;
				}
			}else {		
				t = (T)constructor.newInstance();				
				for (int i = 0; i < row.getLastCellNum(); i++) {
					Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
					setValue(cell, columnMap.get(i), t, cls);
				}								
					
				list.add(t);
			}
		}
		return list;
	}
	
	public static Map<String, List<String>> getFirstSheetDataMap(InputStream is, int startIdx, int endIdx) throws Exception{
		XSSFWorkbook wb = null;
		
		
		Map<String, List<String>> map = new HashMap<>();
						
		try {
			List<String> dataList = null;
			String val = null;
			Cell cell = null;
			String key = null;
			wb = new XSSFWorkbook(is);
			for (Sheet sheet : wb) {
				
				boolean isFirst = true;
				for (Row row : sheet) {
					key = null;
					if(isFirst) { // 첫줄은 항상 field명이 들어와야 한다. 
						isFirst = false;
						
					} else {			
						
						dataList = new ArrayList<>(); 
						for (int i = 0; i < row.getLastCellNum(); i++) {
							if( i == 0 ) {
								cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
								key = getCellValue(cell);
							}
								
							if( i >= startIdx && i <= endIdx ) {								
								cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
								val = getCellValue(cell);
								dataList.add(val);							
							}
						}	
						
						map.put(key, dataList);
					}
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb != null)
					wb.close();		
			} catch (IOException e) {
				
			}
		}
		
		return map;
	}
	
	private static void setValue(Cell cell, String name, Object obj, Class cls) throws Exception{
		
		log.info("==============================");
		log.info("name : " + name);

		Field field = getField(name, cls);
		if(field == null) return;
		
		String val = getCellValue(cell);
		System.out.println(val);
		
		field.setAccessible(true);
		
		if (field.getType() == String.class) {
            field.set(obj, val);            
        }else if(field.getType() == int.class || field.getType() == Integer.class){
        	if(StringUtil.isEmpty(val)) val = "0";
        	val = getZero(val);
        	field.set(obj, Integer.parseInt(val));        	
        }else if(field.getType() == long.class || field.getType() == Long.class) {        	
        	if(StringUtil.isEmpty(val)) val = "0";
        	val = getZero(val);
        	field.set(obj, Long.parseLong(val));
        }else if(field.getType() == float.class || field.getType() == float.class) {        
        	if(StringUtil.isEmpty(val)) val = "0";
        	field.set(obj, Float.parseFloat(val));
        }else if(field.getType() == double.class || field.getType() == double.class) {        	
        	if(StringUtil.isEmpty(val)) val = "0";
        	field.set(obj, Double.parseDouble(val));
        }else {
        	throw new Exception();
        }
	}
	
	private static String getZero(String val) {
		if (val.indexOf(".") >= 0) {
			String[] arr = val.split("\\.");
			val = arr[0];
		}
		return val;
	}

	private static Field getField(String name, Class cls) throws NoSuchFieldException {
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if(field.getName().toLowerCase().equals(name.toLowerCase())) {
				return field;
			}
		}
		return null;
	}
	
	private static String getCellValue(Cell cell) {
		String result = null;
		switch (cell.getCellType()){
		case STRING:
			result =  cell.getRichStringCellValue().getString();
			break;
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				DataFormatter formatter = new DataFormatter();
				result = formatter.formatCellValue(cell);
			} else {
				result = String.valueOf(cell.getNumericCellValue());				
			}
			break;						
		case BLANK:
			result = "";
			break;
		default:
			DataFormatter formatter = new DataFormatter();
			result = formatter.formatCellValue(cell);
		}
		
		return result;
	}

}
