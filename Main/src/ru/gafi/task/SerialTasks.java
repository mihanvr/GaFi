package ru.gafi.task;

/**
 * User: Michael
 * Date: 10.06.13
 * Time: 16:16
 */
public class SerialTasks implements Task {
	private Task[] tasks;
	private Task currentTask;
	private int index;

	public SerialTasks(Task... tasks) {
		this.tasks = tasks;
	}

	public void start() {
		index = 0;
		nextTask();
	}

	private void nextTask() {
		Task nextTask = null;
		while (index < tasks.length && nextTask == null) {
			nextTask = tasks[index++];
			nextTask.start();
			if (nextTask.isDone()) {
				nextTask = null;
			}
		}
		currentTask = nextTask;
	}

	public void update(float dt) {
		currentTask.update(dt);
		if (currentTask.isDone()) {
			nextTask();
		}
	}

	public boolean isDone() {
		return currentTask == null;
	}
}
