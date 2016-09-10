//precision highp float;
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

//styledata
varying float width;
varying vec4 beamcolour;
varying vec4 corecolour;
varying float shotFrequency;

void main()
{
  
  float x = 1.0-fPosition.x; //flip horizontally
  float y = 1.0-fPosition.y; //fliped vertically
    
  //normalise values to -1 to 1, libgdx seems to use 0-1 by default
  x = (x*2.0)-1.0;
  y = (y*2.0)-1.0;
  
  //params (in future make these editable at shader creation?)
  
  vec4 back = vec4(0.0,0.0,0.0,0.0);  //background (default transparent)
  vec4 col = back;
  vec4 beam = beamcolour;// vec4(1.0,0.0,0.0,1.0); //beam
  vec4 core = corecolour;//vec4(2.0,1.0,1.0,1.0);  //core (note ranges outside 0-1 can be used)
  
  //float width =  0.25; // used to be 0.04
  
  
   float tsin = abs(sin((shotFrequency*u_time)-y)); //shotFrequency used to be 2.0
   
  if (x<(width) && x>-width) 
  {
        
    //red bit        
    float intensity = (width-abs(x))*(1.0/width); //should result in 0-1 range    
    
    col = beam*intensity;//vec4(r,g,b,a);  
    
    //white core   
    float corethick = 10.0-(8.0*(tsin));
    
     intensity  = (width-abs(x*corethick))*(1.0/width);
     
     if (intensity<0.0){
       		intensity = 0.0;
     }
     

    vec4 col2 = core*intensity; //vec4(r2,g2,b2,a2);
    
    
    vec4 col3 = vec4(0,0,0,0);
    
     col = col+col2;
    
    
    //col = vec4(r+r2,g+g2,b+b2,a);  
    
    
  }
  
  //fade extream end
  if (y>0.98){ //used to be 90
  	
  	 float intensity =  y-0.90; // 0 to 0.1
  	 intensity = intensity * 10.0; // 0 to 1;
  	
  	col = mix(col,back,intensity);
  	
  
  }
  
  //end bit
  //float xgrad = sin((x-0.1)*9.0)*1.0;
  
   // -1 = 0 = 1 
  // 
  //we only want the extream end so we get the distance from a target point
  float targety = 1.00; //used to be 0.96
  float distarget = abs(targety-y);
  //invert so neares tthe target is strongest
  float aby = 1.0-distarget;
  
 // float abx=1.1-abs(x-0.9); //0 to 1 based on distance from center 
  if (y<0.0){
    aby = 0.0;
  }
  
  float eppy=pow(aby,50.0);//
  float ygrad =(eppy)*tsin;
  if (ygrad<0.0)
  {
    ygrad=0.0;
  }
  
  
  // -1 = 0 = 1 
  // 1 = 0 = 1
  // 0 = 1 = 0
  
  float abx=1.0-abs(x); //1 to 0 based on distance from center
  float epp=abx;//pow(aby,1.0);//
  float xgrad =(epp)*tsin;
  if (xgrad<0.0){
    xgrad=0.0;
  }
  float intensityOfEnd = ygrad*xgrad;
  vec4 col3 = core*intensityOfEnd;//vec4(xgrad*ygrad,xgrad*ygrad,xgrad*ygrad,xgrad*ygrad);
    
   col = col+(col3);
  
  
  gl_FragColor =  col;
  
}