package ru.gafi.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import ru.gafi.Settings;
import ru.gafi.animation.*;
import ru.gafi.common.Point;
import ru.gafi.common.TrackTask;
import ru.gafi.common.Util;
import ru.gafi.common.setters.SvsPosition2;
import ru.gafi.common.setters.SvsScale;
import ru.gafi.common.setters.SvsSize2;
import ru.gafi.task.*;

import java.util.*;

import static ru.gafi.common.Util.toFloatArray;
import static ru.gafi.task.TaskUtils.toArray;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 19:23
 */
public class VTable extends AnimatedActor implements ITableListener {
	protected final TaskManager taskManager;
	private final float MAX_TIME_TO_MOVE = 0.3f;
	private final int CELLS_COUNT_IN_SECOND = 10;
	private float cellSize;
	private Image selectedCell;
	private Map<Figure, Sprite> figureView = new HashMap<>();
	private Actor tableFrame;
	private Actor tableBackground;
	private Group groupFigures;
	private Group groupCells;
	private Group groupTable;
	private float frameGap;
	private Map<Point, VCell> backgroundMap = new HashMap<>();
	private Map<Point, VFigure> mapFigures = new HashMap<>();
	private boolean _mayBeStretch;
	private Rectangle viewRect = new Rectangle();
	private float _scaleFactor;
	private boolean _stretch;
	private TableController _tableController;
	private Point selectedPoint = new Point(-1, -1);
	private VFigure selectedVFigure;
	private TableModel tableModel;
	private Sprite spriteClosedCell;
	private float defScale;
	private List<VTableAction> groupActions = new ArrayList<>();

	public VTable(Skin skin, Settings settings, TaskManager taskManager) {
		super(taskManager);
		this.taskManager = taskManager;
		cellSize = skin.get("table.cell.size", Float.class);
		frameGap = skin.get("table.frame.gap", Float.class);
		defScale = skin.get("defScale", Float.class);
		initFigureView(skin);
		tableFrame = new Button(skin.getDrawable("table.frame"));
		tableBackground = new Button(skin.getDrawable("table.back"));
		selectedCell = new Image(skin.getDrawable("table.selected"));
		selectedCell.setTouchable(Touchable.disabled);
		spriteClosedCell = skin.getSprite("table.cell.closed");
		groupFigures = new Group();
		groupCells = new Group();
		groupTable = new Group();
		initAnimations();

		setStretch(settings.isStretch(), false);
		settings.addChangeListener(getStretchUpdateListener());

		groupTable.addActor(tableBackground);
		groupTable.addActor(groupCells);
		groupTable.addActor(selectedCell);
		groupTable.addActor(groupFigures);
		addActor(tableFrame);
		addActor(groupTable);

		Util.setTransform(this, false);
		groupTable.setTransform(true);
	}

	private void initFigureView(Skin skin) {
		Figure[] values = Figure.values();
		for (int i = 0; i < values.length; i++) {
			Sprite sprite = skin.getSprite("figure.f" + (i + 1));
			sprite.setSize(cellSize, cellSize);
			figureView.put(values[i], sprite);
		}
	}

	public void setViewRect(float x, float y, float width, float height) {
		viewRect.set(x, y, width, height);
		setPosition(x, y);
		setSize(width, height);
		updateTableView(false);
	}

	public Rectangle getViewRect() {
		return viewRect;
	}

	public int columnCount() {
		return tableModel.columnCount();
	}

	public int rowCount() {
		return tableModel.rowCount();
	}

	public void onStartGame() {
		clearGame();
		updateTableSize(false);
		initFromModel();
		unselectPoint();
	}

	public void onAddFigure(Point point, Figure figure) {
		doAction(new ActionAddFigure(point, figure));
	}

	public void onMoveFailure() {
		if (selectedVFigure != null) {
			animateMove(selectedVFigure, getPointPosition(selectedPoint));
			selectedVFigure = null;
		}
		playAnimation("MoveFailure");
	}

	public void onMoveFigure(MoveFigureResult result) {
		doAction(new ActionMoveFigure(result.path));
	}

	private void onStartMove() {
		unselectPoint();
	}

	public void onWin() {
	}

	public void onLose() {
	}

	public void onRemoveFigure(RemoveFigureResult result) {
		doAction(new ActionRemoveFigure(result.point));
	}

	public void onCellOpenedChanged(CellOpenChangedResult result) {
		if (result.opened) {
			doAction(new ActionOpenCell(result.point));
		} else {
			doAction(new ActionCloseCell(result.point));
		}
	}

	@Override
	public void onStepFinish() {
		flushActions();
	}

	@Override
	public void onStepBegin() {
		flushActions();
	}

	public void onClearTable() {
		resetTable();
	}

	private void resetTable() {
		List<VTableAction> actionsRemove = new ArrayList<>();
		List<VTableAction> actionsClose = new ArrayList<>();
		for (int i = 0; i < tableModel.columnCount(); i++) {
			for (int j = 0; j < tableModel.rowCount(); j++) {
				TableCell tableCell = tableModel.getCell(i, j);
				if (tableCell.figure != null) {
					actionsRemove.add(new ActionRemoveFigure(new Point(i, j)));
				}
				if (tableCell.opened) {
					actionsClose.add(new ActionCloseCell(new Point(i, j)));
				}
			}
		}
		taskManager.addTaskInQueue(new TaskClearTable(actionsRemove, actionsClose));
	}

	public VFigure moveFigure(Point from, Point to) {
		VFigure figure = getVFigure(from);
		mapFigures.remove(from);
		mapFigures.put(to, figure);
		return figure;
	}

	public void set(TableController _tableController, TableModel tableModel) {
		this._tableController = _tableController;
		this.tableModel = tableModel;
		_tableController.addListener(this);
	}

	protected void initFromModel() {
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				TableCell tableCell = tableModel.getCell(i, j);
				Point point = new Point(i, j);
				setCellState(point, tableCell.opened);
				if (tableCell.figure != null) {
					addFigure(point, tableCell.figure);
				}
			}
		}
	}

	private VFigure addFigure(Point point, Figure figure) {
		VFigure vFigure = new VFigure(taskManager, cellSize);
		vFigure.setSprite(figureView.get(figure));
		setToCell(vFigure, point);
		mapFigures.put(point, vFigure);
		groupFigures.addActor(vFigure);
		return vFigure;
	}

	protected void initAnimations() {
		STrack selectColorTrack = createColorTrack(Color.RED, Color.WHITE, 0.5f, new SValueSetter() {
			private Color temp = new Color();

			@Override
			public void Set(float[] values) {
				temp.set(values[0], values[1], values[2], values[3]);
				selectedCell.setColor(temp);
			}
		});
		STrack frameColorTrack = createColorTrack(Color.RED, Color.WHITE, 0.5f, new SValueSetter() {
			private Color temp = new Color();

			@Override
			public void Set(float[] values) {
				temp.set(values[0], values[1], values[2], values[3]);
				tableFrame.setColor(temp);
			}
		});
		SClip moveFailureClip = new SClip("MoveFailure");
		moveFailureClip.addTrack(selectColorTrack);
		moveFailureClip.addTrack(frameColorTrack);
		animation.addClip(moveFailureClip);
	}

	private STrack createColorTrack(Color startColor, Color finishColor, float time, SValueSetter valueSetter) {
		STimeCurve timeCurve = new STimeCurve(SInterpolator.def);
		timeCurve.addKeyFrame(0, toFloatArray(startColor));
		timeCurve.addKeyFrame(time, toFloatArray(finishColor));
		return new STrack(timeCurve, valueSetter);

	}

	protected void updateTableSize(boolean animated) {
		createBackground();
		updateTableView(animated);
	}

	public void updateTableView(boolean animated) {
		float availableTableWidth = viewRect.width - frameGap * 2;
		float availableTableHeight = viewRect.height - frameGap * 2;

		float tableWidth = cellSize * columnCount();
		float tableHeight = cellSize * rowCount();

		float sw = availableTableWidth / tableWidth;
		float sh = availableTableHeight / tableHeight;


		_scaleFactor = Math.min(sw, sh);
		_mayBeStretch = (_scaleFactor > defScale);
		if (_mayBeStretch && !_stretch) {
			_scaleFactor = defScale;
		}
		tableBackground.setSize(tableWidth, tableHeight);

		Vector2 frameSize = new Vector2(tableWidth, tableHeight).mul(_scaleFactor).add(frameGap * 2, frameGap * 2);
		Vector2 framePosition = new Vector2(viewRect.x + viewRect.width / 2f - frameSize.x / 2f, viewRect.y + viewRect.height / 2f - frameSize.y / 2f);
		Vector2 tableSize = new Vector2(tableWidth, tableHeight).mul(_scaleFactor);
		Vector2 tablePosition = new Vector2(viewRect.x + viewRect.width / 2f - tableSize.x / 2f, viewRect.y + viewRect.height / 2f - tableSize.y / 2f);

		if (animated) {
			final float TIME = 0.3f;
			taskManager.addParallelTask(new TrackTask(getTrackChangePosition(tableFrame, TIME, framePosition)));
			taskManager.addParallelTask(new TrackTask(getTrackChangeSize(tableFrame, TIME, frameSize)));
			taskManager.addParallelTask(new TrackTask(getTrackChangePosition(groupTable, TIME, tablePosition)));
			taskManager.addParallelTask(new TrackTask(getTrackChangeScale(groupTable, TIME, _scaleFactor)));
		} else {
			setPosition(tableFrame, framePosition);
			setSize(tableFrame, frameSize);
			groupTable.setScale(_scaleFactor);
			setPosition(groupTable, tablePosition);
		}
	}

	private STrack getTrackChangeSize(Actor actor, float time, Vector2 toSize) {
		return changeValue(time, getSize(actor), toSize, new SvsSize2(actor));
	}

	private STrack getTrackChangeScale(Actor actor, float time, float toScale) {
		STimeCurve timeCurve = new STimeCurve(SInterpolator.def);
		timeCurve.addKeyFrame(0, actor.getScaleX());
		timeCurve.addKeyFrame(time, toScale);
		return new STrack(timeCurve, new SvsScale(actor));
	}

	private STrack getTrackChangePosition(Actor actor, float time, Vector2 toPosition) {
		return changeValue(time, getPosition(actor), toPosition, new SvsPosition2(actor));
	}

	private STrack changeValue(float time, Vector2 fromValue, Vector2 toValue,
	                           SValueSetter valueSetter) {
		STimeCurve timeCurve = new STimeCurve(SInterpolator.def);
		timeCurve.addKeyFrame(0, toFloatArray(fromValue));
		timeCurve.addKeyFrame(time, toFloatArray(toValue));
		return new STrack(timeCurve, valueSetter);
	}

	private Vector2 getPosition(Actor actor) {
		return new Vector2(actor.getX(), actor.getY());
	}

	private Vector2 getSize(Actor actor) {
		return new Vector2(actor.getWidth(), actor.getHeight());
	}

	private void setPosition(Actor actor, Vector2 pos) {
		actor.setPosition(pos.x, pos.y);
	}

	private void setSize(Actor actor, Vector2 size) {
		actor.setSize(size.x, size.y);
	}

	private void createBackground() {
		for (int i = 0; i < columnCount(); i++) {
			for (int j = 0; j < rowCount(); j++) {
				Point point = new Point(i, j);
				VCell vCell = new VCell(taskManager, this, point, spriteClosedCell);
				backgroundMap.put(point, vCell);
				groupCells.addActor(vCell);
				setToCell(vCell, new Point(i, j));
			}
		}
	}

	public void clearGame() {
		Point[] keys = new Point[mapFigures.size()];
		mapFigures.keySet().toArray(keys);
		for (Point p : keys) {
			removeFigure(p);
		}
		groupCells.clear();
		backgroundMap.clear();
	}

	private void removeFigure(Point point) {
		VFigure vFigure = getVFigure(point);
		mapFigures.remove(point);
		vFigure.getParent().removeActor(vFigure);
		if (point.equals(selectedPoint)) {
			unselectPoint();
		}
	}

	private void setToCell(Actor actor, Point point) {
		Vector2 pointPosition = getPointPosition(point);
		actor.setPosition(pointPosition.x, pointPosition.y);
	}

	private Vector2 getPointPosition(Point point) {
		return new Vector2(point.x * cellSize, point.y * cellSize);
	}

	public void onPress(Point point, Vector2 touchPos, boolean pressed) {
		if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
			if (tableModel.getCell(point.x, point.y).figure != null && pressed) {
				_tableController.debugRemoveFigure(point);
			}
			return;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			Figure figure = tableModel.getCell(point.x, point.y).figure;
			if (figure != null && pressed) {
				_tableController.debugAddFigure(point, figure);
			}
			return;
		}
		if (pressed) {
			if (tableModel.getCell(point.x, point.y).figure != null) {
				if (selectedVFigure != null) {
					animateMove(selectedVFigure, getPointPosition(selectedPoint));
				}
				doAction(new ActionSelectFigure(point));
			} else if (selectedPoint.x >= 0) {
				_tableController.tryMove(selectedPoint, point);
			}
		} else if (selectedVFigure != null) {
			Point pointUnderCursor = getPointUnderCursor(touchPos);
			if (pointUnderCursor.x == -1) {
				animateMove(selectedVFigure, getPointPosition(selectedPoint));
			} else {
				TableCell tableCell = tableModel.getCell(pointUnderCursor.x, pointUnderCursor.y);
				if (tableCell.figure != null || pointUnderCursor.equals(selectedPoint)) {
					animateMove(selectedVFigure, getPointPosition(selectedPoint));
				} else {
					if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
						_tableController.debugMove(selectedPoint, pointUnderCursor);
					} else {
						_tableController.tryMove(selectedPoint, pointUnderCursor);
					}
				}
			}
		}
	}

	public void onDrag(Point point, Vector2 delta) {
		if (point.equals(selectedPoint)) {
			if (selectedVFigure != null) {
				selectedVFigure.translate(delta.x, delta.y);
			}
		}
	}

	private float animateMove(Actor actor, Vector2 to) {
		return animateMove(actor, new Vector2[]{new Vector2(actor.getX(), actor.getY()), to});
	}

	private float animateMove(Actor actor, Vector2[] path) {
		float speed = CELLS_COUNT_IN_SECOND * cellSize;
		float pathLength = 0;
		Vector2 tmp = Vector2.tmp;
		for (int i = 0; i < path.length - 1; i++) {
			pathLength += tmp.set(path[i + 1]).sub(path[i]).len();
		}
		float time = Math.min(pathLength / speed, MAX_TIME_TO_MOVE);
		STimeCurve timeCurve = new STimeCurve(SInterpolator.def);
		if (time == 0) {
			timeCurve.addKeyFrame(0, toFloatArray(path[path.length - 1]));
		} else {
			float lengthLeft = 0;
			timeCurve.addKeyFrame(0, toFloatArray(path[0]));
			for (int i = 1; i < path.length - 1; i++) {
				lengthLeft += tmp.set(path[i]).sub(path[i - 1]).len();
				timeCurve.addKeyFrame(lengthLeft / pathLength * time, toFloatArray(path[i]));
			}
			timeCurve.addKeyFrame(time, toFloatArray(path[path.length - 1]));
		}

		STrack track = new STrack(timeCurve, new SvsPosition2(actor));
		taskManager.addParallelTask(new TrackTask(track));
		return time;
	}

	private Point getPointUnderCursor(Vector2 touchPos) {
		Actor hit = groupCells.hit((touchPos.x - groupTable.getX()) / groupTable.getScaleX(), (touchPos.y - groupTable.getY()) / groupTable.getScaleY(), true);
		if (hit != null) {
			return ((VCell) hit).getPoint();
		}
		return new Point(-1, -1);
	}

	protected void selectPoint(Point point) {
		unselectPoint();
		selectedCell.setVisible(true);
		selectedPoint = new Point(point.x, point.y);
		setToCell(selectedCell, point);
		VFigure vFigure = getVFigure(point);
		selectedVFigure = vFigure;

		if (vFigure != null) {
			bringToFrontVFigure(selectedVFigure);
			vFigure.playAnimationSelect();
		}
	}

	protected void unselectPoint() {
		selectedPoint = new Point(-1, -1);
		selectedCell.setVisible(false);
		selectedVFigure = null;
	}

	private void bringToFrontVFigure(VFigure vFigure) {
		groupFigures.swapActor(vFigure, groupFigures.getChildren().peek());
	}

	private void setCellState(Point p, boolean opened) {
		VCell cell = backgroundMap.get(p);
		cell.setOpened(opened);
	}

	public VFigure getVFigure(Point p) {
		if (!mapFigures.containsKey(p)) {
			return null;
		}
		return mapFigures.get(p);
	}

	public VCell getVCell(Point p) {
		return backgroundMap.get(p);
	}

	private void doAction(VTableAction action) {
		if (action.actionType == ActionType.SelectFigure) {
			taskManager.addTaskInQueue(action);
			return;
		}

		if (!groupActions.isEmpty() && !IsCompatibility(groupActions.get(groupActions.size() - 1).actionType, action.actionType)) {
			flushActions();
		}
		groupActions.add(action);
	}

	protected void flushActions() {
		if (!groupActions.isEmpty()) {
			taskManager.addTaskInQueue(new GroupTask(toArray(groupActions)));
			groupActions.clear();
		}
	}

	private boolean IsCompatibility(ActionType at0, ActionType at1) {
		if (hasPair(at0, at1, ActionType.MoveFigure, ActionType.MoveFigure)) return false;
		if (at0 == at1) return true;
		if (hasPair(at0, at1, ActionType.AddFigure, ActionType.CloseCell)) return true;
		if (hasPair(at0, at1, ActionType.RemoveFigure, ActionType.OpenCell)) return true;

		return false;
	}

	private boolean hasPair(ActionType at0, ActionType at1, ActionType expected0, ActionType expected1) {
		return at0 == expected0 && at1 == expected1
				|| at1 == expected0 && at0 == expected1;
	}

	public void setStretch(boolean value, boolean animated) {
		_stretch = value;
		if (tableModel != null) {
			updateTableView(animated);
		}
	}

	public Settings.SettingsListener getStretchUpdateListener() {
		return new Settings.SettingsListener() {
			@Override
			public void onChange(Settings settings) {
				setStretch(settings.isStretch(), true);
			}
		};
	}

	private class ActionAddFigure extends VTableAction {
		private Figure figure;
		private Point point;

		public ActionAddFigure(Point point, Figure figure) {
			super(ActionType.AddFigure);
			this.point = point;
			this.figure = figure;
		}

		public void onStart() {
			VFigure vFigure = VTable.this.addFigure(point, figure);
			setTimeDuration(vFigure.playAnimationShow());
		}
	}

	private class ActionCloseCell extends VTableAction {
		private Point point;

		public ActionCloseCell(Point point) {
			super(ActionType.CloseCell);
			this.point = point;
		}

		public void onStart() {
			VCell vCell = VTable.this.getVCell(point);
			VTable.this.setCellState(point, false);
			setTimeDuration(vCell.playAnimationClose());
		}
	}

	private class ActionMoveFigure extends VTableAction {
		private Point from;
		private Point[] path;
		private Point to;
		private VFigure vFigure;

		public ActionMoveFigure(Point[] path) {
			super(ActionType.MoveFigure);
			from = path[0];
			to = path[path.length - 1];
			this.path = path;
		}

		public void onStart() {
			vFigure = VTable.this.getVFigure(from);
			float distanceToTarget = Vector2.tmp.set(VTable.this.getPosition(vFigure)).sub(VTable.this.getPointPosition(to)).len();

			Vector2[] checkPoints;
			Vector2 firstPoint = VTable.this.getPosition(vFigure);
			if (path == null || distanceToTarget <= VTable.this.cellSize) {
				checkPoints = new Vector2[]{
						firstPoint, VTable.this.getPointPosition(to)
				};
			} else {
				checkPoints = new Vector2[path.length];
				for (int i = 0; i < path.length; i++) {
					checkPoints[i] = VTable.this.getPointPosition(path[i]);
				}
			}
			setTimeDuration(VTable.this.animateMove(vFigure, checkPoints));

			VTable.this.onStartMove();
			VTable.this.bringToFrontVFigure(vFigure);
		}

		public void onFinish() {
			VTable.this.moveFigure(from, to);
		}
	}

	private class ActionOpenCell extends VTableAction {
		private Point point;

		public ActionOpenCell(Point point) {
			super(ActionType.OpenCell);
			this.point = point;
		}

		public void onStart() {
			VCell vCell = VTable.this.getVCell(point);
			setTimeDuration(vCell.playAnimationOpen());
		}

		public void onFinish() {
			VTable.this.setCellState(point, true);
		}
	}

	private class ActionRemoveFigure extends VTableAction {
		private Point point;

		public ActionRemoveFigure(Point point) {
			super(ActionType.RemoveFigure);
			this.point = point;
		}

		public void onStart() {
			VFigure vFigure = VTable.this.getVFigure(point);
			setTimeDuration(vFigure.playAnimationHide());
		}

		public void onFinish() {
			VTable.this.removeFigure(point);
		}
	}

	protected class ActionSelectFigure extends VTableAction {
		private Point point;

		public ActionSelectFigure(Point point) {
			super(ActionType.SelectFigure);
			this.point = point;
		}

		public void onStart() {
			VTable.this.selectPoint(point);
		}
	}

	private class TaskClearTable implements Task {
		private SerialTasks serialTasks;

		public TaskClearTable(Collection<VTableAction> removeFigures, Collection<VTableAction> closeCells) {
			List<Task> actionsRemoveFigures = new ArrayList<>();
			List<Task> actionsCloseCell = new ArrayList<>();
			for (VTableAction action : removeFigures) {
				actionsRemoveFigures.add(action);
			}
			for (VTableAction action : closeCells) {
				actionsCloseCell.add(action);
			}
			serialTasks = new SerialTasks(
					new GroupTask(toArray(actionsRemoveFigures)),
					new GroupTask(toArray(actionsCloseCell))
			);
		}

		public void start() {
			serialTasks.start();
		}

		public void update(float dt) {
			serialTasks.update(dt);
		}

		@Override
		public boolean isDone() {
			return serialTasks.isDone();
		}
	}

	abstract class VTableAction extends SimpleTask {
		public ActionType actionType;
		private float timeDuration;
		private float timeLeft;

		public VTableAction(ActionType actionType) {
			this.actionType = actionType;
		}

		@Override
		public boolean isDone() {
			return timeLeft >= timeDuration;
		}

		@Override
		public void update(float dt) {
			timeLeft += dt;
			if (timeLeft >= timeDuration) {
				onFinish();
				done();
			}
		}

		@Override
		public void start() {
			timeLeft = 0;
			onStart();
		}

		protected void setTimeDuration(float value) {
			timeDuration = value;
		}

		public abstract void onStart();

		public void onFinish() {
		}

	}
}
