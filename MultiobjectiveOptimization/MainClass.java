import java.util.ArrayList;

public class MainClass {

	public static void main(String[] args) throws Exception{
		int nbCities =50;
		String mediumFileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//distances_entre_villes_50.txt";
		String lowerFileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//lowerdistances_entre_villes_50.txt";
		String upperFileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//upperdistances_entre_villes_50.txt";

		Problem pb = new Problem(nbCities, mediumFileName, upperFileName, lowerFileName);
		pb.constructDistances();
		ArrayList<Solution> list = new ArrayList<>();
		for(int i = 0; i<=100; i+=10){
			for(int j = 0; j<=100&&i+j<=100; j+=10){
				OPT opt = new OPT(30, 10, pb, 10, (double)(i)/100, (double)(j)/100, (double)(100-i-j)/100);
				opt.optimal.printSolution();
				list.add(opt.optimal);
			}
		}
	}

}