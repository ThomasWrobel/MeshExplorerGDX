//in
attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
 attribute vec2 TexCoord;
attribute vec4 Color;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
 
 //out
varying vec2 v_texCoord0;
varying vec4 vColor;
varying vec2 vTexCoord;
 
void main() {
    v_texCoord0 = a_texCoord0;
    
    vColor = Color;
    vTexCoord = TexCoord;
    
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}