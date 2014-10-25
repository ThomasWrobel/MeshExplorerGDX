package com.lostagain.nl.uti;

import com.badlogic.gdx.math.Vector2;


public class SpiffyVector2Tween {
	
	Vector2 precalced[];

	int totalsteps=0;	 
	int CurrentStep = 0;
	
	enum tweeningtype {
		linear
	}
	tweeningtype type = tweeningtype.linear;
	
	//currently only linear
	public SpiffyVector2Tween(Vector2 start, Vector2 end, int steps){
		
		//precalculate
		
		//create linear vectors in x and y
		NewSpiffyLinearTween X = null;
		NewSpiffyLinearTween Y = null;
		
		
		if (type == tweeningtype.linear){
			 X = new NewSpiffyLinearTween(start.x,end.x,steps);
			 Y = new NewSpiffyLinearTween(start.y,end.y,steps);
		}
						
		//use their results to popular the vector array
		for (int i = 0; i < steps; i++) {
			
			double x = X.precalculated_stepsD[i];
			double y = Y.precalculated_stepsD[i];
			
			precalced[i].x = (float)x;
			precalced[i].y = (float)y;
			
		}
		
		
	}
	
	
	public Vector2 next()	
	{
		CurrentStep++;
		return precalced[CurrentStep];
		
		
	}
	
	public Vector2 previous()
	{
		CurrentStep--;
		return precalced[CurrentStep];
	}
	
	public Vector2 endPoint()
	{
		return precalced[totalsteps];
	}
	
	
	public boolean hasNext() {
		if (CurrentStep<totalsteps){
			return true;
		}
		return false;
	}

	
	
}
