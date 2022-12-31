package com.kokesoft.concurrency;

import static java.lang.String.format;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import org.junit.Test;

import com.kokesoft.concurrency.ConcurrencyTest.DoneableRunner;

public class ConcurrencyAdvancedTest {
	
	public static class Message {
		String id;
		int msecs;
		public Message(String id, int msecs) {
			this.id = id;
			this.msecs = msecs;
		}
		public Message(String id) {
			this(id, 100);
		}
	}

	public static class Consumer extends DoneableRunner {
		Boolean done = false;
		BlockingQueue<Message> q;
		int nmsgs = 0;
		public Consumer(BlockingQueue<Message> q) { this.q = q; }
		@Override
		public void runner() {
			try {
				System.out.println(format("Waiting for messages... (%s)", Thread.currentThread().getName()));
				outer: while(true) {
					Message msg = q.take();
					if ("exit".equalsIgnoreCase(msg.id)) {
						q.put(msg);
						break outer;
					}
					System.out.println(format("Received message %s (%s)", msg.id, Thread.currentThread().getName()));
					try {
						Thread.sleep(msg.msecs);
					} catch (InterruptedException e) {
						System.err.println(e.toString());
					}
					nmsgs++;
				}
				System.out.println(format("Done! processed %d messages (%s)", nmsgs, Thread.currentThread().getName()));
			} catch (InterruptedException e) {
				System.err.println(e.toString());
			}
		}
		public int getNumberMsgs() {
			return nmsgs;
		}
		
	}
	
	public static class Producer extends DoneableRunner {
		List<String> msgs = Arrays.asList("one", "two", "three");
		BlockingQueue<Message> q;
		public Producer(BlockingQueue<Message> q) { this.q = q; }
		public Producer(BlockingQueue<Message> q, List<String> msgs) { this.q = q; this.msgs = msgs; }
		@Override
		public void runner() {
			for(int i = 0; i<msgs.size(); i++) {
				System.out.println("Sending "+msgs.get(i));
				try {
					q.put(new Message(msgs.get(i), (int)Math.round(1000+3000*Math.random())));
					Thread.sleep(Math.round(100+300*Math.random()));
				} catch (InterruptedException e) {
					System.err.println(e.toString());
				}
			}
			try {
				q.put(new Message("exit"));
			} catch (InterruptedException e) {
				System.err.println(e.toString());
			}
			System.out.println("No more messages!");
		}
	}

	@Test
	public void testQueue() {
		try {
			BlockingQueue<Message> q = new LinkedBlockingDeque<Message>();
			List<String> msgs = Arrays.asList(new String[] {
				"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen"
			});
			Thread producer = new Thread(new Producer(q, msgs), "producer");
			List<Consumer> consumers = Arrays.asList(new Consumer(q),
					new Consumer(q),
					new Consumer(q),
					new Consumer(q),
					new Consumer(q)
			); 
			int [] contador = { 1 };
			List<Thread> threads = consumers.stream().map(c -> { return new Thread(c, "consumer-"+contador[0]++); }).collect(Collectors.toList());
			threads.forEach(c -> c.start());
			producer.start();
			threads.forEach(c -> {
				try {
					synchronized (c) {
						c.join(30000);
					}
				} catch (InterruptedException e) {
					System.err.println(e.toString());
				}
			});
			int nmsgs = consumers.stream().map(t -> t.getNumberMsgs()).reduce(0, Integer::sum);
			assertEquals(msgs.size(), nmsgs);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
}
