
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






// this calculated in vertex shader
// width/height = triangle/quad width/height in px;
//vec2 pixel_step = vec2(1/width, 1/height);  
varying vec2 pixel_step;
    

//
//float fwidth = abs(dx) + abs(dy);     
//----------------------------

//incoming texture
//uniform sampler2D u_diffuseTexture;

 //"in" varyings from our vertex shader
varying vec2 v_texCoord0;
varying vec4 v_color;
varying vec2 vTexCoord;
//varying float v_usesDiffuseColor;

varying vec4 v_diffuseColor;
//varying vec4 v_diffuseColor;
varying float v_colorFlag;
 
 
  

varying MED vec2 v_diffuseUV;
 
//uniform sampler2D u_diffuseTexture;

uniform sampler2D u_texture;

//WORKAROUND if fwidth/above extension is not supported
//http://stackoverflow.com/questions/22442304/glsl-es-dfdx-dfdy-analog
float myFunc(vec2 p){
return p.x*p.x - p.y; // that's our function. We want derivative from it.
}

//These things are to get around the fwidth function being missing on some GPUs
float current = myFunc(vTexCoord);
float dfdx    = myFunc(vTexCoord + pixel_step.x) - current; //myfunction is given the next co-ordinate in x? (current vTexCoord + the change in the co-ordinate)
float dfdy    = myFunc(vTexCoord + pixel_step.y) - current; //same for y
  

 
const float smoothing =  0.25/(4.0*32.0); //0.25/(filesmooth*fontfilescale)                     //0.001953125//1.0/16.0;  

float contour(in float d, in float w) {
    // smoothstep(lower edge0, upper edge1, x)
    return smoothstep(0.5 - w, 0.5 + w, d);
}

float samp(in vec2 uv, float w) {
    return contour(texture2D(u_texture, vTexCoord).a, w);
}

void main() {

	float colorFlag = v_colorFlag;
	vec4 diffuse = vec4(1.0,0.0,0.0,1.0);
	 
	 
	if ((colorFlag==0.0))
	{
		//vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor * v_color;
		diffuse = texture2D(u_texture, vTexCoord);//vec4(0.0,1.0,0.0,1.0);
	}
	else 
	{
	//	v_diffuseColor = vec4(0.0,0.0,1.0,1.0); //temp for testing
		diffuse = v_diffuseColor;// texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
		//diffuse = vec4(1.0,1.0,1.0,1.0);
	}


 	//the alpha of the incoming texture acts as the distance from inside a letter to outside
 	float dist = texture2D(u_texture, vTexCoord).a;
 	 	 	
 	// fwidth helps keep outlines a constant width irrespective of scaling
    // GLSL's fwidth = abs(dFdx(uv)) + abs(dFdy(uv))
    
   // float width = fwidth(dist); //<---------------correct formula (works fine desktop)
       	 	
   //float width = abs(dFdx(dist)) + abs(dFdy(dist));  //<-----------(was attempt at replacement for web, does not work)          
     
   float width = abs(dfdx*dist) + abs(dfdx*dist);  
       	 	
 	 	 	// supersampled version

    float alpha = contour( dist, width );
    //float alpha = aastep( 0.5, dist );

    // ------- (comment this block out to get your original behavior)
    // Supersample, 4 extra points
    float dscale = 0.354; // half of 1/sqrt2; you can play with this
    
   // vec2 duv = dscale * (dFdx(vTexCoord) + dFdy(vTexCoord)); //<---------------correct formula (works fine desktop)
   
    vec2 duv = vec2(dscale * dfdx,dscale * dfdy); //<-------------web replacement for now
    
    vec4 box = vec4(vTexCoord-duv, vTexCoord+duv);

    float asum = samp( box.xy, width )
               + samp( box.zw, width )
               + samp( box.xw, width )
               + samp( box.zy, width );

    // weighted average, with 4 extra points having 0.5 weight each,
    // so 1 + 0.5*4 = 3 is the divisor
    alpha = (alpha + 0.5 * asum) / 3.0;

    // -------
	// 
	//diffuse.rgb = vec4(1.0,0.0,0.0,1.0).rgb;
	//diffuse=diffuse+1;
	//alpha = (alpha*0.5) +dist;
	
	
    gl_FragColor = vec4( diffuse.rgb, alpha); //diffuse.rgb
 	 	 	
 	 	 	//OLD VERSION:
 	//now we use that distance to make a new alpha
 //   float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
    
 	//vec4 diffuse =  v_diffuseColor; // * v_color doesnt seem to work diffuse has no effect on color
 	
   // gl_FragColor = vec4(v_color.rgb, alpha);
   
   ///gl_FragColor = vec4(diffuse.rgb,alpha);
     
   // gl_FragColor =    texture2D(u_diffuseTexture, vTexCoord);
    
}


