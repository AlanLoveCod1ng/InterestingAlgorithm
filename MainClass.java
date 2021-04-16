
public class MainClass {

	public static void main(String[] args) {
		int nbCities = 10;
		String fileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//distances_between_cities_10.txt";

		Problem pb = new Problem(nbCities, fileName);
		pb.constructDistances();
		pb.printDistances();

		Solution sol = new Solution(pb);
		OPT opt = new OPT(sol);
		opt.generateOPT();
		for (int i = 0; i < 10; i++) {
			opt.optimal = new Solution(pb);
			opt.generateOPT();
		}
		opt.min();
	}

}
