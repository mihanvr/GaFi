package ru.gafi.task;

/**
 * User: Michael
 * Date: 10.06.13
 * Time: 16:33
 */
public abstract class CancelableTask extends SimpleTask {

	public void stop() {
		finish();
	}

}
