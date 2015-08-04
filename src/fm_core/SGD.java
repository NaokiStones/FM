package fm_core;

import java.util.ArrayList;
import java.util.HashMap;

import util.Tuple;

public class SGD {
	private double w0;
	private ArrayList<Double> w;
	private double[][] V;
	
	private double init_mean;
	private double init_stdev;
	private double eater;	// reg0
	private double lambda0;
	private ArrayList<Double> lambdaW;
	private double[][] lambdaV;
	
	private String task;
	
	private InputData id;
	private ArrayList<Tuple> groupRange;
	
	private int p;
	private int k;
	private HashMap<Integer, Double> record;
	private double tg;
	
	public SGD(){
		w0 = 0;
		task = "regression";
		// kakikake
	}
	
	
	
	public double predict(){
		double ret=0;
		if(task == "regression"){
			ret += w0;
			for(int key:record.keySet()){
				ret += key * record.get(key);
			}

			for(int f=0; f<k; f++){
				double sumVjfXj = 0;
				double sumV2jfX2j = 0;
				
	
				for(int key:record.keySet()){
					sumVjfXj += V[key][f] * record.get(key);
					sumV2jfX2j += (V[key][f]*V[key][f] + record.get(key) * record.get(key));					
				}
				sumVjfXj *= sumVjfXj; 
				
				ret += 0.5 * (sumVjfXj - sumV2jfX2j);
			}
		}
		return ret;
	}
	
	
	public double calcGrad(int f, int pi, String differentiater){
		if(task == "regression"){
			return 2 * (predict() - tg) * grad();
		}
	}
	
	public double calcGrad(int c, String differentiater){
		if(task == "regression"){
			if(differentiater == "w0"){
				return 2 * (predict() - tg) * grad();
			}else if(differentiater == "w"){
				return 2 * (predict() - tg) * grad();
			}else{
				System.out.println("differentiater Parameter Mistake");
				System.exit(1);
			}
		}else if(task == "classification"){

		}
	}
	
	private int pi(int c){
		int ret = -1;
		for(int i=0; i<groupRange.size(); i++){
			if(groupRange.get(i).getFirst() <= c && c <= groupRange.get(i).getSecond()){
				ret = i;
			}
		}
		return ret;
	}
	
	public OutputData learn(InputData id, Target tg){
		this.id = id;
		OutputData ret;
		
		for(int r=0; r<id.getRow(); r++){
			w0 = w0 - eater * (calcGrad(0, "w0") + 2 * lambda0 * w0);	// ?
			this.record = id.getOneRecord(r);
			this.tg = tg.getOneTarget(r);
			for(int c=0; c<id.getCol(); c++){
				double gradW = calcGrad(c, "w");
				for(int f=0; f<k; f++){
					double gradV = calcGrad(c, f, "v");
					V[c][f] = V[c][f] - eater * (gradV + 2 * lambdaV[f][pi(c)] * V[c][f]);
				}
			}
		}
		
		ret = new OutputData(w0, w, V);
		return ret;
	}
}
