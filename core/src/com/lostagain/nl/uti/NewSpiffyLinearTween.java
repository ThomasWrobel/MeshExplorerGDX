package com.lostagain.nl.uti;

public class NewSpiffyLinearTween implements NewSpiffyTween {
	
	double precalculated_stepsD[];
	int precalculated_stepsI[];
	
	int totalsteps=0;	 
	int CurrentStep = 0;
	
	enum tweenmode {
		Double,Int
	}
	tweenmode mode = tweenmode.Double;
	
	public NewSpiffyLinearTween(int start, int end, int steps) {
		
		precalculated_stepsI = get1DSteps(start,end,steps);
		mode = tweenmode.Int;
		
		totalsteps=steps;
				
	}
	public NewSpiffyLinearTween(double start, double end, int steps) {
		
		precalculated_stepsD = get1DSteps(start,end,steps);
		mode = tweenmode.Double;	

		totalsteps=steps;
	}
	
	public NewSpiffyLinearTween(float start, float end, int steps) {
		
		precalculated_stepsD = get1DSteps(start,end,steps);
		mode = tweenmode.Double;
		

		totalsteps=steps;
				
	}
	
	 /** creates a int tween. DO NOT use double methods to call it if you created it this way**/
	
	    public static int[] get1DSteps (int start, int end, int steps) {
	        double[] preciseResult = get1DSteps((double) start, (double) end, steps);
	        int[] result = new int[steps];
	        for (int i=0; i<steps; i++) {
	            result[i] = (int) (preciseResult[i] + 0.5D);
	        }
	        	        
	        return result;
	    }

	    /** creates a double tween. DO NOT use double methods to call it if you created it this way**/
		
	    public static double[] get1DSteps (float start, float end, int steps) {
	        double[] result = get1DSteps((double)start, (double)end, steps);
	        return result;
	    }

	    /** creates a double tween. DO NOT use double methods to call it if you created it this way**/
		
	    public static double[] get1DSteps (double start, double end, int steps) {
	        double distance;
	        double stepSize;
	        double[] result = new double[steps];

	        distance = end - start;
	        stepSize = distance / steps;
	        for (int i=0; i < steps; i++) {
	            result[i] = start + stepSize*i;
	        }
	        return result;
	    }


		public void reset(){
			CurrentStep=0;
		}
		
		
		public double nextD(){
			
			CurrentStep++;		

			double currentVal = precalculated_stepsD[CurrentStep];
			return currentVal;
			
		}
		
		public double previousD(){
			
			CurrentStep--;

			double currentVal = precalculated_stepsD[CurrentStep];
			return currentVal;
		}

		@Override
		public double endPointD() {
			
			return precalculated_stepsD[totalsteps-1];
		}

		public int nextI(){
			
			CurrentStep++;		

			int currentVal = precalculated_stepsI[CurrentStep];
			return currentVal;
			
		}
		
		public int previousI(){
			
			CurrentStep--;
			int currentVal = precalculated_stepsI[CurrentStep];
			
			return currentVal;
		}

		@Override
		public int endPointI() {
			
			return precalculated_stepsI[totalsteps-1];
		}
		
		@Override
		public boolean hasNext() {
			if ((CurrentStep+1)<totalsteps){
				return true;
			}
			return false;
		}




	
}
