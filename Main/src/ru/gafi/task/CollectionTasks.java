package ru.gafi.task;

/**
 * User: Michael
 * Date: 13.06.13
 * Time: 11:47
 */
public interface CollectionTasks extends Task {
	public void add(Task task);
	public void clear();
	public boolean isEmpty();
}
