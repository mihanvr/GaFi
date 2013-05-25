package ru.gafi.menu;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import ru.gafi.Main;
import ru.gafi.Settings;
import ru.gafi.common.Util;
import ru.gafi.task.TaskManager;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 20.05.13
 * Time: 19:22
 */
public class VMenu extends Group {
	private MyButtonSwitch btnStretch;
	private MyButton btnReset;
	private MyButton btnUndo;
	private MyButton btnRedo;
	private MyButton btnExit;
	private Actor background;
	private float buttonSize;
	private float gap;
//	private final float defScale;

	public VMenu(Skin skin, Main main, Settings settings, TaskManager taskManager) {

//		defScale = skin.get("defScale", Float.class);
		gap = skin.get("menu.gap", Float.class);
		buttonSize = skin.get("menu.button.size", Float.class);
		background = new Button(skin.getDrawable("menu.background"));
		btnStretch = new MyButtonSwitch(taskManager,
				skin.getDrawable("menu.button.stretch"),
				skin.getSprite("menu.button.stretch.icon1"),
				skin.getSprite("menu.button.stretch.icon2"), buttonSize);
		btnReset = new MyButton(taskManager,
				skin.getDrawable("menu.button.reset"),
				skin.getSprite("menu.button.reset.icon"), buttonSize);
		btnUndo = new MyButton(taskManager,
						skin.getDrawable("menu.button.undo"),
						skin.getSprite("menu.button.undo.icon"), buttonSize);
		btnRedo = new MyButton(taskManager,
						skin.getDrawable("menu.button.redo"),
						skin.getSprite("menu.button.redo.icon"), buttonSize);
		btnExit = new MyButton(taskManager,
						skin.getDrawable("menu.button.exit"),
						skin.getSprite("menu.button.exit.icon"), buttonSize);

		btnReset.addListener(resetListener(main));
		btnUndo.addListener(undoListener(main));
		btnRedo.addListener(redoListener(main));
		btnExit.addListener(exitListener(main));
		btnStretch.addListener(stretchListener(settings));

		btnStretch.setSwitched(settings.isStretch());
		settings.addChangeListener(new Settings.SettingsListener() {
			public void onChange(Settings settings) {
				if (btnStretch.isSwitched() != settings.isStretch()) {
					btnStretch.setSwitched(settings.isStretch());
				}
			}
		});

		addActor(background);
		addActor(btnReset);
		addActor(btnStretch);
		addActor(btnUndo);
		addActor(btnRedo);
		addActor(btnExit);

		Util.setTransform(this, false);
	}

	private EventListener undoListener(final Main main) {
		return new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				main.undo();
				return true;
			}
		};
	}

	private EventListener redoListener(final Main main) {
		return new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				main.redo();
				return true;
			}
		};
	}

	private EventListener exitListener(final Main main) {
		return new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				main.exit();
				return true;
			}
		};
	}

	private EventListener resetListener(final Main main) {
		return new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				main.resetGame();
				return true;
			}
		};
	}

	private EventListener stretchListener(final Settings settings) {
		return new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				settings.setStretch(!settings.isStretch());
				return true;
			}
		};
	}

	private EventListener tempListener(final Main main) {
		return new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				main.iWantWin();
				return true;
			}
		};
	}

	public void setViewRect(float x, float y, float width, float height) {
		setPosition(x, y);
		setSize(width, height);
		background.setSize(width, height);
		btnExit.setPosition(width - buttonSize - gap, gap);
		setPositionFromLeft(btnUndo, 0);
		setPositionFromLeft(btnRedo, 1);
		setPositionToCenter(btnReset);
		setPositionToCenterBetween(btnStretch, btnRedo.getX() + buttonSize, btnReset.getX());
	}

	private void setPositionFromLeft(Actor actor, int index) {
		actor.setPosition((index + 1) * gap + index * buttonSize, gap);
	}

	private void setPositionToCenter(Actor actor) {
		actor.setPosition(getWidth() / 2f - buttonSize / 2f, gap);
	}

	private void setPositionToCenterBetween(Actor actor, float x0, float x1) {
		actor.setPosition(x0 + (x1 - x0) / 2f - buttonSize / 2f, gap);
	}
}
