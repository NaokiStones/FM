package fm_core;

import java.util.ArrayList;
import java.util.HashMap;

import util.Tuple;

public class InputData {
	private int row;
	private int col;
	private int group;
	private ArrayList<HashMap<Integer, Double> > matrix;
	private ArrayList<Integer> groupRangeUpperLimit;
	private ArrayList<String> names;
	
	public InputData(){
		// kakikake
		System.out.println("Please Input Data");
		groupRangeUpperLimit = new ArrayList<Integer>();
	}
	
	public int getRow(){
		return row;
	}
	public int getCol(){
		return col;
	}
	public int getGroup(){
		return group;
	}
	public ArrayList<Integer> getGroupRangeUpperLimit(){
		return groupRangeUpperLimit;
	}
	public HashMap<Integer, Double> getOneRecord(int r){
		return matrix.get(r);
	}
	
	public InputData(double[][] dMatrix, int group, ArrayList<Integer> groupRange, ArrayList<String> names){
		// params initialization
		row = dMatrix.length;
		col = dMatrix[0].length;
		this.group = group;
		this.groupRangeUpperLimit = groupRange;
		this.names = names;
		
		// prepare Matrix
		for(int i=0; i<row; i++){
			matrix.add(new HashMap<Integer, Double>());
		}
		
		// Input row Matrix to this Matrix
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				if(dMatrix[i][j] != 0.0){
					matrix.get(i).put(j, dMatrix[i][j]);
				}
			}
		}
		
		// Make Group Range Upper Limit
		boolean flag = false;
		for(int i=0; i<groupRangeUpperLimit.size(); i++){
			if(!flag){
				groupRangeUpperLimit.set(i, groupRangeUpperLimit.get(i)-1);
				flag = true;
				continue;
			}
			if(i >= 1){
				groupRangeUpperLimit.set(i, groupRangeUpperLimit.get(i-1) + groupRangeUpperLimit.get(i));
			}
		}
	}
}
