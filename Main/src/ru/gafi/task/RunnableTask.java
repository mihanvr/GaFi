package ru.gafi.task;

/**
 * User: Michael
 * Date: 21.05.13
 * Time: 11:22
 */
public class RunnableTask extends InstantTask {

	private Runnable run;

	public RunnableTask(Runnable run) {
		this.run = run;
	}

	@Override
	public void start() {
		run.run();
	}

}
