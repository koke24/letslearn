package com.kokesoft.challenges.fall2022;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.junit.Test;

import com.kokesoft.challenges.Dijkstra;
import com.kokesoft.challenges.Dijkstra.Graph;
import com.kokesoft.challenges.Dijkstra.Node;
import com.kokesoft.challenges.fall2022.Player.Robot;
import com.kokesoft.challenges.fall2022.Player.Table;

public class PlayerTest {

	//@Test
	public void testShortestPath() {
		Playground playground = null;
		try(CsvScanner is = new CsvScanner(getClass().getResourceAsStream("table1.csv"))) {
			playground = new Playground(is);
		} catch(IOException e) {
			fail(e.toString());
		}
		Table table = new Table(new Scanner(playground.serialize()));
		Node startNode = table.loadNode(0, 1);
		Graph g = Dijkstra.calculateShortestPathFromSource(new Graph(), startNode);
		for(Node n: g.nodes()) {
			if ("(10,5)".equals(n.getName())) {
				System.out.println(String.format("%d - %s", n.getDistance(), n.getShortestPath().stream().map(tn -> tn.getName()).collect(Collectors.joining("-"))));
				break;
			}
		}
		g.clear();
		startNode = null;
		table.table[4][0].scrapAmount = 0;
		table.table[4][1].scrapAmount = 0;
		table.table[4][2].scrapAmount = 0;
		table.table[4][3].scrapAmount = 0;
		startNode = table.loadNode(0, 1);
		g = Dijkstra.calculateShortestPathFromSource(new Graph(), startNode);
		for(Node n: g.nodes()) {
			if ("(10,5)".equals(n.getName())) {
				System.out.println(String.format("%d - %s", n.getDistance(), n.getShortestPath().stream().map(tn -> tn.getName()).collect(Collectors.joining("-"))));
				break;
			}
		}
		g.clear();
		startNode = null;
		table.table[4][5].scrapAmount = 0;
		startNode = table.loadNode(0, 1);
		g = Dijkstra.calculateShortestPathFromSource(new Graph(), startNode);
		boolean found = false;
		for(Node n: g.nodes()) {
			if ("(10,5)".equals(n.getName())) {
				found = true;
				System.out.println(String.format("%d - %s", n.getDistance(), n.getShortestPath().stream().map(tn -> tn.getName()).collect(Collectors.joining("-"))));
				break;
			}
		}
		assertFalse(found);
	}
	
	@Test
	public void testMoveCommand() {
		Playground playground = null;
		try(CsvScanner is = new CsvScanner(getClass().getResourceAsStream("table1.csv"))) {
			playground = new Playground(is);
		} catch(IOException e) {
			fail(e.toString());
		}
		playground.createBlueRobot(2,1);
		playground.createBlueRobot(1,2);
		playground.createBlueRobot(3,2);
		playground.createBlueRobot(2,3);
		playground.adquireBlue(2,2);
		playground.createRedRobot(10,1);
		playground.createRedRobot(9,2);
		playground.createRedRobot(11,2);
		playground.createRedRobot(10,3);
		playground.adquireRed(10,2);
		Table t;
		try(Scanner sc = new Scanner(playground.serialize())) {
			t = new Table(sc);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("MOVE 1 3 2 4 2;");
		try(InputStream in = new ByteArrayInputStream(sb.toString().getBytes())) {
			playground.play(in);
		} catch(IOException e) {
			fail(e.toString());
		}
		// El que lee es el otro jugador
		Table t2;
		try(Scanner sc = new Scanner(playground.serialize())) {
			t2 = new Table(sc);
			t2.readTurn(sc);
			assertEquals(-1, t2.get(0,2).owner);
			assertEquals(1, t2.get(4,2).units);
			assertEquals(0, t2.get(4,2).owner);
			assertEquals(0, t2.get(3,2).units);
		}
		sb.setLength(0);
		sb.append("MOVE 1 9 2 8 2;MOVE 1 10 3 9 3");
		try(InputStream in = new ByteArrayInputStream(sb.toString().getBytes())) {
			playground.play(in);
		} catch(IOException e) {
			fail(e.toString());
		}
		try(Scanner sc = new Scanner(playground.serialize())) {
			t.readTurn(sc);
			assertEquals(1, t.get(4,2).units);
			assertEquals(1, t.get(4,2).owner);
			assertEquals(0, t.get(3,2).units);
			assertEquals(1, t.get(8,2).units);
			assertEquals(0, t.get(8,2).owner);
			assertEquals(0, t.get(9,2).units);
			assertEquals(1, t.get(9,3).units);
			assertEquals(0, t.get(9,3).owner);
			assertEquals(0, t.get(10,3).units);
		}
	}

	@Test
	public void testIdentifyRobot() {
		Playground playground = null;
		try(CsvScanner is = new CsvScanner(getClass().getResourceAsStream("table1.csv"))) {
			playground = new Playground(is);
		} catch(IOException e) {
			fail(e.toString());
		}
		playground.createBlueRobot(2,1);
		playground.createBlueRobot(1,2);
		playground.createBlueRobot(3,2);
		playground.createBlueRobot(2,3);
		playground.adquireBlue(2,2);
		playground.createRedRobot(10,1);
		playground.createRedRobot(9,2);
		playground.createRedRobot(11,2);
		playground.createRedRobot(10,3);
		playground.adquireRed(10,2);
		Table t;
		try(Scanner sc = new Scanner(playground.serialize())) {
			t = new Table(sc);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("MOVE 1 3 2 4 2;");
		try(InputStream in = new ByteArrayInputStream(sb.toString().getBytes())) {
			playground.play(in);
		} catch(IOException e) {
			fail(e.toString());
		}
		// El que lee es el otro jugador
		Table t2;
		try(Scanner sc = new Scanner(playground.serialize())) {
			t2 = new Table(sc);
			t2.readTurn(sc);
			assertEquals(-1, t2.get(0,2).owner);
			assertEquals(1, t2.get(4,2).units);
			assertEquals(0, t2.get(4,2).owner);
			assertEquals(0, t2.get(3,2).units);
		}
		sb.setLength(0);
		sb.append("MOVE 1 9 2 8 2;MOVE 1 10 3 9 3");
		try(InputStream in = new ByteArrayInputStream(sb.toString().getBytes())) {
			playground.play(in);
		} catch(IOException e) {
			fail(e.toString());
		}
		try(Scanner sc = new Scanner(playground.serialize())) {
			t.readTurn(sc);
			assertEquals(1, t.get(4,2).units);
			assertEquals(1, t.get(4,2).owner);
			assertEquals(0, t.get(3,2).units);
			assertEquals(1, t.get(8,2).units);
			assertEquals(0, t.get(8,2).owner);
			assertEquals(0, t.get(9,2).units);
			assertEquals(1, t.get(9,3).units);
			assertEquals(0, t.get(9,3).owner);
			assertEquals(0, t.get(10,3).units);
		}
	}
}
