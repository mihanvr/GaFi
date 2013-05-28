package ru.gafi;

import ru.gafi.game.ActionHistory;
import ru.gafi.game.TableModel;

/**
 * User: Michael
 * Date: 27.05.13
 * Time: 16:13
 */
public class GameModel {
	public TableModel tableModel;
	public ActionHistory actionHistory;

	public static GameModel create(TableModel tableModel) {
		GameModel gameModel = new GameModel();
		gameModel.tableModel = tableModel;
		gameModel.actionHistory = new ActionHistory(System.nanoTime());
		return gameModel;
	}
}
