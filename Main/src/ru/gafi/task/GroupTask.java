package ru.gafi.task;

import java.util.Collection;

import static ru.gafi.task.TaskUtils.*;

/**
 * User: Michael
 * Date: 10.06.13
 * Time: 16:53
 */
public class GroupTask extends SimpleTask {
	private Task[] tasks;

	public GroupTask(Task... tasks) {
		this.tasks = tasks;
	}

	public void start() {
		super.start();
		for (int i = 0; i < tasks.length; i++) {
			tasks[i].start();
		}
		checkOnFinish();
	}

	private void checkOnFinish() {
		for (int i = 0; i < tasks.length; i++) {
			if (!tasks[i].isFinished()) return;
		}
		finish();
	}

	public void update(float dt) {
		for (int i = 0; i < tasks.length; i++) {
			Task task = tasks[i];
			if (!task.isFinished()) {
				task.update(dt);
			}
		}
		checkOnFinish();
	}
}
