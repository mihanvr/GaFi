package ru.gafi.task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 20.05.13
 * Time: 19:40
 */
public class TaskManager {
	private LinkedList<Task> tasksQueue = new LinkedList<>();
	private List<Task> tasks = new ArrayList<>();
	private List<Task> tasksMarkForRemove = new ArrayList<>();
	private Task currentQueueTask;

	public void addTaskInQueue(Task task) {
		tasksQueue.addLast(task);
	}

	public void startTask(Task task) {
		tasks.add(task);
	}

	public void update(float dt) {
		updateTaskInQueue(dt);
		updateTasks(dt);
	}

	void updateTasks(float dt) {
		for (Task task : tasks) {
			task.update(dt);
			if (task.isFinished()) {
				tasksMarkForRemove.add(task);
			}
		}
		if (!tasksMarkForRemove.isEmpty()) {
			for (Task task : tasksMarkForRemove) {
				tasks.remove(task);
			}
			tasksMarkForRemove.clear();
		}
	}

	void updateTaskInQueue(float dt) {
		if (currentQueueTask != null) {
			currentQueueTask.update(dt);
			if (currentQueueTask.isFinished()) {
				nextQueueTask();
			}
		} else {
			nextQueueTask();
		}
	}

	private void nextQueueTask() {
		Task nextTask = null;
		while (!tasksQueue.isEmpty() && nextTask == null) {
			nextTask = tasksQueue.getFirst();
			tasksQueue.removeFirst();
			nextTask.start();
			if (nextTask.isFinished()) {
				nextTask = null;
			}
		}
		currentQueueTask = nextTask;
	}

	public boolean isEmpty() {
		return tasks.isEmpty() && tasksQueue.isEmpty() && currentQueueTask == null;
	}
}


