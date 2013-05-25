package ru.gafi.task;

/**
 * User: Michael
 * Date: 21.05.13
 * Time: 11:21
 */
public class TaskSleep extends Task {
	private float timeLeft;

	public TaskSleep(float sleepTime) {
		timeLeft = sleepTime;
	}

	public void start() {
	}

	public void update(float dt) {
		timeLeft -= dt;
		if (timeLeft <= 0) finish();
	}
}
