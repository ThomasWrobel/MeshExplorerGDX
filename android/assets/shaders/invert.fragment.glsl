#ifdef GL_ES 
precision mediump float;
#endif

//SpriteBatch will use texture unit 0
uniform sampler2D u_diffuseTexture;

 //"in" varyings from our vertex shader
varying vec2 v_texCoord0;
 varying vec4 vColor;
varying vec2 vTexCoord;
 
void main() {

 //sample the texture
    vec4 texColor = texture2D(u_diffuseTexture, vTexCoord);
    
    //invert the red, green and blue channels
    texColor.rgb = 1.0 - texColor.rgb;

  //  gl_FragColor = vec4(1.0,0.0, 0.0, 1.0);
   

   gl_FragColor = texColor;
 // gl_FragColor = vec4(vec3(vTexCoord.s), 1.0);

  //  gl_FragColor = vec4(v_texCoord0, 0.0, 1.0);
    
     //final color
 //   gl_FragColor = vColor * texColor;
}

