package com.lostagain.nl.uti;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.darkflame.client.query.Query;
import com.darkflame.client.semantic.QueryEngine;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;

public class Uti {

	private static String logstag="ME.Uti";
	
	/** (if SSS is functioning correctly it should already be caching results and so no need to do so here
	 * However, if a new database is loaded there will need to be a clear cache function added to SSS else the cached results will be wrong
	 * )
	 * 
	 * @param queryToDestroy
	 * @param appliedConcept
	 */
	
	public static void testIfInQueryResults(String queryToDestroy,
			final SSSNode appliedConcept, final Runnable runIfInResults, final Runnable runIfNotInResults ) {
		

		
		Query answers = Query.createQuerySafely(queryToDestroy);
		

	//	Gdx.app.log(logstag,"vulnerable to="+answers.toString());
		
		DoSomethingWithNodesRunnable RunWhenDone = new DoSomethingWithNodesRunnable(){

			@Override
			public void run(ArrayList<SSSNode> newnodes, boolean invert) {
				
				//Gdx.app.log(logstag,"vulnerable to="+newnodes.toString());
				
				if (newnodes.contains(appliedConcept)){
					runIfInResults.run();
				} else {
					runIfNotInResults.run();
				}
				
				
			}
			
		};
		
		QueryEngine.processQuery(answers, false, null, RunWhenDone);
		
	}

}
