package com.lostagain.nl.me.models;

public interface Animating {
	/**
	 * Updates the look of the thing being animated
	 * @param deltatime = The time in seconds since the last render
	 */
	public void updateAnimationFrame(float deltatime);
}
