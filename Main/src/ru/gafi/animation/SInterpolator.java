package ru.gafi.animation;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 19:58
 */
public interface SInterpolator {
	public float[] interpolate(float[] from, float[] to, float t, float[] out);

	public static SInterpolator def = new SInterpolator() {
		@Override
		public float[] interpolate(float[] from, float[] to, float t, float[] out) {
			for (int i = 0; i < from.length; i++) {
				out[i] = from[i] + (to[i] - from[i]) * t;
			}
			return out;
		}
	};
}
