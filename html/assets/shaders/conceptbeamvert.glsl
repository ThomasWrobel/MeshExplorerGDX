//combined projection and view matrix
//uniform mat4 u_projView;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

uniform float u_width; 
uniform vec4 u_beamcolour;
uniform vec4 u_corecolour;
uniform float u_shotFrequency;

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

//style data
varying float width; 
varying vec4 beamcolour;
varying vec4 corecolour;
varying float shotFrequency;

void main() {

    vColor = Color;
    fPosition = a_texCoord0;
    
    width = u_width;
    beamcolour = u_beamcolour;
    corecolour = u_corecolour;
    shotFrequency = u_shotFrequency;
    
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
	
	
}