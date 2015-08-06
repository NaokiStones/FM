package test;

import java.util.ArrayList;
import java.util.Random;

import fm_core.InputData;
import fm_core.OutputData;
import fm_core.SGD;
import fm_core.Target;

public class SGD_Regression_Test {
	public static void main(String args[]){
		// test case
		double[][] testMatrix = new double[100][300]; 
		int group = 3;	// group数
		int[] groupRange = new int[group];
		groupRange[0] = 30;
		groupRange[1] = 100;
		groupRange[2] = 170;

		String[] names = new String[group];
		names[0] = "name1";
		names[1] = "name2";
		names[2] = "name3";
	

		ArrayList<Double> tg_regression = new ArrayList<Double>();

		Random random = new Random();
		random.setSeed(117);
		// matrix for regression
		for(int i=0; i<100; i++){
			for(int j=0; j<300; j++){
				if(j < 3){
					if(i < 30){
						testMatrix[i][0] = 1;
					}else if(i < 40){
						testMatrix[i][1] = 1;
					}else{
						testMatrix[i][2] = 1;
					}
					
				}else if(i < 30){
					tg_regression.add(700.0);
					if(j<100){
						if(random.nextDouble() < 0.2){
							testMatrix[i][j] = 0.007*random.nextFloat();	// tmp
						}else{
							testMatrix[i][j] = 0;
						}
					}else{
						testMatrix[i][j] = 0;
					}
				}else if(i <40){
					tg_regression.add(100.0);
					if(j <= 100 && j<200){
						if(random.nextDouble() < 0.3){
							testMatrix[i][j] = 0.003*random.nextFloat();	//tmp
						}else{
							testMatrix[i][j] = 0;
						}					
					}else{
						testMatrix[i][j] = 0;
					}
				}else{
					tg_regression.add(300.0);
					if(j<=200){
						if(random.nextDouble() < 0.2){
							testMatrix[i][j] = 0.0002*random.nextFloat();	//tmp
						}else{
							testMatrix[i][j] = 0;
						}
					}else{
						testMatrix[i][j] = 0;
					}
				}
			}
		}
		

		
		// 
		InputData inputData = new InputData(testMatrix, group, groupRange, names);
		Target target = new Target(tg_regression);
		//Target target = new Target(tg_classification);
		SGD sgd = new SGD();
		OutputData outputData = sgd.learn(inputData, target, 20, "regression");
		//System.out.println("OutputData");	//*****
		//System.out.println("w0" + outputData.w0);		//****
		//System.out.println("w0" + outputData.w);		//****
	}
}
