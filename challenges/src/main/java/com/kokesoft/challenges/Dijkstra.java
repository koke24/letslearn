package com.kokesoft.challenges;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Dijkstra {

	protected static class ConnectedNode {
		Node b;
		int distance;
		protected ConnectedNode(Node b, int distance) {
			this.b = b;
			this.distance = distance;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((b == null) ? 0 : b.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConnectedNode other = (ConnectedNode) obj;
			if (b == null) {
				if (other.b != null)
					return false;
			} else if (!b.equals(other.b))
				return false;
			return true;
		}
		protected Node getB() {
			return b;
		}
		protected int getDistance() {
			return distance;
		}
	}
	public static class Node {
		Set<ConnectedNode> adjacents = new HashSet<>();
		String name;
		int id;
		transient int distance = Integer.MAX_VALUE;
		transient List<Node> shortestPath = new LinkedList<>();
		static int idCounter = 1;
		public Node() {
			id = idCounter++;
		}
		public Node(String name) {
			this();
			this.name = name;
		}
		@Override
		public String toString() {
			return String.format("%s (%d)", name, id);
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (id != other.id)
				return false;
			return true;
		}
		public Node connect(Node node, int distance) {
			return withAdjacent(node, distance);
		}
		public Node withAdjacent(Node node, int distance) {
			adjacents.add(new ConnectedNode(node, distance));
			return this;
		}
		public Iterable<ConnectedNode> adjacents() {
			return new ArrayList<>(adjacents);
		}
		public String getName() {
			return name;
		}
		public int getId() {
			return id;
		}
		public int getDistance() {
			return distance;
		}
		public void setDistance(int distance) {
			this.distance = distance;
		}
		public List<Node> getShortestPath() {
			return shortestPath;
		}
		public void setShortestPath(List<Node> shortestPath) {
			this.shortestPath = shortestPath;
		}
	}
	
	public static class Graph {
		Set<Node> nodes = new HashSet<>();
		public void addNode(Node node) {
			nodes.add(node);
			for(ConnectedNode adjacent: node.adjacents()) {
				if (nodes.contains(adjacent.getB()))
					continue;
				addNode(adjacent.getB());
			}
		}
		public Iterable<Node> nodes() {
			return new ArrayList<>(nodes);
		}
		public void clear() {
			nodes.clear();
		}
		@Override
		public int hashCode() {
			int [] n = {0};
			nodes.forEach(node -> n[0] = n[0]*37+node.id);
			return n[0];
		}
		@Override
		public boolean equals(Object obj) {
			if (this==obj)
				return true;
			if (obj==null)
				return false;
			if (!getClass().isInstance(obj))
				return false;
			Graph graph = (Graph)obj;
			if (nodes.size()!=graph.nodes.size())
				return false;
			for(Iterator<Node> i1 = nodes.iterator(), i2 = graph.nodes.iterator(); i1.hasNext() && i2.hasNext();)
				if (!i1.next().equals(i2.next()))
					return false;
			return true;
		}
		
	}
	
	List<Node> nodes = new ArrayList<>();
	
	public Node addNode(String name) {
		Node node = new Node(name);
		nodes.add(node);
		return node;
	}

	public Node addNode(Node node) {
		nodes.add(node);
		return node;
	}
	
	public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
	    
		source.setDistance(0);
		
		if (graph.nodes.isEmpty()) {
			graph.addNode(source);
		}

	    Set<Node> settledNodes = new HashSet<>();
	    Set<Node> unsettledNodes = new HashSet<>();

	    unsettledNodes.add(source);

	    while (unsettledNodes.size() != 0) {
	        Node currentNode = getLowestDistanceNode(unsettledNodes);
	        unsettledNodes.remove(currentNode);
	        for (ConnectedNode adjacent: currentNode.adjacents) {
	            Node adjacentNode = adjacent.getB();
	            Integer edgeWeight = adjacent.getDistance();
	            if (!settledNodes.contains(adjacentNode)) {
	                calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
	                unsettledNodes.add(adjacentNode);
	            }
	        }
	        settledNodes.add(currentNode);
	    }
	    return graph;
	}	

	private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
	    Node lowestDistanceNode = null;
	    int lowestDistance = Integer.MAX_VALUE;
	    for (Node node: unsettledNodes) {
	        int nodeDistance = node.getDistance();
	        if (nodeDistance < lowestDistance) {
	            lowestDistance = nodeDistance;
	            lowestDistanceNode = node;
	        }
	    }
	    return lowestDistanceNode;
	}	
	private static void calculateMinimumDistance(Node evaluationNode,
	  Integer edgeWeigh, Node sourceNode) {
	    Integer sourceDistance = sourceNode.getDistance();
	    if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
	        evaluationNode.setDistance(sourceDistance + edgeWeigh);
	        LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
	        shortestPath.add(sourceNode);
	        evaluationNode.setShortestPath(shortestPath);
	    }
	}
}
