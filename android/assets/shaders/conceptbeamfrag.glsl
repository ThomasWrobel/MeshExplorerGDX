precision highp float;
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
  
  float x = 1-fPosition.x; //flip horizontally
  float y = 1-fPosition.y; //fliped vertically
    
  //normalise values to -1 to 1, libgdx seems to use 0-1 by default
  y = (y*2.0)-1.0;
  x = (x*2.0)-1.0;
  
  //params (in future make these editable at shader creation?)
  
  vec4 back = vec4(0.4,0.4,0.4,1.0);  //background (default transparent)
  vec4 col = back;
  vec4 beam = beamcolour;// vec4(1.0,0.0,0.0,1.0); //beam
  vec4 core = corecolour;//vec4(2.0,1.0,1.0,1.0);  //core (note ranges outside 0-1 can be used)
  
  //float width =  0.25; // used to be 0.04
  
  
   float tsin = abs(sin((shotFrequency*u_time)-x)); //shotFrequency used to be 2.0
   
  if (y<(width) && y>-width) 
  {
        
    //red bit        
    float intensity = (width-abs(y))*(1.0/width); //should result in 0-1 range    
    
    col = beam*intensity;//vec4(r,g,b,a);  
    
    //white core   
    float corethick = 10.0-(8.0*(tsin));
    
     intensity  = (width-abs(y*corethick))*(1.0/width);
     
     if (intensity<0.0){
       		intensity = 0.0;
     }
     

    vec4 col2 = core*intensity; //vec4(r2,g2,b2,a2);
    
    
    vec4 col3 = vec4(0,0,0,0);
    
     col = col+col2;
    
    
    //col = vec4(r+r2,g+g2,b+b2,a);  
    
    
  }
  
  //fade end
  if (x>0.90){
  	
  	 float intensity =  x-0.90; // 0 to 0.1
  	 intensity = intensity * 10; // 0 to 1;
  	
  	col = mix(col,back,intensity);
  	
  
  }
  
  //end bit
  //float xgrad = sin((x-0.1)*9.0)*1.0;
  
   // -1 = 0 = 1 
  // 
  //we only want the extream end so we get the distance from a target point
  float targetx = 0.96;
  float distarget = abs(targetx-x);
  //invert so neares tthe target is strongest
  float abx = 1-distarget;
  
 // float abx=1.1-abs(x-0.9); //0 to 1 based on distance from center 
  if (x<0.0){
    abx = 0.0;
  }
  
  float eppx=pow(abx,50);//
  float xgrad =(eppx)*tsin;
  if (xgrad<0.0)
  {
    xgrad=0.0;
  }
  
  
  // -1 = 0 = 1 
  // 1 = 0 = 1
  // 0 = 1 = 0
  
  float aby=1.0-abs(y); //1 to 0 based on distance from center
  float epp=aby;//pow(aby,1.0);//
  float ygrad =(epp)*tsin;
  if (ygrad<0.0){
    ygrad=0.0;
  }
  float intensityOfEnd = xgrad*ygrad;
  vec4 col3 = core*intensityOfEnd;//vec4(xgrad*ygrad,xgrad*ygrad,xgrad*ygrad,xgrad*ygrad);
    
   col = col+(col3);
  
  
  gl_FragColor =  col;
  
}