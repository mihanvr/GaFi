package ru.gafi.common.setters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.gafi.animation.SValueSetter;

/**
 * User: Michael
 * Date: 21.05.13
 * Time: 16:18
 */
public class SvsSize2 implements SValueSetter {
	private Actor actor;

	public SvsSize2(Actor actor) {
		this.actor = actor;
	}

	@Override
	public void Set(float[] values) {
		actor.setSize(values[0], values[1]);
	}
}
