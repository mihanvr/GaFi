package ru.gafi.animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 20.05.13
 * Time: 19:57
 */
public class STimeCurve {
	private SInterpolator interpolator;
	private List<SKeyFrame> keyFrames = new ArrayList<>();
	private float[] temp;

	public STimeCurve(SInterpolator interpolator) {
		check(interpolator);
		this.interpolator = interpolator;
	}

	public void addKeyFrame(SKeyFrame keyFrame) {
		if (keyFrames.size() > 0) {
			if (keyFrame.time <= length()) {
				throw new RuntimeException(String.format("adding keyFrame.time (%s) <= previous keyFrame.time (%s), but must be >", keyFrame.time, length()));
			}
		}
		if (keyFrames.size() == 0) {
			temp = new float[keyFrame.values.length];
		}
		keyFrames.add(keyFrame);
	}

	public void addKeyFrame(float time, float... values) {
		addKeyFrame(new SKeyFrame(time, values));
	}

	public float length() {
		if (keyFrames.isEmpty()) return 0f;
		return lastKeyFrame().time;
	}

	private SKeyFrame lastKeyFrame() {
		return keyFrames.get(keyFrames.size() - 1);
	}

	private void check(SInterpolator sInterpolator) {
		if (sInterpolator == null) {
			throw new NullPointerException();
		}
	}

	public float[] get(float time) {
		if (keyFrames.isEmpty()) throw new RuntimeException("keyFrames is empty");
		if (time <= keyFrames.get(0).time) return keyFrames.get(0).values;
		if (time >= lastKeyFrame().time) return lastKeyFrame().values;
		int index = findLeftIndex(time);
		SKeyFrame leftKeyFrame = keyFrames.get(index);
		SKeyFrame rightKeyFrame = keyFrames.get(index + 1);
		float currentTime = time - leftKeyFrame.time;
		float timeInterval = rightKeyFrame.time - leftKeyFrame.time;
		return interpolator.interpolate(leftKeyFrame.values, rightKeyFrame.values, currentTime / timeInterval, temp);
	}

	private int findLeftIndex(float time) {
		for (int i = 0; i < keyFrames.size() - 1; i++) {
			if (keyFrames.get(i + 1).time > time) return i;
		}
		return keyFrames.size();
	}

}
