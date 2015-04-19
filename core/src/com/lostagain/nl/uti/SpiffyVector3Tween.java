package com.lostagain.nl.uti;

import com.badlogic.gdx.math.Vector3;


public class SpiffyVector3Tween {
	
	Vector3 precalced[];

	int totalsteps=0;	 
	int CurrentStep = 0;
	
	enum tweeningtype {
		linear
	}
	tweeningtype type = tweeningtype.linear;
	
	//currently only linear
	public SpiffyVector3Tween(Vector3 start, Vector3 end, int steps){
		
		totalsteps = steps;
		
		//Allocate array
		precalced = new Vector3[steps];
		
		//create linear vectors in x and y
		NewSpiffyLinearTween X = null;
		NewSpiffyLinearTween Y = null;
		NewSpiffyLinearTween Z = null;
		
		if (type == tweeningtype.linear){
			 X = new NewSpiffyLinearTween(start.x,end.x,steps);
			 Y = new NewSpiffyLinearTween(start.y,end.y,steps);
			 Z = new NewSpiffyLinearTween(start.z,end.z,steps);
		}
		
		
						
		//use their results to populate the vector array
		for (int i = 0; i < (steps-1); i++) {
			
			double x = X.precalculated_stepsD[i];
			double y = Y.precalculated_stepsD[i];
			double z = Z.precalculated_stepsD[i];

			precalced[i] = new Vector3((float)x,(float)y,(float)z);
			
		}
		
		//the last step will just the be end point
		precalced[steps-1] = end;
		
	}
	
	
	public Vector3 next()	
	{
		CurrentStep++;
		return precalced[CurrentStep];
		
		
	}
	
	public Vector3 previous()
	{
		CurrentStep--;
		return precalced[CurrentStep];
	}
	
	public Vector3 endPoint()
	{
		return precalced[totalsteps-1];
	}
	
	
	public boolean hasNext() {
		if ((CurrentStep+1)<totalsteps){
			return true;
		}
		return false;
	}

	
	
}
