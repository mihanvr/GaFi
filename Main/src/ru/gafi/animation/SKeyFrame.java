package ru.gafi.animation;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 19:59
 */
public class SKeyFrame {
	public float time;
	public float[] values;

	public SKeyFrame(float time, float... values) {
		this.time = time;
		this.values = values;
	}
}
