package fm_core;

import java.util.ArrayList;
import java.util.HashMap;

import util.Tuple;

public class SGD {
	private double w0;
	private ArrayList<Double> w;
	private double[][] V;
	

	private double eater = 0.1;	// reg0, number:by intuition
	private double lambda0;
	private ArrayList<Double> lambdaW;
	private double[][] lambdaV;
	
	private String task;
	
	private InputData id;
	private ArrayList<Tuple> groupRange;
	
	private int p;
	private int k;
	private int col;
	private int row;
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
	
	private double calcVGrad(int f){
		double ret = 0;
		for(int key:record.keySet()){
			if(key == f){
				continue;
			}else{
				ret += V[key][f] * record.get(key); 
			}
		}
		return ret;
	}
	
	private double calcGrad(int f, int pi, String differentiater){
		double ret = 0;
		if(task == "regression"){
			ret = 2 * (predict() - tg) * calcVGrad(f);	// grad() => x_{l} * sum(v_{i,f} * x_{j})_{j != l}
		}
		return ret;
	}
	
	private double calcGrad(int c, String differentiater){
		double ret = 0;
		if(task == "regression"){
			if(differentiater == "w0"){
				ret = 2 * (predict() - tg) * 1;	// grad() => 1
			}else if(differentiater == "w"){
				ret = 2 * (predict() - tg) * record.get(c);	// grad() => x_{l}
			}else{
				System.out.println("differentiater Parameter Mistake");
				System.exit(1);
			}
		}else if(task == "classification"){
			// add something...
		}
		return ret;
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
	
	public void init(){
		// Init Lambdas
	}
	
	public OutputData learn(InputData id, Target tg){
		this.id = id;
		OutputData ret;
		
		this.col = id.getCol();
		this.row = id.getRow();
		
		init();
		
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
