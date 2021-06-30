import java.util.ArrayList;

public class MainClass {

	public static void main(String[] args) throws Exception{
		int nbCities =50;
		String mediumFileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//distances_entre_villes_50.txt";
		String lowerFileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//lowerdistances_entre_villes_50.txt";
		String upperFileName = "C://Users//18301//Desktop//File//CPS 3410//code//Assignment//CPS3410AS//src//upperdistances_entre_villes_50.txt";

		Problem upperPB = new Problem(nbCities, upperFileName);
		Problem lowerPB = new Problem(nbCities, lowerFileName);
		Problem mediumPB = new Problem(nbCities, mediumFileName);
		upperPB.constructDistances();
		lowerPB.constructDistances();
		mediumPB.constructDistances();
		ArrayList<Solution> list = new ArrayList<>();
		for(int upper = 8000; upper>=6000; upper-=100){
			for(int lower = 6000; lower>=4000; lower-=100){
				OPT opt = new OPT(30, 10, mediumPB, 10);
				list = opt.optimalList;
				boolean found = false;
				int upperFitness = 0;
				int lowerFitness = 0;
				for(int i = list.size()-1; i>=0; i--){
					Solution sol = list.get(i);
					Solution upperSol = new Solution(sol.cities, upperPB);
					Solution lowerSol = new Solution(sol.cities, lowerPB);
					upperFitness = upperSol.fitness;
					lowerFitness = lowerSol.fitness;
					if(upperSol.fitness<=upper&&lowerSol.fitness<=lower){
						System.out.print("Upper < "+upper+", lower < "+lower+" "+"Upper = "+upperFitness+", lower = "+lowerFitness+" ");
						sol.printSolution();
						found  = true;
						break;
					}
				}
				if(!found){
					System.out.println("Upper < "+upper+", lower < "+lower+" "+"Upper = "+upperFitness+", lower = "+lowerFitness+" ");
				}
			}
		}
	}

}