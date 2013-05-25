package ru.gafi.animation;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 19:55
 */
public class SClip {
	private EventTimeLine eventTimeLine = new EventTimeLine();
	private List<STrack> tracks = new ArrayList<>();
	private float _length;
	private float _time;
	private SWrapMode _wrapMode = SWrapMode.Once;
	private boolean dirtyTracks;
	private boolean includeStartTime = true;
	public String name;

	public SClip(String name) {
		this.name = name;
	}

	public SWrapMode getWrapMode() {
		return _wrapMode;
	}

	public void setWrapMode(SWrapMode value) {
		_wrapMode = value;
		_time = normalizedTime(_time);
	}

	public float length() {
		if (dirtyTracks) {
			updateLength();
			dirtyTracks = false;
		}
		return _length;
	}

	private void _setTime(float time) {
		float newTime = normalizedTime(time);
		if (newTime != _time || includeStartTime) {
			_time = newTime;
			updateTracks(_time);
		}
	}

	public void setTime(float time) {
		includeStartTime = true;
		_setTime(time);
	}

	public float getTime() {
		return _time;
	}

	public boolean isPlaying() {
		switch (_wrapMode) {
			case Once:
				return _time < length();
			default:
				return true;
		}
	}

	public void reset() {
		float resetTime;
		switch (_wrapMode) {
			default:
				resetTime = 0;
				break;
		}
		setTime(resetTime);
	}

	public void incTime(float dt) {
		float prevTime = _time;
		float nextTime = _time + dt;
		_setTime(nextTime);
		executeEvents(prevTime, nextTime);
		includeStartTime = false;
	}

	private void executeEvents(float prevTime, float nextTime) {
		if (eventTimeLine.getEventsCount() == 0) return;
		if (_wrapMode == SWrapMode.Once) {
			if (prevTime < length()) {
				eventTimeLine.executeEventsAtRange(prevTime, Math.min(nextTime, length()), includeStartTime);
			}
		}
		if (_wrapMode == SWrapMode.Loop) {
			while (nextTime > prevTime) {
				if (nextTime > length()) {
					eventTimeLine.executeEventsAtRange(prevTime, length(), includeStartTime);
					nextTime -= length();
					prevTime = 0;
				} else {
					eventTimeLine.executeEventsAtRange(prevTime, nextTime, includeStartTime);
					prevTime = nextTime;
				}
			}
		}
		if (_wrapMode == SWrapMode.PingPong) {
			while (nextTime > prevTime) {
				if (prevTime < length()) {
					float finishTime = Math.min(nextTime, length());
					eventTimeLine.executeEventsAtRange(prevTime, finishTime, includeStartTime);
					prevTime = finishTime;
				} else {
					if (nextTime > 2 * length()) {
						eventTimeLine.executeEventsAtRange(2 * length() - prevTime, 0, includeStartTime);
						prevTime = 0;
						nextTime -= 2 * length();
					} else {
						eventTimeLine.executeEventsAtRange(2 * length() - prevTime, 2 * length() - nextTime, includeStartTime);
						prevTime = nextTime;
					}
				}
			}
		}
	}

	private float normalizedTime(float time) {
		switch (_wrapMode) {
			case Loop:
				return time % length();
			case PingPong:
				return time % (2 * length());
			default:
				return Math.min(time, length());
		}
	}

	private float WrapTime(float time) {
		switch (_wrapMode) {
			case PingPong:
				if (time <= length()) return time;
				return length() * 2 - time;
			default:
				return time;
		}
	}

	public void addTrack(STrack track) {
		tracks.add(track);
		dirtyTracks = true;
	}

	public void addEvent(SEvent e) {
		eventTimeLine.addEvent(e);
	}

	public void updateLength() {
		_length = 0;
		for (STrack track : tracks) {
			_length = Math.max(_length, track.length());
		}
		_length = Math.max(_length, eventTimeLine.length());
	}

		/* 0 <= startTime <= length
		 * 0 <= finishTime <= length */

	private void updateTracks(float time) {
		float wrapTime = WrapTime(time);
		if (tracks.isEmpty()) return;
		for (int i = 0; i < tracks.size(); i++) {
			STrack t = tracks.get(i);
			t.setTime(wrapTime);
		}
	}
}
