package duplicate;
import java.util.*;

public class TS{
	public Problem problem;// record problem
	public Solution optimal;// record global optimal solution so far
	public int numOfCities;
	TabuList tabuList;
	int stopCondition;// we terminate searching if times reaching stopCondition
	int iterationTimes;// number of iteration of tabu element
	TabuList trend = new TabuList(20);

	TS(Solution optimal,int stopCondition, int iterationTimes){
		this.optimal = optimal; 
		this.problem = optimal.problem;
		this.numOfCities = optimal.cities.length;
		this.stopCondition = stopCondition;
		this.iterationTimes = iterationTimes;
		tabuList = new TabuList(iterationTimes);
		generateTS();
	}

	public void generateTS(){
		int stopCurrent = 0; // current times
		Solution optimalNeighbor = optimal;
		tabuList.add(optimalNeighbor);
		while(true){
			int [] cities = optimal.cities;
			PriorityQueue<Solution> neighborList = new PriorityQueue<>(10, new fitnessComparator());
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

			if(trend.currentSize == 20){//judging whether the overall trend of pre 10 and last 10
				int averageFirstTenObj1 = 0;
                int averageFirstTenObj2 = 0;
				int averageLastTenObj1 = 0;
                int averageLastTenObj2 = 0;

				int count = 0;
				for(Solution e: trend.list){
					if(count<10){
						averageFirstTenObj1 += e.fitnessObj1;
                        averageFirstTenObj2 += e.fitnessObj2;
					}
					if(count>=10&&count<20){
						averageLastTenObj1 += e.fitnessObj1;
                        averageLastTenObj2 += e.fitnessObj2;
					}
					count++;
				}
				averageFirstTenObj1 /= 10;
                averageFirstTenObj2 /= 10;
				averageLastTenObj1 /= 10;
                averageLastTenObj2 /= 10;
				if(!compare(averageFirstTenObj1, averageFirstTenObj2, averageLastTenObj1, averageLastTenObj2)){
					stopCurrent++;
				}
				else{
					stopCurrent = 0;
				}
				if(stopCurrent == stopCondition){
					break;
				}
			}

			optimalNeighbor = neighborList.poll(); // refresh optimalNeighbor
			tabuList.add(optimalNeighbor);
			trend.add(optimalNeighbor);
			if(optimalNeighbor.dominate(optimal)){
				optimal = optimalNeighbor;
			}
//			optimalNeighbor.printSolution();
		}
	}

	public boolean compare(int averageFirstTenObj1, int averageFirstTenObj2, int averageLastTenObj1, int averageLastTenObj2) {
		if(averageFirstTenObj1> averageLastTenObj1&&averageFirstTenObj2>averageLastTenObj2){
			return true;
		}
		return false;
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
		if(s1.dominate(s2)){
			return -1;
		}
        if(s2.dominate(s1)){
			return 1;
		}
        if(s1.crowdIndex>s2.crowdIndex){
            return -1;
        }
		if(s2.crowdIndex>s1.crowdIndex){
            return 1;
        }
		return 0;
	}
}
