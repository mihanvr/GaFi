package ru.gafi.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ru.gafi.animation.SClip;
import ru.gafi.animation.SInterpolator;
import ru.gafi.animation.SKeyFrame;
import ru.gafi.common.Util;
import ru.gafi.common.setters.SvsPosition;
import ru.gafi.common.setters.SvsScale;
import ru.gafi.task.TaskManager;

import static ru.gafi.common.Util.createTrack;

/**
 * User: Michael
 * Date: 21.05.13
 * Time: 10:39
 */
public class VFigure extends AnimatedActor {

	private Image image;
	private TextureRegionDrawable textureRegionDrawable;

	protected VFigure(TaskManager taskManager, float size) {
		super(taskManager);
		textureRegionDrawable = new TextureRegionDrawable();
		image = new Image(textureRegionDrawable);
		setSize(size, size);
		initAnimations();
		addActor(image);
		setTouchable(Touchable.disabled);
		Util.setTransform(this, false);
	}

	public void setSprite(Sprite sprite) {
		textureRegionDrawable.setRegion(sprite);
		image.setSize(sprite.getWidth(), sprite.getHeight());
		image.setPosition(getWidth() / 2f - sprite.getWidth() / 2f, getHeight() / 2 - sprite.getHeight() / 2f);
	}

	protected void initAnimations() {
		SClip clipHide = createHideClip();
		SClip clipShow = createShowClip();
		SClip clipSelect = createSelectClip();
		animation.addClip(clipHide);
		animation.addClip(clipShow);
		animation.addClip(clipSelect);
	}

	private SClip createHideClip() {
		SClip clip = new SClip("Hide");
		float seek = getWidth() / 2f;
		clip.addTrack(createTrack(SInterpolator.def, new SvsScale(image), new SKeyFrame[]{
				new SKeyFrame(0f, 1f),
				new SKeyFrame(0.3f, 0f),
		}));
		clip.addTrack(createTrack(SInterpolator.def, new SvsPosition(image), new SKeyFrame[]{
				new SKeyFrame(0f, 0f),
				new SKeyFrame(0.3f, seek),
		}));
		return clip;
	}

	private SClip createShowClip() {
		SClip clip = new SClip("Show");
		float seek = getWidth() / 2f;
		clip.addTrack(createTrack(SInterpolator.def, new SvsScale(image), new SKeyFrame[]{
				new SKeyFrame(0f, 0f),
				new SKeyFrame(0.3f, 1f),
		}));
		clip.addTrack(createTrack(SInterpolator.def, new SvsPosition(image), new SKeyFrame[]{
				new SKeyFrame(0f, seek),
				new SKeyFrame(0.3f, 0f),
		}));
		return clip;
	}

	private SClip createSelectClip() {
		SClip clip = new SClip("Select");
		float seek = getWidth() * (1f - 0.8f) / 2f;
		clip.addTrack(createTrack(SInterpolator.def, new SvsScale(image), new SKeyFrame[]{
				new SKeyFrame(0f, 1f),
				new SKeyFrame(0.15f, 0.8f),
				new SKeyFrame(0.3f, 1f),
		}));
		clip.addTrack(createTrack(SInterpolator.def, new SvsPosition(image), new SKeyFrame[]{
				new SKeyFrame(0f, 0f),
				new SKeyFrame(0.15f, seek),
				new SKeyFrame(0.3f, 0f)
		}));
		return clip;
	}

	public float playAnimationHide() {
		return playAnimation("Hide");
	}

	public float playAnimationShow() {
		return playAnimation("Show");
	}

	public float playAnimationSelect() {
		return playAnimation("Select");
	}
}