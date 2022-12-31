package com.kokesoft.challenges;

import static org.junit.Assert.*;

import java.util.stream.Collectors;

import org.junit.Test;

import com.kokesoft.challenges.Dijkstra.Graph;
import com.kokesoft.challenges.Dijkstra.Node;

public class DijkstraTest {

	@Test
	public void oneTest() {
		Dijkstra dijkstra = new Dijkstra();
		Node nodeA = dijkstra.addNode("A");
		Node nodeB = new Node("B");
		Node nodeC = new Node("C");
		Node nodeD = new Node("D");
		Node nodeE = new Node("E");
		Node nodeF = new Node("F");
		nodeA.connect(nodeB, 10)
			.connect(nodeC, 15);
		nodeB.connect(nodeF, 15)
			.connect(nodeD, 12);
		nodeD.connect(nodeF, 1)
			.connect(nodeE, 2);
		nodeF.connect(nodeE, 5);
		nodeC.connect(nodeE, 10);
		Graph graph = Dijkstra.calculateShortestPathFromSource(new Graph(), nodeA);
		for(Node dest: graph.nodes()) {
			if ("B".equals(dest.getName())) {
				assertEquals("A", dest.getShortestPath().stream().map(n -> n.getName()).collect(Collectors.joining("-")));
			} else
			if ("D".equals(dest.getName())) {
				assertEquals("A-B", dest.getShortestPath().stream().map(n -> n.getName()).collect(Collectors.joining("-")));
			} else
			if ("F".equals(dest.getName())) {
				assertEquals("A-B-D", dest.getShortestPath().stream().map(n -> n.getName()).collect(Collectors.joining("-")));
			} else
			if ("E".equals(dest.getName())) {
				assertEquals("A-B-D", dest.getShortestPath().stream().map(n -> n.getName()).collect(Collectors.joining("-")));
			} else
			if ("C".equals(dest.getName())) {
				assertEquals("A", dest.getShortestPath().stream().map(n -> n.getName()).collect(Collectors.joining("-")));
			}
		}
	}
}
