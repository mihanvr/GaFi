package ru.gafi.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ru.gafi.animation.SClip;
import ru.gafi.animation.SInterpolator;
import ru.gafi.animation.SKeyFrame;
import ru.gafi.common.Point;
import ru.gafi.common.Util;
import ru.gafi.common.setters.SvsPosition;
import ru.gafi.common.setters.SvsScale;
import ru.gafi.task.TaskManager;

import static ru.gafi.common.Util.createTrack;

/**
 * User: Michael
 * Date: 21.05.13
 * Time: 10:45
 */
public class VCell extends AnimatedActor {
	private final VTable table;
	private final Point point;
	private Image image;

	public VCell(TaskManager taskManager, VTable table, Point point, Sprite sprite) {
		super(taskManager);
		this.table = table;
		this.point = point;
		setSize(sprite.getWidth(), sprite.getHeight());

		initImage(sprite);
		initAnimations();
		addActor(image);
		addListener();
		Util.setTransform(this, false);
	}

	private void addListener() {

		addListener(new InputListener() {
			private Vector2 delta = new Vector2();
			private Vector2 lastPoint = new Vector2();

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (pointer == 0) {
					onTouchDown(Vector2.tmp.set(event.getStageX(), event.getStageY()));
					lastPoint.set(x, y);
				}
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (pointer == 0) {
					onTouchUp(Vector2.tmp.set(event.getStageX(), event.getStageY()));
				}
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				if (pointer == 0) {
					delta.set(x - lastPoint.x, y - lastPoint.y);
					lastPoint.set(x, y);
					onDrag(delta);
				}
			}
		});
	}

	public void setOpened(boolean opened) {
		image.setVisible(!opened);
	}

	private void initImage(Sprite sprite) {
		image = new Image(sprite);
		image.setTouchable(Touchable.disabled);
		image.setSize(sprite.getWidth(), sprite.getHeight());
	}

	protected void initAnimations() {
		SClip clipOpen = createOpenClip();
		SClip clipClose = createCloseClip();
		animation.addClip(clipOpen);
		animation.addClip(clipClose);
	}

	private SClip createOpenClip() {
		SClip clip = new SClip("Open");

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

	private SClip createCloseClip() {
		SClip clip = new SClip("Close");
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

	protected void onTouchDown(Vector2 touchPos) {
		table.onPress(point, touchPos, true);
	}

	protected void onTouchUp(Vector2 touchPos) {
		table.onPress(point, touchPos, false);
	}

	protected void onDrag(Vector2 delta) {
		table.onDrag(point, delta);
	}

	public float playAnimationOpen() {
		return playAnimation("Open");
	}

	public float playAnimationClose() {
		return playAnimation("Close");
	}

	public Point getPoint() {
		return point;
	}
}
