package ru.gafi.game;

import ru.gafi.common.Point;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 20.05.13
 * Time: 20:54
 */
public class PathFinder {
	private static Point[] NeighborNodes = new Point[]{
			new Point(-1, 0),
			new Point(1, 0),
			new Point(0, -1),
			new Point(0, 1),
	};
	private Set<Point> closeSet;
	private SortedSet<Point, Node> openSet;

	public PathFinder() {
		closeSet = new HashSet<>();
		openSet = new SortedSet<>();
	}

	public FindPathResult find(TableModel tableModel, Point start, Point finish) {
		closeSet.clear();
		openSet.clear();

		openSet.add(start, new Node(null, start, cost(start, finish)));

		while (!openSet.isEmpty()) {
			Node current = openSet.poll();
			if (current.point.equals(finish)) {
				return new FindPathResult(true, ReconstructPath(current));
			}
			closeSet.add(current.point);
			for (int i = 0; i < NeighborNodes.length; i++) {
				Point neighbornPoint = new Point(current.point.x + NeighborNodes[i].x,
						current.point.y + NeighborNodes[i].y);
				if (neighbornPoint.x < 0 || neighbornPoint.y < 0 || neighbornPoint.x >= tableModel.columnCount() ||
						neighbornPoint.y >= tableModel.rowCount()) continue;
				if (closeSet.contains(neighbornPoint)) continue;
				if (tableModel.getCell(neighbornPoint.x, neighbornPoint.y).figure != null) continue;
				int tentativeCost = current.cost + 1;
				if (openSet.containsKey(neighbornPoint)) {
					Node open = openSet.get(neighbornPoint);
					if (tentativeCost >= open.cost) {
						continue;
					}
				}
				Node node = new Node(current, neighbornPoint, tentativeCost);
				openSet.add(neighbornPoint, node);
			}
		}
		return new FindPathResult(false, null);
	}

	private static Point[] ReconstructPath(Node node) {
		List<Point> list = new ArrayList<>();
		while (node != null) {
			list.add(node.point);
			node = node.prev;
		}
		Point[] array = new Point[list.size()];
		Collections.reverse(list);
		list.toArray(array);
		return array;
	}

	private static int cost(Point from, Point to) {
		return Math.abs(to.x - from.x) + Math.abs(to.y - from.y);
	}

	private class Node {
		public int cost;
		public Point point;
		public Node prev;

		public Node(Node prev, Point point, int cost) {
			this.prev = prev;
			this.point = point;
			this.cost = cost;
		}
	}

	private class SortedSet<K, T> {
		private Map<K, T> dict = new HashMap<>();
		private Queue<K> queue = new LinkedList<>();

		public T get(K key) {
			return dict.get(key);
		}

		public void add(K key, T value) {
			if (dict.containsKey(key)) {
				dict.put(key, value);
			} else {
				dict.put(key, value);
				queue.add(key);
			}
		}

		public T poll() {
			K key = queue.poll();
			T value = dict.get(key);
			dict.remove(key);
			return value;
		}

		public boolean containsKey(K key) {
			return dict.containsKey(key);
		}

		public boolean isEmpty() {
			return dict.isEmpty();
		}

		public void clear() {
			dict.clear();
			queue.clear();
		}
	}

	public class FindPathResult {
		public boolean pathFinded;
		public Point[] path;

		public FindPathResult(boolean pathFinded, Point[] path) {
			this.pathFinded = pathFinded;
			this.path = path;
		}
	}
}
