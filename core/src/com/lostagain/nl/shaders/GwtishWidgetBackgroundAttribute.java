package com.lostagain.nl.shaders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

///------------------
// Create a custom attribute, see https://github.com/libgdx/libgdx/wiki/Material-and-environment
// See also: http://blog.xoppa.com/using-materials-with-libgdx/
/**
 * The presence of this parameter will cause the ConceptBeamShader to be used
 * */
public class GwtishWidgetBackgroundAttribute extends Attribute {
	public final static String Alias = "GwtishWidgetBackgroundAttribute";
	public final static long ID = register(Alias);

	public float glowWidth;
	public Color backColor;
	public Color borderColour;

	/**
	 * the radius of the curved corners
	 * Behaves strangly if less then 1f.
	 * (the background starts becoming the colour of the border)
	 */
	public float cornerRadius = 1f;
	


	/**
	 * The presence of this parameter will cause the ConceptBeamShader to be used
	 * @param width - width of beam
	 * @param  glowColor - its color
	 * @param corecolor - color of its core (normally white for a intense glow at the middle of the beam)
	 */
	public GwtishWidgetBackgroundAttribute (final float glowWidth,final Color backColor, final Color coreColor , final float cornerRadius) {

		super(ID);
		this.glowWidth = glowWidth;
		this.backColor = backColor.cpy();
		this.borderColour = coreColor.cpy();
		this.cornerRadius=cornerRadius;

	}

	@Override
	public Attribute copy () {
		return new GwtishWidgetBackgroundAttribute(glowWidth,backColor,borderColour,cornerRadius);
	}

	@Override
	protected boolean equals (Attribute other) {
		if (
				(((GwtishWidgetBackgroundAttribute)other).glowWidth == glowWidth) &&
				(((GwtishWidgetBackgroundAttribute)other).cornerRadius == cornerRadius) &&
				(((GwtishWidgetBackgroundAttribute)other).backColor == backColor) &&
				(((GwtishWidgetBackgroundAttribute)other).borderColour == borderColour) 
				)

		{
			return true;

		}
		return false;
	}

	@Override
	public int compareTo(Attribute o) { //not sure how to deal with this stuff yet

		//Ensuring attribute we are comparing too is the same type, if not we truth
		if (type != o.type) return type < o.type ? -1 : 1; //if not the same type and less then we return -1 else we return 1

		//if they are the same type we continue	
		double otherwidth = ((GwtishWidgetBackgroundAttribute)o).glowWidth; //just picking width here arbitarily for the moment
		//not sure yet when draw order will be important for glowing square backgrounds


		return glowWidth == otherwidth ? 0 : (glowWidth < otherwidth ? -1 : 1);

	}
}