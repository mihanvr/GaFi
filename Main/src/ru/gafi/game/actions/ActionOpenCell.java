package ru.gafi.game.actions;

import ru.gafi.common.Point;

/**
* User: Michael
* Date: 27.05.13
* Time: 14:42
*/
public class ActionOpenCell extends GameAction {
	public Point point;

	public ActionOpenCell(Point point) {
		super(GameActionType.OpenCell);
		this.point = point;
	}
}
