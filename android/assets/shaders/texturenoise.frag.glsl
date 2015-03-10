precision highp float;
uniform float time;
uniform vec2 resolution;
varying vec3 fPosition;
float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main()
{
  
  float threshold = 0.9;
  
  float x = fPosition.x;
  float y = fPosition.y;
  vec2 test = vec2(x,y);
  
 float picked = rand(test);
 
 if (picked>threshold){
   
     gl_FragColor = vec4(0.0,0.0,0.0,1.0);
     
 } else {
   
   gl_FragColor = vec4(0.0,0.0,0.0,0.0);
   
     //gl_FragColor = vec4(fNormal, 1.0);
     
 }
  
 
    
    
    
  

  

}

