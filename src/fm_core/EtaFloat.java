package fm_core;

public class EtaFloat extends Eta {
	// float Version
	

	private float defaultEta;
	private float[] accW;
	private float[][] accV;
	private float alpha=0;
	private String updateType;
	
	public EtaFloat(String updateType, int p, int k, float alpha, float initAcc){
		this.updateType = updateType;
		this.alpha = alpha;
		
		// Initialize defaultEta
		defaultEta = initAcc;
		
		// Initialize Vector and Matrix
		accW = new float[p+1];		// w0 + w
		accV = new float[p][k];	// V
		for(int i=0; i<p+1; i++) accW[i] = initAcc;
		for(int i=0; i<p; i++){
			for(int f=0; f<k; f++){
				accV[i][f] = initAcc;
			}
		}
	}
	
		
	public float updateAndGetEta(String s, float gt, int... args){	// [0]: i, [1]: f
		float ret = 0;
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
			ret = defaultEta / ((float)(Math.pow((args[0]+1), gt)));
		}else if(updateType.equals("time")){
			// float eta = this.eta / (t0 + t*0.1);
			ret = defaultEta / ((float)(args[0] + args[1]*0.1));
		}else{
			System.out.println("cannot find updateType");
			System.exit(1);
		}
		return ret;
	}
}
