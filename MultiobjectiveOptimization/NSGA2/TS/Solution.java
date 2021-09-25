package duplicate;
import java.util.*;

import jxl.*;
import jxl.write.*;
import java.io.*;




public class Solution {

	Problem problem;
	public int[] cities;
	public int fitnessObj1;
	public int fitnessObj2;
	public double normalizedObj1;
	public double normalizedObj2;
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

		computerNormalized();
	}
	
	public void computerNormalized(){
		double obj1Ideal = 2020;
    	double obj2Ideal = 159;

		double obj1Nadir = 5919;
		double obj2Nadir = 1366;

		double obj1Worst = 8442;
    	double obj2Worst = 5629;

		normalizedObj1 = (fitnessObj1-obj1Ideal)/(obj1Nadir-obj1Ideal);
    	normalizedObj2 = (fitnessObj2-obj2Ideal)/(obj2Nadir-obj2Ideal);
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

	public boolean dominate(Solution secondarySolution){
		if(this.fitnessObj1<secondarySolution.fitnessObj1&&this.fitnessObj2<secondarySolution.fitnessObj2){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean isEqual(Solution s){
		if(this.fitnessObj1==s.fitnessObj1&&this.fitnessObj2==s.fitnessObj2){
			return true;
		}
		return false;
	}

	@Override
	public int hashCode(){
		return fitnessObj1+fitnessObj2;
	}

	@Override
	public boolean equals(Object o){
		Solution temp = (Solution)(o);
		if(this.fitnessObj1!=temp.fitnessObj1||this.fitnessObj2!=temp.fitnessObj2){
			return false;
		}
		for(int i = 0; i<temp.cities.length; i++){
			if(temp.cities[i]!=this.cities[i]){
				return false;
			}
		}
		return true;
	}
}


class OPT{
	public Problem problem;
	public ArrayList<Solution> parentList;
	public ArrayList<ArrayList<Solution>> paretoFronts;
	public HashSet<Solution> parentSet;
//	public ArrayList<Solution> childList;
	public HashSet<Solution> childSet;
	public int Generations;
	public int parentSize;

	public double mutationRate;
	public double crossOveRate;

	public int stopCondition;
	public int iterationTimes;

	OPT(int parentSize, int Generations, Problem problem, double mutationRate, int stopCondition, int iterationTimes) throws Exception{
		this.problem = problem;
		this.parentSize = parentSize;
		this.mutationRate = mutationRate;

		this.stopCondition = stopCondition;
		this.iterationTimes = iterationTimes;

		parentList = new ArrayList<>();
//		childList = new ArrayList<>();
		parentSet = new HashSet<>();
		childSet = new HashSet<>();
		while(parentSet.size()<parentSize){
			Solution newSol = new Solution(problem);
//			newSol = VNS(newSol);
			parentSet.add(newSol);
		}
		parentList.addAll(parentSet);
		this.Generations = Generations;


		generateOPT();
	}

	public void generateOPT() throws Exception{
		int generationCount = 0;
		int column = 0;
		double previousHV = 0;
		File file = new File("NSGA-NoDuplicate.xls");
		file.createNewFile();
		WritableWorkbook workbook=Workbook.createWorkbook(file);
		WritableSheet sheet = workbook.createSheet("Alan", 0);
		Label label;
			int count = 0;
		while(true){
			for(Solution e: parentList){//refresh the solution
				e.crowdIndex = 0;
				e.dominatedNumber = 0;
				e.dominatingSolutions = new ArrayList<>();
				e.rank = 0;
			}
			ArrayList<Solution> combination = new ArrayList<>(parentSet);
			combination.addAll(childSet);
			paretoFronts = FNDS(combination); //fast-non-dominated-sort

			parentList = new ArrayList<>();

			parentSet.clear();
			childSet.clear();
			int frontIndex = 0;
			while(frontIndex<paretoFronts.size()&&parentSet.size()+paretoFronts.get(frontIndex).size()<=parentSize){
				crowdAssignment(paretoFronts.get(frontIndex));
				parentSet.addAll(paretoFronts.get(frontIndex));
				frontIndex++;
			}
			if(frontIndex<paretoFronts.size()){
				Collections.sort(paretoFronts.get(frontIndex), new crowdComparator());
				parentSet.addAll(paretoFronts.get(frontIndex).subList(0, parentSize - parentSet.size()));
			}
			parentList.addAll(parentSet);
			generateChildren();

			int repeatNum = 0;
			HashSet<Solution> set = new HashSet<>(parentList);
			repeatNum = parentList.size() - set.size();

			int ranks = 0;
			int count1 = 0;
			for(ArrayList<Solution> front: paretoFronts){
				if(count1<1000){
					ranks++;
					count1+=front.size();
				}
				if(count1>=1000){
					break;
				}
			}

			generationCount++;
			double hyperVolume = getHyperVolume();
			String a = repeatNum+hyperVolume>previousHV ? "↑" : "↓";
			String repeatString = repeatNum+"";
			String HV = hyperVolume+"";
			if(repeatString.equals(HV)){
				a = "-";
			}
			System.out.println(generationCount+"th generation: "+hyperVolume+" ranks: "+ranks+" #repeat: "+ repeatNum + a);
			previousHV = hyperVolume;
			count = 0;
			if(generationCount==20 ||generationCount==50||
			generationCount==100||generationCount==500||generationCount==1000){
				int i = 0;
				for(Solution e: paretoFronts.get(0)){
					e.printSolution();
					label = new Label(column, count, e.fitnessObj1+"");
					sheet.addCell(label);
					label = new Label(column+1, count, e.fitnessObj2+"");
					sheet.addCell(label);
					count++;
				}
				
				column+=2;
			}

			if(generationCount>Generations){
				break;
			}

		}
		workbook.write();
		workbook.close();
	}


	public void generateChildren(){
		Solution parent1 = null;
		Solution parent2 = null;
		while(childSet.size()<parentSize){
			parent1 = binaryTournament(parentList);
			parent2 = binaryTournament(parentList);
			crossOver(parent1, parent2);
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

	public void crossOver(Solution parent1, Solution parent2){
		Solution child1 = PMX(parent1, parent2);
		Solution child2 = PMX(parent2, parent1);
		mutation(child1, child2);
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

	public void mutation(Solution child1, Solution child2){
		if(Math.random()<mutationRate){
			child1 = TS(child1);
		}
		if(Math.random()<mutationRate){
			child2 = TS(child2);
		}
		if(!childSet.contains(child1)&&!parentSet.contains(child1)){
			childSet.add(child1);
		}
		if(!childSet.contains(child2)&&!parentSet.contains(child2)){
			childSet.add(child2);
		}
	}


	public Solution TS(Solution sol){
		TS ts1 = new TS(sol, stopCondition, iterationTimes);
		return ts1.optimal;
	}

	public Solution randomMutation(Solution s1){
		int num1 = (int)(Math.random()*s1.cities.length);
		int num2 = (int)(Math.random()*s1.cities.length);
		while(num2==num1||num1==0||num2==0){
			num2 = (int)(Math.random()*s1.cities.length);
			num1 = (int)(Math.random()*s1.cities.length);
		}
		int[] temp = new int [s1.cities.length];
		System.arraycopy(s1.cities, 0, temp, 0, s1.cities.length);
		temp[num1] = s1.cities[num2];
		temp[num2] = s1.cities[num1];
		return new Solution(temp,problem);
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
				if(primarySolution.dominate(secondarySolution)){
					primarySolution.dominatingSolutions.add(secondarySolution);
				}
				else if(secondarySolution.dominate(primarySolution)){
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
			HashSet<Solution> set = new HashSet<>();//remove duplicate
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

	public double getHyperVolume(){
		ArrayList<Solution> optimalFront = paretoFronts.get(0);
		double ref1 = 1.0;
		double ref2 = 1.0;
		double result = 0;
		Collections.sort(optimalFront,(a,b)->a.fitnessObj1-b.fitnessObj1);
		for(Solution e: optimalFront){
			result += (ref1-e.normalizedObj1)*(ref2 - e.normalizedObj2);
			ref2 = e.normalizedObj2;
		}
		return result;
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