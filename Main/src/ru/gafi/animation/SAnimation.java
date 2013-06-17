package ru.gafi.animation;

import ru.gafi.task.CancelableTask;
import ru.gafi.task.TaskManager;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 20:16
 */
public class SAnimation {
	private SClip _playingClip;
	private Map<String, SClip> dict = new HashMap<>();
	private TaskManager taskManager;
	private Updater updater;

	public SAnimation(TaskManager taskManager) {
		this.taskManager = taskManager;
	}

	public void addClip(SClip clip) {
		dict.put(clip.name, clip);
	}

	public SClip getClip(String name) {
		return dict.get(name);
	}

	public boolean play(String name) {
		_playingClip = getClip(name);
		if (_playingClip != null) {
			_playingClip.reset();
			taskManager.addParallelTask(updater = new Updater());
			return true;
		}
		return false;
	}

	public void stop() {
		_playingClip = null;
		if (updater != null) {
			updater.stop();
		}
	}

	private void update(float dt) {
		if (_playingClip != null) {
			_playingClip.incTime(dt);
			if (!_playingClip.isPlaying()) {
				stop();
			}
		} else {
			stop();
		}
	}

	private class Updater extends CancelableTask {

		@Override
		public void update(float dt) {
			SAnimation.this.update(dt);
		}

		@Override
		protected void startAfterCheck() {}
	}
}
