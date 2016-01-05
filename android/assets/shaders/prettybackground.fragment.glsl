// HSV Demo by Dima

#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec2 mouse;
uniform vec2 u_resolution;

varying vec2 vTexCoord;

varying vec4 mixcolour;

float noise2d(vec2 p) {
	return fract(sin(dot(p.xy/.32 ,vec2(1.98,7.3))) * 4.5453);
}

void main( void ) {

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
	vec4 diffuseCol = mix(mixcolour,vec4(r,g,b,1),0.1);
		
		
		
		
		
	gl_FragColor = diffuseCol;


	//vec2 position = vTexCoord;//( gl_FragCoord.xy / resolution.xy );
	//time = 0.5;
	
	//float color = 0.0;
	//color += sin( position.x * cos( u_time / 15.0 ) * 80.0 ) + cos( position.y * cos( u_time / 15.0 ) * 10.0 );
	//color += sin( position.y * sin( u_time / 10.0 ) * 40.0 ) + cos( position.x * sin( u_time / 25.0 ) * 40.0 );
	//color += sin( position.x * sin( u_time / 5.0 ) * 10.0 ) + sin( position.y * sin( u_time / 35.0 ) * 80.0 );
	//color *= sin( u_time / 10.0 ) * 0.5;

	    //Sample the texture at the interpolated coordinate
 //   lowp vec4 col = texture2D( texture, vTexCoord ) * vColor;

   // float x,y;
   // x=fract(vTexCoord.x*75.0);
  //  y=fract(vTexCoord.y*75.0);

    // Draw a black and white grid.
   // if(x > 0.9 || y > 0.9) {
       
   //     gl_FragColor = vec4(0.2,0.2,0.6,1f);
   // }
   // else
   // {
  //      gl_FragColor = vec4(0.1,0.1,0.8,1f);
   // }

//	vec2 p = vTexCoord.xy;//- resolution.xy/2.0;//gl_FragCoord.xy - resolution.xy/2.0;
//	vec3 col = vec3(0.1,0.0,0.0);
	
//	float pi = 2.0*atan(1.0, 0.0);
//	float radius = 50.0;
//	float speed = 3.0;
	
//	float hue = atan(p.y,p.x);
	
////	hue = hue * 2;
//	hue = mod(hue,1);
		
//	float saturation = 1.0 - length(p) / radius;
//	float value = 0.5+ 0.5*sin(time*speed);
	
	//if ( saturation > 0.0 ) {
	//	col.r = clamp( + 2.0 * pi / 3.0 - abs( hue + 0.0 * pi / 3.0 ), 0.0, 1.0);
	//	col.g = clamp( - 1.0 * pi / 3.0 + abs( hue + 1.0 * pi / 3.0 ), 0.0, 1.0);
	//	col.b = clamp( - 1.0 * pi / 3.0 + abs( hue - 1.0 * pi / 3.0 ), 0.0, 1.0);
	//	col.rgb = mix ( col.rgb , vec3(value), saturation );
	//}
	
	
//	gl_FragColor = vec4( col, 1.0 );
//gl_FragColor = vec4(vTexCoord, 0.0, 1.0);

}


