import jxl.*;
public class MainClass {

	public static void main(String[] args) throws Exception{
		int nbCities = 50;
		String fileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//distances_entre_villes_50.txt";

		Problem pb = new Problem(nbCities, fileName);
		pb.constructDistances();
		//pb.printDistances();
	//	Solution best = new Solution(pb);
		// int sumFitness = 0;
		// long t1 = System.currentTimeMillis();
		// for(int i = 0; i< 10; i++){
			Solution sol = new Solution(pb);
			OPT opt = new OPT(sol,10000, 2000);
			opt.optimal.printSolution();
		// 	sumFitness+= opt.optimal.fitness;
		// }
		// long t2 = System.currentTimeMillis() - t1;
		// sumFitness = sumFitness/10;
		// System.out.println("Iterating 10 times cost "+t2+" milliseconds, and average fitness is "+sumFitness);
		
	}

}
