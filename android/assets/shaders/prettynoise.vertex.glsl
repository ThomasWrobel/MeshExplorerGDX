//combined projection and view matrix
//uniform mat4 u_projView;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform vec4 u_mixcolour;

//in
attribute vec3 a_position;
//attribute vec3 a_normal;
attribute vec2 a_texCoord0;
//attribute vec2 a_texCoord;
//attribute vec2 texCoord;
attribute vec4 Color;

//"out" varyings to our fragment shader
varying vec4 vColor;
varying vec2 vTexCoord;
varying vec4 mixcolour;

void main() {

    vColor = Color;
    vTexCoord = a_texCoord0;
    mixcolour = u_mixcolour;
    
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
	
	
}