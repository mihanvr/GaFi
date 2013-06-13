package ru.gafi.game;

import ru.gafi.game.actions.ActionStepBegin;
import ru.gafi.game.actions.GameAction;

/**
 * User: Michael
 * Date: 13.06.13
 * Time: 12:33
 */
public class ActionHistoryDummy extends ActionHistory {
	public ActionHistoryDummy() {
		super(0);
	}

	@Override
	public void startRecord(long beginSeed) {

	}

	@Override
	public void stopRecord(long endSeed) {

	}

	@Override
	public void push(GameAction action) {

	}
}
