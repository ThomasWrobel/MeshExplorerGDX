package com.lostagain.nl.shaders;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.GWTish.ZIndexAttribute;
import com.lostagain.nl.me.features.ConceptObject;
import com.lostagain.nl.shaders.InvertShader.InvertAttribute;
import com.lostagain.nl.shaders.PrettyNoiseShader.PrettyNoiseShaderAttribute;

public class MySorter extends DefaultRenderableSorter {
	
	/**
	 * used to snapshot all the objects being sorted
	 */
	boolean snapshotNextSort = false;
	Array<Renderable> preSortSnapshot;
	Array<Renderable> postSortSnapshot;
	
	@Override
	public void sort(Camera usethiscamera, Array<Renderable> renderables) {
		if (snapshotNextSort){

			Gdx.app.log("zindex", "____snapping___");
			preSortSnapshot =  new Array<Renderable>(renderables);
		}
		
		//super.sort(usethiscamera, renderables);
		camera=usethiscamera;
		customSorter(usethiscamera, renderables);
		
		if (snapshotNextSort){
			postSortSnapshot =  new Array<Renderable>(renderables);
			snapshotNextSort = false;
			logSnapshots();
		}
	}

	

	/**
	 * The goal of this sorter is to sort the renderables the same way LibGDX would do normally (in DefaultRenderableSorter)<br>
	 * except if they have a ZIndex Attribute.<br>
	 * A Zindex attribute provides a groupname string and a number.<br>
	 * Renderables with the attribute are placed next to others of the same group, with the order within the group determined by the number<br>
	 * 
	 * For example an array of renderables like;<br><br>
	 * 0."testgroup",20<br>
	 * 1."testgroup2",10<br>
	 * 2.(no zindex attribute)<br>
	 * 3."testgroup",50<br>
	 * <br>Should become;<br><br>
	 * 0."testgroup",20<br>
	 * 1."testgroup",50<br>
	 * 2.(no zindex attribute)<br>
	 * 3."testgroup2",10<br>
	 * <br> 
	 * assuming the object in testgroup2 is closer to the camera, the one without a index second closest, and the rest furthest<br>
	 * (It is assumed that things within the same group wont be drastically different distances)<br>
	 * 
	 * @param camera
	 * @param resultList
	 */
	private void customSorter(Camera camera, Array<Renderable> resultList) {
		
		//make a copy of the list to sort. (This is probably a bad start)
		Array <Renderable> renderables = new Array <Renderable> (resultList);
		
		//we work by clearing and rebuilding the Renderables array (probably not a good method)
		resultList.clear();
		
		//loop over the copy we made
		for (Renderable o1 : renderables) {
		
			//depending of if the Renderable as a ZIndexAttribute or not, we sort it differently
			//if it has one we do the following....
			if (o1.material.has(ZIndexAttribute.ID)){
				
				//get the index and index group name of it.
				int      o1Index   =  ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID)).zIndex;
				String o1GroupName =  ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID)).group;
							
				//setup some variables
				boolean placementFound = false; //Determines if a placement was found for this renderable (this happens if it comes across another with the same groupname)
				int defaultPosition = -1; //if it doesn't find another renderable with the same groupname, this will be its position in the list. Consider this the "natural" position based on distance from camera
				
				//start looping over all objects so far in the results (urg, told you this was probably not a good method)
				for (int i = 0; i < resultList.size; i++) {
					
					//first get the renderable and its ZIndexAttribute (null if none found)
					Renderable o2 = resultList.get(i);
					ZIndexAttribute o2szindex = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID));
					
					if (o2szindex!=null){
						//if the renderable we are comparing too has a zindex, then we get its information
						int    o2index    = o2szindex.zIndex;
						String o2groupname = o2szindex.group;		

						//if its in the same group as o1, then we start the processing of placing them nexto eachother
						if (o2groupname.equals(o1GroupName)){
							
							//we either place it in front or behind based on zindex
							if (o1Index<o2index){
								//if lower z-index then behind it
								resultList.insert(i, o1);
								placementFound = true;
								break;
							}
							
							if (o1Index>o2index){
								//if higher z-index then it should go in front UNLESS there is another of this group already there too
								//in which case we just continue (which will cause this to fire again on the next renderable in the inner loop)
								if (resultList.size>(i+1)){
									
									Renderable o3 = resultList.get(i+1);
									ZIndexAttribute o3szindex = ((ZIndexAttribute)o3.material.get(ZIndexAttribute.ID));
									
									if (o3szindex!=null){
										String o3groupname = o3szindex.group;	
										if (o3groupname!=null && o3groupname.equals(o1GroupName)){
											//the next element is also a renderable with the same groupname, so we loop and test that one instead	
											continue;
										}
									}
									
								}
							//	Gdx.app.log("zindex", "__..placeing at:"+(i+1));
								//else we place after the current one
								resultList.insert(i+1, o1);
								placementFound = true;
								break;
							}
													
						}
						
					}
					
										
					//if no matching groupname found we need to work out a default placement.
					int placement = normalcompare(o1, o2); //normal compare is the compare function in DefaultRenderableSorter. 
					
					if (placement>0){
						//after then we skip
						//(we are waiting till we are either under something or at the end
						
					} else {
						//if placement is before, then we remember this position as the default (but keep looking as there still might be matching groupname, which should take priority)											
						defaultPosition = i;
						//break; //break out the loop
					}
					
					
				}
				
				//if we have checked all the renderables positioned in the results list, and none were found with matching groupname
				//then we use the defaultposition to insert it
				if (!placementFound){
					//Gdx.app.log("zindex", "__no placement found using default which is:"+defaultPosition);
					if (defaultPosition>-1){
						resultList.insert(defaultPosition, o1);
					} else {
						resultList.add(o1);
					}
					
				}
				
				continue;
								
			}
			
			//...(breath out)...
			//ok NOW we do placement for things that have no got a ZIndexSpecified
			boolean placementFound = false;
			
			//again, loop over all the elements in results
			for (int i = 0; i < resultList.size; i++) {
				
				Renderable o2 = resultList.get(i);
												
				//if not we compare by default to place before/after
				int placement = normalcompare(o1, o2);
				
				if (placement>0){
					//after then we skip
					//(we are waiting till we are either under something or at the end)
					continue;
				} else {
					//before					
					resultList.insert(i, o1);
					placementFound = true;
					break; //break out the loop
				}
				
				
			}
			//if no placement found we go at the end by default
			if (!placementFound){
				resultList.add(o1);
				
			};
			
			
		} //go back to check the next element in the incomeing list of renderables (that is, the copy we made at the start)

		//done
		
		
	}
	
	
	//Copy of the default sorters compare function
	//;
	private Camera camera;
	private final Vector3 tmpV1 = new Vector3();
	private final Vector3 tmpV2 = new Vector3();

	public int normalcompare (final Renderable o1, final Renderable o2) {
		final boolean b1 = o1.material.has(BlendingAttribute.Type) && ((BlendingAttribute)o1.material.get(BlendingAttribute.Type)).blended;
		final boolean b2 = o2.material.has(BlendingAttribute.Type) && ((BlendingAttribute)o2.material.get(BlendingAttribute.Type)).blended;
		if (b1 != b2) return b1 ? 1 : -1;
		// FIXME implement better sorting algorithm
		// final boolean same = o1.shader == o2.shader && o1.mesh == o2.mesh && (o1.lights == null) == (o2.lights == null) &&
		// o1.material.equals(o2.material);
		o1.worldTransform.getTranslation(tmpV1);
		o2.worldTransform.getTranslation(tmpV2);
		final float dst = (int)(1000f * camera.position.dst2(tmpV1)) - (int)(1000f * camera.position.dst2(tmpV2));
		final int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
		return b1 ? -result : result;
	}

	public void snapShotNextSort()
	{
		snapshotNextSort = true;
		
	}
	
	
	private void logSnapshots() {

		Gdx.app.log("zindex", "____(all "+preSortSnapshot.size+" objects)___");
		Gdx.app.log("zindex", "____(presort)___");
		debugtest(preSortSnapshot);
		Gdx.app.log("zindex", "____(postsort)___");
		debugtest(postSortSnapshot);
		
		
	}

	public void testSort(){
		
		
		Widget testWidget1  = new Widget(11f,11f);
		ConceptObject testWidget2 = new ConceptObject(StaticSSSNodes.ability);   //new Widget(11f,11f);
		Widget testWidget3  = new Widget(11f,11f);
		Widget testWidget4  =  new Widget(11f,11f);
		Widget testWidget5  = new Widget(11f,11f);
		Widget testWidget6  = new Widget(11f,11f);
		
		
		testWidget1.getMaterial().set(new ZIndexAttribute(20,"testgroup"));
		//testWidget2.getMaterial().set(new ZIndexAttribute(1,"testgroup"));
		testWidget3.getMaterial().set(new ZIndexAttribute(1,"testgroup"));
		//testWidget4
		testWidget5.getMaterial().set(new ZIndexAttribute(25,"testgroup"));
		
		Renderable one   = new Renderable();
		testWidget1.getRenderable(one);
		one.userData = "object one";
		
		Renderable two   = new Renderable();
		testWidget2.getRenderable(two);
		two.userData = "object two (c)";
		
		Renderable three = new Renderable();
		testWidget3.getRenderable(three);
		three.userData = "object three (cl)";
		
		Renderable four = new Renderable();
		testWidget4.getRenderable(four);
		four.userData = "object four";
		
		Renderable five = new Renderable();
		testWidget5.getRenderable(five);
		five.userData = "object five";
		
		Array<Renderable> renderables = new Array<Renderable>();
		renderables.add(four);
		renderables.add(one);
		renderables.add(two);
		renderables.add(three);
		renderables.add(five);
		
		debugtest(renderables);
		
		
		Gdx.app.log("zindex", "_____________________________________________(sorting)___");
		//this.sort(MainExplorationView.camera,renderables);
		camera=MainExplorationView.camera;
		customSorter(MainExplorationView.camera, renderables);
		
		
		Gdx.app.log("zindex", "______________________________________________(sort done)___");

		debugtest(renderables);
		
	}
	
	//In order to allow arbitrary render order overrides,
	//this function needs to check if o1 or o2 has a special material parameter?
	//That parameter will give a z-index value which compares to the other renderable's z-index value
	//If one of them doesn't have a value, its assumed to be zero
	//Then we use those values to compare them.
	
	private void debugtest(Array<Renderable> renderables) {
	//	Gdx.app.log("z-index", "____debugging object order___");
		
		for (Renderable renderable : renderables) {
			
			String name = (String) renderable.userData;
			
			if (renderable.material.has(ZIndexAttribute.ID)){
				
				int zindex1  = ((ZIndexAttribute)renderable.material.get(ZIndexAttribute.ID)).zIndex;
				String group = ((ZIndexAttribute)renderable.material.get(ZIndexAttribute.ID)).group;
				Gdx.app.log("zindex","name="+name+ " (zindex = "+zindex1+" , "+group+")");
				
			} else {
				//figure out if z-index is in there somewhere
				
				Gdx.app.log("zindex", "name="+name +" shader("+renderable.material.id+")");
				
			}
			
			
		}
		
		
	}

	/*
	//If neither has a z-index, the normal compare function is used.
	@Override
	public int compare(Renderable o1, Renderable o2) {
		
		if (o1.material.has(ZIndexAttribute.ID)){
			
			int obj_zindex1   = ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID)).zIndex;
			String groupname1 = ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID)).group;
			
			if (o2.material.has(ZIndexAttribute.ID)){
				
				int obj_zindex2   = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID)).zIndex;
				String groupname2 = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID)).group;
				
			//	Gdx.app.log("zindex", "obj_zindex1="+obj_zindex1+",obj_zindex2="+obj_zindex2);
				
				//int resultorder = (obj_zindex1 - obj_zindex2);

				//Gdx.app.log("zindex", "resultorder="+obj_zindex1+","+obj_zindex2);
				boolean oneIsGlobal=false;
				if (   groupname1.equalsIgnoreCase("global") ||    groupname2.equalsIgnoreCase("global") 		)
				{
					 oneIsGlobal=true;
				}
				
					
				if ( obj_zindex1==obj_zindex2  ){
					
					return super.compare(o1, o2);
					
				} else if (obj_zindex1<obj_zindex2 && (groupname1.equalsIgnoreCase(groupname2)  || oneIsGlobal)         ) {
				//	Gdx.app.log("zindex", "resultorder1="+obj_zindex1+","+obj_zindex2);
					return -1;
				} else if (obj_zindex1>obj_zindex2 && (groupname1.equalsIgnoreCase(groupname2)  || oneIsGlobal)         ) {
				//	Gdx.app.log("zindex", "resultorder2="+obj_zindex1+","+obj_zindex2);
					return 1;
				}
					
				
				//Gdx.app.log("zindex", "resultorder="+resultorder);
				
				//if they both have a z-index we just compare them directly
				//return resultorder;
				
			} else {
				//if 1 has and 2 doesn't we assume 2 is zero
				return (obj_zindex1 - 0); //super.compare(o1, o2);//
			}
			
		} else {			
			//if o1 does not have index, but o2 does; 
			if (o2.material.has(ZIndexAttribute.ID)){
				int obj_zindex2 = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID)).zIndex;				
			
				return (0 - obj_zindex2); //super.compare(o1, o2);//
			}
		}
		
	
		
		//if nether has z index we use the normal compare
		return super.compare(o1, o2);
	}
	
	
	*/
	
	
	
	
	

}
