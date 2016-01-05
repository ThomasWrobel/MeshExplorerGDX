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

//combined projection and view matrix
//uniform mat4 u_projView;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;


//in
attribute vec3 a_position;
attribute vec2 a_texCoord0;
uniform vec4 u_vcolor;


uniform vec4 u_mixcolour;

//"out" varyings to our fragment shader
varying vec4 vColor;
varying vec2 vTexCoord;
//
 varying vec4 mixcolour;
 

void main() {

    vColor     = u_vcolor;    
    vTexCoord  = a_texCoord0;        
    mixcolour = u_mixcolour;
    
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
	
	
}