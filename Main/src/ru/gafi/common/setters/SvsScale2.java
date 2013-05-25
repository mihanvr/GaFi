package ru.gafi.common.setters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.gafi.animation.SValueSetter;

/**
 * User: Michael
 * Date: 21.05.13
 * Time: 11:49
 */
public class SvsScale2 implements SValueSetter {
	private Actor actor;

	public SvsScale2(Actor actor) {
		this.actor = actor;
	}

	@Override
	public void Set(float[] values) {
		actor.setScale(values[0], values[1]);
	}
}
