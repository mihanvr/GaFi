package ru.gafi.task;

import java.util.Collection;

/**
 * User: Michael
 * Date: 11.06.13
 * Time: 12:45
 */
public class TaskUtils {

	public static Task[] toArray(Collection<? extends Task> tasks) {
		if (tasks == null) {
			return new Task[0];
		}
		Task[] array = new Task[tasks.size()];
		tasks.toArray(array);
		return array;
	}

}
