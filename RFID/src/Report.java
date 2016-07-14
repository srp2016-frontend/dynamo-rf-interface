import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class Report {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File("result.obj")));
		@SuppressWarnings("unchecked")
		HashMap<Point, ArrayList<Read>> rssiStore = (HashMap<Point, ArrayList<Read>>)input.readObject();
		try {
			FileOutputStream fileOut = new FileOutputStream("poi-test.xls");
			HSSFWorkbook workbook = new HSSFWorkbook();
			for(int j = 1; j <= 4; j++) {
				HSSFSheet worksheet = workbook.createSheet("Reader " + j);
				int rows = 0;
				for(Point p : rssiStore.keySet()) {
					HSSFRow row = worksheet.createRow(rows);
					row.createCell(0).setCellValue(p.x + "," + p.y);
					int items = 0;
					for(int i = 0; i < rssiStore.get(p).size(); i++) 
					{
						if(rssiStore.get(p).get(i).reader == j)
						{
							row.createCell(items++ + 1).setCellValue(rssiStore.get(p).get(i).rssi);
						}
					}
					if(items > 0)
						rows++;
				}
			}

			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
}
