package com.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Gpp {
	
	public static int cost[][] = { 
									{ 0, 3, 4, 10, 32, 5, 15, 20 },
						            { 3, 0, 25, 8, 7, 12, 6, 9 },
						            { 4, 25, 0, 19, 13, 8, 10, 11 },
						            { 10, 8, 19, 0, 42, 4, 28, 15 },
						            { 32, 7, 13, 42, 0, 16, 70, 8 },
						            { 5, 12, 8, 4, 16, 0, 10, 9 },
						            { 15, 6, 10, 28, 70, 10, 0, 14 },
						            { 20, 9, 11, 15, 8, 9, 14, 0 }
								};
	
	public static int lowerBound = 0;
	public static int upperBound = 0;
	public static int currentBest = 0;
	public static int selectedVertex = 0;
	
	public static ArrayList<Integer> x = new ArrayList<>();
	public static ArrayList<Integer> bestSolution = new ArrayList<>();

	// Calculates overall Upper Bound 
	public static void calculateUpperBound() {
		
		for(int i=0 ; i<(cost.length/2); i++) {
			for(int j=(cost.length/2) ; j<cost.length; j++) {
				upperBound += cost[i][j];
			}
		}
	}
	
	// Calculates overall lower bound 
	public static int calculateLowerBound() {
		
		int bound =0;
		ArrayList<Integer> smallestSums = new ArrayList<Integer>();
		for(int i=0 ; i<cost.length; i++) {
			ArrayList<Integer> columnData = new ArrayList<Integer>();
			for(int j=0 ; j<cost.length; j++) {
				columnData.add(cost[j][i]);
			}
			
			Collections.sort(columnData);
			int sumElements=0;
			for(int k =1; k<=(cost.length/2); k++) {
				sumElements += columnData.get(k);
			}
			smallestSums.add(sumElements);
			Collections.sort(smallestSums);
		}
		
		for(int k =0; k<(cost.length/2); k++) {
			bound += smallestSums.get(k);
		}
		
		return bound;
	}
	
	/* Calculates lower bound estimates given 2 arrays - 
	 * a. set of node choices 
	 * b. set of selected nodes 
	*/
	public static Map<Integer, Integer> calculateEstimates(int [][] inputMatrix, ArrayList<Integer> nodes, ArrayList<Integer> selectedNodes) { 
		
		// nodeWiseBoundsMap <noce, bound>
		Map<Integer, Integer> nodeWiseBoundMap = new HashMap<Integer,Integer>();
		
		for(int n=0; n<nodes.size(); n++) {
			ArrayList<Integer> columnSumlist = new ArrayList<Integer>();
			for(int i=0 ; i<cost.length; i++) {
				ArrayList<Integer> columnData = new ArrayList<Integer>();
				for(int j=0 ; j<cost.length; j++) {
					columnData.add(cost[j][i]);
				}
				int nodeCost=0;
				for(int s=0; s< selectedNodes.size(); s++) {
					nodeCost += columnData.get((selectedNodes.get(s)-1)); 
					columnData.set((selectedNodes.get(s)-1), Integer.MAX_VALUE);
				}
				
				nodeCost += columnData.get(nodes.get(n)-1);
				columnData.set(nodes.get(n)-1, Integer.MAX_VALUE);

				Collections.sort(columnData);
				if(columnData.get(0) == 0)
					columnData.remove(0);

				int sumElements=0;
				int exitLoop;
				if(selectedNodes.size() > 0 )
					exitLoop = (cost.length/2)- (selectedNodes.size()+1);
				else
					exitLoop = (cost.length/2)-1;
				for(int k=0; k<exitLoop; k++) {
					sumElements += columnData.get(k);
				}
				
				nodeCost += sumElements;
				columnSumlist.add(nodeCost);
			}

			columnSumlist.set(nodes.get(n)-1, Integer.MAX_VALUE);
			for(int s=0; s< selectedNodes.size(); s++) {
				columnSumlist.set((selectedNodes.get(s)-1), Integer.MAX_VALUE);
			}
			
			Collections.sort(columnSumlist);
			int bound =0;
			for(int i=0; i<cost.length/2; i++) {
				bound += columnSumlist.get(i);
			}
			
			nodeWiseBoundMap.put(nodes.get(n), bound);
		}
		
		return nodeWiseBoundMap;
	}
	
	public static void build(int k) {
		
		ArrayList<Integer> choices = new ArrayList<Integer>();
		
		if(k==0)
			selectedVertex = 0;
		else {
			selectedVertex = x.get(k-1);
		}
		
		int start = selectedVertex + 1;
		int end = (cost.length/2) + k + 1;
		
		
		for(int c=start; c<=end; c++) {
			choices.add(c);
		}
	
		Map<Integer, Integer> nodeWiseBoundMap = calculateEstimates(cost, choices, x);
		Map<Integer, Integer> sortedNodeWiseBoundMap  = sortByValue(nodeWiseBoundMap);
		
		ArrayList<Integer> nodeList = new ArrayList<>(sortedNodeWiseBoundMap.keySet());
		printOutput(sortedNodeWiseBoundMap, x);

		
		int i =0;
		while(i < nodeList.size()) {
			
			if(k== (cost.length/2)-1 || (k<(cost.length/2)-1 && sortedNodeWiseBoundMap.get(nodeList.get(i)) < currentBest && sortedNodeWiseBoundMap.get(nodeList.get(i)) > lowerBound )) {
				
				x.add(nodeList.get(i));
				
				if(x.size() <= cost.length/2){

					if(x.size()==cost.length/2) {
						
						int totalBound = sortedNodeWiseBoundMap.get(nodeList.get(i));
						
						if(currentBest > totalBound){
							currentBest = totalBound;
							bestSolution = new ArrayList<>(x);
						}
						
						x.remove(x.size()-1);
						i++;
					}
					else {
							build(k+1);
							x.remove(x.size()-1);
							i++;
						}
				}
			}
			else
				return;
			
		} 
	}
	
	public static void main(String args[]) {
		
		calculateUpperBound();
		calculateLowerBound();
		
		currentBest = upperBound;
		
		build(0);
		
		System.out.println();
		System.out.println("Minimum sum of edge weights: " + currentBest);
		System.out.println("Best Partition is: " + bestSolution);
	}
	
	// Sorts the map  by value
	public static HashMap<Integer, Integer> sortByValue(Map<Integer, Integer> nodeWiseBoundMap){
	        List<Map.Entry<Integer, Integer> > list = new LinkedList<Map.Entry<Integer, Integer> >(nodeWiseBoundMap.entrySet()); 

	        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer> >() { 
		        public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2){ 
		        	return (o1.getValue()).compareTo(o2.getValue()); 
			    } 
	        }); 
		  
		    HashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>(); 
		    for (Map.Entry<Integer, Integer> aa : list) { 
		        temp.put(aa.getKey(), aa.getValue()); 
		    } 
		    return temp; 
	}
	
	// Formats output
	public static void printOutput(Map<Integer, Integer> map, ArrayList<Integer> selectedValues) {
		
		ArrayList<Integer> nodeList = new ArrayList<>(map.keySet());
		
		if(selectedValues.size() == 0 ) {
			for(int i =0; i<nodeList.size(); i++) {
				System.out.println("{ " + nodeList.get(i) + " } " +  map.get(nodeList.get(i)));
			}
			System.out.println();
		}else {
			String s = "{ " ;
			for(int i =0; i<selectedValues.size(); i++) {
				s += selectedValues.get(i) + " , ";
			}
			
			for(int i =0; i<nodeList.size(); i++) {
				System.out.println(s + nodeList.get(i) + " } " + map.get(nodeList.get(i)));
			}
			System.out.println();
			
		}
		
	}
	
	
}
