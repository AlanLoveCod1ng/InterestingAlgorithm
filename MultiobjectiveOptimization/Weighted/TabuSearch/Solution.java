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
	public Problem problem;// record problem
	public Solution optimal;// record global optimal solution so far
	public int numOfCities;
	TabuList tabuList;
	int stopCondition;// we terminate searching if times reaching stopCondition
	int iterationTimes;// number of iteration of tabu element
	TabuList trend = new TabuList(20);

	public double lower;
	public double medium;

	OPT(int stopCondition, int iterationTimes, Problem problem, double medium, double lower){
		this.problem = problem;
		this.medium = medium;
		this.lower = lower;
		this.optimal = new Solution(problem, medium, lower);

		this.numOfCities = optimal.cities.length;
		this.stopCondition = stopCondition;
		this.iterationTimes = iterationTimes;
		tabuList = new TabuList(iterationTimes);
		generateOPT();
	}

	public void generateOPT(){
		int stopCurrent = 0; // current times
		Solution optimalNeighbor = optimal;
		tabuList.add(optimalNeighbor);
		while(true){
			int [] cities = optimal.cities;
			PriorityQueue<Solution>  neighborList = new PriorityQueue<>(10, new fitnessComparator());
			for(int i = 0; i < numOfCities; i++){
				for(int j = i+2; Math.abs(i-j)>1&&j<numOfCities; j++){
					cities = optimalNeighbor.cities;
					int [] cities1 = cities.clone();
					int [] temp = new int[j-i];//create a temp arr
					int index = 0;
					for(int z = j; z>i; z--){//reverse the temp arr
						temp[index] = cities[z];
						index++;
					}
					System.arraycopy(temp, 0, cities1, i+1, temp.length);
					Solution newSolution = new Solution(cities1, problem, medium, lower);
					neighborList.add(newSolution);
				}
			}
			while(tabuList.contains(neighborList.peek())){
				neighborList.poll();
			}

			if(trend.currentSize == 20){
				int averageFirstTen = 0;
				int averageLastTen = 0;
				int count = 0;
				for(Solution e: trend.list){
					if(count<10){
						averageFirstTen += e.fitness;
					}
					if(count>=10&&count<20){
						averageLastTen += e.fitness;
					}
					count++;
				}
				averageFirstTen /= 10;
				averageLastTen /= 10;
				if(averageFirstTen<=averageLastTen){
					stopCurrent++;
				}
				if(averageLastTen<averageFirstTen){
					stopCurrent = 0;
				}
				if(stopCurrent == stopCondition){
					break;
				}
			}
			

			optimalNeighbor = neighborList.poll(); // refresh optimalNeighbor
			tabuList.add(optimalNeighbor);
			trend.add(optimalNeighbor);
			if(optimalNeighbor.fitness<optimal.fitness){
				optimal = optimalNeighbor;
			}
//			optimalNeighbor.printSolution();
		}
	}

}
class TabuList{
	LinkedList<Solution> list = new LinkedList<>();
	int maxSize;
	int currentSize = 0;
	boolean limit = false;
	TabuList(int maxSize){
		this.maxSize = maxSize;
	}
	void add(Solution e){
		if(limit){
			list.addLast(e);
			list.removeFirst();
		}
		else{
			list.add(e);
			currentSize++;
			if(currentSize==maxSize){
				limit = true;
			}
		}
	}
	boolean contains(Solution sol){
		for(Solution e: list){
			if(e.equals(sol)){
				return true;
			}
		}
		return false;
	}
}
class fitnessComparator implements Comparator<Solution>{
	@Override

	public int compare(Solution s1, Solution s2) {
		if(s1.fitness>s2.fitness){
			return 1;
		}
		if(s1.fitness<s2.fitness){
			return -1;
		}
		return 0;
	}
}
