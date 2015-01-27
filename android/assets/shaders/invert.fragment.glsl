#ifdef GL_ES 
precision mediump float;
#endif

//SpriteBatch will use texture unit 0
uniform sampler2D u_diffuseTexture;

 //"in" varyings from our vertex shader
varying vec2 v_texCoord0;

#if defined(colorFlag)
varying vec4 v_color;
#endif

#if defined(diffuseTextureFlag) || defined(specularTextureFlag)
#define textureFlag
#endif

#ifdef diffuseColorFlag
uniform vec4 u_diffuseColor;
#endif

varying vec2 vTexCoord;

#ifdef diffuseTextureFlag
varying MED vec2 v_diffuseUV;
#endif
 
void main() {

	#if defined(diffuseTextureFlag) && defined(diffuseColorFlag) && defined(colorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor * v_color;
	#elif defined(diffuseTextureFlag) && defined(diffuseColorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
	#elif defined(diffuseTextureFlag) && defined(colorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * v_color;
	#elif defined(diffuseTextureFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);
	#elif defined(diffuseColorFlag) && defined(colorFlag)
		vec4 diffuse = u_diffuseColor * v_color;
	#elif defined(diffuseColorFlag)
		vec4 diffuse = u_diffuseColor;
	#elif defined(colorFlag)
		vec4 diffuse = v_color;
	#else
		vec4 diffuse = vec4(1.0);
	#endif

 //sample the texture
    //vec4 texColor = texture2D(diffuse, vTexCoord);
    
    //invert the red, green and blue channels
    vec3 texColor =  diffuse.rgb;//1.0 - diffuse.rgb;

  //  gl_FragColor = vec4(1.0,0.0, 0.0, 1.0);
   
   gl_FragColor = vec4(texColor, 1.0);
   
 // gl_FragColor = vec4(vec3(vTexCoord.s), 1.0);

  //  gl_FragColor = vec4(v_texCoord0, 0.0, 1.0);
    
     //final color
 //   gl_FragColor = vColor * texColor;
}

