package com.kokesoft.concurrency;

import static java.lang.String.format;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.Test;

public class ConcurrencyTest {
	
	protected static abstract class DoneableRunner implements Runnable {
		Boolean done = false;
		@Override
		public void run() {
			done = null;
			try {
				runner();
			} finally {
				done = true;
			}
		}
		public Boolean getDone() {
			return done;
		}
		abstract void runner();
	}
	
	public static class Runner extends DoneableRunner {
		int secs;
		public Runner() { this(10); }
		public Runner(int secs) {
			this.secs = secs;
		}
		@Override
		public void runner() {
			try {
				System.out.println(format("Doing something long... (%s)", Thread.currentThread().getName()));
				int sec = 0;
				while(sec<secs) {
					for(int i = 0; i<10; i++)
						Thread.sleep(100);
					sec++;
				}
				System.out.println(format("Done! (%s)", Thread.currentThread().getName()));
			} catch (InterruptedException e) {
				System.err.println(e.toString());
			}
		}
	}
	
	@Test
	public void testOne() {
		try {
			Thread t1 = new Thread(new Runner(5), "one");
			t1.start();
			synchronized (t1) {
				t1.join(30000);
			}
		} catch (InterruptedException e) {
			fail(e.toString());
		}
	}

	@Test
	public void testTwo() {
		try {
			Thread t1 = new Thread(new Runner(5), "one");
			Thread t2 = new Thread(new Runner(5), "two");
			t1.start();
			synchronized (t1) {
				t1.wait(30000);
				t2.start();
			}
			synchronized (t2) {
				t2.join(30000);
			}
		} catch (InterruptedException e) {
			fail(e.toString());
		}
	}

	@Test
	public void testThree() {
		try {
			Thread t1 = new Thread(new Runner(7), "one");
			Thread t2 = new Thread(new Runner(5), "two");
			t1.start();
			t2.start();
			synchronized (t1) {
				t1.join(30000);
			}
			synchronized (t2) {
				t2.join(30000);
			}
		} catch (InterruptedException e) {
			fail(e.toString());
		}
	}

	public static class Consumer extends DoneableRunner {
		Boolean done = false;
		Queue<String> q;
		public Consumer(Queue<String> q) { this.q = q; }
		@Override
		public void runner() {
			try {
				System.out.println(format("Waiting for messages... (%s)", Thread.currentThread().getName()));
				int msgs = 0;
				outer: while(true) {
					String msg = q.poll();
					while(msg!=null) {
						if ("exit".equalsIgnoreCase(msg))
							break outer;
						System.out.println("Received message: "+msg);
						msgs += msg!=null?1:0;
						msg = q.poll();
					}
					synchronized (q) {
						q.wait(10000);
					}
					if (q.isEmpty()) {
						System.err.println("queue empty after 10 secs. Exiting");
						break;
					}
				}
				System.out.println(format("Done! processed %d messages (%s)", msgs, Thread.currentThread().getName()));
			} catch (InterruptedException e) {
				System.err.println(e.toString());
			}
		}
	}
	
	public static class Producer extends DoneableRunner {
		List<String> msgs = Arrays.asList("one", "two", "three");
		Queue<String> q;
		public Producer(Queue<String> q) { this.q = q; }
		public Producer(Queue<String> q, List<String> msgs) { this.q = q; this.msgs = msgs; }
		@Override
		public void runner() {
			for(int i = 0; i<msgs.size(); i++) {
				System.out.println("Sending "+msgs.get(i));
				q.offer(msgs.get(i));
				synchronized (q) { q.notify(); }
				try {
					Thread.sleep(Math.round(1000+3000*Math.random()));
				} catch (InterruptedException e) {
					System.err.println(e.toString());
				}
			}
			q.offer("exit");
			synchronized (q) { q.notify(); }
			System.out.println("No more messages!");
		}
	}

	@Test
	public void testFour() {
		try {
			Queue<String> q = new LinkedList<String>();
			Thread producer = new Thread(new Producer(q), "producer");
			Thread consumer = new Thread(new Consumer(q), "consumer");
			consumer.start();
			producer.start();
			synchronized (consumer) {
				consumer.join();
			}
		} catch (InterruptedException e) {
			fail(e.toString());
		}
	}

}
