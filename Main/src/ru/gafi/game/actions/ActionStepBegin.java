package ru.gafi.game.actions;

import java.util.ArrayList;
import java.util.List;

/**
* User: Michael
* Date: 27.05.13
* Time: 14:42
*/
public class ActionStepBegin extends GameAction {
	public long beginSeed;
	public long endSeed;
	public List<GameAction> actions = new ArrayList<>();

	public ActionStepBegin(long beginSeed) {
		super(GameActionType.StepBegin);
		this.beginSeed = beginSeed;
	}

	public void setEndSeed(long endSeed) {
		this.endSeed = endSeed;
	}

	public void add(GameAction action) {
		actions.add(action);
	}
}
