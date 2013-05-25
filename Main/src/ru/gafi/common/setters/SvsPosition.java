package ru.gafi.common.setters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.gafi.animation.SValueSetter;

/**
 * User: Michael
 * Date: 21.05.13
 * Time: 12:47
 */
public class SvsPosition implements SValueSetter {

	private Actor actor;

	public SvsPosition(Actor actor) {
		this.actor = actor;
	}

	@Override
	public void Set(float[] values) {
		actor.setPosition(values[0], values[0]);
	}
}
