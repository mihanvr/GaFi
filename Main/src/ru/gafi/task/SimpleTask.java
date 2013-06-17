package ru.gafi.task;

/**
 * User: Michael
 * Date: 10.06.13
 * Time: 16:17
 */
public abstract class SimpleTask implements Task {
	private boolean _done;

	@Override
	public void start() {
		_done = false;
	}

	protected void done() {
		_done = true;
	}

	@Override
	public boolean isDone() {
		return _done;
	}
}
