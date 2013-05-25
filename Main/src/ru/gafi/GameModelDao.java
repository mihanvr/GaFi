package ru.gafi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ru.gafi.game.Figure;
import ru.gafi.game.TableCell;
import ru.gafi.game.TableModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 23:52
 */
public class GameModelDao {

	private final String GAME_SAVE_NAME = "tableModel.dao";
	private final int CURRENT_VERSION = 0;

	private FileHandle fileHandle;

	public GameModelDao() {
		fileHandle = Gdx.files.local(GAME_SAVE_NAME);
	}

	private FileHandle getFileHandle() {
		return fileHandle;
	}

	public void saveGame(TableModel tableModel) {
		try (DataOutputStream writer = new DataOutputStream(getFileHandle().write(false))) {
			writer.writeInt(CURRENT_VERSION);
			saveTable(writer, tableModel);
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

	public TableModel getSavedTableModel() {
		try (DataInputStream reader = new DataInputStream(getFileHandle().read())) {
			int version = reader.readInt();
			return loadTable(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveTable(DataOutputStream writer, TableModel tableModel) throws IOException {
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

	public TableModel loadTable(DataInputStream reader) throws IOException {
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

}
