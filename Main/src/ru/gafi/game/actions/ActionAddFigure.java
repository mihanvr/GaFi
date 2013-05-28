package ru.gafi.game.actions;

import ru.gafi.common.Point;
import ru.gafi.game.Figure;

/**
* User: Michael
* Date: 27.05.13
* Time: 14:42
*/
public class ActionAddFigure extends GameAction {
	public Point point;
	public Figure figure;

	public ActionAddFigure(Point point, Figure figure) {
		super(GameActionType.AddFigure);
		this.point = point;
		this.figure = figure;
	}
}
