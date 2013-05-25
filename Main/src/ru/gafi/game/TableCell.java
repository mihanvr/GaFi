package ru.gafi.game;

public class TableCell {
	public Figure figure;
	public boolean opened;

	public TableCell(Figure figure, boolean opened) {
		this.figure = figure;
		this.opened = opened;
	}

}
