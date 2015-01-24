//combined projection and view matrix
uniform mat4 u_projView;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

//in
attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
attribute vec2 a_texCoord;
attribute vec2 texCoord;
attribute vec4 Color;

//"out" varyings to our fragment shader
varying vec4 vColor;
varying vec2 vTexCoord;
 
void main() {

    vColor = Color;
    vTexCoord = a_texCoord0;
    
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
	
	
}