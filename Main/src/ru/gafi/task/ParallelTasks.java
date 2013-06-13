package ru.gafi.task;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Michael
 * Date: 13.06.13
 * Time: 11:22
 */
public class ParallelTasks extends CancelableTask implements CollectionTask {

	private List<Task> newTasks = new ArrayList<>();
	private List<Task> newTasksBuffer = new ArrayList<>();
	private List<Task> startedTasks = new ArrayList<>();
	private List<Task> tasksMarkForRemove = new ArrayList<>();

	public void add(Task task) {
		newTasks.add(task);
	}

	@Override
	public void start() {
		startNewTasks();
	}

	@Override
	public void update(float dt) {
		startNewTasks();
		if (!startedTasks.isEmpty()) {
			for (Task task : startedTasks) {
				task.update(dt);
				if (task.isFinished()) {
					tasksMarkForRemove.add(task);
				}
			}
		}
		if (!tasksMarkForRemove.isEmpty()) {
			for (Task task : tasksMarkForRemove) {
				startedTasks.remove(task);
			}
			tasksMarkForRemove.clear();
		}
	}

	private void startNewTasks() {
		if (!newTasks.isEmpty()) {
			newTasksBuffer.addAll(newTasks);
			newTasks.clear();
			for (Task task : newTasksBuffer) {
				task.start();
				if (!task.isFinished()) {
					startedTasks.add(task);
				}
			}
			newTasksBuffer.clear();
		}
	}

	@Override
	public void clear() {
		newTasks.clear();
		startedTasks.clear();
	}

	public boolean isEmpty() {
		return newTasks.isEmpty() && startedTasks.isEmpty();
	}
}
