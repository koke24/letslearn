package com.kokesoft.challenges;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReverseTest {
	
	static class Node implements Cloneable {
		int a;
		Node next;
		public Node(int a, Node next) {
			this.a = a;
			this.next = next;
		}
		@Override
		public Object clone() {
			return deepClone();
		}
		public Object deepRecursiveClone() {
			return new Node(this.a, this.next!=null?(Node)this.next.clone():null);
		}
		public Object deepClone() {
			Node h = new Node(this.a, null), c = h;
			Node current = this.next;
			while(current!=null) {
				Node n = new Node(current.a, null);
				c.next = n;
				current = current.next;
			}
			return h;
		}
		public Object shallowClone() {
			return new Node(this.a, this.next);
		}
		public String toString() {
			return a+(next!=null?","+next.toString():"");
		}
		public boolean equals(Object o) {
			if (o==null) return false;
			if (!(o instanceof Node)) return false;
			Node n = (Node)o;
			if (a!=n.a) return false;
			if (next!=null) return next.equals(n.next);
			return next==n.next;
		}
	}
	
	public Node reverse1(Node n) {
		if (n.next!=null) {
			Node nn = reverse1(n.next);
			Node tn = nn;
			while(tn.next!=null) tn = tn.next;
			tn.next = new Node(n.a, null);
			return nn;
		} else return new Node(n.a, null);
	}
	
	public Node reverse2(Node n) {
		return preverse(n)[0];
	}

	private Node [] preverse(Node n) {
		if (n.next!=null) {
			Node [] nn = preverse(n.next);
			if (nn[1]==null) nn[1] = nn[0];
			nn[1].next = new Node(n.a, null);
			nn[1] = nn[1].next;
			return nn;
		} else return new Node[] {new Node(n.a, null), null };
	}
	
	public Node reverse3(Node n) {
		return reverse3(n, null);
	}
	private Node reverse3(Node n, Node append) {
		if (n.next!=null)
			return reverse3(n.next, new Node(n.a, append));
		return new Node(n.a, append);
	}
	
	public Node reverse4(Node n) {
		Node head = null, current = n;
		while(current!=null) {
			Node t = current.next;
			current.next = head;
			head = current;
			current = t;
		}
		return head;
	}

	public Node reverse5(Node n) {
		Node head = null, current = n;
		while(current!=null) {
			Node t = (Node)current.clone();
			t.next = head;
			head = t;
			current = current.next;
		}
		return head;
	}

	@Test
	public void cloneTest() {
		Node f = new Node(4, new Node(6, null));
		System.out.println(f);
		assertEquals(f, f.clone());
	}
	@Test
	public void reverseTest2() {
		Node f = new Node(4, new Node(6, null));
		System.out.println(f);
		assertEquals("4,6", f.toString());
		Node nf = reverse1(f);
		assertEquals("6,4", nf.toString());
		nf = reverse2(f);
		assertEquals("6,4", nf.toString());
		nf = reverse3(f);
		assertEquals("6,4", nf.toString());
		nf = reverse5(f);
		assertEquals("6,4", nf.toString());
		f = reverse4(f);
		assertEquals("6,4", f.toString());
		f = reverse4(f);
		assertEquals("4,6", f.toString());
	}
	@Test
	public void reverseTest3() {
		Node f = new Node(4, new Node(6, new Node(1, null)));
		System.out.println(f);
		assertEquals("4,6,1", f.toString());
		Node nf = reverse1(f);
		assertEquals("1,6,4", nf.toString());
		nf = reverse2(f);
		assertEquals("1,6,4", nf.toString());
		nf = reverse3(f);
		assertEquals("1,6,4", nf.toString());
		nf = reverse5(f);
		assertEquals("1,6,4", nf.toString());
		f = reverse4(f);
		assertEquals("1,6,4", f.toString());
		f = reverse4(f);
		assertEquals("4,6,1", f.toString());
	}
	@Test
	public void reverseTest() {
		Node f = new Node(4, new Node(6, new Node(2, new Node(9, null))));
		System.out.println(f);
		assertEquals("4,6,2,9", f.toString());
		Node nf = reverse1(f);
		assertEquals("9,2,6,4", nf.toString());
		nf = reverse2(f);
		assertEquals("9,2,6,4", nf.toString());
		nf = reverse3(f);
		assertEquals("9,2,6,4", nf.toString());
		nf = reverse5(f);
		assertEquals("9,2,6,4", nf.toString());
		f = reverse4(f);
		assertEquals("9,2,6,4", f.toString());
		f = reverse4(f);
		assertEquals("4,6,2,9", f.toString());
	}
}
