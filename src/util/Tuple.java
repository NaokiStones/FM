package util;

public class Tuple{
	public final int _a;
	public final int _b;
	
	public Tuple(int a, int b){
		_a = a;
		_b = b;
	}
	
	public int getFirst(){
		return _a;
	}
	public int getSecond(){
		return _b;
	}

}
