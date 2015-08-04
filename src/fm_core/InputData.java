package fm_core;

import java.util.ArrayList;
import java.util.HashMap;

import util.Tuple;

public class InputData {
	private int row;
	private int col;
	private int group;
	private ArrayList<HashMap<Integer, Double> > matrix;
	private ArrayList<Tuple> groupRange;
	private ArrayList<String> names;
	
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
	public ArrayList<Tuple> getGroupRange(){
		return groupRange;
	}
	
	public InputData(double[][] dMatrix, int group, ArrayList<Tuple> groupRange, ArrayList<String> names){
		// params initialization
		row = dMatrix.length;
		col = dMatrix[0].length;
		this.group = group;
		this.groupRange = groupRange;
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
	}
}
