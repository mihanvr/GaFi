package ru.gafi.game;

import ru.gafi.game.actions.ActionStepBegin;
import ru.gafi.game.actions.GameAction;

import java.util.Stack;

/**
 * User: Michael
 * Date: 27.05.13
 * Time: 15:56
 */
public class ActionHistory {
	public Stack<ActionStepBegin> qUndo = new Stack<>();
	public Stack<ActionStepBegin> qRedo = new Stack<>();
	public long currentSeed;
	private ActionStepBegin recordAction;

	public ActionHistory(long currentSeed) {
		this.currentSeed = currentSeed;
	}

	public void startRecord(long beginSeed) {
		recordAction = new ActionStepBegin(beginSeed);
	}

	public void stopRecord(long endSeed) {
		recordAction.setEndSeed(endSeed);
		qUndo.push(recordAction);
		currentSeed = endSeed;
	}

	public void push(GameAction action) {
		qRedo.clear();
		if (recordAction != null) {
			recordAction.add(action);
		}
	}

	public ActionStepBegin popUndo() {
		if (qUndo.size() > 0) {
			ActionStepBegin action = qUndo.pop();
			qRedo.push(action);
			currentSeed = action.beginSeed;
			return action;
		}
		return null;
	}

	public ActionStepBegin popRedo() {
		if (qRedo.size() > 0) {
			ActionStepBegin action = qRedo.pop();
			qUndo.push(action);
			currentSeed = action.endSeed;
			return action;
		}
		return null;
	}

	public boolean isUndoEmpty() {
		return qUndo.isEmpty();
	}

	public boolean isRedoEmpty() {
		return qRedo.isEmpty();
	}

	public void clear() {
		qUndo.clear();
		qRedo.clear();
	}
}
