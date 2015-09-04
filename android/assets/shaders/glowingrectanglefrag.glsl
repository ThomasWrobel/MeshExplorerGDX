varying vec2 iResolution;
varying vec2 fPosition;

// Based and modified from;
// Created by Marc Lepage - mlepage/2015
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

// Rect center
vec2 center = iResolution.xy / 2.0;

// Rect half size
vec2 hsize  = iResolution.xy / 2.0;

// Rect radius
float radius = 3.0;

// Background color
//vec4 bgColor = vec4(0.0, 0.0, 0.0, 0.5);

//background style data
varying float v_glowWidth; 
varying vec4  v_backColor;
varying vec4  v_coreColor;
varying vec4  v_glowColor;


// Rounded rect distance function
float udRoundRect(vec2 p, vec2 b, float r)
{
	return length(max(abs(p) - b, 0.0)) - r;
}

void main()
{

	vec2 fragCoord = fPosition*iResolution.xy; 
	vec2  uv = fPosition;
		
    float iGlobalTime  = 0.5;
    
    //colour based on mode
    
    //specified color
    //vec4 contentColor = v_coreColor;
    
    //uv based color
	vec4  contentColor = vec4(uv,1.0,v_coreColor.a); //vec4(1.0,0.0,1.0,1.0); //vec4(uv, 0.5 + 0.5*sin(iGlobalTime), 1.0);

	
    // Simple animation (comment out to fill viewport)
   // center += iResolution.xy* 0.25*sin(iGlobalTime/3.0);
   // hsize *= (0.5 + 0.25*cos(iGlobalTime/5.0));
  //  radius = max(abs(radius*cos(iGlobalTime/7.0)), 8.0);
    
    // Mix content with background using rounded rectangle
    
    hsize = hsize - 1.5; //inwards from border
    
   // fragCoord.x = fragCoord.x-0.5;    
   // fragCoord.y = fragCoord.y-0.5;  
    
    float rr = udRoundRect(fragCoord - center, hsize - radius, radius);
    
    //float a =1.0;
   // if (rr<0.5 && rr>-0.5){ //0 seems to be bounday, negative in, positive out        
    //    a=0.0;
    //}
    if (rr>0){
    	//no background outside line
    	v_backColor = vec4(0.0,0.0,0.0,0.0);
    }
    
    rr = 1.0-abs(rr);
    float a = clamp(rr, 0.0,1.0);
    
    
    float w = v_glowWidth;//1.0; //width of fade 
    a = smoothstep(0.5 - w, 0.5 + w,rr);
    
	//
  //	float PI = 3.1415;
    
   // rr = clamp(rr, -PI*40.0,-PI*4.0);
    
  //  rr=cos(rr)*25.0;
    
    
//  rr = smoothstep(-41.0,11.0,rr);
    
   gl_FragColor = mix(contentColor, v_backColor, 1.0-a); //<---this
   // gl_FragColor = contentColor;
    
}