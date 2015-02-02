package com.lostagain.nl.me.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ModelMaker {
	
	static public ModelInstance createRectangleAt(int x, int y,int z, int w, int h,Color MColor,Material mat) {

		ModelInstance newmodel  = new ModelInstance(createRectangle(0, 0, w,h, 0,mat ));

		newmodel.transform.setToTranslation(x,y,z);
		
		return newmodel;
	}
	
	static public Model createRectangle(float x1,float y1,float x2,float y2,float z,Material mat ) {

		
		Vector3 corner1 = new Vector3(x1,y1,z);
		Vector3 corner2 = new Vector3(x2,y1,z);
		Vector3 corner3 = new Vector3(x2,y2,z);
		Vector3 corner4 = new Vector3(x1,y2,z);	
	
    
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder meshBuilder;

		//Node node = modelBuilder.node();
		//node.translation.set(11,11,5);		
		
		meshBuilder = modelBuilder.part("bit", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal|Usage.TextureCoordinates, mat);

		
		//meshBuilder.cone(5, 5, 5, 10);
		
		VertexInfo newtest1 = new VertexInfo();
		Vector3 testnorm=new Vector3(0,1,0);
		newtest1.set(corner1, testnorm, Color.WHITE, new Vector2(0f,0f));

		VertexInfo newtest2 = new VertexInfo();
		newtest2.set(corner2, testnorm, Color.WHITE, new Vector2(0f,1f));

		VertexInfo newtest3 = new VertexInfo();
		newtest3.set(corner3, testnorm, Color.WHITE, new Vector2(1f,1f));
		
		VertexInfo newtest4 = new VertexInfo();
		newtest4.set(corner4, testnorm, Color.WHITE, new Vector2(1f,0f));
		
		meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
	 
		

		Model model = modelBuilder.end();
		
		

		return model;
	}
}
