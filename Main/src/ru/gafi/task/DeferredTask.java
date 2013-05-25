package ru.gafi.task;

/**
 * User: Michael
 * Date: 22.05.13
 * Time: 10:12
 */
public class DeferredTask extends Task {

	private float timeLeft;
	private Runnable runnable;

	public DeferredTask(float timeLeft, Runnable runnable) {
		this.timeLeft = timeLeft;
		this.runnable = runnable;
	}

	@Override
	public void start() {

	}

	@Override
	public void update(float dt) {
		timeLeft -= dt;
		if (timeLeft <= 0) {
			runnable.run();
		}
	}
}
