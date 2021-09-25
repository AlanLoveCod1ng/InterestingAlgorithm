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

		OPT opt = new OPT(300,200,pb, 0.9, 20, 100);
	}

}