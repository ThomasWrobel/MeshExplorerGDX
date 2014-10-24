package com.lostagain.nl.me.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class ModelManagment {

	private static String logstag="ModelManagment";
	
	//might need to be divided into transparent and non-transparent at some point
	//for optimization
	public static Array<ModelInstance> allModelInstances = new Array<ModelInstance>();
	
	/**all hitable models **/
	public static Array<hitable> hitables = new Array<hitable>();


	public static void addmodel(ModelInstance model) {
		allModelInstances.add(model);
		
	}

	public static void removeModel(ModelInstance model) {
		allModelInstances.removeValue(model,true);
	}

	
	public static boolean testForHit(Ray ray) {
		
		Gdx.app.log(logstag,"_-testing hit in :"+hitables.size+" models");

		
        final hitable test = hitables.get(0);

		// 
		 
		// test.getTransform().getTranslation(position);
      //  position.add(test.getCenter());

		//Gdx.app.log(logstag,"_-testing hit in :"+position.x +" x");
		//Gdx.app.log(logstag,"_-testing hit in :"+position.y +" y");
		
        
		Gdx.app.log(logstag,"_-testing ray in :"+ray.origin.x+" models");
		Gdx.app.log(logstag,"_-testing ray in :"+ray.origin.y+" models");
		
		Vector3 position = new Vector3();
		int result = -1;
	    float distance = -1;
	 
	    for (int i = 0; i < hitables.size; ++i) {
	    	
	        final hitable instance = hitables.get(i);
	 
	        //instance.getTransform().getTranslation(position);
	      //  position.add(instance.getCenter());
	        
	        float dist2 = ray.origin.dst2(position);
	        if (distance >= 0f && dist2 > distance)
	            continue;
	 
	        if (Intersector.intersectRaySphere(ray, instance.getCenter(), instance.getRadius(), null)) {
	    		Gdx.app.log(logstag,"_hit in :"+i);
	            result = i;
	            distance = dist2;
	            
	    		
	        }
	        
	        
	        
	        
	    }
	 
	    if (result!=-1){
	    	return true;
	    }
		return false;
	}

	public static void addHitable(hitable model) {
		hitables.add(model);
		
	}

	public static void removeHitable(hitable model) {
		hitables.removeValue(model,true);
	}

	
	public static void untouchAll() {
		
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
