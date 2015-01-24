// HSV Demo by Dima

#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec2 mouse;
uniform vec2 resolution;

varying vec2 vTexCoord;

void main( void ) {

	vec2 position = vTexCoord;//( gl_FragCoord.xy / resolution.xy );
	//time = 0.5;
	
	float color = 0.0;
	color += sin( position.x * cos( u_time / 15.0 ) * 80.0 ) + cos( position.y * cos( u_time / 15.0 ) * 10.0 );
	color += sin( position.y * sin( u_time / 10.0 ) * 40.0 ) + cos( position.x * sin( u_time / 25.0 ) * 40.0 );
	color += sin( position.x * sin( u_time / 5.0 ) * 10.0 ) + sin( position.y * sin( u_time / 35.0 ) * 80.0 );
	color *= sin( u_time / 10.0 ) * 0.5;

	gl_FragColor = vec4( vec3( color, color * 0.5, sin( color + u_time / 3.0 ) * 0.75 ), 1.0 );

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