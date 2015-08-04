package fm_core;

import java.util.ArrayList;

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
	
	public SGD(){
		w0 = 0;
		task = "regression";
		// kakikake
	}
	
	private
	
	public double calcGrad(int f, int pi, String differentiater){
		if(task == "regression"){
			return 2 * (predict() - y) * grad();
		}
	}
	
	public double calcGrad(int c, String differentiater){
		if(task == "regression"){
			if(differentiater == "w0"){
				return 2 * (predict() - y) * grad();
			}else if(differentiater == "w"){
				return 2 * (predict() - y) * grad();
			}else{
				System.out.println("differentiater Parameter Mistake");
				System.exit(1);
			}
		}else if(task == "classification"){
			
		}
	}
	
	private int pi(int c){
		return groupRange[c];
	}
	
	public OutputData learn(InputData id, Target tg){
		this.id = id;
		OutputData ret;
		for(int r=0; r<id.getRow(); r++){
			w0 = w0 - eater * (calcGrad(0, "w0") + 2 * lambda0 * w0);	// ?
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
