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
	public ArrayList<Solution> parentList;
	public ArrayList<Solution> childList;
	public int Generations;
	public int optimalParentSize;
	public int parentSize;
	public double sumWeight;
	public Solution optimal;

	public double lower;
	public double medium;
//	public double upper;

	OPT(int parentSize, int Generations, Problem problem, int optimalParentSize, double medium,/* double upper,*/ double lower){
		this.problem = problem;
		this.parentSize = parentSize;
		this.optimalParentSize = optimalParentSize;

		this.lower = lower;
		this.medium = medium;
//		this.upper = upper;

		parentList = new ArrayList<>();
		for(int i = 0; i < parentSize; i++){
			Solution newSol = new Solution(problem, medium,/* upper,*/ lower);
			newSol = VNS(newSol);
			sumWeight += 1/(double)newSol.fitness;
			parentList.add(newSol);
		}
		listSort(parentList, 0, parentList.size()-1);
		this.Generations = Generations;
		generateOPT();
	}

	public void generateOPT(){
		for(int z = 0; z< Generations; z++){
			childList = new ArrayList<>();
			for(int i = 0; i< (parentSize - optimalParentSize)/2; i++){
				double randomNumber = Math.random()*sumWeight;
				Solution parent1 = null;
				for(Solution e: parentList){//find the first Parent
					if(randomNumber<1/(double)e.fitness){
						parent1 = e;
						break;
					}
					randomNumber-=1/(double)e.fitness;
				}
				randomNumber = Math.random()*sumWeight;
				Solution parent2 = null;
				for(Solution e: parentList){//find the second Parent
					if(randomNumber<1/(double)e.fitness){
						parent2 = e;
						break;
					}
					randomNumber-=1/(double)e.fitness;
				}
				crossOver(parent1, parent2, childList);// we call mutation() in the crossOver method
			}
			for(int i = 0; i<optimalParentSize; i++){
				childList.add(parentList.get(parentList.size()-1-i));
			}
			listSort(childList, 0, childList.size()-1);
			parentList = childList;
			optimal = parentList.get(parentList.size()-1);
			updateSumWeight();
			childList = null;
		}
		optimal = parentList.get(parentList.size()-1);
	}

	public void crossOver(Solution parent1, Solution parent2, ArrayList<Solution> childList){
		Solution child1 = PMX(parent1, parent2);
		Solution child2 = PMX(parent2, parent1);
		mutation(child1, child2, childList);
	}
	public Solution PMX(Solution primary, Solution secondary){
		int [] child_cities = (int [])primary.cities.clone();
		int [] secondary_cities = secondary.cities;
		for(int i = 0; i<child_cities.length/2; i++){
			if(child_cities[i]==secondary_cities[i]){
				continue;
			}
			for(int j = i; j < child_cities.length; j++){
				if(child_cities[j] == secondary_cities[i]){
					child_cities[j] = child_cities[i];
					child_cities[i] = secondary_cities[i];
				}
			}
		}
		return new Solution(child_cities,problem,medium,/* upper,*/ lower);
	}

	public void mutation(Solution child1, Solution child2, ArrayList<Solution> childList){
		child1 = VNS(child1);
		child2 = VNS(child2);
		childList.add(child1);
		childList.add(child2);
	}

	public void listSort(ArrayList<Solution> list, int beg, int end){
		if(beg<end){//quick sort
            int partition = list.get((beg+end)/2).fitness;
            int i = beg;
            int j = end;
            while(i<j){
                while(list.get(j).fitness<partition&&j>beg&&i<j){
                    j--;
                }
                while(list.get(i).fitness>partition&&i<end&&i<j){
                    i++;
                }
                if(list.get(i).fitness<=partition&&list.get(j).fitness>=partition){
                    Solution temp = list.get(i);
                    list.set(i,list.get(j)) ;
                    list.set(j, temp);
                }
                if(list.get(i).fitness==list.get(j).fitness&&i!=j)j--;
                if(i==j)break;
            }
            listSort(list,beg,i);
            listSort(list, j+1, end);
        }
	}

	public Solution VNS(Solution sol){
		while(true){
			Solution tempOptimal = sol;
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
					Solution newSolution = new Solution(cities1, problem,medium,/* upper,*/ lower);
					if(newSolution.fitness<tempOptimal.fitness){
						tempOptimal = newSolution;
					}
				}
			}
			if(tempOptimal.fitness != sol.fitness){
				sol = tempOptimal;
				continue;
			}
			//city insertion
			cities = tempOptimal.cities;
			Solution previous = new Solution(tempOptimal.cities.clone(), problem,medium,/* upper,*/ lower);
			Solution origin = new Solution(tempOptimal.cities.clone(), problem,medium,/* upper,*/ lower);
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
					Solution newSolution = new Solution(cities1, problem,medium,/* upper,*/ lower);
					previous = newSolution;
					if(newSolution.fitness<tempOptimal.fitness){
						tempOptimal = newSolution;
					}
				}
			}
			if(tempOptimal.fitness != sol.fitness){
				sol = tempOptimal;
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
					Solution newSolution = new Solution(cities1, problem,medium,/* upper,*/ lower);
					if(newSolution.fitness<tempOptimal.fitness){
						tempOptimal = newSolution;
						i=0;
						j=i+3;
					}
				}
			}
			if(tempOptimal.fitness != sol.fitness){
				sol = tempOptimal;
				continue;
			}
			if(tempOptimal.fitness == sol.fitness){
				return sol;
			}
		}
	}

	public void updateSumWeight(){
		double newWeight = 0;
		for(Solution e: parentList){
			newWeight+=1/(double)e.fitness;
		}
		sumWeight = newWeight;
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
