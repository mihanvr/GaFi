package ru.gafi.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.SnapshotArray;
import ru.gafi.animation.*;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 20.05.13
 * Time: 20:27
 */
public class Util {

	public static void setTransform(Group group, boolean value) {
		group.setTransform(value);
		SnapshotArray<Actor> children = group.getChildren();
		Actor[] begin = children.begin();
		for (Actor actor : begin) {
			if (actor instanceof Group) {
				setTransform((Group) actor, value);
			}
		}
		children.end();
	}

	public static int[][] transpose(int[][] mask) {
		int row = mask.length;
		int column = mask[0].length;
		int[][] transp = new int[column][row];
		for (int i = 0; i < column; i++) {
			for (int j = 0; j < row; j++) {
				transp[i][j] = mask[j][i];
			}
		}
		return transp;
	}

	public static Color intColor(int r, int g, int b) {
		return new Color(r / 255f, g / 255f, b / 255f, 1f);
	}

	public static STrack createTrack(SInterpolator interpolator, SValueSetter valueSetter, SKeyFrame[] keyFrames) {
		STimeCurve timeCurve = new STimeCurve(interpolator);
		for (int i = 0; i < keyFrames.length; i++) {
			timeCurve.addKeyFrame(keyFrames[i]);
		}
		return new STrack(timeCurve, valueSetter);
	}

	public static float[] toFloatArray(Color color) {
		return new float[]{color.r, color.g, color.b, color.a};
	}

	public static float[] toFloatArray(Vector2 vector2) {
		return new float[]{vector2.x, vector2.y};
	}

}
