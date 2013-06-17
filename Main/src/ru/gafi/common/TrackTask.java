package ru.gafi.common;

import ru.gafi.animation.STrack;
import ru.gafi.task.SimpleTask;

/**
 * User: Michael
 * Date: 21.05.13
 * Time: 10:48
 */
public class TrackTask extends SimpleTask {

	private float time;
	private STrack track;

	public TrackTask(STrack track) {
		this.track = track;
	}

	public void start() {
		track.setTime(0);
	}

	public void update(float dt) {
		time += dt;
		if (time >= track.length()) {
			track.setTime(track.length());
			done();
		} else {
			track.setTime(time);
		}
	}
}
