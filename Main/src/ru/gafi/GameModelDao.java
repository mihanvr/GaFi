package ru.gafi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ru.gafi.common.Point;
import ru.gafi.game.ActionHistory;
import ru.gafi.game.Figure;
import ru.gafi.game.TableCell;
import ru.gafi.game.TableModel;
import ru.gafi.game.actions.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 23:52
 */
public class GameModelDao {

	private final String GAME_SAVE_NAME = "gafi.dao";
	private final int CURRENT_VERSION = 0;

	private FileHandle fileHandle;

	public GameModelDao() {
		fileHandle = Gdx.files.local(GAME_SAVE_NAME);
	}

	private FileHandle getFileHandle() {
		return fileHandle;
	}

	public void saveGame(GameModel gameModel) {
		try (DataOutputStream writer = new DataOutputStream(getFileHandle().write(false))) {
			writer.writeInt(CURRENT_VERSION);
			saveTable(writer, gameModel.tableModel);
			saveActionHistory(writer, gameModel.actionHistory);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeSavedGame() {
		getFileHandle().delete();
	}

	public boolean canBeResumed() {
		FileHandle fileHandle = getFileHandle();
		if (!fileHandle.exists()) return false;
		return isActualVersion(fileHandle);
	}

	private boolean isActualVersion(FileHandle fileHandle) {
		try (DataInputStream reader = new DataInputStream(fileHandle.read())) {
			int version = reader.readInt();
			return version == CURRENT_VERSION;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public GameModel getSavedGameModel() {
		try (DataInputStream reader = new DataInputStream(getFileHandle().read())) {
			int version = reader.readInt();
			GameModel gameModel = new GameModel();
			gameModel.tableModel = loadTable(reader);
			gameModel.actionHistory = loadActionHistory(reader);
			return gameModel;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void saveTable(DataOutputStream writer, TableModel tableModel) throws IOException {
		writer.writeInt(tableModel.columnCount());
		writer.writeInt(tableModel.rowCount());
		for (int i = 0; i < tableModel.columnCount(); i++) {
			for (int j = 0; j < tableModel.rowCount(); j++) {
				TableCell tableCell = tableModel.getCell(i, j);
				Figure figure = tableCell.figure;
				if (figure == null) {
					writer.writeInt(-1);
				} else {
					writer.writeInt(figure.ordinal());
				}
				writer.writeBoolean(tableCell.opened);
			}
		}
	}

	private TableModel loadTable(DataInputStream reader) throws IOException {
		int width = reader.readInt();
		int height = reader.readInt();
		TableModel table = new TableModel(width, height);
		for (int i = 0; i < table.columnCount(); i++) {
			for (int j = 0; j < table.rowCount(); j++) {
				int figureOrdinal = reader.readInt();
				boolean opened = reader.readBoolean();
				TableCell tableCell = table.getCell(i, j);
				if (figureOrdinal != -1) {
					tableCell.figure = Figure.values()[figureOrdinal];
				}
				tableCell.opened = opened;
			}
		}
		return table;
	}

	private void saveActionHistory(DataOutputStream writer, ActionHistory history) throws IOException {
		GameAction[] undos = new GameAction[history.qUndo.size()];
		GameAction[] redos = new GameAction[history.qRedo.size()];
		history.qUndo.toArray(undos);
		history.qRedo.toArray(redos);
		writer.writeLong(history.currentSeed);
		writer.writeInt(history.qUndo.size());
		for (int i = 0; i < undos.length; i++) {
			writeGameAction(writer, undos[i]);
		}
		writer.writeInt(history.qRedo.size());
		for (int i = 0; i < redos.length; i++) {
			writeGameAction(writer, redos[i]);
		}
	}

	private void writeGameAction(DataOutputStream writer, GameAction action) throws IOException {
		switch (action.type) {
			case AddFigure:
				ActionAddFigure actionAddFigure = (ActionAddFigure) action;
				writer.writeChar('+');
				writePoint(writer, actionAddFigure.point);
				writeFigure(writer, actionAddFigure.figure);
				break;
			case RemoveFigure:
				ActionRemoveFigure actionRemoveFigure = (ActionRemoveFigure) action;
				writer.writeChar('-');
				writePoint(writer, actionRemoveFigure.point);
				writeFigure(writer, actionRemoveFigure.figure);
				break;
			case OpenCell:
				ActionOpenCell actionOpenCell = (ActionOpenCell) action;
				writer.writeChar('o');
				writePoint(writer, actionOpenCell.point);
				break;
			case MoveFigure: {
				ActionMove actionMove = (ActionMove) action;
				writer.writeChar('m');
				writer.writeInt(actionMove.path.length);
				for (int i = 0; i < actionMove.path.length; i++) {
					writePoint(writer, actionMove.path[i]);
				}
				break;
			}
			case StepBegin: {
				ActionStepBegin actionStepBegin = (ActionStepBegin) action;
				writer.writeChar('b');
				writer.writeLong(actionStepBegin.beginSeed);
				writer.writeLong(actionStepBegin.endSeed);
				writer.writeInt(actionStepBegin.actions.size());
				for (int i = 0; i < actionStepBegin.actions.size(); i++) {
					writeGameAction(writer, actionStepBegin.actions.get(i));
				}
				break;
			}
		}
	}

	private void writePoint(DataOutputStream writer, Point point) throws IOException {
		writer.writeInt(point.x);
		writer.writeInt(point.y);
	}

	private void writeFigure(DataOutputStream writer, Figure figure) throws IOException {
		writer.writeByte(figure.ordinal());
	}

	private ActionHistory loadActionHistory(DataInputStream reader) throws IOException {
		long currentSeed = reader.readLong();
		ActionHistory history = new ActionHistory(currentSeed);
		int undoLength = reader.readInt();
		for (int i = 0; i < undoLength; i++) {
			history.qUndo.push(readBeginAction(reader));
		}
		int redoLength = reader.readInt();
		for (int i = 0; i < redoLength; i++) {
			history.qRedo.push(readBeginAction(reader));
		}
		return history;
	}

	private ActionStepBegin readBeginAction(DataInputStream reader) throws IOException {
		reader.readChar();
		long beginSeed = reader.readLong();
		long endSeed = reader.readLong();
		int actionsLength = reader.readInt();
		ActionStepBegin actionStepBegin = new ActionStepBegin(beginSeed);
		for (int i = 0; i < actionsLength; i++) {
			actionStepBegin.add(readGameAction(reader));
		}
		actionStepBegin.setEndSeed(endSeed);
		return actionStepBegin;
	}

	private GameAction readGameAction(DataInputStream reader) throws IOException {
		char mnemonic = reader.readChar();
		switch (mnemonic) {
			case '+':
				return new ActionAddFigure(
						readPoint(reader),
						readFigure(reader)
				);
			case '-':
				return new ActionRemoveFigure(
						readPoint(reader),
						readFigure(reader)
				);
			case 'o':
				return new ActionOpenCell(readPoint(reader));
			case 'm': {
				int pathLength = reader.readInt();
				Point[] path = new Point[pathLength];
				for (int i = 0; i < pathLength; i++) {
					path[i] = readPoint(reader);
				}
				return new ActionMove(path);
			}
		}
		throw new IOException(String.format("undefined mnemonic '%s'", mnemonic));
	}

	private Point readPoint(DataInputStream reader) throws IOException {
		int x = reader.readInt();
		int y = reader.readInt();
		return new Point(x, y);
	}

	private Figure readFigure(DataInputStream reader) throws IOException {
		int ordinal = reader.readByte();
		return Figure.values()[ordinal];
	}

}
