import java.util.ArrayList;
import java.util.Random;

public class Solution implements Comparable<Solution> {

	Problem problem;
	public int[] cities;
	public int fitness;


	public Solution(Problem problem) {
		this.problem = problem;
		cities = new int[problem.getNbCities()];
		generateRandom();
		evaluateSolution();

	}
	public Solution(int [] cities,Problem problem) {
		this.problem = problem;
		fitness = -1;
		this.cities = cities;
		evaluateSolution();
	}

	// Initialize a solution : a cycle passing by all cities
	public void generateRandom() {
		int a = -1;
		boolean recommence = true;

		cities[0] = 0; // Arbitrary with the city 0
		for (int i = 1; i < problem.getNbCities(); i++) {
			recommence = true;
			while (recommence) {
				recommence = false;
				a = getRandom(problem.getNbCities());
				for (int j = 0; j < i; j++) {
					if (a == cities[j])
						recommence = true;
				}
			}
			cities[i] = a;
		}
	}

	public void evaluateSolution() {
		fitness = 0;
		for (int i = 0; i < problem.getNbCities() - 1; i++)
			fitness += problem.getDistances()[cities[i]][cities[i + 1]];
		fitness += problem.getDistances()[cities[0]][cities[problem.getNbCities() - 1]];
	}

	public void printSolution() {
		for (int i = 0; i < problem.getNbCities(); i++)
			System.out.print(cities[i] + "-");
		System.out.println("--> " + fitness + " km");
	}

	@Override
	public int compareTo(Solution o) {
		// ascending order
		return (this.fitness - o.fitness);
	}

	// Get random between 0 and born -1
	public static int getRandom(int borne) {
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(borne);
		return randomInt;
	}

}


class OPT{
	public Problem problem;
	public ArrayList<Solution> nList2OPT;
	public Solution optimal;

	OPT(Solution optimal){
		this.optimal = optimal;
		this.problem = optimal.problem;
		nList2OPT = new ArrayList<>();
		nList2OPT.add(optimal);
	}

	public void generateOPT(){
		Solution tempSolution = optimal;
		int [] cities = tempSolution.cities;
		for(int i = 0; i < cities.length; i++){
			for(int j = i+2; Math.abs(i-j)>1&&j<cities.length; j++){
				cities = optimal.cities;
				int [] cities1 = cities.clone();
				int [] temp = new int[j-i];//create a temp arr
				int index = 0;
				for(int z = j; z>i; z--){//reverse the temp arr
					temp[index] = cities[z];
					index++;
				}
				System.arraycopy(temp, 0, cities1, i+1, temp.length);
				Solution newSolution = new Solution(cities1, problem);
				if(newSolution.fitness<optimal.fitness){
					optimal = newSolution;
					continue;
				}
			}
		}
	}

	public void min(){
		optimal.printSolution();
	}
}
