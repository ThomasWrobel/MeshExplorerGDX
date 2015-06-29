
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

//incoming texture
//uniform sampler2D u_diffuseTexture;

 //"in" varyings from our vertex shader
varying vec2 v_texCoord0;
varying vec4 v_color;
varying vec2 vTexCoord;
//varying float v_usesDiffuseColor;

varying vec4 v_diffuseColor;
//varying vec4 v_diffuseColor;
 
 
 
#if defined(colorFlag)
varying vec4 v_color;
#endif
  

varying MED vec2 v_diffuseUV;
 

uniform sampler2D u_diffuseTexture;

 
const float smoothing =  0.25/(4.0*32.0); //0.25/(filesmooth*fontfilescale)                     //0.001953125//1.0/16.0;  
 
void main() {

	float colorFlag = 0.0;
	vec4 diffuse = vec4(1.0,0.0,0.0,1.0);
	 
	if ((colorFlag==1.0))
	{
		//vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor * v_color;
		diffuse = vec4(0.0,1.0,0.0,1.0);
	}
	else 
	{
	//	v_diffuseColor = vec4(0.0,0.0,1.0,1.0); //temp for testing
		diffuse = v_diffuseColor;// texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
		//diffuse = vec4(1.0,1.0,1.0,1.0);
	}


 	//the alpha of the incoming texture acts as the distance from inside a letter to outside
 	float distance = texture2D(u_diffuseTexture, vTexCoord).a;
 	 	 	
 	//now we use that distance to make a new alpha
    float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
    
 	//vec4 diffuse =  v_diffuseColor; // * v_color doesnt seem to work diffuse has no effect on color
 	
   // gl_FragColor = vec4(v_color.rgb, alpha);
   
   gl_FragColor = vec4(diffuse.rgb,alpha);
     
   // gl_FragColor =    texture2D(u_diffuseTexture, vTexCoord);
    
}

