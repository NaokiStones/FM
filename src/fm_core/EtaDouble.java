package fm_core;

public class EtaDouble extends Eta {
	// Double Version

	// Ada
	private int p;
	private int k;
	private double defaultEta = 0.1;	// tmp
	private double[] accW;
	private double[][] accV;
	private double alpha=0;
	private String updateType;
	
	// Power
	private double tp;
	
	// Time
	private double t0;
	
	// Ada Constracter
	public EtaDouble(String updateType, int p, int k, double alpha, double initAcc){
		this.updateType = updateType;
		this.alpha = alpha;
		this.p = p;
		this.k = k;
		
		// Initialize defaultEta
		defaultEta = initAcc;
		
		// Initialize Vector and Matrix
		accW = new double[p+1];		// w0 + w
		accV = new double[p][k];	// V
		for(int i=0; i<p+1; i++) accW[i] = initAcc;
		for(int i=0; i<p; i++){
			for(int f=0; f<k; f++){
				accV[i][f] = initAcc;
			}
		}
	}
	
	// power Constracter
	public EtaDouble(String updateType, double alpha, double tp){
		this.updateType = updateType;
		this.alpha = alpha;
		this.tp = tp;
	}
	
	// time Constracter
	public EtaDouble(String updateType, double t0){
		this.updateType = updateType;
		this.t0 = t0;
	}
	
	// fix
	public EtaDouble(String updateType){
		this.updateType = updateType;
	}
		
	public void updateEta(String s, double gt, int... args){	// [0]: i, [1]: f
		double ret = 0;
		if(updateType.equals("fix")){
			ret = defaultEta;		// always default
		}else if(updateType.equals("ada")){
			if(s.equals("w0")){
				accW[0] += gt;
				ret = (alpha) / (accW[0]);
			}else if(s.equals("w")){
				int idx = args[0] + 1; 
				accW[idx] += gt;
				ret = (alpha) / (accW[idx]);				
			}else if(s.equals("v")){
				int idxI = args[0];
				int idxF = args[1];
				accV[idxI][idxF] += gt;
				ret = (alpha) / (accV[idxI][idxF]);
			}else{
				System.out.println("Eta:updateAndGetEta:: cannot find defferentiater");
				System.exit(1);
			}
		}else if(updateType.equals("power")){
			//eater / Math.pow((t + 1), power_t)
			// gt = power_t
			ret = defaultEta / (Math.pow((args[0]+1), gt));
		}else if(updateType.equals("time")){
			// double eta = this.eta / (t0 + t*0.1);
			ret = defaultEta / (args[0] + args[1]*0.1);
		}else{
			System.out.println("cannot find updateType");
			System.exit(1);
		}
	}
	
	public double getEta(String differentiater,  int... indexes){	// 0:i={1,2,...,p} 1:f={1,2,...,k}
		double ret = 0;
		if(updateType.equals("fix")){
			ret = defaultEta;		// always default
		}else if(updateType.equals("ada")){
			if(differentiater.equals("w0")){
				ret = (alpha) / (accW[0]);
			}else if(differentiater.equals("w")){
				ret = (alpha) / (accW[indexes[0] + 1]);				
			}else if(differentiater.equals("v")){
				int idxI = indexes[0];
				int idxF = indexes[1];
				ret = (alpha) / (accV[idxI][idxF]);
			}else{
				System.out.println("Eta:updateAndGetEta:: cannot find defferentiater");
				System.exit(1);
			}
		}else if(updateType.equals("power")){
			int t = indexes[0];
			
			ret = defaultEta / (Math.pow((t+1.0), tp));
		}else if(updateType.equals("time")){
			int t = indexes[0];
			
			ret = defaultEta / (t0 + t * 0.1);
		}else{
			System.out.println("cannot find updateType");
			System.exit(1);
		}
		return ret;
	}

	@Override
	public void updateEtaFloat(String s, float gt, int... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getEtaFloat(String s, int... indexes) {
		// TODO Auto-generated method stub
		return 0;
	}
}
