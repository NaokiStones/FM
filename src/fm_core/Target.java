package fm_core;

import java.util.ArrayList;

public class Target {
	private String task;
	private ArrayList<Double> target;
	
	public Target(){
		task = "regression";
	}
	public Target(ArrayList<Double> targetSignal){
		target = targetSignal;
	}
}
