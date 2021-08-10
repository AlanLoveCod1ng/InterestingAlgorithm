package duplicate;
import java.util.*;
import jxl.*;
import jxl.write.*;
import java.io.*;
public class MainClass {

	public static void main(String[] args) throws Exception{
		int nbCities =29;
		String mediumFileName = "C://Users//18301//Desktop//File//CPS3410//code//Assignment//CPS3410AS//src//dataset//292.txt";
		String lowerFileName = "C://Users//18301//Desktop//File//CPS3410//code//Assignment//CPS3410AS//src//dataset//291.txt";
		Problem pb = new Problem(nbCities, mediumFileName,  lowerFileName);
		pb.constructDistances();

		// File file = new File("NSGA2.xls");
		// file.createNewFile();
		// WritableWorkbook workbook=Workbook.createWorkbook(file);
		// WritableSheet sheet = workbook.createSheet("Alan", 0);
		// Label label;

		OPT opt = new OPT(50,100,pb, 0.9);
		// int count = 0;
		// ArrayList<Solution> paretoFront = opt.paretoFronts.get(0);
		// for(Solution e: paretoFront){
		// 	e.printSolution();
		// 	label = new Label(0, count, e.fitnessObj1+"");
		// 	sheet.addCell(label);
		// 	label = new Label(1, count, e.fitnessObj2+"");
		// 	sheet.addCell(label);
		// 	count++;
		// }
		// workbook.write();
		// workbook.close();
	}

}