package fm_core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class SGD {
    private double w0;
    private ArrayList<Double> w = new ArrayList<Double>();
    private double[][] V;

    private double eater = 0.1; // reg0, number:by intuition
    private double lambda0;
    private ArrayList<Double> lambdaW = new ArrayList<Double>();
    private double[][] lambdaV;

    private String task;

    private InputData id;
    private ArrayList<Integer> groupRangeUpperLimit = new ArrayList<Integer>();

    private int k;
    private int col;
    private int row;
    private HashMap<Integer, Double> record = new HashMap<Integer, Double>();
    private double tg;
    private Random random = new Random();
    private int groupNum;
    private ArrayList<Double> results = new ArrayList<Double>();
    private double alpha = 0.5;
    private double t0 = 2;
    private double power_t = 0.1;

    public SGD() {
        w0 = 0;
        task = "regression";
        // kakikake
    }

    public double predict() {
        double ret = 0;
        if(task.equals("regression")) {

            ret += w0;
            for(int key : record.keySet()) {
                ret += w.get(key) * record.get(key);
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
        //System.out.println("ret:" + ret);//************************
        if(Double.isNaN(ret)) {
            System.out.println("NaN!");
            System.exit(1);
        }
        //********************************
        return ret;
    }

    private double calcVGrad(int l, int f) {
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ret *= record.get(l);
        return ret;
    }

    private double calcGrad(int key, int f, int pi, String differentiater) {
        double ret = 0;
        if(task.equals("regression")) {
            ret = 2 * (predict() - tg) * calcVGrad(key, f); // grad() => x_{l} * sum(v_{i,f} * x_{j})_{j != l}
        }
        return ret;
    }

    private double calcGrad(int c, String differentiater) {
        double ret = 0;
        if(task.equals("regression")) {
            if(differentiater.equals("w0")) {
                ret = 2 * (predict() - tg) * 1; // grad() => 1
            } else if(differentiater.equals("w")) {
                ret = 2 * (predict() - tg) * record.get(c); // grad() => x_{l}
            } else {
                System.out.println("differentiater Parameter Mistake");
                System.exit(1);
            }

        } else if(task.equals("classification")) {
            // add something...
        }
        return ret;
    }

    private int pi(int c) {
        int ret = -1;
        for(int i = 0; i < groupRangeUpperLimit.size(); i++) {
            if(c <= groupRangeUpperLimit.get(i)) {
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
        // tmp

        for(int i = 0; i < k; i++) {
            for(int j = 0; j < groupNum; j++) {
                lambdaV[i][j] = 0.1 * random.nextDouble(); // tmp
            }
        }

        // Initialize weights
        w0 = 0;
        for(int i = 0; i < col; i++) {
            w.add(0.0);
        }
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

    public OutputData learn(InputData id, Target tg, int k) {
        this.id = id;
        this.k = k;
        OutputData ret;

        this.col = id.getCol();
        this.row = id.getRow();
        this.groupNum = id.getGroup();
        this.groupRangeUpperLimit = id.getGroupRangeUpperLimit();

        init();

        int i = 0;
        for(int iter = 0; iter < 200; iter++) { // tmp
            System.out.println(iter); //***********************
            double diff = 0;
            for(int r = 0; r < id.getRow(); r++, i++) {
                //double tmpEater = eater / (t0 + i*0.1);
                //double tmpEater = eater / Math.pow((i + 1), power_t);
                double tmpEater = 0.005;

                w0 = w0 - tmpEater * (calcGrad(0, "w0") + 2 * lambda0 * w0); // ?
                //System.out.println("w0:" + w0);//**************
                this.record = id.getOneRecord(r); // pick up one record
                this.tg = tg.getOneTarget(r); // pickup the target for the chosen record

                for(int key : record.keySet()) {
                    double gradWi = calcGrad(key, "w");
                    int groupOfKey = pi(key);
                    double nextWi = w.get(key) - tmpEater
			* (gradWi + 2 * lambdaW.get(groupOfKey) * w.get(key));
                    w.set(key, nextWi);
                    for(int f = 0; f < k; f++) {
                        double gradVij = calcGrad(key, f, pi(key), "v");
                        //System.out.println("key:" + key +  ", f:" + f + ", groupOfKey:" + groupOfKey); //******************
                        V[key][f] -= tmpEater * (gradVij + 2 * lambdaV[f][groupOfKey] * V[key][f]);
                        // System.out.println("V[key][f]:" + V[key][f]); //***************
                    }
                    
                }
                diff += (this.tg - predict()) * (this.tg - predict());
            }
            // effect evaluation
            
            
                       
            results.add(diff);
        }

        System.out.println(results); //****

        ret = new OutputData(w0, w, V);
        return ret;
    }
}
