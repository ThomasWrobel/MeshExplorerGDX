// HSV Demo by Dima

#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec2 mouse;
uniform vec2 resolution;
uniform float u_alpha;

varying vec2 vTexCoord;


// http://byteblacksmith.com/improvements-to-the-canonical-one-liner-glsl-rand-for-opengl-es-2-0/
float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

//highp float rand2(vec2 co)
 float rand2(vec2 co)
{
   // highp 
    float a = 12.9898;
   // highp 
    float b = 78.233;
   // highp 
    float c = 43758.5453;
    //highp 
    float dt= dot(co.xy ,vec2(a,b));
    //highp 
    float sn= mod(dt,3.14);
    return fract(sin(sn) * c);
}

void main( void ) 
{




	vec2 position = ( gl_FragCoord.xy / resolution.xy ) + mouse / 4.0;
	
	float r;
	float g;
	float b;
	
	vec4 tint = vec4(1.0,1.0,1.0,1.0);
	
    //full color noise mode 
    #ifdef rgbmodeFlag
	 r = rand(vec2(u_time*position.y, u_time/position));
	 g = rand(vec2(u_time*position.y+1, u_time/position));
	 b = rand(vec2(u_time*position.y+2, u_time/position));
	#else
	 r = rand(vec2(u_time*position.y, u_time/position));
	 g = r;
	 b = r;
	#endif
	 
	//multiple by tint (standard tint is white)
	//float col = rand(vec2(u_time*position.y, u_time/position));
	
	 r = tint.r * r;
	 g = tint.g * g;
	 b = tint.b * b;
	 
	
  	gl_FragColor = vec4(r, g, b, u_alpha);
  	
}