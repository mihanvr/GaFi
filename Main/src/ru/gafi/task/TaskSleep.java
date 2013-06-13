package ru.gafi.task;

/**
 * User: Michael
 * Date: 21.05.13
 * Time: 11:21
 */
public class TaskSleep implements Task {
	private float timeDuration;
	private float timeLeft;

	public TaskSleep(float timeDuration) {
		this.timeDuration = timeDuration;
	}

	public void start() {
		timeLeft = 0;
	}

	public void update(float dt) {
		timeLeft += dt;
	}

	@Override
	public boolean isFinished() {
		return timeLeft >= timeDuration;
	}
}
