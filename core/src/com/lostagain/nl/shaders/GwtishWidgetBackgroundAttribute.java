package com.lostagain.nl.shaders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

///------------------
// Create a custom attribute, see https://github.com/libgdx/libgdx/wiki/Material-and-environment
// See also: http://blog.xoppa.com/using-materials-with-libgdx/
/**
 * Controlls the background of all widgets.
 * This controlls the background and border.
 * NOTE: This overrides any ColorAttribute setting for the background. Use this .backColor to pick a background ONLY
 * */
public class GwtishWidgetBackgroundAttribute extends Attribute {
	public final static String Alias = "GwtishWidgetBackgroundAttribute";
	public final static long ID = register(Alias);

	public float borderWidth;
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
	 * @param  borderColour - color of its core (normally white for a intense glow at the middle of the beam)
	 */
	public GwtishWidgetBackgroundAttribute (final float glowWidth,final Color backColor, final Color borderColour , final float cornerRadius) {

		super(ID);
		this.borderWidth = glowWidth;
		this.backColor = backColor.cpy();
		this.borderColour = borderColour.cpy();
		this.cornerRadius=cornerRadius;

	}

	public float Overall_Opacity_Multiplier = 1f;
	
	public float getOverall_Opacity_Multiplier() {
		return Overall_Opacity_Multiplier;
	}
	
	/**
	 * This value will be multiplied by the alpha channel of any get...Color() method used.
	 * The idea is to use it as a temp value to allow BlendingAttribute opacity to effect the text in the shader too.
	 * REMEMBER TO RESET THIS VALUE TO 1 BY DEFAULT IF NO BLENDING IS SET
	 * @param overall_Opacity_Multiplier
	 **/
	public void setOverall_Opacity_Multiplier(float overall_Opacity_Multiplier) {
		Overall_Opacity_Multiplier = overall_Opacity_Multiplier;
	}
	
	
	@Override
	public Attribute copy () {
		return new GwtishWidgetBackgroundAttribute(borderWidth,backColor,borderColour,cornerRadius);
	}

	@Override
	protected boolean equals (Attribute other) {
		if (
				(((GwtishWidgetBackgroundAttribute)other).borderWidth == borderWidth      ) &&
				(((GwtishWidgetBackgroundAttribute)other).cornerRadius == cornerRadius) &&
				(((GwtishWidgetBackgroundAttribute)other).backColor == backColor      ) &&
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
		double otherwidth = ((GwtishWidgetBackgroundAttribute)o).borderWidth; //just picking width here arbitrarily for the moment
		//not sure yet when draw order will be important for glowing square backgrounds


		return borderWidth == otherwidth ? 0 : (borderWidth < otherwidth ? -1 : 1);

	}

	public Color getBackColor() {
		
		Color effectiveBackColour = backColor.cpy();
		effectiveBackColour.a = effectiveBackColour.a * Overall_Opacity_Multiplier;
		
		return effectiveBackColour;
	}

	public Color getBorderColour() {
		Color effectiveBorderColour = borderColour.cpy();
		effectiveBorderColour.a = effectiveBorderColour.a * Overall_Opacity_Multiplier;
		
		return effectiveBorderColour;
	}

	
	
}