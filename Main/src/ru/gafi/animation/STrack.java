package ru.gafi.animation;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 19:56
 */
public class STrack {

	private STimeCurve timeCurve;
	private SValueSetter valueSetter;

	public STrack(STimeCurve timeCurve, SValueSetter valueSetter) {
		this.timeCurve = timeCurve;
		this.valueSetter = valueSetter;
	}

	public void setTime(float time) {
		valueSetter.Set(timeCurve.get(time));
	}

	public float length() {
		return timeCurve.length();
	}
}

