package ru.gafi.task;

/**
 * User: Michael
 * Date: 10.06.13
 * Time: 16:33
 */
public abstract class CancelableTask extends SimpleTask {

	private boolean canceled;

	@Override
	public final void start() {
		super.start();
		if (!canceled) {
			startAfterCheck();
		} else {
			done();
		}
	}

	protected abstract void startAfterCheck();

	public void stop() {
		super.done();
		canceled = true;
	}

	@Override
	protected void done() {
		super.done();
		canceled = false;
	}
}
