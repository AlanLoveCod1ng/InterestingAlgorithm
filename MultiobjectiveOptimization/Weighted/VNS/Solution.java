import java.util.*;



public class Solution implements Comparable<Solution> {

	Problem problem;
	public int[] cities;
	public int fitness;
	public int fitnessObj1;
	public int fitnessObj2;
	public boolean selected = false;
	public double lower;
	public double medium;
//	public double upper;

	// public Solution(Problem problem) {
	// 	this.problem = problem;
	// 	cities = new int[problem.getNbCities()];
	// 	generateRandom();
	// 	evaluateSolution();
	// }

	public Solution(Problem problem, double medium,/* double upper,*/ double lower){
		this.problem = problem;
		cities = new int[problem.getNbCities()];
		this.lower = lower;
		this.medium = medium;
//		this.upper = upper;
		generateRandom();
		evaluateSolution();
		evaluateSubFitness();
	}

	public Solution(int [] cities,Problem problem, double medium,/* double upper,*/ double lower) {
		this.problem = problem;
		fitness = -1;
		this.lower = lower;
		this.medium = medium;
//		this.upper = upper;
		this.cities = cities;
		evaluateSolution();
		evaluateSubFitness();
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
		double temp = 0;
		for (int i = 0; i < problem.getNbCities() - 1; i++)
			temp += lower*problem.getLowerDistances()[cities[i]][cities[i + 1]] +
//					   upper*problem.getUpperDistances()[cities[i]][cities[i + 1]] +
					   medium*problem.getMediumDistances()[cities[i]][cities[i + 1]];
		temp += lower*problem.getLowerDistances()[cities[0]][cities[problem.getNbCities() - 1]] +
//				upper*problem.getUpperDistances()[cities[0]][cities[problem.getNbCities() - 1]] +
				medium*problem.getMediumDistances()[cities[0]][cities[problem.getNbCities() - 1]];
		fitness = (int)temp;
	}

	public void evaluateSubFitness() {
		fitnessObj1 = 0;
		for (int i = 0; i < problem.getNbCities() - 1; i++)
			fitnessObj1 += problem.getLowerDistances()[cities[i]][cities[i + 1]];
		fitnessObj1 += problem.getLowerDistances()[cities[0]][cities[problem.getNbCities() - 1]];

		fitnessObj2 = 0;
		for (int i = 0; i < problem.getNbCities() - 1; i++)
			fitnessObj2 += problem.getMediumDistances()[cities[i]][cities[i + 1]];
		fitnessObj2 += problem.getMediumDistances()[cities[0]][cities[problem.getNbCities() - 1]];
	}

	public void printSolution() {
		for (int i = 0; i < problem.getNbCities(); i++)
			System.out.print(cities[i] + "-");
		System.out.println("--> " + fitness + " km. medium= "+medium+" "+fitnessObj2+ " lower= "+lower+" "+fitnessObj1);
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
	public Solution optimal;

	double lower;
	double medium;

	OPT(Problem problem, double lower, double medium){
		this.problem = problem;
		this.lower = lower;
		this.medium = medium;

		this.optimal = new Solution(problem, lower, medium);
		generateOPT();
	}

	public void generateOPT(){
		while(true){
			Solution tempOptimal = optimal;
			int [] cities = tempOptimal.cities;
			//2-opt
			for(int i = 0; i < cities.length; i++){
				for(int j = i+2; Math.abs(i-j)>1&&j<cities.length; j++){
					int [] cities1 = cities.clone();
					int [] temp = new int[j-i];//create a temp arr
					int index = 0;
					for(int z = j; z>i; z--){//reverse the temp arr
						temp[index] = cities[z];
						index++;
					}
					System.arraycopy(temp, 0, cities1, i+1, temp.length);
					Solution newSolution = new Solution(cities1, problem, lower, medium);
					if(newSolution.fitness<tempOptimal.fitness){
						tempOptimal = newSolution;
					}
				}
			}
			if(tempOptimal.fitness != optimal.fitness){
				optimal = tempOptimal;
				continue;
			}
			//city insertion
			cities = tempOptimal.cities;
			Solution previous = new Solution(tempOptimal.cities.clone(), problem, lower, medium);
			Solution origin = new Solution(tempOptimal.cities.clone(), problem, lower, medium);
			for(int i = 0; i < cities.length-2; i++){
				previous = origin;
				for(int j = i+1; j<cities.length-1; j++){
					cities = previous.cities;
					int [] cities1 = cities.clone();
					int temp = cities1[i+1];
					for(int z = i+2; z<cities1.length;z++){
						cities1[z-1] = cities1[z];
					}
					cities1[cities1.length-1] = temp;
					Solution newSolution = new Solution(cities1, problem, lower, medium);
					previous = newSolution;
					if(newSolution.fitness<tempOptimal.fitness){
						tempOptimal = newSolution;
					}
				}
			}
			if(tempOptimal.fitness != optimal.fitness){
				optimal = tempOptimal;
				continue;
			}
			//3-opt
			for(int i = 0; i < cities.length-2; i++){
				for(int j = i+3; j<cities.length-2; j++){
					cities = tempOptimal.cities;
					int [] cities1 = cities.clone();
					int temp = cities1[i+1];
					cities1[i+1] = cities1[j+1];
					cities1[j+1] = temp; 
					Solution newSolution = new Solution(cities1, problem, lower, medium);
					if(newSolution.fitness<tempOptimal.fitness){
						tempOptimal = newSolution;
						i=0;
						j=i+3;
					}
				}
			}
			if(tempOptimal.fitness != optimal.fitness){
				optimal = tempOptimal;
				continue;
			}
			if(tempOptimal.fitness == optimal.fitness){
				break;
			}
		}
	}

	public void min(){
		optimal.printSolution();
	}
}