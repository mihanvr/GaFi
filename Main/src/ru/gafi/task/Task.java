package ru.gafi.task;

public interface Task {
	public void start();

	public void update(float dt);

	public boolean isDone();
}
