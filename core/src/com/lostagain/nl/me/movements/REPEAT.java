package com.lostagain.nl.me.movements;

import com.badlogic.gdx.math.Matrix4;
import com.lostagain.nl.me.movements.Movement.MovementTypes;

/**
 * A dummy movement telling the sequence to repeat
 * @author Tom
 *
 */
public class REPEAT extends Movement {
	public REPEAT() {
		super(new Matrix4().setToTranslation(0, 0, 0), 100000); //note; should never fire anyway, this whole thing just acts as a flag to repeat
		currenttype = MovementTypes.REPEAT;
	}

}
