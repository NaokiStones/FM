package fm_core;

public abstract class Eta {
	abstract public void updateEta(String s, double gt, int... args);
	abstract public double getEta(String s,  int... indexes);

	abstract public void updateEtaFloat(String s, float gt, int... args);
	abstract public float getEtaFloat(String s,  int... indexes);

}
