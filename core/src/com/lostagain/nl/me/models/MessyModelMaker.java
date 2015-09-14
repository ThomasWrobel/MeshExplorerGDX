package com.lostagain.nl.me.models;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.lostagain.nl.me.locationFeatures.LocationsHub;
import com.lostagain.nl.shaders.MyShaderProvider;
import com.lostagain.nl.shaders.NoiseShader;

//functions in here should slowly be made tidy and moved to "modelmaker"
public class MessyModelMaker {


	final static String logstag = "ME.MessyModelMaker";

	
	
	
	
	//3d bits
	//public Model model;
	

	//public static Array<ModelInstance> instances = new Array<ModelInstance>();
	

	static Array<ModelInstance> lines = new Array<ModelInstance>();

	 //Experiments
	
	//static Pixmap pixmap;
	//static ByteBuffer temp;
	
	
	private static ArrayList<ModelInstance> animatedbacks = new ArrayList<ModelInstance>();
	

	
	public static ModelInstance addNoiseRectangle(int x, int y, int w, int h) {
		return addNoiseRectangle(x,  y,  w,  h, false);
	}
	/**
	 *  creates a new rectangle with a noise texture at the specified location
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param centerAlignOrigin - is the origin at the center(true) or corner (default /false)
	 * @return
	 */
	public static ModelInstance addNoiseRectangle(int x, int y, int w, int h,boolean centerAlignOrigin) {
		

		Material mat = getNoiseMaterial(false);
		ModelInstance newmodel;
		if (centerAlignOrigin){
			
			newmodel  = new ModelInstance(createRectangle(x-(w/2), y-(w/2), x+(w/2),y+(h/2), -110, Color.BLACK,mat));
		
		} else {
			newmodel  = new ModelInstance(createRectangle(x, y, x+w,y+h, -110, Color.BLACK,mat));
			
		}
		
		ModelManagment_old.addmodel(newmodel,ModelManagment_old.RenderOrder.zdecides);	
		return giveAnimatedNoiseTextureToRectangle(newmodel);
	}
	
	public static ModelInstance addRectangle(int x, int y,int z, int w, int h, Material mat) {
		
		
		ModelInstance newmodel  = new ModelInstance(createRectangleAt(x, y,z, w,h,  Color.BLACK, mat));
		ModelManagment_old.addmodel(newmodel,ModelManagment_old.RenderOrder.zdecides);		
		
		return newmodel;
	}
	
	/** applies an animated noise texture to the specified modelinstance 
	 * adds it to the animated backs list for autoupdating each frame**/
	public static ModelInstance giveAnimatedNoiseTextureToRectangle(ModelInstance rect){

		
		//Material mat = createNoiseMaterial();
		//rect.materials.set(0, mat);
		
		
		animatedbacks.add(rect);
				
		return rect;
	}
	
	public static ModelInstance removeAnimatedNoiseTextureToRectangle(ModelInstance rect){

		
		//rect.materials.set(0, mat);
				
		animatedbacks.remove(rect);
				
		return rect;
	}
	
	
	
	public static void removeModelInstance(ModelInstance model){
		
		
		ModelManagment_old.removeModel(model);
		
		animatedbacks.remove(model);
		
	}


	public static ModelInstance addConnectingLine(LocationsHub From,LocationsHub To){

		Gdx.app.log(logstag,"___________AddConnectingLine:"+From.getWidth());
		Gdx.app.log(logstag,"___________AddConnectingLine:"+From.isVisible());
		Gdx.app.log(logstag,"___________AddConnectingLine:"+From.getX(Align.center)+","+From.getY(Align.center)+" to "+To.getX(Align.center)+","+To.getY(Align.center));
		Gdx.app.log(logstag,"___________AddConnectingLine From:"+From.LocationsNode.getPLabel());
		Gdx.app.log(logstag,"___________AddConnectingLine To:"+To.LocationsNode.getPLabel());
		
		float x = From.getX(Align.center);
		float y = From.getY(Align.center);
		float width = 30;
		//float height = 500;

		//get angle
		float x2 = To.getX(Align.center);
		float y2 = To.getY(Align.center);
		
		//get z pos (all locations should be on the same plane)
		float zloc = -200;

		
		ModelInstance newline = createLine(x, y, width, x2, y2,zloc,Color.RED,true,true,1);


		lines.add(newline);
		//instances.add(newline);
		ModelManagment_old.addmodel(newline,ModelManagment_old.RenderOrder.zdecides);
		
		//lines.add(newlinetop);
		//instances.add(newlinetop);
		


		return newline;
	}

	/**
	 * 
	 * @param tooX
	 * @param tooY
	 * @param width
	 * @param fromX
	 * @param fromY
	 * @param atZ - z height (for now line is flat 2d at a fixed z height)
	 * @param col - colour
	 * @param withStartBlob
	 * @param withEndBlob - a white blob at the end
	 * @param lengthMultiplayer - useful if you want a line to "overshot its end point. (say, to simulate infinity)"
	 * @return
	 */
	static public ModelInstance createLine(float tooX, float tooY, float width,
			float fromX, float fromY,float atZ, Color col, boolean withStartBlob, boolean withEndBlob, int lengthMultiplayer) {
		
		Vector2 fromPoint = new Vector2(fromX,fromY);
		Vector2 tooPoint  = new Vector2(tooX,tooY);

		fromPoint.sub(tooPoint);      

		//Log.info("angle="+corner2.angle());
		//Log.info("length="+corner2.len());

		float ang = fromPoint.angle()+90;

		//Gdx.app.log(logstag, " creating from point:"+fromPoint.x+","+fromPoint.y+" ---- "+tooPoint.x+","+tooPoint.y+"  ang="+ang);
		//ang=ang+45;
		
		ModelInstance newline = createGlowingRectangleAt(fromX,fromY,width,fromPoint.len()*lengthMultiplayer,atZ,col,withStartBlob,withEndBlob);
		
		//newline.materials.get(0).set(new BlendingAttribute(0.25f));

		Matrix4 newmatrix = new Matrix4();
		newmatrix.setToRotation(0, 0, 1, ang);

		newline.transform.mul(newmatrix);
		//newlinetop.transform.mul(newmatrix);

		Gdx.app.log(logstag,"x="+fromX+"y="+fromY);

		return newline;
	}



	private static ModelInstance createGlowingRectangleAt(float x,float y,float width,float height,float z, Color MColor, boolean withStartBlob, boolean withEndBlob) {

		//Color MColor = Color.RED;
		Model newractangle =  createGlowingRectangle(0,height,width,0,0,MColor,withStartBlob,withEndBlob);

		ModelInstance newinstance = new ModelInstance(newractangle); 

		newinstance.transform.setToTranslation(x,y,z);
		

		return newinstance;
	}

/**
 * //x1 =0
		//y1 =height
		//x2 =width
		//y2 =0
 * @param x1
 * @param y1
 * @param x2
 * @param y2
 * @param z
 * @param MColor
 * @param withStartBlob
 * @param withEndBlob
 * @return
 */
	public static Model createGlowingRectangle(float x1,float y1,float x2,float y2,float z,Color MColor, boolean withStartBlob, boolean withEndBlob) {
		//x1 =0
		//y1 =height
		//x2 =width
		//y2 =0
		
		float width = Math.abs(x1-x2);
		//
		float vertdisp = width/2;
		
		
		//(-10f,500f,10f,-500f,0f);
		//(x,,,y                      )
		Vector3 corner1 = new Vector3(x1-vertdisp,y1,z);
		Vector3 corner2 = new Vector3(x1-vertdisp,y2,z);
		Vector3 corner3 = new Vector3(x2-vertdisp,y2,z);
		Vector3 corner4 = new Vector3(x2-vertdisp,y1,z);	


		Model  model3 =  glowingRectangle(corner1, corner2, corner3, corner4,MColor,withStartBlob,withEndBlob);


		return model3;
	}

	
	static Model glowingRectangle(Vector3 corner1,
			Vector3 corner2, Vector3 corner3, Vector3 corner4, Color MColor, boolean withStartBlob, boolean withEndBlob ) {

		//MColor = Color.WHITE;
		
		
		Material lowmaterial = new Material(ColorAttribute.createDiffuse(MColor), 
				ColorAttribute.createSpecular(Color.WHITE),new BlendingAttribute(0.3f), 
				FloatAttribute.createShininess(16f));	

		
        FileHandle imageFileHandle = Gdx.files.internal("data/beam_low.png"); 
        Texture lowtexture = new Texture(imageFileHandle);
        		
        lowmaterial.set(TextureAttribute.createDiffuse(lowtexture));
		
		Material uppermaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE), 
				ColorAttribute.createSpecular(Color.WHITE),new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f));

		
		BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE);


		 
        FileHandle imageFileHandle2 = Gdx.files.internal("data/beam_top.png"); 
        
        //Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
        Texture texture = new Texture(imageFileHandle2);
      //  texture.bind();
        
        lowmaterial.set(blendingAttribute);	
        uppermaterial.set(blendingAttribute);		
        uppermaterial.set(TextureAttribute.createDiffuse(texture));
        //-------------------------
        

        FileHandle imageFileHandle3 = Gdx.files.internal("data/beam_blob.png"); 
        
       // Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE1);
        Texture blobtexture = new Texture(imageFileHandle3);
      //  blobtexture.bind();
        
        Material blob = new Material(ColorAttribute.createDiffuse(Color.WHITE), 
				ColorAttribute.createSpecular(Color.WHITE),new BlendingAttribute(0.7f), 
				FloatAttribute.createShininess(16f));

        blob.set(TextureAttribute.createDiffuse(blobtexture));
        blob.set(blendingAttribute);		
      
        
		/*
		modelBuilder.end();
		modelBuilder.begin();
		modelBuilder.node().id = "bottom";
		modelBuilder.createRect(
				corner1.x,
				corner1.y, 
				corner1.z,

				corner2.x,
				corner2.y, 
				corner2.z,

				corner3.x,
				corner3.y, 
				corner3.z,

				corner4.x,
				corner4.y, 
				corner4.z,   		   			

				0,
				1,
				0,
				rectmaterial, 
				Usage.Position | Usage.Normal);
		
		Model model = modelBuilder.end();
		*/
		
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder meshBuilder;
		
		meshBuilder = modelBuilder.part("bottom", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, lowmaterial);
		//meshBuilder.getAttributes().findByUsage(Usage.TextureCoordinates).alias = "a_texCoord";
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
	 
		Node node = modelBuilder.node();
		node.translation.set(0,0,5);		
		
		meshBuilder = modelBuilder.part("part2", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,uppermaterial );
		//meshBuilder.getAttributes().findByUsage(Usage.TextureCoordinates).alias = "a_texCoord";
		meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
		
		float hwidth =0;
		float hheight =0;
		
		if (withStartBlob || withEndBlob){
			 hwidth = (corner3.x-corner3.y)+10;	//width of ends is 10 more then the rest	
			 hheight = (float) (hwidth *1.3); //height slightly longer
		}
		
		//now the glow start blob
		if (withStartBlob){

			Node node3 = modelBuilder.node();
			node3.translation.set(0,0,6);
			
		meshBuilder = modelBuilder.part("startblob", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,blob );
		//meshBuilder.getAttributes().findByUsage(Usage.TextureCoordinates).alias = "a_texCoord";
	//	float hwidth = (corner3.x-corner3.y)+10;	//width of ends is 10 more then the rest	
		//float hheight = (float) (hwidth *1.3); //height slightly longer
		
		Gdx.app.log(logstag,"hwidth="+hwidth);
		//Gdx.app.log(logstag,"width=="+(corner4.y-corner4.x));
		
		//hwidth used to be 30
		meshBuilder.rect(
				-hwidth,
				-hwidth, 
				0,

				hwidth,
				-hwidth, 
				0,

				hwidth,
				hheight, 
				0,

				-hwidth,
				hheight, 
				0,   		   			

				0,
				1,	
				0);
		}
		
		if (withEndBlob){
		Node node4 = modelBuilder.node();
		node4.translation.set(0,corner1.y,7);
		
		
		
		meshBuilder = modelBuilder.part("endblob", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,blob );
		//meshBuilder.getAttributes().findByUsage(Usage.TextureCoordinates).alias = "a_texCoord";
		
		//now the glow end blob (bit smaller)
		meshBuilder.rect(
				-hwidth,
				-hheight, 
				0,

				hwidth,
				-hheight, 
				0,

				hwidth,
				hheight, 
				0,

				-hwidth,
				hheight, 
				0,   		   			

				0,
				1,
				0);
		
		
		
		//corner3.y
		/*
		VertexInfo blob1 = new VertexInfo();
		blob1.set(corner1, testnorm, Color.WHITE, new Vector2(0f,0f));

		VertexInfo blob2 = new VertexInfo();
		blob2.set(corner2, testnorm, Color.WHITE, new Vector2(0f,1f));

		VertexInfo blob3 = new VertexInfo();
		blob3.set(corner3, testnorm, Color.WHITE, new Vector2(1f,1f));
		
		VertexInfo blob4 = new VertexInfo();
		blob4.set(corner4, testnorm, Color.WHITE, new Vector2(1f,0f));
		
		meshBuilder.rect(blob1, blob2, blob3, blob4);
		//meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
		
		meshBuilder.rect(blob1, blob2, blob3, blob4);

		
		meshBuilder.rect(
				-50,
				-50, 
				corner1.z,

				-50,
				50, 
				corner2.z,

				50,
				50, 
				corner3.z,

				50,
				-50, 
				corner4.z,   		   			

				0,
				1,
				0);
		*/
		//meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
		}
		
		Model model = modelBuilder.end();
		
		
		return model;
	}



	
	private static void sortTransparentObjects(final Camera camera){
		/*
		
		instances.sort(new Comparator<ModelInstance>() {
			
			private final Vector3 tmpV1 = new Vector3();
			private final Vector3 tmpV2 = new Vector3();
			
			@Override
			public int compare(ModelInstance o1, ModelInstance o2) {
				
				
				//Rather then single bits look for any;
												
			
				//OMLY TESTS FIRST MATERIAL NODE OF EACH OBJECT (should test all)
				final boolean b1 = o1.materials.get(0).has(BlendingAttribute.Type) && ((BlendingAttribute)o1.materials.get(0).get(BlendingAttribute.Type)).blended;
				final boolean b2 = o2.materials.get(0).has(BlendingAttribute.Type) && ((BlendingAttribute)o2.materials.get(0).get(BlendingAttribute.Type)).blended;
				
				if (b1 != b2) return b1 ? 1 : -1;
				
				
				// FIXME implement better sorting algorithm
				// final boolean same = o1.shader == o2.shader && o1.mesh == o2.mesh && (o1.lights == null) == (o2.lights == null) &&
				// o1.material.equals(o2.material);
				
				o1.transform.getTranslation(tmpV1);				
				o2.transform.getTranslation(tmpV2);
				
				//o1.worldTransform.getTranslation(tmpV1);
				//o2.worldTransform.getTranslation(tmpV2);
				
				final float dst = (int)(1000f * camera.position.dst2(tmpV1)) - (int)(1000f * camera.position.dst2(tmpV2));
				final int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
				
				return b1 ? -result : result;
				
			}
		});
		*/
		
	}
	
	/*
	private static Model createNoiseRectangle(float x1,float y1,float x2,float y2,float z,Color MColor) {
		

       // BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.1f);

		
     
     
        
        return createRectangle( x1, y1, x2, y2, z, MColor, mat );
        
	}*/
	public static Material getNoiseMaterial() {
		return getNoiseMaterial(false);
	}

	public static Material getNoiseMaterial(boolean alphaFromLocation) {
		
		  Material mat =
				  new Material(
				  new NoiseShader.NoiseShaderAttribute(false,Color.ORANGE,alphaFromLocation),
				  new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.99f)
				  );
		
		/*
		BlendingAttribute blendingAttribute2 = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.2f);
		
    Texture texture = new Texture(createNoiseImage(300,300));
    //ColorAttribute.createDiffuse(MColor), new BlendingAttribute(0.5f), 
        Material mat = new Material(
				ColorAttribute.createSpecular(Color.WHITE),
				FloatAttribute.createShininess(16f));
        
        mat.set(TextureAttribute.createDiffuse(texture));
        mat.set(blendingAttribute2);*/
		
		return mat;
	}
	
	

	static public ModelInstance createRectangleAt(int x, int y,int z, int w, int h,Color MColor,Material mat) {

		ModelInstance newmodel  = new ModelInstance(createRectangle(0, 0, w,h, 0, Color.BLACK,mat ));


		newmodel.transform.setToTranslation(x,y,z);
		
		return newmodel;
	}
	
	static public Model createRectangle(float x1,float y1,float x2,float y2,float z,Color MColor,Material mat ) {
		//x1 =0
		//y1 =height
		//x2 =width
		//y2 =0
		
	//	float width = Math.abs(x1-x2);
		//
	//	float vertdisp = width/2;
		
		
		//(-10f,500f,10f,-500f,0f);
		//(x,,,y                      )
		
		Vector3 corner1 = new Vector3(x1,y1,z);
		Vector3 corner2 = new Vector3(x2,y1,z);
		Vector3 corner3 = new Vector3(x2,y2,z);
		Vector3 corner4 = new Vector3(x1,y2,z);	
		
		/*
		Vector3 corner1 = new Vector3(0,0,0);
		Vector3 corner2 = new Vector3(444,0,0);
		Vector3 corner3 = new Vector3(444,444,0);
		Vector3 corner4 = new Vector3(0,444,0);	
		 */

     // FileHandle imageFileHandle3 = Gdx.files.internal("data/beam_blob.png"); 
        //Texture blobtexture = new Texture(imageFileHandle3);
    
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder meshBuilder;

		//Node node = modelBuilder.node();
		//node.translation.set(11,11,5);		
		
		meshBuilder = modelBuilder.part("bit", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal|Usage.TextureCoordinates, mat);

		
		//meshBuilder.cone(5, 5, 5, 10);
		
		VertexInfo newtest1 = new VertexInfo();
		Vector3 testnorm=new Vector3(0,1,0);
		newtest1.set(corner1, testnorm, MColor, new Vector2(0f,0f));

		VertexInfo newtest2 = new VertexInfo();
		newtest2.set(corner2, testnorm, MColor, new Vector2(0f,1f));

		VertexInfo newtest3 = new VertexInfo();
		newtest3.set(corner3, testnorm, MColor, new Vector2(1f,1f));
		
		VertexInfo newtest4 = new VertexInfo();
		newtest4.set(corner4, testnorm, MColor, new Vector2(1f,0f));
		
		meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
	 
		

		Model model = modelBuilder.end();
		
		

		return model;
	}

	
	
	//Doesnt work in gwt
	public static Pixmap createNoiseImage(int w, int h){
		
		Pixmap pixmap;
		pixmap = new Pixmap(w, h, Format.RGBA8888);
		//pixmap.setColor(22f, 22f, 22f, 0f);
		
		//pixmap.fill();
				
			for (int x = 0;x < pixmap.getWidth() ; x++) {
				
				for (int y = 0; y < pixmap.getHeight(); y++) {
					
					
					int color = (int) (Math.random()*16777215);
					pixmap.drawPixel(x, y, color);
					
					
				}				
				
			}
			
		/*
		 temp = pixmap.getPixels();
				
	for (int i = 0; i < temp.limit() ; i++) {
		//byte val = temp.get(i);
		
		byte val = (byte) (Math.random()*200);
		temp.put(val);
		
	}
	temp.flip();*/
	//40000
	
		return pixmap;
		
	}

	public static void addToBackground(ModelInstance modelinstance) {

		
		ModelManagment_old.addmodel(modelinstance,ModelManagment_old.RenderOrder.zdecides);
		
	}
	
	
}
