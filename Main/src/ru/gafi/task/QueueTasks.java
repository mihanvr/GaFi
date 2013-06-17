package ru.gafi.task;

import java.util.LinkedList;

/**
 * User: Michael
 * Date: 13.06.13
 * Time: 9:19
 */
public class QueueTasks extends CancelableTask implements CollectionTasks {
	private LinkedList<Task> tasksQueue = new LinkedList<>();
	private Task currentQueueTask;

	public void add(Task task) {
		tasksQueue.add(task);
	}

	@Override
	public void startAfterCheck() {
		nextQueueTask();
	}

	@Override
	public void update(float dt) {
		if (currentQueueTask != null) {
			currentQueueTask.update(dt);
			if (currentQueueTask.isDone()) {
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
			if (nextTask.isDone()) {
				nextTask = null;
			}
		}
		currentQueueTask = nextTask;
	}

	@Override
	public void clear() {
		tasksQueue.clear();
		currentQueueTask = null;
	}

	public boolean isEmpty() {
		return tasksQueue.isEmpty() && currentQueueTask == null;
	}
}
