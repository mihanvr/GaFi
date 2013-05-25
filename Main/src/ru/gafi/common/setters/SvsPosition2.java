package ru.gafi.common.setters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.gafi.animation.SValueSetter;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 21.05.13
 * Time: 21:01
 */
public class SvsPosition2 implements SValueSetter {

	private Actor actor;

	public SvsPosition2(Actor actor) {
		this.actor = actor;
	}

	@Override
	public void Set(float[] values) {
		actor.setPosition(values[0], values[1]);
	}
}
