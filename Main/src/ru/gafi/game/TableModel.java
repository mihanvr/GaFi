package ru.gafi.game;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 20.05.13
 * Time: 20:20
 */
public class TableModel {
	private int _rowCount;
	private int _columnCount;
	private TableCell[][] _cells;

	public TableModel(int columnCount, int rowCount) {
		_columnCount = columnCount;
		_rowCount = rowCount;
		_cells = new TableCell[columnCount][rowCount];

		for (int i = 0; i < columnCount; i++) {
			for (int j = 0; j < rowCount; j++) {
				_cells[i][j] = new TableCell(null, false);
			}
		}
	}

	public int columnCount() {
		return _columnCount;
	}

	public int rowCount() {
		return _rowCount;
	}

	public TableCell getCell(int x, int y) {
		return _cells[x][y];
	}

}

