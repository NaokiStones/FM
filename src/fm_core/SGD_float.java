package fm_core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class SGD_float {
    private float w0;
    private float[] w;
    private float[][] V;

    private float eta = (float) 0.1; // reg0, number:by intuition
    private float lambda0;
    private ArrayList<Float> lambdaW = new ArrayList<Float>();
    private float[][] lambdaV;

    private String task;

    private int[] groupRangeUpperLimit;

    private int k;
    private int col;
    private int row;
    //private HashMap<Integer, float> record = new HashMap<Integer, float>();
    //private float y;
    private Random random = new Random();
    private int groupNum;
    private ArrayList<Float> results = new ArrayList<Float>();
    private float alpha = (float) 0.5;
    private float t0 = 2;
    private float power_t = (float) 0.1;

    public SGD_float() {
        w0 = 0;
        task = "regression";
        random.setSeed(111);
        // kakikake
    }

    private float sigmoid(float x) {
        return (float)(1 / (1.0 + Math.exp(-1.0 * x)));
    }

    private float predict(float y, Map<Integer, Float> record) {
        float ret = 0;
        if(task.equals("regression") || task.equals("classification")) {

            ret += w0;
            for(int key : record.keySet()) {
                ret += w[key] * record.get(key);
            }

            for(int f = 0; f < k; f++) {
                float sumVjfXj = 0;
                float sumV2jfX2j = 0;

                for(int key : record.keySet()) {
                    sumVjfXj += V[key][f] * record.get(key);
                    sumV2jfX2j += (V[key][f] * V[key][f] * record.get(key) * record.get(key));
                }

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
            if(ret < 0.5) { // magic number
                ret = (float)0.0;
            } else {
                ret = (float)1.0;
            }
        }
        //System.out.println("ret:" + ret);//************************
        if(Float.isNaN(ret)) {
            System.out.println("NaN!");
            System.exit(1);
        }
        //********************************
        return ret;
    }

    private float calcVGrad(int l, int f, Map<Integer, Float> record) {
        float ret = 0;
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

    private float calcGradV(int key, int f, Map<Integer, Float> record,float y, String differentiater) {
        float ret = 0;
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

    private float calcGrad(int c, Map<Integer, Float> record, float y, String differentiater) {
        float ret = 0;
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
        lambda0 = ((float)0.1 * random.nextFloat()); // tmp
        for(int i = 0; i < groupNum; i++) {
            lambdaW.add((float)(0.1 * random.nextFloat())); // tmp
        }

        lambdaV = new float[k][groupNum];
        V = new float[col][k];

        for(int i = 0; i < col; i++) {
            Arrays.fill(V[i], 0);
        }
        

        for(int i = 0; i < k; i++) {
            for(int j = 0; j < groupNum; j++) {
                lambdaV[i][j] = (float)0.1 * random.nextFloat(); // tmp
            }
        }

        // Initialize weights
        w0 = 0;
        w = new float[this.col];
        Arrays.fill(w, 0);

        for(int i = 0; i < col; i++) {
            for(int j = 0; j < k; j++) {
                V[i][j] = (float)Math.min(random.nextGaussian(), 7);
                //****************************
                if(V[i][j] > 7) {
                    System.out.println("V[i][j] is too big!");
                    System.exit(1);
                }
                //******************************
            }
        }
    }
    
    public float dloss(float predicted, float y, int i, int f, String differentiater, Map<Integer, Float> record){
    	float ret = 0;
    	if(task.equals("regression")){
    		float diff = predicted -y;
    		ret = 2 * diff * calcVGrad(i, f, record);
    	}else if(task.equals("classification")){
    		ret = sigmoid(predicted * y * calcVGrad(i, f, record));
    	}
    	return ret;
    }

    public OutputData_Float learn(InputData_Float id, Target tg, int k, String task) {
        this.k = k;
        OutputData_Float ret;

        this.col = id.getCol();
        this.row = id.getRow();
        this.groupNum = id.getGroup();
        this.task = task;
        this.groupRangeUpperLimit = id.getGroupRangeUpperLimit();

        init();

        int t = 0;
        for(int iter = 0; iter < 100; iter++) { // tmp
            System.out.println(iter); //***********************
            float diff = 0;
            for(int p = 0; p < id.getRow(); p++, t++) {
                //float tmpEta = eater / (t0 + t*0.1);
                //float tmpEta = eater / Math.pow((t + 1), power_t);
                float eta = (float)0.005;

                Map<Integer, Float> record = id.getOneRecord(p); // pick up one record
                float y = (float)tg.getOneTarget(p); // pickup the target for the chosen record
                
                w0 = w0 - eta * (calcGrad(0, record, y, "w0") + 2 * lambda0 * w0);
                //System.out.println("w0:" + w0);//**************

                for(int i : record.keySet()) {
                    float gradWi = calcGrad(i, record, y, "w");  // dloss(predict("w", i), y);
                    int pi = pi(i);
                    float nextWi = w[i] - eta * (gradWi + 2 * lambdaW.get(pi) * w[i]);
                    w[i] = nextWi;
                    for(int f = 0; f < k; f++) {
                        float gradVij = dloss(predict(y, record), y, i, f, "v", record);	//calcGradV(i, f, pi, y, "v"); // dloss(predict("v", i, f), y)
                        //System.out.println("key:" + key +  ", f:" + f + ", groupOfKey:" + groupOfKey); //******************
                        V[i][f] -= eta * (gradVij + 2 * lambdaV[f][pi] * V[i][f]);
                        // System.out.println("V[key][f]:" + V[key][f]); //***************
                    }

                }
                if(task.equals("regression")) {
                    float pred = predict(y, record);
                    float d = y - pred;
                    diff += d * d;
                } else if(task.equals("classification")) {
                    if(y != predict(y, record)) {
                         diff++;
                    }
                }
            }
            // effect evaluation
            results.add(diff);
        }

        System.out.println(results); //****

        ret = new OutputData_Float(w0, w, V);
        return ret;
    }
}
