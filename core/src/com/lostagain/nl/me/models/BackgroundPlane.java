package com.lostagain.nl.me.models;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * A coloured plane that blocks the clicks hitting stuff under it
 * @author Tom
 *
 */
public class BackgroundPlane implements hitable {

	@Override
	public Vector3 getCenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PosRotScale getTransform() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fireTouchDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireTouchUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLastHitsRange(float range) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getLastHitsRange() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isBlocker() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rayHits(Ray ray) {
		// TODO Auto-generated method stub
		return false;
	}

}
