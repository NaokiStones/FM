package fm_core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SGD {
    private double w0;
    private double[] w;
    private double[][] V;

    private double eta = 0.1; // reg0, number:by intuition
    private double lambda0;
    private ArrayList<Double> lambdaW = new ArrayList<Double>();
    private double[][] lambdaV;

    private String task;

    private int[] groupRangeUpperLimit;

    private int k;
    private int col;
    private int row;
    //private HashMap<Integer, Double> record = new HashMap<Integer, Double>();
    //private double y;
    private Random random = new Random();
    private int groupNum;
    private ArrayList<Double> results = new ArrayList<Double>();
    private double alpha = 0.5;
    private double t0 = 2;
    private double power_t = 0.1;

    public SGD() {
        w0 = 0;
        task = "regression";
        random.setSeed(111);
        // kakikake
    }

    private double sigmoid(double x) {
        return 1 / (1.0 + Math.exp(-1.0 * x));
    }

    private double predict(double y, Map<Integer, Double> record) {
        double ret = 0;
        if(task.equals("regression") || task.equals("classification")) {

            ret += w0;
            for(int key : record.keySet()) {
                ret += w[key] * record.get(key);
            }

            for(int f = 0; f < k; f++) {
                double sumVjfXj = 0;
                double sumV2jfX2j = 0;

                //System.out.println("============");//****
                for(int key : record.keySet()) {
                    sumVjfXj += V[key][f] * record.get(key);
                    sumV2jfX2j += (V[key][f] * V[key][f] * record.get(key) * record.get(key));
                    //System.out.println("V[key][f]:" + V[key][f]); //**************
                    //System.out.println("record.get(key):" + record.get(key)); //*****
                    //System.out.println("sumVjfXj:" + sumVjfXj); //*****
                    //System.out.println("sumV2jfX2j:" + sumV2jfX2j); //*****
                    //System.out.println("");//****
                }
                //System.out.println("sumVjfXj:" + sumVjfXj); //*****
                //System.out.println("sumV2jfX2j:" + sumV2jfX2j); //*****
                sumVjfXj *= sumVjfXj;

                ret += 0.5 * (sumVjfXj - sumV2jfX2j);

            }
        }

        if(task.equals("classification")) {
            if(y == 1.0) {
                ret = 1 - sigmoid(ret);
            } else {
                ret = sigmoid(ret);
            }
            //System.out.println("ret" + ret);	//****
            if(ret < 0.5) { // magic number
                ret = 0.0;
            } else {
                ret = 1.0;
            }
        }
        //System.out.println("ret:" + ret);//************************
        if(Double.isNaN(ret)) {
            System.out.println("NaN!");
            System.exit(1);
        }
        //********************************
        return ret;
    }

    private double calcVGrad(int l, int f, Map<Integer, Double> record) {
        double ret = 0;
        for(int j : record.keySet()) {
            if(j == l) {
                continue;
            } else {
                //System.out.println("key:" + key + ", f:" + f); //*************
                ret += V[j][f] * record.get(j);
            }
        }
        if(!record.containsKey(l)) {
            System.out.println("f is not contained");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ret *= record.get(l);
        return ret;
    }

    private double calcGradV(int key, int f, Map<Integer, Double> record,double y, String differentiater) {
        double ret = 0;
        if(task.equals("regression")) {
            ret = 2 * (predict(y, record) - y) * calcVGrad(key, f, record); // grad() => x_{l} * sum(v_{i,f} * x_{j})_{j != l}
        } else if(task.equals("classification")) {
            ret = (sigmoid(predict(y, record)) - 1) * y * calcVGrad(key, f, record);
        } else {
            System.out.println("task");
            System.exit(1);
        }
        return ret;
    }

    private double calcGrad(int c, Map<Integer, Double> record, double y, String differentiater) {
        double ret = 0;
        if(task.equals("regression")) {
            if(differentiater.equals("w0")) {
                ret = 2 * (predict(y, record) - y) * 1; // grad() => 1
            } else if(differentiater.equals("w")) {
                ret = 2 * (predict(y, record) - y) * record.get(c); // grad() => x_{l}
            } else {
                System.out.println("differentiater Parameter Mistake");
                System.exit(1);
            }

        } else if(task.equals("classification")) {
            // add something...
            if(differentiater.equals("w0")) {
                ret = sigmoid(predict(y, record) * y - 1) * y * 1;
            } else if(differentiater.equals("w")) {
                ret = sigmoid(predict(y, record) * y - 1) * y * record.get(c);
            } else {
                System.out.println("differentiater Parameter Mistake");
                System.exit(1);
            }
        }
        return ret;
    }

    private int pi(int c) {
        int ret = -1;
        for(int i = 0, GRUL_Length = groupRangeUpperLimit.length; i < GRUL_Length; i++) {
            if(c <= groupRangeUpperLimit[i]) {
                ret = i;
                break;
            }
        }
        //assert groupRangeUpperLimit.size()!=0:"groupRangeUpperLimit size is 0";//******
        //System.out.println(groupRangeUpperLimit.get(groupRangeUpperLimit.size()-1));//********
        //System.out.println("c:" + c );//******************
        return ret;
    }

    public void init() {
        // Initialize Lambdas
        lambda0 = 0.1 * random.nextDouble(); // tmp
        for(int i = 0; i < groupNum; i++) {
            lambdaW.add(0.1 * random.nextDouble()); // tmp
        }

        lambdaV = new double[k][groupNum];
        V = new double[col][k];

        for(int i = 0; i < col; i++) {
            Arrays.fill(V[i], 0);
        }
        

        for(int i = 0; i < k; i++) {
            for(int j = 0; j < groupNum; j++) {
                lambdaV[i][j] = 0.1 * random.nextDouble(); // tmp
            }
        }

        // Initialize weights
        w0 = 0;
        w = new double[this.col];
        Arrays.fill(w, 0);

        for(int i = 0; i < col; i++) {
            for(int j = 0; j < k; j++) {
                V[i][j] = Math.min(random.nextGaussian(), 7);
                //****************************
                if(V[i][j] > 7) {
                    System.out.println("V[i][j] is too big!");
                    System.exit(1);
                }
                //******************************
            }
        }
    }
    
    public double dloss(double predicted, double y, int i, int f, String differentiater, Map<Integer, Double> record){
    	double ret = 0;
    	if(task.equals("regression")){
    		double diff = predicted -y;
    		ret = 2 * diff * calcVGrad(i, f, record);
    	}else if(task.equals("classification")){
    		ret = sigmoid(predicted * y * calcVGrad(i, f, record));
    	}
    	return ret;
    }

    public OutputData learn(InputData id, Target tg, int k, String task) {
        this.k = k;
        OutputData ret;

        this.col = id.getCol();
        this.row = id.getRow();
        this.groupNum = id.getGroup();
        this.task = task;
        this.groupRangeUpperLimit = id.getGroupRangeUpperLimit();

        init();

        int t = 0;
        for(int iter = 0; iter < 100; iter++) { // tmp
            System.out.println(iter); //***********************
            double diff = 0;
            for(int p = 0; p < id.getRow(); p++, t++) {
                //double tmpEta = eater / (t0 + t*0.1);
                //double tmpEta = eater / Math.pow((t + 1), power_t);
                double eta = 0.005;

                Map<Integer, Double> record = id.getOneRecord(p); // pick up one record
                double y = tg.getOneTarget(p); // pickup the target for the chosen record
                
                w0 = w0 - eta * (calcGrad(0, record, y, "w0") + 2 * lambda0 * w0);
                //System.out.println("w0:" + w0);//**************

                for(int i : record.keySet()) {
                    double gradWi = calcGrad(i, record, y, "w");  // dloss(predict("w", i), y);
                    int pi = pi(i);
                    double nextWi = w[i] - eta * (gradWi + 2 * lambdaW.get(pi) * w[i]);
                    w[i] = nextWi;
                    for(int f = 0; f < k; f++) {
                        double gradVij = dloss(predict(y, record), y, i, f, "v", record);	//calcGradV(i, f, pi, y, "v"); // dloss(predict("v", i, f), y)
                        //System.out.println("key:" + key +  ", f:" + f + ", groupOfKey:" + groupOfKey); //******************
                        V[i][f] -= eta * (gradVij + 2 * lambdaV[f][pi] * V[i][f]);
                        // System.out.println("V[key][f]:" + V[key][f]); //***************
                    }

                }
                if(task.equals("regression")) {
                    double pred = predict(y, record);
                    double d = y - pred;
                    diff += d * d;
                } else if(task.equals("classification")) {
                    if(y != predict(y, record)) {
                        //System.out.println("tg:" + this.tg + ", predict:" + predict());	//****
                        diff++;
                    }
                }
            }
            // effect evaluation

            results.add(diff);
        }

        System.out.println(results); //****

        ret = new OutputData(w0, w, V);
        return ret;
    }
}
