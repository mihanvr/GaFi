package ru.gafi.task;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Michael
 * Date: 13.06.13
 * Time: 11:22
 */
public class ParallelTasks extends CancelableTask implements CollectionTasks {

	private List<Task> newTasks = new ArrayList<>();
	private List<Task> newTasksBuffer = new ArrayList<>();
	private List<Task> updateTasks = new ArrayList<>();
	private List<Task> tasksMarkForRemove = new ArrayList<>();

	@Override
	protected void startAfterCheck() {}

	public void add(Task task) {
		newTasks.add(task);
	}

	@Override
	public void update(float dt) {
		updateTasks(dt);
		startNewTasks();
		removeDoneTasks();
	}

	private void removeDoneTasks() {
		if (!tasksMarkForRemove.isEmpty()) {
			for (Task task : tasksMarkForRemove) {
				updateTasks.remove(task);
			}
			tasksMarkForRemove.clear();
		}
	}

	private void updateTasks(float dt) {
		if (!updateTasks.isEmpty()) {
			for (Task task : updateTasks) {
				task.update(dt);
				if (task.isDone()) {
					tasksMarkForRemove.add(task);
				}
			}
		}
	}

	private void startNewTasks() {
		if (!newTasks.isEmpty()) {
			newTasksBuffer.addAll(newTasks);
			newTasks.clear();
			for (Task task: newTasksBuffer) {
				task.start();
				if (!task.isDone()) {
					updateTasks.add(task);
				}
			}
			newTasksBuffer.clear();
		}
	}

	@Override
	public void clear() {
		newTasks.clear();
		updateTasks.clear();
	}

	public boolean isEmpty() {
		return newTasks.isEmpty() && updateTasks.isEmpty();
	}
}
