
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

uniform float u_glowWidth; 
uniform vec4  u_backColor;
uniform vec4  u_coreColor;
uniform vec4  u_glowColor;
uniform vec2  u_pixel_step;

//in
attribute vec3 a_position;
//attribute vec3 a_normal;
attribute vec2 a_texCoord0;
//attribute vec2 a_texCoord;
//attribute vec2 texCoord;
attribute vec4 Color;

//"out" varyings to our fragment shader
varying vec4 vColor;
varying vec2 fPosition;
varying vec2 pixel_step;

//style data
varying float v_glowWidth; 
varying vec4  v_backColor;
varying vec4  v_coreColor;
varying vec4  v_glowColor;

void main() {

    vColor = Color;
    fPosition = a_texCoord0;
    
   v_glowWidth=u_glowWidth; 
   v_backColor=u_backColor;
   v_coreColor=u_coreColor;
   v_glowColor=u_glowColor;
    
    pixel_step = u_pixel_step;
    
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
	
	
}