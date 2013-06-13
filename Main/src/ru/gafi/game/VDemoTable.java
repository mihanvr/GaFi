package ru.gafi.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import ru.gafi.Main;
import ru.gafi.Settings;
import ru.gafi.common.Point;
import ru.gafi.task.InstantTask;
import ru.gafi.task.Task;
import ru.gafi.task.TaskManager;
import ru.gafi.task.TaskSleep;

/**
 * User: Michael
 * Date: 13.06.13
 * Time: 11:38
 */
public class VDemoTable extends VTable {

	private TableModel tableModel;
	private DemoTableController tableController;
	private Task finishTask;

	public VDemoTable(Skin skin, Settings settings, TaskManager taskManager, final Main main) {
		super(skin, settings, taskManager);
		tableModel = new TableModel(6, 6);
		tableController = new DemoTableController();
		tableController.SetTable(tableModel);
		tableController.setHistory(new ActionHistoryDummy());
		set(tableController, tableModel);
		finishTask = new InstantTask() {
			@Override
			public void start() {
				main.stopDemo();
			}
		};
	}

	@Override
	public void onPress(Point point, Vector2 touchPos, boolean pressed) {

	}

	@Override
	public void onDrag(Point point, Vector2 delta) {

	}

	public void playDemo() {
		clearGame();
		updateTableSize(false);
		initFromModel();
		unselectPoint();
		tableController.clearTable();
		tableController.debugAddFigure(new Point(0, 0), Figure.f1);
		tableController.debugAddFigure(new Point(1, 0), Figure.f1);
		tableController.debugAddFigure(new Point(2, 0), Figure.f1);
		tableController.debugAddFigure(new Point(4, 1), Figure.f1);
		tableController.debugAddFigure(new Point(0, 5), Figure.f5);
		tableController.debugAddFigure(new Point(0, 3), Figure.f5);
		tableController.debugAddFigure(new Point(1, 4), Figure.f5);
		tableController.debugAddFigure(new Point(2, 5), Figure.f5);
		tableController.debugAddFigure(new Point(5, 0), Figure.f5);
		tableController.debugAddFigure(new Point(3, 0), Figure.f3);
		tableController.debugAddFigure(new Point(3, 1), Figure.f3);
		tableController.debugAddFigure(new Point(3, 2), Figure.f3);
		tableController.debugAddFigure(new Point(3, 3), Figure.f2);
		flushActions();
		taskManager.addTaskInQueue(new TaskSleep(1f));
		taskManager.addTaskInQueue(new ActionSelectFigure(new Point(4, 1)));
		taskManager.addTaskInQueue(new TaskSleep(1f));
		taskManager.addTaskInQueue(new ActionSelectFigure(new Point(1, 1)));
		taskManager.addTaskInQueue(new TaskSleep(1f));
		tableController.tryMove(new Point(4, 1), new Point(1, 1));
		flushActions();

		taskManager.addTaskInQueue(new TaskSleep(1f));
		taskManager.addTaskInQueue(new ActionSelectFigure(new Point(5, 0)));
		taskManager.addTaskInQueue(new TaskSleep(1f));
		taskManager.addTaskInQueue(new ActionSelectFigure(new Point(2, 3)));
		taskManager.addTaskInQueue(new TaskSleep(1f));
		tableController.tryMove(new Point(5, 0), new Point(2, 3));
		taskManager.addTaskInQueue(new TaskSleep(1f));
		flushActions();
		taskManager.addTaskInQueue(finishTask);
	}

	public void stopDemo() {

	}

}
