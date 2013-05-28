package ru.gafi;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import ru.gafi.animation.SInterpolator;
import ru.gafi.animation.SKeyFrame;
import ru.gafi.animation.STrack;
import ru.gafi.common.Point;
import ru.gafi.common.TrackTask;
import ru.gafi.common.Util;
import ru.gafi.common.setters.SvsPosition2;
import ru.gafi.game.*;
import ru.gafi.menu.VMenu;
import ru.gafi.task.DeferredTask;
import ru.gafi.task.RunnableTask;
import ru.gafi.task.TaskManager;

import static ru.gafi.common.Util.intColor;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 14:22
 */
public class Main implements ApplicationListener, InputProcessor {

	private Image backgroundImage;
	private VMenu vMenu;
	private VTable vTable;
	private TaskManager taskManager;
	private GameModelDao tableModelDao;
	private GameModel gameModel;
	private TableController _tableController;
	private Stage stage;
	private Settings settings;
	private FPSLogger fpsLogger = new FPSLogger();
	private Skin skin;
	private float menuHeight;
	private boolean continuousRedering;

	@Override
	public void create() {
		taskManager = new TaskManager();
		settings = new Settings();
		tableModelDao = new GameModelDao();
		skin = createSkin();

		menuHeight = skin.get("menu.height", Float.class);
		vMenu = new VMenu(skin, this, settings, taskManager);
		vTable = new VTable(skin, settings, taskManager);

		stage = new Stage();
		backgroundImage = new Image(skin.getDrawable("background"));
		backgroundImage.setColor(intColor(20, 115, 154));
		stage.addActor(backgroundImage);
		stage.addActor(vTable);
		stage.addActor(vMenu);

		setInputProcessor();
		start();
	}

	private Skin createSkin() {
		float buttonSize, menuGap, cellSize;
		String atlasName;

		float width = Gdx.graphics.getWidth();
//		width = 1280;
		float baseWidth;
		if (width >= 1800) {
			baseWidth = 2048;
			atlasName = "2048.atlas";
			buttonSize = 152;
			cellSize = 122;
		} else if (width >= 1200) {
			baseWidth = 1280;
			atlasName = "1280.atlas";
			buttonSize = 96;
			cellSize = 76;
		} else {
			baseWidth = 800;
			atlasName = "800.atlas";
			buttonSize = 60;
			cellSize = 48;
		}
		float defScale = 1f;

		Gdx.app.log("Skin", "used " + atlasName);
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasName));
		for (Texture tex : atlas.getTextures()) {
			tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		}

		skin = new Skin();
		NinePatch buttonFrame = atlas.createPatch("button_frame");

		menuGap = buttonFrame.getLeftWidth();
		float menuHeight = buttonSize + menuGap*2;

		skin.add("menu.button.size", buttonSize);
		skin.add("menu.gap", menuGap);
		skin.add("table.cell.size", cellSize);
		skin.add("menu.height", menuHeight);
		skin.add("defScale", defScale);

		Sprite backSprite = atlas.createSprite("main_back");
		backSprite.setColor(intColor(20, 115, 154));
		skin.add("background", new TextureRegionDrawable(backSprite), Drawable.class);

		skin.add("menu.background", new NinePatchDrawable(new NinePatch(buttonFrame, intColor(14, 103, 107))), Drawable.class);
		skin.add("menu.button.stretch", new NinePatchDrawable(new NinePatch(buttonFrame, intColor(30, 175, 45))), Drawable.class);
		skin.add("menu.button.stretch.icon1", atlas.createSprite("stretch_on"));
		skin.add("menu.button.stretch.icon2", atlas.createSprite("stretch_off"));
		skin.add("menu.button.reset", new NinePatchDrawable(new NinePatch(buttonFrame, intColor(157, 167, 19))), Drawable.class);
		skin.add("menu.button.reset.icon", atlas.createSprite("reset"));
		skin.add("menu.button.undo", new NinePatchDrawable(new NinePatch(buttonFrame, intColor(126, 19, 167))), Drawable.class);
		skin.add("menu.button.undo.icon", atlas.createSprite("undo"));
		skin.add("menu.button.redo", new NinePatchDrawable(new NinePatch(buttonFrame, intColor(126, 19, 167))), Drawable.class);
		skin.add("menu.button.redo.icon", atlas.createSprite("redo"));
		skin.add("menu.button.exit", new NinePatchDrawable(new NinePatch(buttonFrame, intColor(200, 36, 36))), Drawable.class);
		skin.add("menu.button.exit.icon", atlas.createSprite("power_off"));

		NinePatch tableFrame = atlas.createPatch("table_frame");
		float frameGap = tableFrame.getLeftWidth();
		skin.add("table.frame.gap", frameGap);
		skin.add("table.frame", ninePathDrawable(tableFrame, intColor(50, 124, 30)), Drawable.class);
		skin.add("table.back", new TiledDrawable(atlas.createSprite("open")), Drawable.class);
		skin.add("table.selected", new TextureRegionDrawable(atlas.createSprite("select")), Drawable.class);
		skin.add("table.cell.closed", atlas.createSprite("close"));

		Figure[] values = Figure.values();
		for (int i = 0; i < values.length; i++) {
			int index = i + 1;
			skin.add("figure.f" + index, atlas.createSprite("f" + index));
		}

		return skin;
	}

	private Sprite getSprite(TextureAtlas atlas, String spriteName, float scale) {
		Sprite sprite = atlas.createSprite(spriteName);
		sprite.scale(scale);
		return sprite;
	}

	private NinePatchDrawable ninePathDrawable(NinePatch ninePatch, Color color) {
		ninePatch.setColor(color);
		return new NinePatchDrawable(ninePatch);
	}

	private void start() {
		if (tableModelDao.canBeResumed()) {
			resumeGame();
		} else {
			newDefaultGame();
		}
	}

	private void setInputProcessor() {
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(inputMultiplexer);
		Gdx.input.setCatchBackKey(true);
	}


	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
		backgroundImage.setSize(width, height);
		backgroundImage.layout();

		vMenu.setViewRect(0f, height - menuHeight, width, menuHeight);
		vTable.setViewRect(0f, 0f, width, height - menuHeight);
	}

	@Override
	public void render() {
//		fpsLogger.log();
		if (continuousRedering) {
			taskManager.update(Gdx.graphics.getDeltaTime());
		}
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		stage.draw();
		continuousRedering = !taskManager.isEmpty();
		Gdx.graphics.setContinuousRendering(continuousRedering);
//		Gdx.app.log("dip", stage.getSpriteBatch().renderCalls+"");
	}

	@Override
	public void pause() {
		save();
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}

	public void exit() {
		save();
		Gdx.app.exit();
	}

	private void newGame(int columnCount, int rowCount) {
		TableModel tableModel = new TableModel(columnCount, rowCount);
		startGame(GameModel.create(tableModel), true);
	}

	private void save() {
		saveTable();
		savePreferences();
	}

	private void savePreferences() {
		settings.flush();
	}

	private void saveTable() {
		tableModelDao.saveGame(gameModel);
	}

	private void removeSavedGame() {
		tableModelDao.removeSavedGame();
	}

	public void resumeGame() {
		if (tableModelDao.canBeResumed()) {
			GameModel gameModel = tableModelDao.getSavedGameModel();
			if (gameModel == null) {
				Gdx.app.error("Main", "GameModel not loaded");
				newDefaultGame();
			} else {
				startGame(gameModel, false);
			}
		}
	}

	public void newDefaultGame() {
		newGame(14, 7);
	}

	public void startGame(GameModel gameModel, boolean withInitMove) {
		this.gameModel = gameModel;
		_tableController = new TableController();
		_tableController.SetTable(gameModel.tableModel);
		_tableController.setHistory(gameModel.actionHistory);

		vTable.set(_tableController, gameModel.tableModel);
		new EndGameListener(this, _tableController);
		//		_tableController.AddListener(new TableControllerLogger());

		_tableController.StartGame();
		if (withInitMove) {
			_tableController.makeFirstMove();
		}

	}

	public void gameEnded(boolean win) {
		removeSavedGame();
		Runnable resetGame = new Runnable() {
			@Override
			public void run() {
				resetGame();
			}
		};
		if (win) {
			taskManager.addTaskInQueue(new RunnableTask(resetGame));
		} else {
			taskManager.addTaskInQueue(new RunnableTask(resetGame));
		}
	}

	public void iWantWin() {
		_tableController.debugPreWin();
	}

	public void resetGame() {
		_tableController.clearTable();
		_tableController.makeFirstMove();
	}

	public void undo() {
		_tableController.undo();
	}

	public void redo() {
		_tableController.redo();
	}

	public void animationExit() {
		final float TIME_BEFORE_EXIT = 0.5f;

		STrack trackForMenu = Util.createTrack(SInterpolator.def, new SvsPosition2(vMenu), new SKeyFrame[]{
				new SKeyFrame(0f, new float[]{vMenu.getX(), vMenu.getY()}),
				new SKeyFrame(TIME_BEFORE_EXIT, new float[]{0, stage.getHeight()})
		});

		Runnable exitRunnable = new Runnable() {
			@Override
			public void run() {
				exit();
			}
		};

		taskManager.startTask(new TrackTask(trackForMenu));
		taskManager.startTask(new DeferredTask(TIME_BEFORE_EXIT, exitRunnable));
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
			exit();
			return true;
		}
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean keyTyped(char character) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean scrolled(int amount) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	class EndGameListener implements ITableListener {
		private Main _main;

		public EndGameListener(Main main, TableController tableController) {
			_main = main;
			tableController.addListener(this);
		}

		public void onStartGame() {
		}

		public void onAddFigure(Point point, Figure figure) {
		}

		public void onMoveFailure() {
		}

		public void onMoveFigure(MoveFigureResult result) {
		}

		public void onWin() {
			_main.gameEnded(true);
		}

		public void onLose() {
			_main.gameEnded(false);
		}

		public void onRemoveFigure(RemoveFigureResult result) {
		}

		public void onCellOpenedChanged(CellOpenChangedResult result) {
		}

		public void onClearTable() {
		}
	}
}
