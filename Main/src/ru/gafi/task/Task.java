package ru.gafi.task;

public abstract class Task {
	private boolean _finished;

	public abstract void start();

	public abstract void update(float dt);

	protected void finish() {
		_finished = true;
	}

	public boolean isFinished() {
		return _finished;
	}
}
