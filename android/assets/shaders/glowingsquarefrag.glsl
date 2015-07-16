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


uniform float u_time;
uniform vec2 resolution;
varying vec2 fPosition;
varying vec3 fNormal;

//style data
varying float v_glowWidth; 
varying vec4  v_backColor;
varying vec4  v_coreColor;
varying vec4  v_glowColor;

// this calculated in vertex shader
// width/height = triangle/quad width/height in px;
//vec2 pixel_step = vec2(1/width, 1/height);  
varying vec2 pixel_step;
    
void main()
{
  
  float x = fPosition.x; //flip horizontally
  float y = fPosition.y; //fliped vertically
    
    
  //normalise values to -1 to 1, libgdx seems to use 0-1 by default
  //y = (y*2.0)-1.0;
  //x = (x*2.0)-1.0;
  
  //params (in future make these editable at shader creation?)
  
  vec4 back = v_backColor; //background (default transparent)
  vec4 beam = v_glowColor; //vec4(1.0,0.0,0.0,1.0); //beam
  vec4 core = v_coreColor; //vec4(2.0,1.0,1.0,1.0);  //core (note ranges outside 0-1 can be used)
  
  vec4 result = vec4(0.0,0.0,0.0,0.0);
  
	float glowAlpha = 0.0;
	float edgeDis   = 0.0;
	
	
    float gwy = v_glowWidth * pixel_step.y;
  	float gwx = v_glowWidth * pixel_step.x;
  	
  if (y<(gwy)) {
  	//if we are on the top or bottom our size is based on the y step
  
    edgeDis= y - (gwy/2.0);
    edgeDis= (gwy/2.0)-abs(edgeDis);
    
                       
    glowAlpha = smoothstep(0.0, (gwy/2.0), edgeDis);       
  } 
  
  if (y>(1.0-gwy)) {
  
  	//if we are on the top or bottom our size is based on the y step
 
  	
    edgeDis= (1.0-(gwy/2.0))-y;// - (v_glowWidth/2);
    
    edgeDis= (gwy/2.0)-abs(edgeDis);
    
    glowAlpha = smoothstep(0.0, (gwy/2.0), edgeDis);
  } 
  
   if (x<(gwx)) {
  
  	//if we are on the top or bottom our size is based on the y step
  
  	
    edgeDis= x - (gwx/2.0);
    edgeDis= (gwx/2.0)-abs(edgeDis);
    
    glowAlpha = smoothstep(0.0, (gwx/2.0), edgeDis);
                              
  } 
  
  if (x>(1.0-gwx)) {
  	

  	
    edgeDis= (1.0-(gwx/2.0))-x;// - (v_glowWidth/2);
    
    edgeDis= (gwx/2.0)-abs(edgeDis);
    
    glowAlpha = smoothstep(0.0, (gwx/2.0), edgeDis);
  } 
  
    
    //v_glowColor.a =  v_glowColor.a* glowAlpha;
  	beam.a =  v_glowColor.a* glowAlpha;
  	
  	//add a sharper core
  	float coreAlpha = pow(glowAlpha,6.0);
  //	v_coreColor.a = v_coreColor.a * coreAlpha;
  	core.a  = v_coreColor.a * coreAlpha;
  	
    
    //now blend core with glow
   // result = (v_glowColor * v_glowColor.a) + (v_coreColor * (1-v_glowColor.a));
   // result.a = v_glowColor.a + v_coreColor.a;	
    //result   = v_glowColor     + v_coreColor;
    result = beam + core;
   // result.a = v_glowColor.a   + v_coreColor.a;
    result.a = beam.a   + core.a;
    
    //now blend with background (under it!)
    result = (result * result.a) + (v_backColor * (1.0-result.a));
   // result.a = 0.5;//result.a + v_backColor.a;
    
    	    //NOTE: Background alpha seems lost when combined...why?
    
  gl_FragColor =  result;
  
}