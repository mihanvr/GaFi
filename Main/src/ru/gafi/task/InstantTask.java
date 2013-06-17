package ru.gafi.task;

/**
 * User: Michael
 * Date: 10.06.13
 * Time: 16:22
 */
public abstract class InstantTask implements Task {
	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public void update(float dt) {

	}
}
