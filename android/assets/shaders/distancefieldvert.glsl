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
uniform vec4 u_diffuseColor;

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

 
void main() {

  //  v_texCoord0 = a_texCoord0;
   
    v_color = a_color;
    vTexCoord = a_texCoord0;
   // v_usesDiffuseColor =  a_usesDiffuseColor;
  	v_diffuseColor = u_diffuseColor;
  	
  	v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
  
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}