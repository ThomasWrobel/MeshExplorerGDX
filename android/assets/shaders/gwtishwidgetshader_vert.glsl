
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

uniform vec4 u_textColor;
//uniform vec4 u_backColor;

uniform float u_textPaddingX;
uniform float u_textPaddingY;

//glow
uniform vec4  u_glowColor;
uniform float u_glowSize; //size of glow (values above 1 will look strange)
		
//outline
uniform vec4  u_outColor;
uniform float u_outlinerInnerLimit; //Arbitrarily big size for no outline
uniform float u_outlinerOuterLimit; //Arbitrarily big size for no outline

//shadow
uniform float u_shadowXDisplacement;
uniform float u_shadowYDisplacement;
uniform float u_shadowBlur;
uniform vec4  u_shadowColour;
//--
			
uniform float u_colorFlag; //color mode tells the shader it uses a diffuse color(1) rather then just the textures(0)
varying float v_colorFlag;

//background
uniform float u_backGlowWidth; 
uniform vec4  u_backBackColor;
uniform vec4  u_backCoreColor;
uniform float u_backCornerRadius;

uniform vec2  u_resolution;

 //out
varying vec2 v_texCoord0;
varying vec4 v_color;
varying vec2 vTexCoord;

varying vec4 v_textColor;
//varying vec4 v_backColor;

//padding
varying float v_textPaddingX;
varying float v_textPaddingY;

//glow
varying vec4  v_glowColor;
varying float v_glowSize; //size of glow (values above 1 will look strange)
		
//outline
varying vec4  v_outColor;
varying float v_outlinerInnerLimit; //Arbitrarily big size for no outline
varying float v_outlinerOuterLimit; //Arbitrarily big size for no outline

//shadow
varying float v_shadowXDisplacement;
varying float v_shadowYDisplacement;
varying float v_shadowBlur;
varying vec4  v_shadowColour;

			
			
			

			
//bacground shapeing data
varying vec2 iResolution;
varying vec2 fPosition;
//background style data
varying float v_backGlowWidth; 
varying vec4  v_backBackColor;
varying vec4  v_backCoreColor;
varying float v_backCornerRadius;





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
	
  //Colours!!
 //  v_backColor = u_backColor;
   
   
    v_color = a_color;
    
    vTexCoord = a_texCoord0;
        
    pixel_step = u_pixel_step;
    


	
	//glow
	v_glowColor = u_glowColor;
	v_glowSize  = u_glowSize; //size of glow (values above 1 will look strange)
		
	//outline
	v_outColor           = u_outColor;
	v_outlinerInnerLimit = u_outlinerInnerLimit; 
	v_outlinerOuterLimit = u_outlinerOuterLimit; 

	//shadow
	v_shadowXDisplacement = u_shadowXDisplacement;
	v_shadowYDisplacement = u_shadowYDisplacement;
	v_shadowBlur          = u_shadowBlur;
	v_shadowColour        = u_shadowColour;
    
    
    //background
   v_backGlowWidth   =u_backGlowWidth; 
   v_backBackColor   =u_backBackColor;
   v_backCoreColor   =u_backCoreColor;
   v_backCornerRadius=u_backCornerRadius;
    
  	iResolution = u_resolution;
  	fPosition = a_texCoord0;
    

    
   // v_usesDiffuseColor =  a_usesDiffuseColor;
  	v_textColor = u_textColor;
  	
    //padding
    v_textPaddingX = u_textPaddingX;
    v_textPaddingY = u_textPaddingY;
    
    
    
	
	
  	v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
  	
  	    

     
  
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}