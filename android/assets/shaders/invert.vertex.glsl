//in
attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
attribute vec2 a_texCoord;
attribute vec2 texCoord;
attribute vec4 Color;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
 
 //out
varying vec2 v_texCoord0;

varying vec2 vTexCoord;
 
#if defined(diffuseTextureFlag) || defined(specularTextureFlag)
#define textureFlag
#endif
 
#if defined(colorFlag)
varying vec4 v_color;
attribute vec4 a_color;
#endif // colorFlag

 
#ifdef textureFlag
attribute vec2 a_texCoord0;
#endif // textureFlag
 
#ifdef diffuseTextureFlag
uniform vec4 u_diffuseUVTransform;
varying vec2 v_diffuseUV;
#endif


void main() {
  //  v_texCoord0 = a_texCoord0;
  
    #if defined(colorFlag)
		v_color = a_color;
	#endif // colorFlag
	
	#ifdef diffuseTextureFlag
		v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
	#endif //diffuseTextureFlag
	
	
   // vColor = Color;
    vTexCoord = a_texCoord0;
    
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}