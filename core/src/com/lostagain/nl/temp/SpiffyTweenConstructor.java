package com.lostagain.nl.temp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Logger;


public class SpiffyTweenConstructor {


	static Logger Log = Logger.getLogger("SpiffyTweenConstructor");
	
	public static SpiffyTween<Double> Create(double start,double end, int steps) {
		
		
		SpiffyTween<Double> meep = new SpiffyTween<Double>(start,end, steps);
		
		//Log.info("All values of tween");
    	//while (meep.hasNext()){
    	//	Log.info("value="+meep.next());
    	//}
    	
		
		return meep;
		
		
		
	}

}


class SpiffyTween<T extends Number> implements SpiffyGenericTween<T>
{

	static Logger Log = Logger.getLogger("SpiffyTween <Number>");
	
	private T start;
	private T end;
	int totalsteps=0;
	 
	int CurrentStep = 0;
	ArrayList<T> steps = new ArrayList<T>();
	
	public SpiffyTween(T start,T end, int steps) {
		super();
		
		this.start = start;
		this.end = end;
		this.totalsteps = steps;
		
		
		precalculate();
		
	}
	
	

	private void precalculate() {
		
		//calc step difference 
		double dif = ((end.doubleValue() -start.doubleValue())/totalsteps);
		
		Log.info("dif="+dif);
		
		//T laststepvalue=start;
		//precalculate the steps
		int i=0;
		while(i<totalsteps){
			
			
			T stepvalue = (T)((Number)(start.doubleValue() +(dif*i)));
			
			
			steps.add(stepvalue);
			

			//Log.info("add step="+stepvalue);
			i++;
		}
		
		
		
	}

	public void reset(){
		CurrentStep=0;
	}
	
	public T next(){
		
		T currentVal = steps.get(CurrentStep);
		CurrentStep++;		
		
		return currentVal;
		
	}
	
	public T previous(){
		
		CurrentStep--;
		T currentVal = steps.get(CurrentStep);
		return currentVal;
	}


	@Override
	public boolean hasNext() {
		if (CurrentStep<totalsteps){
			return true;
		}
		return false;
	}



	@Override
	public T endPoint() {
		// TODO Auto-generated method stub
		return end;
	}
	
	
	
}



/**

 * SpiffyTween<Number>
SpiffyTween<Number[]>
SpiffyTween<Point>
SpiffyTween<ArrayList<Number>>
SpiffyTween<Color>

*/