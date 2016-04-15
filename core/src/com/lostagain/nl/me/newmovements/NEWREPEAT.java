package com.lostagain.nl.me.newmovements;

import com.lostagain.nl.GWTish.PosRotScale;

/**
 * A dummy movement telling the sequence to repeat
 * @author Tom
 *
 */
public class NEWREPEAT extends NewMovement {
	public NEWREPEAT() {
		super(new PosRotScale(), 100000); //note; should never fire anyway, this whole thing just acts as a flag to repeat
		currenttype = MovementTypes.REPEAT;
	}

}
