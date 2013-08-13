package org.sbs;

public class T {
	
	public boolean getb(){
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				if(j==3){
					return false;
				}
				System.err.println(i+" , "+j);
			}
		}
		return true;
	}
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {
		T t = new T();
		t.getb();
	}

}
