package ru.gafi.task;

import java.util.LinkedList;

/**
 * User: Michael
 * Date: 13.06.13
 * Time: 9:19
 */
public class QueueTask extends CancelableTask implements CollectionTask {
	private LinkedList<Task> tasksQueue = new LinkedList<>();
	private Task currentQueueTask;

	public void add(Task task) {
		tasksQueue.add(task);
	}

	@Override
	public void start() {
		nextQueueTask();
	}

	@Override
	public void update(float dt) {
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

	@Override
	public void clear() {
		tasksQueue.clear();
		currentQueueTask = null;
	}

	public boolean isEmpty() {
		return tasksQueue.isEmpty() && currentQueueTask == null;
	}
}
