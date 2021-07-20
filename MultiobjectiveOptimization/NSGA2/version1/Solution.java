import java.util.*;





public class Solution {

	Problem problem;
	public int[] cities;
	public int fitnessObj1;
	public int fitnessObj2;
	public boolean selected = false;

	public boolean dominated = false;
	public int rank = 0;
	public int dominatedNumber = 0;
	public double crowdIndex = 0;
	
	ArrayList<Solution> dominatingSolutions = new ArrayList<>();

	public Solution(Problem problem){
		this.problem = problem;
		cities = new int[problem.getNbCities()];

		generateRandom();
		evaluateSolution();
	}

	public Solution(int [] cities,Problem problem) {
		this.problem = problem;
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
		// for (int i = 0; i < problem.getNbCities(); i++)
		// 	System.out.print(cities[i] + "-");
		System.out.println("--> " + " medium= "+fitnessObj2+ " lower= "+fitnessObj1);
	}

	// Get random between 0 and born -1
	public static int getRandom(int borne) {
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(borne);
		return randomInt;
	}

	public boolean isEqual(Solution s){
		if(this.fitnessObj1==s.fitnessObj1&&this.fitnessObj2==s.fitnessObj2){
			return true;
		}
		return false;
	}
}


class OPT{
	public Problem problem;
	public ArrayList<Solution> parentList;
	public ArrayList<ArrayList<Solution>> paretoFronts;
	public ArrayList<Solution> childList;
	public int Generations;
	public int parentSize;

	OPT(int parentSize, int Generations, Problem problem) throws Exception{
		this.problem = problem;
		this.parentSize = parentSize;

		parentList = new ArrayList<>();
		childList = new ArrayList<>();
		for(int i = 0; i < parentSize; i++){
			Solution newSol = new Solution(problem);
			newSol = VNS(newSol);
			parentList.add(newSol);
		}
		this.Generations = Generations;


		generateOPT();
	}

	public void generateOPT() throws Exception{
		int generationCount = 0;
		while(true){
			for(Solution e: parentList){
				e.crowdIndex = 0;
				e.dominatedNumber = 0;
				e.dominatingSolutions = new ArrayList<>();
				e.rank = 0;
			}
			ArrayList<Solution> combination = new ArrayList<>(parentList);
			combination.addAll(childList);
			paretoFronts = FNDS(combination); //fast-non-dominated-sort

			parentList = new ArrayList<>();
			childList = new ArrayList<>();
			int frontIndex = 0;
			while(frontIndex<paretoFronts.size()&&parentList.size()+paretoFronts.get(frontIndex).size()<=parentSize){
				crowdAssignment(paretoFronts.get(frontIndex));
				parentList.addAll(paretoFronts.get(frontIndex));
				frontIndex++;
			}
			if(frontIndex<paretoFronts.size()){
				Collections.sort(paretoFronts.get(frontIndex), new crowdComparator());
				parentList.addAll(paretoFronts.get(frontIndex).subList(0, parentSize - parentList.size()));
			}

			generateChildren();

			generationCount++;

			if(generationCount>Generations){
				break;
			}
		}
	}


	public void generateChildren(){
		Solution parent1 = null;
		Solution parent2 = null;
		for(int i = 0; i<parentSize/2; i++){
			parent1 = binaryTournament(parentList);
			parent2 = binaryTournament(parentList);
			crossOver(parent1, parent2, childList);
		}
	}

	public Solution binaryTournament(ArrayList<Solution> list){
		Solution best = null;
		for(int i = 0; i<2; i++){
			Solution temp = list.get((int)((Math.random()*list.size())));
			if(best==null||crowdComparison(temp, best)){
				best = temp;
			}
		}
		return best;
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
		return new Solution(child_cities,problem);
	}

	public void mutation(Solution child1, Solution child2, ArrayList<Solution> childList){
		child1 = VNS(child1);
		child2 = VNS(child2);
		childList.add(child1);
		childList.add(child2);
	}

	// public void listSort(ArrayList<Solution> list, int beg, int end){
	// 	if(beg<end){//quick sort
    //         int partition = list.get((beg+end)/2).fitness;
    //         int i = beg;
    //         int j = end;
    //         while(i<j){
    //             while(list.get(j).fitness<partition&&j>beg&&i<j){
    //                 j--;
    //             }
    //             while(list.get(i).fitness>partition&&i<end&&i<j){
    //                 i++;
    //             }
    //             if(list.get(i).fitness<=partition&&list.get(j).fitness>=partition){
    //                 Solution temp = list.get(i);
    //                 list.set(i,list.get(j)) ;
    //                 list.set(j, temp);
    //             }
    //             if(list.get(i).fitness==list.get(j).fitness&&i!=j)j--;
    //             if(i==j)break;
    //         }
    //         listSort(list,beg,i);
    //         listSort(list, j+1, end);
    //     }
	// }

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
					Solution newSolution = new Solution(cities1, problem);
					if(dominate(newSolution, tempOptimal)){
						tempOptimal = newSolution;
					}
				}
			}
			if(!tempOptimal.isEqual(sol)){
				sol = tempOptimal;
				continue;
			}
			//city insertion
			cities = tempOptimal.cities;
			Solution previous = new Solution(tempOptimal.cities.clone(), problem);
			Solution origin = new Solution(tempOptimal.cities.clone(), problem);
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
					Solution newSolution = new Solution(cities1, problem);
					previous = newSolution;
					if(dominate(newSolution, tempOptimal)){
						tempOptimal = newSolution;
					}
				}
			}
			if(!tempOptimal.isEqual(sol)){
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
					Solution newSolution = new Solution(cities1, problem);
					if(dominate(newSolution, tempOptimal)){
						tempOptimal = newSolution;
						i=0;
						j=i+3;
					}
				}
			}
			if(!tempOptimal.isEqual(sol)){
				sol = tempOptimal;
				continue;
			}
			if(tempOptimal.isEqual(sol)){
				return sol;
			}
		}
	}

	public boolean dominate(Solution primarySolution, Solution secondarySolution){
		if(primarySolution.fitnessObj1<=secondarySolution.fitnessObj1&&primarySolution.fitnessObj2<=secondarySolution.fitnessObj2){
			return true;
		}
		else{
			return false;
		}
	}

	public ArrayList<ArrayList<Solution>> FNDS(ArrayList<Solution>combination){
//fast-non-dominated-sort
		ArrayList<ArrayList<Solution>>frontList = new ArrayList<>();
		ArrayList<Solution> front = new ArrayList<>();
		for(int i = 0; i < combination.size(); i++){
			Solution primarySolution = combination.get(i);
			for(int j = 0; j<combination.size(); j++){
				if(i==j){
					continue;
				}
				Solution secondarySolution = combination.get(j);
				if(dominate(primarySolution, secondarySolution)){
					primarySolution.dominatingSolutions.add(secondarySolution);
				}
				else if(dominate(secondarySolution, primarySolution)){
					primarySolution.dominatedNumber++;
				}
			}
			if(primarySolution.dominatedNumber == 0){
				primarySolution.rank = 0;
				front.add(primarySolution);
			}
		}

		frontList.add(front);

		for(int i = 0; i<frontList.size();i++){
			front = frontList.get(i);
			if(front.isEmpty()){
				break;
			}
			HashSet<Solution> set = new HashSet<>();
			for(Solution p: front){
				for(Solution q: p.dominatingSolutions){
					q.dominatedNumber--;
					if(q.dominatedNumber==0){
						q.rank = i+1;
						if(!set.contains(q)){
							set.add(q);
						}
					}
				}
			}
			front = new ArrayList<>(set);
			frontList.add(front);
		}
		frontList.remove(frontList.get(frontList.size()-1));
		return frontList;
//fast-non-dominated-sort
	}

	public void crowdAssignment(ArrayList<Solution> front){
		for(Solution e : front){
			e.crowdIndex = 0;
		}
		//objective1
		Collections.sort(front, (a,b)->a.fitnessObj1-b.fitnessObj1);
		int objectiveMax = front.get(front.size()-1).fitnessObj1;
		int objectiveMin = front.get(0).fitnessObj1;
		front.get(front.size()-1).crowdIndex = Double.POSITIVE_INFINITY;
		front.get(0).crowdIndex = Double.POSITIVE_INFINITY;
		for(int i = 1; i<front.size()-1; i++){
			double temp1 = front.get(i+1).fitnessObj1 - front.get(i-1).fitnessObj1;
			double temp2 = objectiveMax - objectiveMin;
			double temp = temp1/temp2;
			front.get(i).crowdIndex += temp;
		}
		Collections.sort(front, (a,b)->a.fitnessObj2-b.fitnessObj2);
		objectiveMax = front.get(front.size()-1).fitnessObj2;
		objectiveMin = front.get(0).fitnessObj2;
		front.get(front.size()-1).crowdIndex = Double.POSITIVE_INFINITY;
		front.get(0).crowdIndex = Double.POSITIVE_INFINITY;
		for(int i = 1; i<front.size()-1; i++){
			double temp1 = front.get(i+1).fitnessObj2 - front.get(i-1).fitnessObj2;
			double temp2 = objectiveMax - objectiveMin;
			double temp = temp1/temp2;
			front.get(i).crowdIndex += temp;
		}
	}

	public boolean crowdComparison(Solution primarySolution, Solution secondarySolution){
		if(primarySolution.rank<secondarySolution.rank){
			return true;
		}
		if(primarySolution.rank==secondarySolution.rank&&primarySolution.crowdIndex>secondarySolution.crowdIndex){
			return true;
		}
		return false;
	}

	// public void updateSumWeight(){
	// 	double newWeight = 0;
	// 	for(Solution e: parentList){
	// 		newWeight+=1/(double)e.fitness;
	// 	}
	// 	sumWeight = newWeight;
	// }
}
class crowdComparator implements Comparator<Solution>{
	@Override

	public int compare(Solution s1, Solution s2) {
		if(s1.crowdIndex>s2.crowdIndex){
			return -1;
		}
		if(s1.crowdIndex<s2.crowdIndex){
			return 1;
		}
		return 0;
	}
}
