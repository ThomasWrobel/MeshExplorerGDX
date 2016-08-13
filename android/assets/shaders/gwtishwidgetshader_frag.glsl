
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

//
// The Goal of this shader is to allow a very very aproximate emulatation of a styled div with text in it.
// This shader should be applied to a rectangle only.
//
// Currently supported;
// - Displays text using a image containing a distance field rendering of a font (to allow it to be sharp at any distance)
// - Text can have its colour changed
// - Text can have a shadow applied
// - Text can have a glow applied
// - Text can have a outline applied
// - The background to the text can be set to any color
// - Background can have borders of varying size
// - Background can have curved corners to the border
//
// TODO (maybe)
// Background shadow support (to emulate CSS Box shadow)
//


// this calculated in vertex shader
// width/height = triangle/quad width/height in px;
//vec2 pixel_step = vec2(1/width, 1/height);  
varying vec2 pixel_step;
    
uniform vec2 u_sizeDiff;

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
//varying vec4 v_backColor;

//padding
varying float v_textPaddingX;
varying float v_textPaddingY;

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


//background style data
varying float v_backBorderWidth; 
varying vec4  v_backBackColor;
varying vec4  v_backCoreColor; //border color for some reason
varying vec4  v_backCornerRadius;

varying vec2 iResolution;
varying vec2 fPosition;

// Background method Based and modified from;
// Created by Marc Lepage - mlepage/2015
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

// Rect center
vec2 center = iResolution.xy / 2.0;

// Rect half size
vec2 hsize  = iResolution.xy / 2.0;

// Rect radius
float radius = v_backCornerRadius;



//WORKAROUND if fwidth/above extension is not supported
//http://stackoverflow.com/questions/22442304/glsl-es-dfdx-dfdy-analog
float myFunc(vec2 p){
	return p.x*p.x - p.y; // that's our function. We want derivative from it.
}

//These things are to get around the fwidth function being missing on some GPUs
float current = myFunc(vTexCoord);
float dfdx    = myFunc(vTexCoord + pixel_step.x) - current; //myfunction is given the next co-ordinate in x? (current vTexCoord + the change in the co-ordinate)
float dfdy    = myFunc(vTexCoord + pixel_step.y) - current; //same for y
  

//the following were attempts at smoothing the font 
const float smoothing =  0.25/(4.0*32.0); //0.25/(filesmooth*fontfilescale)                     //0.001953125//1.0/16.0;  

float contour(in float d, in float w) {
    // smoothstep(lower edge0, upper edge1, x)
    return smoothstep(0.5 - w, 0.5 + w, d);
}

float samp(in vec2 uv, float w) {
    return contour(texture2D(u_texture, vTexCoord).a, w);
}



//http://iquilezles.org/www/articles/distfunctions/distfunctions.htm
//Rounded rect distance function
float udRoundRect(vec2 p, vec2 b, float r)
{
	return length(max(abs(p) - b, 0.0)) - r;
}

//Alternative to the above we now use instead;
//Adapted from;
//https://www.shadertoy.com/view/4dfXDn
float boxDist2D(vec2 p, vec2 size, float radius)
{
	size -= vec2(radius);
	vec2 d = abs(p) - size;
  	return min(max(d.x, d.y), 0.0) + length(max(d, 0.0)) - radius;
}
//---------------------------------

//Simple function to move a point by a certain amount
vec2 translate(vec2 point, vec2 dis)
{
	return point - dis;
}

//Gets the background col at the specified point
//This will include the border, which can be coloured and curved at the corners if needed.
//
//In future we could emulate css style BoxShadow here too, unlike css we cant go outside
//the edges of the box, so the background would have to be made smaller to give the shadow room
//We could possibly do this automatically via vertex manipulation?
vec4 getBackColour()
{
	vec2  fragCoord    = fPosition*iResolution.xy; 
	vec2  uv           = fPosition;
		
    float iGlobalTime  = 0.5;
    
    //uv based color (put it in as a option?)
	//vec4  contentColor = vec4(uv,1.0,v_backCoreColor.a); //vec4(1.0,0.0,1.0,1.0); //vec4(uv, 0.5 + 0.5*sin(iGlobalTime), 1.0);
  	vec4  BorderColor = v_backCoreColor; 
  	
  	//
  	//If no border is specified, we change the border to the backcolor
  	//This is to ensure there is no gap at the edge. (it seems even when set to zero the border has a slight size - likely due to AA)
  	//
    if (v_backBorderWidth==0.0){
      BorderColor=v_backBackColor;
    }
  
  //The final result of working out the background colour at this point goes into resultColour  
    vec4  resultColor = BorderColor;
   
   // fragCoord.x = fragCoord.x-0.5;    
   // fragCoord.y = fragCoord.y-0.5;      
    
   
    float hbw = v_backBorderWidth / 2.0;
    
   //old function
   // float rr = udRoundRect(fragCoord - center, hsize - radius , radius); //- radius        
	
	//new function for 2D Rectangular Distance
	vec2 p = fragCoord;// (fPosition + vec2(0.0))*iResolution.xy;		
    float rr = boxDist2D(translate(p, hsize), (iResolution.xy/2.0)-v_backBorderWidth, radius); //Half resolution works....why?
          
	//Notes on Box "Distance Function" above     
    //rr==0 seems to be the edge returned 
    //need to seperate transitions
    //rr>0 means "towards edge" ie. interpolate between the border colour and transparent
    //rr<0 means "towards center" ie. interpolate between the border colour and background
      //  rr = 1.0-abs(rr);
        //float a = clamp(rr, 0.0,1.0);
    
    if (rr>hbw){
    	//v_backBackColor =  ;    	    	
   	    rr = 1.0-abs(rr);
        float a = clamp(rr, 0.0,1.0);
    	resultColor =mix(BorderColor, vec4(0.0,0.0,0.0,0.0), 1.0-a); 
    	
    } else if (rr <=-hbw){
    	    	
   	    rr = 1.0-abs(rr);
       float a = clamp(rr, 0.0,1.0);
    	resultColor =mix(BorderColor, v_backBackColor, 1.0-a); 
    } else {
   	 	resultColor = BorderColor;
    }
    
   // 
   //(if we add shadow support, it will created before that if statement and replace the vec4(0,0,0,0), as that currently represents the transparent
   //region of the shader)
   //
    
    
   //return the background colour as a result
   return resultColor;//mix(contentColor, v_backBackColor, 1.0-a); 
}


//
//And now, a rather complex function for the styled text using the distance field image of it supplied
//
vec4 getStyledText()
{
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
		if (dist>v_outlinerOuterLimit){ //outermost limit (0 is max/outer edge)  0.05 is default for v_outlinerOuterLimit
			if (dist<v_outlinerInnerLimit){ //inner limit  0.2 is a good default 
			
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
    	
    	//ensure inside texture still, else do nothing
    	  if (xo>0.0 && yo>0.0 && xo<1.0 && yo<1.0  ) {
     
    	
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
    			
    	
    }
    
    return newCol;


}

void main() {


	//if theres padding the co-ordinates need to be scalled down by the paddings step size
	float paddingXstepSize = (v_textPaddingX * pixel_step.x);
    float paddingYstepSize = (v_textPaddingY * pixel_step.y);
    
    //now we need to scale down the TexCoord by the size difference between that and the resolution
    //(as the TexCoords should NOT including padding size)    
     vTexCoord.x =  vTexCoord.x *  u_sizeDiff.x;    
     vTexCoord.y =  vTexCoord.y *  u_sizeDiff.y;
    
   //alter by padding to get padded co-ordinates
   vTexCoord.x = vTexCoord.x - paddingXstepSize;
   vTexCoord.y = vTexCoord.y - paddingYstepSize;
   
   
   
   //if either of the co-ordinates are now less then zero or greater then the limits we set the text color settings to nothing
   if (vTexCoord.x<0.0 || vTexCoord.y<0.0 || vTexCoord.x>1.0 || vTexCoord.y>1.0 ) {
   
     	v_textColor   = vec4(0.0,0.0,0.0,0.0);
     	v_outColor    = vec4(0.0,0.0,0.0,0.0);
     	v_glowColor   = vec4(0.0,0.0,0.0,0.0);
     	//	v_shadowColour= vec4(0.0,0.0,0.0,0.0);  //shadow has a slightly different tollerance so its dealt with in its generation
    
	}
    
   
    
	//mix with back colour	
    
	
	//      = vec4(0.0,0.0,0.0,1.0);
	//newCol = clamp(newCol,vec4(0.0,0.0,0.0,0.0),vec4(1.0,1.0,1.0,1.0));
	
	//v_backColor = vec4(1.0,1.0,1.0,1.0);
	//v_backColor = clamp(v_backColor,vec4(0.0,0.0,0.0,0.0),vec4(1.0,1.0,1.0,1.0));
	
	
	//                addColor*addColor.a + sceneColor*(1-addColor.a);
	
	vec4 newCol = vec4(0.0,0.0,0.0,0.0);
	
	if (v_textColor!= vec4(0.0,0.0,0.0,0.0)   ||  v_outColor!= vec4(0.0,0.0,0.0,0.0) ||  v_glowColor!= vec4(0.0,0.0,0.0,0.0)  ||  v_shadowColour!= vec4(0.0,0.0,0.0,0.0)  ){
		
		newCol = getStyledText();
		
	}
	
	
	vec4 v_backColor = vec4(0.0,0.0,0.0,0.0);
	
	//if theres a background colour or a border colour, then we get the backcolor for this particular spot
	if ( v_backBackColor!= vec4(0.0,0.0,0.0,0.0) || v_backCoreColor!= vec4(0.0,0.0,0.0,0.0)  ){
		
		v_backColor = getBackColour();
		
	}
	
	
	
	vec4 finalCol =  (newCol * newCol.a) + (v_backColor * (1.0-newCol.a));
	finalCol.a =  newCol.a + v_backColor.a;
	
	//finalCol.a = vec4(0.0,0.0,0.0,1.0);
	//finalCol = clamp(finalCol,vec4(0.0,0.0,0.0,0.0),vec4(1.0,1.0,1.0,1.0));
	
	
    gl_FragColor =  finalCol; //diffuse.rgb
 	 	 	
 	 	
    
}


