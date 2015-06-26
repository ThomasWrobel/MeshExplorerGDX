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
  
  float x = 1.0-fPosition.x; //flip horizontally
  float y = 1.0-fPosition.y; //fliped vertically
    
  //normalise values to -1 to 1, libgdx seems to use 0-1 by default
  y = (y*2.0)-1.0;
  x = (x*2.0)-1.0;
  
  //params (in future make these editable at shader creation?)
  
  vec4 back = vec4(0.0,0.0,0.0,0.0);  //background (default transparent)
  vec4 col = back;
  vec4 beam = beamcolour;// vec4(1.0,0.0,0.0,1.0); //beam
  vec4 core = corecolour;//vec4(2.0,1.0,1.0,1.0);  //core (note ranges outside 0-1 can be used)
  
  //float width =  0.25; // used to be 0.04
  
  
   float tsin = abs(sin((shotFrequency*u_time))); 
   

  
  
 // float targetx = 0.5;
  //float distarget = abs(x);
  //invert so neares tthe target is strongest
 // float abx = 1-distarget;
  
 // float abx=1.1-abs(x-0.9); //0 to 1 based on distance from center 
 // if (x<0.0){
//    abx = 0.0;
//  }
  
  //float eppx=pow(abx,1.0);//
  //float xgrad =(eppx)*tsin;
 // if (xgrad<0.0)
 // {
 //   xgrad=0.0;
 // }
 
  float abx=1.0-abs(x); //1 to 0 based on distance from center
//  float epp=abx;//pow(aby,1.0);//
  float xgrad =(abx)*tsin;
  if (xgrad<0.0){
    xgrad=0.0;
  }
  
  
  float aby=1.0-abs(y); //1 to 0 based on distance from center
//  float epp=aby;//pow(aby,1.0);//
  float ygrad =(aby)*tsin;
  if (ygrad<0.0){
    ygrad=0.0;
  }
  
  float intensityOfCore = xgrad*ygrad;
  
 // vec4 col3 = (core*intensityOfEnd);
  
  //beam coloyred outer bit  
  //vec4 col3 = (beam*intensityOfEnd);
  
  //now work out core intensity
  float avxc=1.0-abs(x); //1 to 0 based on distance from center
  float xgradc =(avxc);
  if (xgradc<0.0){
    xgradc=0.0;
  }
  
  
  float abyc=1.0-abs(y); //1 to 0 based on distance from center
  float ygradc =(abyc);
  if (ygradc<0.0){
    ygradc=0.0;
  }
  
  float intensityOfBeam = xgradc*ygradc;
  
  //mix color and core
  vec4  col3 = (beam*(intensityOfBeam));
  col3 = col3+(core*(intensityOfCore));
   
   col = col+(col3);
     gl_FragColor =  col;
  
}