package ru.gafi.game.actions;

import ru.gafi.common.Point;

/**
* User: Michael
* Date: 27.05.13
* Time: 14:42
*/
public class ActionMove extends GameAction {
	public Point[] path;

	public ActionMove(Point[] path) {
		super(GameActionType.MoveFigure);
		this.path = path;
	}
}
