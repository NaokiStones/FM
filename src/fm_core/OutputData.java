package fm_core;

import java.util.ArrayList;

public class OutputData {
	public double w0;
	public ArrayList<Double> w;
	public double[][] V;
	
	public OutputData(double w0, ArrayList<Double> w, double[][] V){
		this.w0 = w0;
		this.w = w;
		this.V = V;
	}
}
