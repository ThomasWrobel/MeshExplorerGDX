package com.lostagain.nl.uti;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;


/**
 * A way to represent and manipulate colors using HSL rather then RGB.</br>
 * This could be used for many things, but for now its used in the background colormap to allow a basecolor</br>
 * with slight varience in hue without messing up Luminous or Saturation</br>
 *  </br>
 * Based of ; http://en.wikipedia.org/wiki/HSL_color_space.</br>
 **/
public class HSLColor {
	
	
	    /** Hue */
	
	    public float h;
	    
	    /** Saturation */	    
	    public float s;
	    
	    /** Luminance */
	    public float l;

	    /** alpha is just preserved **/
	    private float a;

	    /**
	        Default constructor, all values set to 0 except alpha which is 1
	     */
	    public HSLColor()
	    {
	        this(0.0f, 0.0f, 0.0f, 1.0f);
	    }

	    /**
	        Construct a new HSL based of a existing RGB color which it tries to match.
	        
	        @param color The RGB col to convert to HSL
	     */
	    public HSLColor(Color color)
	    {
	        Vector3 hslVec = rgbToHsl(color);
	        
	        h = hslVec.x;
	        s = hslVec.y;
	        l = hslVec.z;
	        a = color.a;
	        
	    }

	    
	    /**
	     * 
	        Constructs a color in the HSL color space.
	        @param h Hue
	        @param s Saturation
	        @param l Luminance
	        @param a Alpha
	     */
	    public HSLColor(float h, float s, float l, float a)
	    {
	        this.h = h;
	        this.s = s;
	        this.l = l;
	        this.a = a;
	    }

	    public HSLColor copy() {
			
			return new HSLColor(h,s,l,a);
			
			
		}
	    
	    @Override
	    public String toString()
	    {	    	
	    	return ""+h+","+s+","+l+","+a+"";
	    }
	    
	    
	    /**
	     * Converts this HSL color value to RGB. 
	     * returns r, g, and b as floats between 0 and 1.
	     *
	     * @return The RGB representation
	     */
	    public Color toRGB()
	    {
	        float r, g, b;

	        if(s == 0)
	        {
	            r = l;
	            g = l;
	            b = l;
	        }
	        else
	        {
	            float q = (l < 0.5f) ? (l * (1.0f + s)) : (l + s - l * s);
	            float p = 2.0f * l - q;
	            
	            r = hue2rgb(p, q, h + 1.0f / 3.0f);	            
	            g = hue2rgb(p, q, h);
	            b = hue2rgb(p, q, h - 1.0f / 3.0f);
	        }

	        return new Color(r, g, b, a);
	    }

	    private static float hue2rgb(float p, float q, float t)
	    {
	        if(t < 0.0f) t += 1.0f;
	        if(t > 1.0f) t -= 1.0f;
	        if(t < 1.0f / 6.0f) return p + (q - p) * 6.0f * t;
	        if(t < 1.0f / 2.0f) return q;
	        if(t < 2.0f / 3.0f) return p + (q - p) * (2.0f / 3.0f - t) * 6.0f;
	        return p;
	    }

	    /**
	     * Converts an RGB color value to HSL.
	     * returns h, s, and l in floats between 0 and 1
	     *
	     * @param rgba the could value of the
	     * @return           The HSL representation
	     */
	    private static Vector3 rgbToHsl(Color rgba)
	    {
	        float r = rgba.r;
	        float g = rgba.g;
	        float b = rgba.b;

	        float max = (r > g && r > b) ? r : (g > b) ? g : b;
	        float min = (r < g && r < b) ? r : (g < b) ? g : b;

	        float h, s, l;
	        h = s = l = (max + min) / 2.0f;

	        if(max == min){
	            h = s = 0.0f;
	        } else {
	            float d = max - min;
	            s = l > 0.5f ? d / (2.0f - max - min) : d / (max + min);

	            if (r > g && r > b)
	                h = (g - b) / d + (g < b ? 6.0f : 0.0f);
	            else if(g > b)
	                h = (b - r) / d + 2.0f;
	            else
	                h = (r - g) / d + 4.0f;

	            h /= 6.0f;
	        }

	        return new Vector3(h, s, l);
	    }

	
	
}
