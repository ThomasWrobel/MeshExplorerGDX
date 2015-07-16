
#ifdef GL_ES 
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif






// this calculated in vertex shader
// width/height = triangle/quad width/height in px;
//vec2 pixel_step = vec2(1/width, 1/height);  
varying vec2 pixel_step;
    

//
//float fwidth = abs(dx) + abs(dy);     
//----------------------------

//incoming texture
//uniform sampler2D u_diffuseTexture;

 //"in" varyings from our vertex shader
varying vec2 v_texCoord0;
varying vec4 v_color;
varying vec2 vTexCoord;

varying vec4 v_textColor;
varying vec4 v_backColor;



//glow
varying vec4  v_glowColor;
varying float v_glowSize; //size of glow (values above 1 will look strange)
		
//outline
varying vec4  v_outColor;
varying float v_outlinerInnerLimit; //Arbitrarily big size for no outline
varying float v_outlinerOuterLimit; //Arbitrarily big size for no outline

//shadow
varying float v_shadowXDisplacement;
varying float v_shadowYDisplacement;
varying float v_shadowBlur;
varying vec4  v_shadowColour;
//--

varying float v_colorFlag;
 

 
 
  

varying MED vec2 v_diffuseUV;
 
//uniform sampler2D u_diffuseTexture;

uniform sampler2D u_texture;

//WORKAROUND if fwidth/above extension is not supported
//http://stackoverflow.com/questions/22442304/glsl-es-dfdx-dfdy-analog
float myFunc(vec2 p){
return p.x*p.x - p.y; // that's our function. We want derivative from it.
}

//These things are to get around the fwidth function being missing on some GPUs
float current = myFunc(vTexCoord);
float dfdx    = myFunc(vTexCoord + pixel_step.x) - current; //myfunction is given the next co-ordinate in x? (current vTexCoord + the change in the co-ordinate)
float dfdy    = myFunc(vTexCoord + pixel_step.y) - current; //same for y
  

 
const float smoothing =  0.25/(4.0*32.0); //0.25/(filesmooth*fontfilescale)                     //0.001953125//1.0/16.0;  

float contour(in float d, in float w) {
    // smoothstep(lower edge0, upper edge1, x)
    return smoothstep(0.5 - w, 0.5 + w, d);
}

float samp(in vec2 uv, float w) {
    return contour(texture2D(u_texture, vTexCoord).a, w);
}

void main() {

	float colorFlag = v_colorFlag;
	vec4 diffuse = vec4(1.0,0.0,0.0,1.0);
	 
	 
	if ((colorFlag==0.0))
	{
		//vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor * v_color;
		diffuse = texture2D(u_texture, vTexCoord);//vec4(0.0,1.0,0.0,1.0);
	}
	else 
	{
	//	v_diffuseColor = vec4(0.0,0.0,1.0,1.0); //temp for testing
		diffuse = v_textColor;// texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
		//diffuse = vec4(1.0,1.0,1.0,1.0);
	}


 	//the alpha of the incoming texture acts as the distance from inside a letter to outside
 	float dist = texture2D(u_texture, vTexCoord).a;
 	 	 	
 	// fwidth helps keep outlines a constant width irrespective of scaling
    // GLSL's fwidth = abs(dFdx(uv)) + abs(dFdy(uv))
    
  //  float width = fwidth(dist); //<---------------correct formula (works fine desktop)
       	 	
   //float width = abs(dFdx(dist)) + abs(dFdy(dist));  //<-----------(was attempt at replacement for web, does not work)          
     
   float width = abs(dfdx*dist) + abs(dfdx*dist);  //<-----------(was attempt at replacement for web, does not work?)          
     
       	 	
 	 	 	// supersampled version
//width
	width=width+0.01; //0.01 makes edges a bit softer
    float alpha = contour( dist,width); //width doesnt seem correct always? probably mistake with web replacement for fwidth
  
    //float alpha = aastep( 0.5, dist );

    // ------- (comment this block out to get your original behavior)
    //Supersample doesnt seem to work well right now? at least its still pixely from far away
    
    // Supersample, 4 extra points
    float dscale = 0.354; // half of 1/sqrt2; you can play with this
   
   // vec2 duv = dscale * (dFdx(vTexCoord) + dFdy(vTexCoord)); //<---------------correct formula (works fine desktop)
   
    vec2 duv = vec2(dscale * dfdx,dscale * dfdy); //<-------------web replacement for now
    
    vec4 box = vec4(vTexCoord-duv, vTexCoord+duv);

    float asum = samp( box.xy, width )
               + samp( box.zw, width )
               + samp( box.xw, width )
               + samp( box.zy, width );

    // weighted average, with 4 extra points having 0.5 weight each,
    // so 1 + 0.5*4 = 3 is the divisor
    alpha = (alpha + 0.5 * asum) / 3.0;

    // -------
    
	vec4 newCol = vec4(diffuse.rgb,diffuse.a*alpha);
	
	//outline (optional)
	if (v_outColor.a>0.0){
		if (dist>0.05){ //outermost limit (0 is max/outer edge)
			if (dist<0.2){ //inner limit
			
					newCol   = v_outColor;
    				//newCol.a = 1.0;
    		}
    	}
    }
    
	
    //glow (the glow replaces the normal texture, it doesnt glow over it)
    if (v_glowColor.a>0.0){
    	if (dist>0.0) {
    		if (dist<0.5){ 
    	     //inner limit
    	     float glowSize = v_glowSize;
    		 alpha= smoothstep(0.5-v_glowSize, 0.5+v_glowSize, dist);
    		
    		   float newalpha = newCol.a + (v_glowColor.a * alpha);
    		   
    		 //now blend glow with whats there already
    		 newCol = (v_glowColor * alpha) + (newCol * (1.0-alpha));
    		
    		 
    		// newCol   = v_glowColor;
    		 newCol.a = newalpha;
    		    		 
    		}
    
   		 }
    }
    
    //shadow (the shadow will go under the normal texture, hence we need to create both and blend)
    //first we only create a shadow if theres one set (detected by shadow alpha being >0)
    if (v_shadowColour.a>0.0){
    	
    	//now we know we have a shadow we need to do a second texture look up, this time using our offset
    	float xo = vTexCoord.x -(v_shadowXDisplacement * pixel_step.x);//v_shadowXDisplacement;
    	float yo = vTexCoord.y -(v_shadowYDisplacement * pixel_step.y);//;
    	
    	//the alpha of the incoming texture acts as the distance from inside a letter to outside
 		float sdist = texture2D(u_texture, vec2(xo,yo)).a;
 		
    			
    			vec4 shadowCol   = v_shadowColour;
    			
    			float blurSize = v_shadowBlur;
    		    float salpha = smoothstep(0.5-blurSize, 0.5+blurSize, sdist);
    			
    		    shadowCol.a =  shadowCol.a * salpha;
    		    float olda = newCol.a;
    		    
    		   float newalpha = newCol.a+shadowCol.a;
    		   
    		    //now blend with original (under it!)
    		    newCol = (newCol * newCol.a) + (shadowCol * (1.0-newCol.a));
    		  
    		    newCol.a = newalpha;
    			
    			//newCol.a = shadowCol.a + newCol.a ;
    	
    }
    
    
    
    
	//mix with back colour	
    
	
	//      = vec4(0.0,0.0,0.0,1.0);
	//newCol = clamp(newCol,vec4(0.0,0.0,0.0,0.0),vec4(1.0,1.0,1.0,1.0));
	
	//v_backColor = vec4(1.0,1.0,1.0,1.0);
	//v_backColor = clamp(v_backColor,vec4(0.0,0.0,0.0,0.0),vec4(1.0,1.0,1.0,1.0));
	
	
	//                addColor*addColor.a + sceneColor*(1-addColor.a);
	
	
	//NOTE: the alpha of newCol is not correct here
	//it seems to be 100% except where the shadow starts
	
	
	vec4 finalCol =  (newCol * newCol.a) + (v_backColor * (1.0-newCol.a));
	finalCol.a =  newCol.a + v_backColor.a;
	
	//finalCol.a = vec4(0.0,0.0,0.0,1.0);
	//finalCol = clamp(finalCol,vec4(0.0,0.0,0.0,0.0),vec4(1.0,1.0,1.0,1.0));
	
	
    gl_FragColor =  finalCol; //diffuse.rgb
 	 	 	
 	 	
    
}


