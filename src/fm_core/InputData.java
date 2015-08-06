package fm_core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InputData {
	private int row;
	private int col;
	private int group;
	private List<Map<Integer, Double>> matrix = new ArrayList<Map<Integer, Double>>();
	private int[] groupRangeUpperLimit;
	private String[] names;
	
	public InputData(){
		// kakikake
		System.out.println("Please Input Data");
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
	public int[] getGroupRangeUpperLimit(){
		return groupRangeUpperLimit;
	}
	public Map<Integer, Double> getOneRecord(int r){
		return matrix.get(r);
	}
	
	public InputData(double[][] dMatrix, int group, int[] groupRange, String[] names){
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
		for(int i=0, GRUL_Length = groupRangeUpperLimit.length; i< GRUL_Length; i++){
			if(!flag){
				groupRangeUpperLimit[i] = groupRangeUpperLimit[i] -1;
				flag = true;
				continue;
			}
			if(i >= 1){
				groupRangeUpperLimit[i] += groupRangeUpperLimit[i-1];
				//groupRangeUpperLimit.set(i, groupRangeUpperLimit[i-1] + groupRangeUpperLimit[i]));
			}
		}
	}
}
