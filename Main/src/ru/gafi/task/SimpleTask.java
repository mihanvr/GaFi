package ru.gafi.task;

/**
 * User: Michael
 * Date: 10.06.13
 * Time: 16:17
 */
public abstract class SimpleTask implements Task {
	private boolean finished;

	@Override
	public void start() {
		finished = false;
	}

	protected void finish() {
		finished = true;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}
}
