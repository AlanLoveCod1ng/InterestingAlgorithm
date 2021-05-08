import java.util.*;


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
	public Problem problem;// record problem
	public Solution optimal;// record global optimal solution so far
	public int numOfCities;
	TabuList <Solution> tabuList;
	int stopCondition;// we terminate searching if times reaching stopCondition
	int iterationTimes;// number of iteration of tabu element

	OPT(Solution optimal,int stopCondition, int iterationTimes){
		this.optimal = optimal; 
		this.problem = optimal.problem;
		this.numOfCities = optimal.cities.length;
		this.stopCondition = stopCondition;
		this.iterationTimes = iterationTimes;
		tabuList = new TabuList<>(iterationTimes);
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
					Solution newSolution = new Solution(cities1, problem);
					neighborList.add(newSolution);
				}
			}
			while(tabuList.contains(neighborList.peek())){
				neighborList.poll();
			}

			if(neighborList.peek().fitness>optimalNeighbor.fitness||// if our next optimalNeighbor is less that previous, we will increase
				neighborList.peek().fitness==optimalNeighbor.fitness){//if the stopCurrent == stopCondition, we think no more optimal by default
				stopCurrent++;
				if(stopCurrent == stopCondition){
					break;
				}
			}
			else{
				stopCurrent=0;
			}

			optimalNeighbor = neighborList.poll(); // refresh optialNeighbor
			tabuList.add(optimalNeighbor);
			if(optimalNeighbor.fitness<optimal.fitness){
				optimal = optimalNeighbor;
			}
		}
	}

}
class TabuList<E>{
	LinkedList<E> list = new LinkedList<>();
	int maxSize;
	int currentSize = 0;
	boolean limit = false;
	TabuList(int maxSize){
		this.maxSize = maxSize;
	}
	void add(E e){
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
	boolean contains(E e){
		return list.contains(e);
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
