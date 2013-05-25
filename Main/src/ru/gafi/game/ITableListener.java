package ru.gafi.game;

import ru.gafi.common.Point;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 20.05.13
 * Time: 20:45
 */
public interface ITableListener {

	void onStartGame();

	void onAddFigure(Point point, Figure figure);

	void onMoveFailure();

	void onMoveFigure(MoveFigureResult result);

	void onWin();

	void onLose();

	void onRemoveFigure(RemoveFigureResult result);

	void onCellOpenedChanged(CellOpenChangedResult result);

	void onClearTable();

	public class MoveFigureResult {
		public Point from, to;
		public Point[] path;

		public MoveFigureResult(Point from, Point to, Point[] path) {
			this.from = from;
			this.to = to;
			this.path = path;
		}
	}

	public class RemoveFigureResult {
		public Point point;
		public Figure figure;

		public RemoveFigureResult(Point point, Figure figure) {
			this.point = point;
			this.figure = figure;
		}
	}

	public class CellOpenChangedResult {
		public Point point;
		public boolean opened;

		public CellOpenChangedResult(Point point, boolean opened) {
			this.point = point;
			this.opened = opened;
		}
	}
}


