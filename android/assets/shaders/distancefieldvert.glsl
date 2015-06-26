
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




//in
attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord;
attribute vec4 a_color;
attribute vec2 texCoord;
attribute vec2 a_texCoord0;

//attribute float a_usesDiffuseColor;


uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

uniform mat4 u_projTrans; //new

uniform vec4 u_diffuseColor;


uniform float u_colorFlag; //color mode tells the shader it uses a diffuse color(1) rather then just the textures(0)
varying float v_colorFlag;
 //out
varying vec2 v_texCoord0;
varying vec4 v_color;
varying vec2 vTexCoord;
varying vec4 v_diffuseColor;
//varying float v_usesDiffuseColor;


#if defined(colorFlag)
varying vec4 v_color;
attribute vec4 a_color;
#endif // colorFlag

uniform vec4 u_diffuseUVTransform;
varying vec2 v_diffuseUV;

 uniform vec2 u_pixel_step;
varying vec2 pixel_step;

void main() {
	v_colorFlag=u_colorFlag;
  //  v_texCoord0 = a_texCoord0;
   
    v_color = a_color;
    vTexCoord = a_texCoord0;
    
    
    pixel_step = u_pixel_step;
   // v_usesDiffuseColor =  a_usesDiffuseColor;
  	v_diffuseColor = u_diffuseColor;
  	
  	v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
  
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}