package ru.gafi.task;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 19:40
 */
public class TaskManager extends CancelableTask {

	private ParallelTasks parallelTasks = new ParallelTasks();
	private QueueTask queueTask = new QueueTask();

	public void addTaskInQueue(Task task) {
		queueTask.add(task);
	}

	public void addParallelTask(Task task) {
		parallelTasks.add(task);
	}

	@Override
	public void start() {
		super.start();
		queueTask.start();
		parallelTasks.start();
	}

	public void update(float dt) {
		queueTask.update(dt);
		parallelTasks.update(dt);
	}

	public void clear() {
		queueTask.clear();
		parallelTasks.clear();
	}

	public boolean isEmpty() {
		return parallelTasks.isEmpty() && queueTask.isEmpty();
	}
}


