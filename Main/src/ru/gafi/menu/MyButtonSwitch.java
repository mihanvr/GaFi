package ru.gafi.menu;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import ru.gafi.task.TaskManager;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 21.05.13
 * Time: 20:30
 */
public class MyButtonSwitch extends MyButton {

	private Sprite iconSprite1;
	private Sprite iconSprite2;
	private boolean switched;

	public MyButtonSwitch(TaskManager taskManager, Drawable drawable, Sprite iconSprite1, Sprite iconSprite2, float size) {
		super(taskManager, drawable, iconSprite1, size);
		this.iconSprite1 = iconSprite1;
		this.iconSprite2 = iconSprite2;
	}

	public void setSwitched(boolean value) {
		switched = value;
		setIcon(value ? iconSprite1 : iconSprite2);
	}

	public boolean isSwitched() {
		return switched;
	}

}
