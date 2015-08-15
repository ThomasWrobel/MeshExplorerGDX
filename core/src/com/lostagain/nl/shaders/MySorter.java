package com.lostagain.nl.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.utils.Array;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.me.features.ConceptObject;
import com.lostagain.nl.shaders.InvertShader.InvertAttribute;
import com.lostagain.nl.shaders.PrettyNoiseShader.PrettyNoiseShaderAttribute;

public class MySorter extends DefaultRenderableSorter {
	
	/**
	 * Lets us override the normal draw order. Things with positive zindex go infront of natural ordering
	 * negative goes behind
	 * @author Tom
	 */
	public static class ZIndexAttribute extends Attribute {
		
		public final static String Alias = "ZIndexAttribute";
		public final static long ID = register(Alias);		
		int zIndex = 0;
		/**
		 * The presence of this parameter will override the normakl draw order
		 */
		public ZIndexAttribute (int zindex) {		
			super(ID);				
			this.zIndex=zindex;
		}

		@Override
		public Attribute copy () {
			return new ZIndexAttribute(zIndex);
		}

		@Override
		protected boolean equals (Attribute other) {				
			if (((ZIndexAttribute)other).zIndex == zIndex)
			{
				return true;
			}
				return false;
		}
		
		@Override
		public int compareTo(Attribute o) {

			Gdx.app.log("zindex", "co_z..");
			
			if (o.type == ID){
				
				
				int co_z = ((ZIndexAttribute)o).zIndex;
				
				Gdx.app.log("zindex", "co_z="+co_z+","+zIndex);
				
				return co_z-zIndex;						
			}
			
		    return 0;		        
		}
	}	
	
	
	@Override
	public void sort(Camera camera, Array<Renderable> renderables) {
		super.sort(camera, renderables);
	}

	public void testSort(){
		
		
		Widget testWidget1  = new Widget(11f,11f);
		ConceptObject testWidget2 = new ConceptObject(StaticSSSNodes.ability);   //new Widget(11f,11f);
		Widget testWidget3  = testWidget2.MeshIconsLabel;  //new Widget(11f,11f);
		Widget testWidget4 = new Widget(11f,11f);
		
		
		
		testWidget1.getMaterial().set(new ZIndexAttribute(20));
		//testWidget2.getMaterial().set(new ZIndexAttribute(1));
		//testWidget3.getMaterial().set(new ZIndexAttribute(-1));
		//testWidget4
		
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
		
		Array<Renderable> renderables = new Array<Renderable>();
		renderables.add(four);
		renderables.add(one);
		renderables.add(two);
		renderables.add(three);
	
		
		debugtest(renderables);
		
		
		Gdx.app.log("zindex", "____(sorting)___");
		this.sort(MainExplorationView.camera,renderables);
		Gdx.app.log("zindex", "____(sort done)___");

		debugtest(renderables);
		
	}
	
	//In order to allow arbitrary render order overrides,
	//this function needs to check if o1 or o2 has a special material parameter?
	//That parameter will give a z-index value which compares to the other renderable's z-index value
	//If one of them doesn't have a value, its assumed to be zero
	//Then we use those values to compare them.
	
	private void debugtest(Array<Renderable> renderables) {
		Gdx.app.log("zindex", "____debugging object order___");
		
		for (Renderable renderable : renderables) {
			
			String name = (String) renderable.userData;
			Gdx.app.log("zindex", "name="+name);
			
			
		}
		
		
	}

	//If neither has a z-index, the normal compare function is used.
	@Override
	public int compare(Renderable o1, Renderable o2) {
		/*
		if (o1.material.has(ZIndexAttribute.ID)){
			int obj_zindex1 = ((ZIndexAttribute)o1.material.get(ZIndexAttribute.ID)).zIndex;
				
			if (o2.material.has(ZIndexAttribute.ID)){
				
				int obj_zindex2 = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID)).zIndex;
			//	Gdx.app.log("zindex", "obj_zindex1="+obj_zindex1+",obj_zindex2="+obj_zindex2);
				
				int resultorder = (obj_zindex1 - obj_zindex2);

				//Gdx.app.log("zindex", "resultorder="+obj_zindex1+","+obj_zindex2);
				
				if (obj_zindex1==obj_zindex2){
					return super.compare(o1, o2);
				} else if (obj_zindex1<obj_zindex2) {
					Gdx.app.log("zindex", "resultorder1="+obj_zindex1+","+obj_zindex2);
					return -1;
				} else if (obj_zindex1>obj_zindex2) {
					Gdx.app.log("zindex", "resultorder2="+obj_zindex1+","+obj_zindex2);
					return 1;
				}
					
				
				//Gdx.app.log("zindex", "resultorder="+resultorder);
				
				//if they both have a z-index we just compare them directly
				//return resultorder;
				
			} else {
				//if 1 has and 2 doesn't we assume 2 is zero
				return -1; //super.compare(o1, o2);//(obj_zindex1 - 0);
			}
			
		} else {			
			//if o1 does not have index, but o2 does; 
			if (o2.material.has(ZIndexAttribute.ID)){
				int obj_zindex2 = ((ZIndexAttribute)o2.material.get(ZIndexAttribute.ID)).zIndex;				
			
				return 1; //super.compare(o1, o2);//(0 - obj_zindex2);
			}
		}
		*/
	
		
		//if nether has z index we use the normal compare
		return super.compare(o1, o2);
	}
	
	
	
	
	
	
	

}
