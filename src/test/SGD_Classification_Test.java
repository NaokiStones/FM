package test;

import java.util.ArrayList;
import java.util.Random;

import fm_core.InputData;
import fm_core.OutputData;
import fm_core.SGD;
import fm_core.Target;

public class SGD_Classification_Test {
	public static void main(String args[]){
		// test case
		double[][] testMatrix = new double[100][300]; 
		int group = 2;	// group数
		int[] groupRange = new int[group];
		groupRange[0] = 150;
		groupRange[1] = 150;

		String[] names = new String[group];
		names[0] = "name1";
		names[1] = "name2";

		ArrayList<Double> tg_classification = new ArrayList<Double>();

		Random random = new Random();
		random.setSeed(117);
		// matrix for classification
		for(int i=0; i<100; i++){
			if(i < 50){
				tg_classification.add(1.0);
			}else{
				tg_classification.add(0.0);
			}
			for(int j=0; j<20; j++){
				if(i <50 && j<150){
					if(random.nextDouble() < 0.2){
						testMatrix[i][j] = 0.03 * random.nextDouble();
					}else{
						testMatrix[i][j] = 0;
					}
				}else if(50<=i && 150<=j){
					if(random.nextDouble() < 0.3){
						testMatrix[i][j] = 0.012 * random.nextDouble();
					}else{
						testMatrix[i][j] = 0;
					}
				}else{
					testMatrix[i][j] = 0;
				}
			}
		}
		
		// 
		InputData inputData = new InputData(testMatrix, group, groupRange, names);

		Target target = new Target(tg_classification);
		SGD sgd = new SGD();
		OutputData outputData = sgd.learn(inputData, target, 20, "classification");
		//System.out.println("OutputData");	//*****
		//System.out.println("w0" + outputData.w0);		//****
		//System.out.println("w0" + outputData.w);		//****
	}
}
