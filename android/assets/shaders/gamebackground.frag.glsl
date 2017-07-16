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


//attributes from vertex shader
varying vec4 vColor;                          
varying vec2 vTexCoord;                      

//our texture samplers
uniform sampler2D u_texture;   //diffuse map 
uniform sampler2D u_normals;   //normal map  

//values used for shading algorithm...
uniform vec2 Resolution;      //resolution of screen
uniform vec3 LightPos;        //light position, normalized
uniform vec4 LightColor;      //light RGBA -- alpha is intensity
uniform vec4 AmbientColor;    //ambient RGBA -- alpha is intensity 
uniform vec3 Falloff;         //attenuation coefficients


//tidy;
uniform float u_time;
uniform vec2 mouse;
uniform vec2 u_resolution;
varying vec4 mixcolour;


float noise2d(vec2 p) {
	return fract(sin(dot(p.xy/.32 ,vec2(1.98,7.3))) * 4.5453);
}

void main() {


    vec2 p = ( gl_FragCoord.xy / u_resolution.xy );
	
	float a = 0.0;
	for (int i = 1; i < 20; i++) {
		float fi = float(i);
		float s = floor(2024.0*(p.x)/fi + 50.0*fi + (u_time*0.01));
		
		if (p.y < noise2d(vec2(s))*fi/35.0 - fi*.05 + 1.0 + 0.125*cos(u_time + float(i)/30.00 + p.x*2.0+(cos((u_time*0.01)*.7)*10.+length(p)*10.)+10.)*4.) {
			a = float(i)/20.;
		}
	}
	
	float r=a*p.x;
	float g=a*p.y;
	float b=a;
	vec4 DiffuseColor = mix(mixcolour,vec4(r,g,b,1),0.1);
	


    //RGBA of our diffuse color
    //vec4 DiffuseColor = texture2D(u_texture, vTexCoord);

    //RGB of our normal map
    //vec3 NormalMap = texture2D(u_normals, vTexCoord).rgb;
    
    //vec3 NormalMap = pow(DiffuseColor.rgb,0.5);
    
    vec3 NormalMap = pow(DiffuseColor.rgb, vec3(0.5,0.5,0.5) );
    

    //The delta position of light
    vec3 LightDir = vec3(LightPos.xy - (gl_FragCoord.xy / Resolution.xy), LightPos.z);

    //Correct for aspect ratio
    LightDir.x *= Resolution.x / Resolution.y;

    //Determine distance (used for attenuation) BEFORE we normalize our LightDir
    float D = length(LightDir);

    //normalize our vectors
    vec3 N = normalize(NormalMap * 2.0 - 1.0);
    vec3 L = normalize(LightDir);

    //Pre-multiply light color with intensity
    //Then perform "N dot L" to determine our diffuse term
    vec3 Diffuse = (LightColor.rgb * LightColor.a) * max(dot(N, L), 0.0);

    //pre-multiply ambient color with intensity
    vec3 Ambient = AmbientColor.rgb * AmbientColor.a;

    //calculate attenuation
    float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );

    //the calculation which brings it all together
    vec3 Intensity  = Ambient + Diffuse * Attenuation;
    vec3 FinalColor = DiffuseColor.rgb  * ((Intensity/2.0)+0.5);
    
    //vColor = vec4(1.0,1.0,1.0,1.0);
    
    //--------------------
    
    
    // gl_FragColor = DiffuseColor;
    
     gl_FragColor = vColor * vec4(FinalColor, DiffuseColor.a);
     
}