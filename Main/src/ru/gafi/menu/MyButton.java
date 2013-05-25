package ru.gafi.menu;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ru.gafi.animation.SClip;
import ru.gafi.animation.SInterpolator;
import ru.gafi.animation.SKeyFrame;
import ru.gafi.common.setters.SvsPosition2;
import ru.gafi.game.AnimatedActor;
import ru.gafi.task.TaskManager;

import static ru.gafi.common.Util.createTrack;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 16:55
 */
public class MyButton extends AnimatedActor {

	private float ANIMATION_TIME = 0.2f;

	private Group group;
	private Button button;
	private Image image;
	private TextureRegionDrawable imageDrawable;

	public MyButton(TaskManager taskManager, Drawable drawable, Sprite iconSprite, float size) {
		super(taskManager);
		group = new Group();
		button = new Button(drawable);
		image = new Image(imageDrawable = new TextureRegionDrawable(iconSprite));
		button.setTouchable(Touchable.disabled);
		image.setTouchable(Touchable.disabled);
		group.setTouchable(Touchable.disabled);
		setSize(size, size);
		button.setSize(size, size);
		image.setPosition(size / 2 - iconSprite.getWidth() / 2, size / 2 - iconSprite.getHeight() / 2);
		group.addActor(button);
		group.addActor(image);
		addActor(group);

		initAnimations();
		initListeners();
	}

	private void initListeners() {
		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				playAnimation("touchDown");
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				playAnimation("touchUp");
			}
		});
	}

	private void initAnimations() {
		SClip touchDownClip = createTouchDownClip();
		SClip touchUpClip = createTouchUpClip();

		animation.addClip(touchDownClip);
		animation.addClip(touchUpClip);
	}

	private SClip createTouchDownClip() {
		SClip clip = new SClip("touchDown");
		float seek = button.getWidth() * 0.05f;
		float tx = seek;
		float ty = -seek;
		clip.addTrack(createTrack(SInterpolator.def, new SvsPosition2(group), new SKeyFrame[]{
				new SKeyFrame(0f, 0f, 0f),
				new SKeyFrame(ANIMATION_TIME, tx, ty)
		}));
		return clip;
	}

	private SClip createTouchUpClip() {
		SClip clip = new SClip("touchUp");
		float seek = button.getWidth() * 0.05f;
		float tx = seek;
		float ty = -seek;
		clip.addTrack(createTrack(SInterpolator.def, new SvsPosition2(group), new SKeyFrame[]{
				new SKeyFrame(0f, tx, ty),
				new SKeyFrame(ANIMATION_TIME, 0f, 0f)
		}));
		return clip;
	}

	protected void setIcon(Sprite iconSprite) {
		imageDrawable.setRegion(iconSprite);
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		button.setSize(width, height);
		image.setPosition(width / 2 - image.getWidth() / 2, height / 2 - image.getHeight() / 2);
	}
}
