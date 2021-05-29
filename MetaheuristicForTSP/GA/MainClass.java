
public class MainClass {

	public static void main(String[] args) {
		int nbCities =50;
		String fileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//distances_entre_villes_50.txt";

		Problem pb = new Problem(nbCities, fileName);
		pb.constructDistances();
		//pb.printDistances();
		Solution best = new Solution(pb);
		OPT opt = new OPT(30, 10, pb, 10);
	}
}
