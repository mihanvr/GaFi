package ru.gafi.game;

import ru.gafi.common.ICommand;
import ru.gafi.common.Point;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 20.05.13
 * Time: 20:45
 */
public class TableController {
	public enum Action {Add, Remove, Open, Close, Move};

	private List<ITableListener> listeners = new ArrayList<>();
	private boolean[][] burnTable;
	private TableModel tableModel;
	private Stack<ICommand> qUndo = new Stack<>();
	private Stack<ICommand> qRedo = new Stack<>();
	private List<ICommand> commandStore = new ArrayList<>();
	private CommandSet commandSet;
	private PathFinder pathFinder;
	private Figure[] figures = Figure.values();
	private Random random = new Random();

	public TableController() {
		pathFinder = new PathFinder();
	}

	private int columnCount() {
		return tableModel.columnCount();
	}

	private int rowCount() {
		return tableModel.rowCount();
	}

	public void tryMove(Point from, Point to) {
		if (isValidMove(from, to)) {
			PathFinder.FindPathResult findResult = findPath(from, to);
			if (findResult.pathFinded) {
				commandStore.clear();
				execute(new CommandMoveFigure(this, from, to, findResult.path));
				burnCompleteFigures();
				boolean gameEnded = false;
				if (!usefulStep()) {
					addRandomFigures();
					if (isLose()) {
						lose();
						gameEnded = true;
					}
				}
				if (!gameEnded && isWin()) {
					win();
					gameEnded = true;
				}
				if (!gameEnded && getCountOfBusyCells() == 0) {
					addRandomFigures();
				}
				qUndo.push(new CommandSet(commandStore));
			} else {
				fireOnMoveFailure();
			}
		} else {
			fireOnMoveFailure();
		}
	}

private boolean isValidMove(Point from, Point to) {
		return tableModel.getCell(from.x, from.y).figure != null && tableModel.getCell(to.x, to.y).figure == null;
	}

	public void addRandomFigures() {
		addRandomFigures(calculateCountAddingFigures());
		burnCompleteFigures();
	}

	private int calculateCountAddingFigures() {
		int countOfFreeCells = getCountOfFreeCells();
		int allCells = rowCount() * columnCount();
		float perc = countOfFreeCells / (float) allCells;
		int count;
		if (perc > 0.5) {
			count = Math.min(countOfFreeCells - allCells / 2, 5);
		} else {
			count = 2;
		}
		return count;
	}

	private void fireOnMoveFailure() {
		for (ITableListener listener : listeners) {
			listener.onMoveFailure();
		}
	}

	public void moveFigure(Point from, Point to, Point[] path) {
		TableCell fromCell = tableModel.getCell(from.x, from.y);
		TableCell toCell = tableModel.getCell(to.x, to.y);
		toCell.figure = fromCell.figure;
		fromCell.figure = null;

		fireOnMoveFigure(new ITableListener.MoveFigureResult(from, to, path));
	}

	private void fireOnMoveFigure(ITableListener.MoveFigureResult result) {
		for (ITableListener listener : listeners) {
			listener.onMoveFigure(result);
		}
	}

	public void StartGame() {
		fireOnStartGame();
	}

	private void fireOnStartGame() {
		for (ITableListener listener : listeners) {
			listener.onStartGame();
		}
	}

	public void addRandomFigures(int count) {
		int availableCells = getCountOfFreeCells();
		int realCount = Math.min(availableCells, count);
		for (int i = 0; i < realCount; i++) {
			addRandomFigure();
		}
	}

	private void addRandomFigure() {
		int x = randomRange(0, columnCount());
		int y = randomRange(0, rowCount());
		int figureIndex = randomRange(0, figures.length);
		Point point = findNearestFreeCell(x, y);
		Figure figure = figures[figureIndex];
		execute(new CommandAddFigure(this, point, figure));
	}

	private int randomRange(int from, int to) {
		return from + random.nextInt(to - from);
	}

	private void addFigure(Point point, Figure figure) {
		tableModel.getCell(point.x, point.y).figure = figure;

		byte[] action = new byte[4];
		action[0] = (byte) Action.Add.ordinal();
		action[1] = (byte) point.x;
		action[2] = (byte) point.y;
		action[3] = (byte) figure.ordinal();

		fireOnAddFigure(point, figure);
	}

	private void fireOnAddFigure(Point point, Figure figure) {
		for (ITableListener listener : listeners) {
			listener.onAddFigure(point, figure);
		}
	}

	private Point findNearestFreeCell(int x, int y) {
		while (tableModel.getCell(x, y).figure != null) {
			if (x == columnCount() - 1) {
				x = 0;
				if (y == rowCount() - 1) {
					y = 0;
				} else {
					y++;
				}
			} else {
				x++;
			}
		}
		return new Point(x, y);
	}

	private int getCountOfFreeCells() {
		int countOfClosedCells = getCountOfBusyCells();
		int allCellsCount = columnCount() * rowCount();
		int availableCells = allCellsCount - countOfClosedCells;
		return availableCells;
	}

	private int getCountOfBusyCells() {
		int count = 0;
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				if (tableModel.getCell(i, j).figure != null) {
					count++;
				}
			}
		}
		return count;
	}

	public boolean usefulStep() {
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				if (burnTable[i][j]) return true;
			}
		}
		return false;
	}

	public void burnCompleteFigures() {
		clearBurnTable();
		fillBurnTable();
		burnTable();
	}

	private void burnTable() {
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				if (burnTable[i][j]) {
					Point point = new Point(i, j);
					execute(new CommandRemoveFigure(this, point, tableModel.getCell(i, j).figure));
					if (!tableModel.getCell(i, j).opened) {
						execute(new CommandChangeCellOpened(this, point, true));
					}
				}
			}
		}
	}

	private void fillBurnTable() {
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				Figure figure = tableModel.getCell(i, j).figure;
				if (figure != null) {
					checkForBurn(i, j);
				}
			}
		}
	}

	private void checkForBurn(int x, int y) {
		Figure figure = tableModel.getCell(x, y).figure;
		if (figure == null) return;
		for (int i = 0; i < figure.columnCount(); i++) {
			int sx = x - i;
			if (sx < 0 || sx + figure.columnCount() > columnCount()) continue;
			for (int j = 0; j < figure.rowCount(); j++) {
				if (figure.mask[i][j] == 0) continue;
				int sy = y - j;
				if (sy < 0 || sy + figure.rowCount() > rowCount()) continue;
				if (figureCollected(figure, sx, sy)) {
					markForBurn(figure, sx, sy);
				}
			}
		}
	}

	private void clearBurnTable() {
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				burnTable[i][j] = false;
			}
		}
	}

	private void markForBurn(Figure figure, int x, int y) {
		for (int i = 0; i < figure.columnCount(); i++) {
			for (int j = 0; j < figure.rowCount(); j++) {
				if (figure.mask[i][j] > 0) burnTable[x + i][y + j] = true;
			}
		}
	}

	private boolean figureCollected(Figure figure, int x, int y) {
		for (int i = 0; i < figure.columnCount(); i++) {
			for (int j = 0; j < figure.rowCount(); j++) {
				if (figure.mask[i][j] > 0 && tableModel.getCell(x + i, y + j).figure != figure) return false;
			}
		}
		return true;
	}

	private void setCellOpened(Point p, boolean opened) {
		TableCell tableCell = tableModel.getCell(p.x, p.y);
		if (tableCell.opened != opened) {
			tableCell.opened = opened;

			byte[] action = new byte[3];
			if (opened) {
				action[0] = (byte) Action.Open.ordinal();
			} else {
				action[0] = (byte) Action.Close.ordinal();
			}
			action[1] = (byte) p.x;
			action[2] = (byte) p.y;

			fireOnCellOpenedChanged(new ITableListener.CellOpenChangedResult(p, opened));
		}
	}

	private void fireOnCellOpenedChanged(ITableListener.CellOpenChangedResult result) {
		for (ITableListener listener : listeners) {
			listener.onCellOpenedChanged(result);
		}
	}

	public void removeFigure(Point p) {
		Figure figure = tableModel.getCell(p.x, p.y).figure;
		tableModel.getCell(p.x, p.y).figure = null;

		byte[] action = new byte[4];
		action[0] = (byte) Action.Remove.ordinal();
		action[1] = (byte) p.x;
		action[2] = (byte) p.y;
		action[3] = (byte) figure.ordinal();

		fireOnRemoveFigure(new ITableListener.RemoveFigureResult(p, figure));
	}

	private void fireOnRemoveFigure(ITableListener.RemoveFigureResult result) {
		for (ITableListener listener : listeners) {
			listener.onRemoveFigure(result);
		}
	}

	public void debugPreWin() {
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				Point point = new Point(i, j);

				TableCell tableCell = tableModel.getCell(i, j);
				if (tableCell.figure != null) {
					removeFigure(point);
				}
				if (!tableCell.opened) {
					setCellOpened(point, true);
				}
			}
		}

		addFigure(new Point(0, 0), figures[0]);
		addFigure(new Point(1, 0), figures[0]);
		addFigure(new Point(2, 0), figures[0]);
		addFigure(new Point(0, 1), figures[0]);
		setCellOpened(new Point(1, 1), false);
	}

	public void debugAddFigure(Point point, Figure figure) {
		if (getCountOfFreeCells() == 0) return;
		Point nearestFreeCell = findNearestFreeCell(point.x, point.y);
		addFigure(nearestFreeCell, figure);
	}

	public void debugRemoveFigure(Point point) {
		removeFigure(point);
	}

	public void DebugMove(Point from, Point to) {
		moveFigure(from, to, new Point[]{from, to});
	}

	private void win() {
		clearCommands();
		fireOnWin();
	}

	private void fireOnWin() {
		for (ITableListener listener : listeners) {
			listener.onWin();
		}
	}

	private void lose() {
		clearCommands();
		fireOnLose();
	}

	private void fireOnLose() {
		for (ITableListener listener : listeners) {
			listener.onLose();
		}
	}

	public void checkOnWin() {
		if (isWin()) win();
	}

	public void checkOnLose() {
		if (isLose()) lose();
	}

	private boolean isWin() {
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				TableCell tableCell = tableModel.getCell(i, j);
				if (!tableCell.opened) return false;
			}
		}
		return true;
	}

	private boolean isLose() {
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				TableCell tableCell = tableModel.getCell(i, j);
				if (tableCell.figure == null) return false;
			}
		}
		return true;
	}

	private void clearCommands() {
		qUndo.clear();
		qRedo.clear();
		commandStore.clear();
	}

	private PathFinder.FindPathResult findPath(Point from, Point to) {
		return pathFinder.find(tableModel, from, to);
	}

	public void addListener(ITableListener _tableListener) {
		listeners.add(_tableListener);
	}

	public void SetTable(TableModel tableModel) {
		this.tableModel = tableModel;
		burnTable = new boolean[columnCount()][rowCount()];
	}

	public void makeFirstMove() {
		addRandomFigures(rowCount() * columnCount() / 2);
		burnCompleteFigures();
	}

	public void clearTable() {
		fireOnClearTable();
		clearHistory();
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				TableCell tableCell = tableModel.getCell(i, j);
				tableCell.figure = null;
				tableCell.opened = false;
			}
		}

	}

	private void fireOnClearTable() {
		for (ITableListener listener : listeners) {
			listener.onClearTable();
		}
	}

	private void execute(ICommand command) {
		qRedo.clear();
		commandStore.add(command);
		command.execute();
	}

	public void undo() {
		if (qUndo.size() > 0) {
			ICommand command = qUndo.pop();
			qRedo.push(command);
			command.cancel();
		}
	}

	public void redo() {
		if (qRedo.size() > 0) {
			ICommand command = qRedo.pop();
			qUndo.push(command);
			command.execute();
		}
	}

	private void clearHistory() {
		qRedo.clear();
		qUndo.clear();
	}

	private abstract class TcaCommand implements ICommand {
		public ActionType type;

		private TcaCommand(ActionType type) {
			this.type = type;
		}
	}

	private abstract class TcCommand extends TcaCommand {
		protected TableController tableController;

		private TcCommand(TableController tableController, ActionType type) {
			super(type);
			this.tableController = tableController;
		}
	}

	private class CommandAddFigure extends TcCommand {
		public Figure figure;
		public Point point;

		public CommandAddFigure(TableController tableController, Point point, Figure figure) {
			super(tableController, ActionType.AddFigure);
			this.point = point;
			this.figure = figure;
		}

		public void execute() {
			tableController.addFigure(point, figure);
		}

		public void cancel() {
			tableController.removeFigure(point);
		}
	}

	private class CommandRemoveFigure extends TcCommand {
		private Figure figure;
		private Point point;

		public CommandRemoveFigure(TableController tableController, Point point, Figure figure) {
			super(tableController, ActionType.RemoveFigure);
			this.point = point;
			this.figure = figure;
		}

		public void execute() {
			tableController.removeFigure(point);
		}

		public void cancel() {
			tableController.addFigure(point, figure);
		}
	}

	private class CommandChangeCellOpened extends TcCommand {
		private Point point;
		private boolean opened;

		public CommandChangeCellOpened(TableController tableController, Point point, boolean opened) {
			super(tableController, ActionType.OpenCell);
			this.point = point;
			this.opened = opened;
		}

		public void execute() {
			tableController.setCellOpened(point, opened);
		}

		public void cancel() {
			tableController.setCellOpened(point, !opened);
		}
	}

	private class CommandMoveFigure extends TcCommand {
		private Point from, to;
		private Point[] path;

		public CommandMoveFigure(TableController tableController, Point from, Point to, Point[] path) {
			super(tableController, ActionType.MoveFigure);
			this.from = from;
			this.to = to;
			this.path = path;
		}

		public void execute() {
			tableController.moveFigure(from, to, path);
		}

		public void cancel() {
			Point[] reversed = new Point[path.length];
			System.arraycopy(path, 0, reversed, 1, path.length - 1);
			reversed[0] = from;
			for (int i = 0; i < reversed.length / 2; i++) {
				Point temp = reversed[i];
				reversed[i] = reversed[reversed.length - i - 1];
				reversed[reversed.length - i - 1] = temp;
			}
			tableController.moveFigure(to, from, reversed);
		}
	}

	private class CommandSet extends TcaCommand {
		private TcCommand[] commands;

		public CommandSet(Collection<ICommand> commands) {
			super(ActionType.StepBegin);
			this.commands = new TcCommand[commands.size()];
			commands.toArray(this.commands);
		}

		public void execute() {
			for (int i = 0; i < commands.length; i++) {
				commands[i].execute();
			}
		}

		public void cancel() {
			for (int i = commands.length - 1; i >= 0; i--) {
				commands[i].cancel();
			}
		}
	}
}


