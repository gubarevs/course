package ua.khai.hubarev.course;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;


public class Graph {
	
	public static class Arc {
		final int from, to;	// Концы дуги
		final int capacity;	// Пропускная способность дуги
		final int length;
		int flow = 0;// Величина потока по дуге
		
		public Arc(int from, int to, int capacity,int length) {
			this.from = from;
			this.to = to;
			this.capacity = capacity;
			this.length=length;
		}
	}
	
	/**
	 * Количество вершин графа.
	 */
	private final int count;
	
	/**
	 * Источник и сток сети.
	 */
	int source, sink;
	
	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getSink() {
		return sink;
	}

	public void setSink(int sink) {
		this.sink = sink;
	}
	private final List<Arc> arcs = new ArrayList<>();
	
	private final List<Arc>[] graph;
	
	@SuppressWarnings("unchecked")
	public Graph(int n, int source, int sink) {
		if (n < 2) throw new IllegalArgumentException("There should exist at least 2 nodes");
		// Инициализация списков смежности.
		graph = new List[count = n];
		for (int i = 0; i < n; i++) graph[i] = new ArrayList<Arc>();
		// Инициализация истока и стока.
		this.source = source;
		this.sink = sink;
	}
	public void addArc(int from, int to, int capacity,int length) {
		if (from < 0 || to < 0 || from >= count || to >= count || capacity < 0) {
			throw new IllegalArgumentException("Invalid arc parameters");
		}
		Arc arc = new Arc(from, to, capacity,length);
		arcs.add(arc);
		graph[from].add(arc);
	}
	
	public void addArc(Arc arc) {
		if (arc.from < 0 || arc.to < 0 || arc.from >= count || arc.to >= count || arc.capacity < 0) {
			throw new IllegalArgumentException("Invalid arc parameters");
		}
		arcs.add(arc);
		graph[arc.from].add(arc);
	}
	public void removeArc(int from, int to) {
		// Поиск дуги в списке дуг
		Arc arcToRemove = null;
		for(Arc arc : graph[from]) {
			if (arc.from == from && arc.to == to) {
				arcToRemove = arc;
				break;
			}
		}
		if (null == arcToRemove) return;
		// Удаление дуги из списков смежности.
		graph[from].remove(arcToRemove);
		graph[to].remove(arcToRemove);
		arcs.remove(arcToRemove);
	}
	public int edmondsKarp() {
		// Существующий поток удаляется.
		clearFlow();
		while(searchPath() > 0) ;
		int maxFlow = 0;
		for (Arc arc : graph[source]) {
			maxFlow += arc.flow;
		}
		return maxFlow;
	}
	
	/**
	 * Обнуляет поток по всем дугам.
	 */
	private void clearFlow() {
		arcs.forEach(arc -> arc.flow = 0);
	}
	
	private int searchPath() {
		Arc[] path = new Arc[count];
		boolean[] passed = new boolean[count];
		LinkedList<Integer> queue = new LinkedList<>();
		
		// Обход по остаточной сети.
		queue.addLast(source);
		passed[source] = true;
		while (!queue.isEmpty()) {
			int u = queue.removeFirst();
			for (Arc arc : graph[u]) {
				int end = -1;
				if (arc.from == u && !passed[arc.to] && arc.flow < arc.capacity) {
					// Прямая дуга, можно увеличить поток вдоль дуги.
					end = arc.to;
				} else if (arc.to == u && !passed[arc.from] && arc.flow > 0) {
					end = arc.from;
				}
				if (end >= 0) {
					path[end] = arc;
					if (end == sink) {
						return modifyPath(path);
					} else {
						passed[end] = true;
						queue.add(end);
					}
				}
			}
		}
		return 0;
	}
	
	private int chainLength=0;
	
	public int getChainLength() {return chainLength;}
	
	private int splittersCount=0;
	
	public int getSplittersCount() {return splittersCount;}
	
	private int modifyPath(Arc[] p) {
		// Вычисляем максимально возможное увеличение потока вдоль пути.
		int addition = Integer.MAX_VALUE;
		int next = sink, pred;
		while (next != source) {
			Arc arc = p[next];
			if (arc.to == next) {
				pred = arc.from;
				if (addition > arc.capacity - arc.flow) {
					addition = arc.capacity - arc.flow;
				}
			} else {
				pred = arc.to;
				if (addition > arc.flow) {
					addition = arc.flow;
				}
			}
			next = pred;
		}
		// Производим изменение потока вдоль пути.
		next = sink;
		chainLength=0;
		splittersCount=0;
		while (next != source) {
			Arc arc = p[next];
			chainLength+=arc.length;
			splittersCount++;
			if (arc.to == next) {
				pred = arc.from;
				arc.flow += addition;
			} else {
				pred = arc.to;
				arc.flow -= addition;
			}
			next = pred;
		}
		
		
		return addition;
	}
	
	public static Graph build(int n, Stream<String> arcs, int source, int sink) {
		Graph g = new Graph(n, source, sink);
		arcs
			.filter(s -> s.matches("\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*"))
			.map(String::trim)
			.map((s -> s.split("\\s*,\\s*")))
			.forEach(triple -> g.addArc(Integer.parseInt(triple[0]),
					                    Integer.parseInt(triple[1]),
					                    Integer.parseInt(triple[2]),
					                    Integer.parseInt(triple[3])));
		return g;
	}
	
	public static Graph build(int n, Arc[] arcs, int source, int sink) {
		Graph g = new Graph(n, source, sink);
		for(Arc arc: arcs) {
			g.addArc(arc);
		}
		
		return g;
	}
}