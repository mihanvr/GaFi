package ru.gafi.game.actions;


import ru.gafi.common.Point;
import ru.gafi.game.Figure;

/**
* User: Michael
* Date: 27.05.13
* Time: 14:42
*/
public class ActionRemoveFigure extends GameAction {
	public Point point;
	public Figure figure;

	public ActionRemoveFigure(Point point, Figure figure) {
		super(GameActionType.RemoveFigure);
		this.point = point;
		this.figure = figure;
	}
}
