package ru.gafi.game;

import static ru.gafi.common.Util.transpose;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 20.05.13
 * Time: 20:20
 */
public enum Figure {

	f1(build(new int[][]{
			{0, 1, 0},
			{1, 1, 1}})),
	f2(build(new int[][]{
			{1, 1, 1},
			{0, 1, 0}})),
	f3(build(new int[][]{
			{0, 1, 0},
			{1, 1, 1},
			{0, 1, 0}})),
	f4(build(new int[][]{
			{0, 1, 0},
			{1, 0, 1},
			{0, 1, 0}})),
	f5(build(new int[][]{
			{1, 0, 1},
			{0, 1, 0},
			{1, 0, 1}}));

	public int[][] mask;

	Figure(int[][] mask) {
		this.mask = mask;
	}

	public int rowCount() {
		return mask[0].length;
	}

	public int columnCount() {
		return mask.length;
	}

	private static int[][] build(int[][] array) {
		int[][] transpose = transpose(array);
		reverseRows(transpose);
		return transpose;
	}

	private static void reverseRows(int[][] array) {
		int column = array.length;
		int row = array[0].length;

		for (int i = 0; i < column; i++) {
			for (int j = 0; j < row / 2; j++) {
				int tmp = array[i][j];
				array[i][j] = array[i][row - j - 1];
				array[i][row - j - 1] = tmp;
			}
		}
	}

}
