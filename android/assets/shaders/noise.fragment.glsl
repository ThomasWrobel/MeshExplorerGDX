// HSV Demo by Dima

#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec2 mouse;
uniform vec2 resolution;

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

void main( void ) {
	float x;
	vec2 position = ( gl_FragCoord.xy / resolution.xy ) + mouse / 4.0;

	x = rand(vec2(u_time*position.y, u_time/position));
  	gl_FragColor = vec4(x, x, x, 1.0);
}