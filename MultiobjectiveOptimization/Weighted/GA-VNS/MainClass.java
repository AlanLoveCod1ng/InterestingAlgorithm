import java.util.*;
import jxl.*;
import jxl.write.*;
import java.io.*;
public class MainClass {

	public static void main(String[] args) throws Exception{
		int nbCities =50;
		String mediumFileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//dataset//objective1.txt";
		String lowerFileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//dataset//objective2.txt";
//		String upperFileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//upperdistances_entre_villes_50.txt";
		Problem pb = new Problem(nbCities, mediumFileName, /*upperFileName,*/ lowerFileName);
		pb.constructDistances();

		File file = new File("test.xls");
		file.createNewFile();
		WritableWorkbook workbook=Workbook.createWorkbook(file);
		WritableSheet sheet = workbook.createSheet("Alan", 0);
		Label label;


		for(int i = 0; i<=100; i+=10){
			OPT opt = new OPT(30, 10, pb, 10, (double)(i)/100, (double)(100-i)/100);
			opt.optimal.printSolution();
			label = new Label(0, i/10, opt.optimal.fitnessObj1+"");
			sheet.addCell(label);
			label = new Label(1, i/10, opt.optimal.fitnessObj2+"");
			sheet.addCell(label);
		}
		workbook.write();
		workbook.close();
	}

}
