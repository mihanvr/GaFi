package ru.gafi.game;

import com.badlogic.gdx.scenes.scene2d.Group;
import ru.gafi.animation.SAnimation;
import ru.gafi.animation.SClip;
import ru.gafi.task.TaskManager;

/**
 * User: Michael
 * Date: 21.05.13
 * Time: 11:26
 */
public abstract class AnimatedActor extends Group {
	public SAnimation animation;

	protected AnimatedActor(TaskManager taskManager) {
		animation = new SAnimation(taskManager);
	}

	public float playAnimation(String name) {
		SClip clip = animation.getClip(name);
		animation.play(name);
		return clip.length();
	}

}
