package com.kokesoft.concurrency;

import static java.lang.String.format;
import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;

public class ExecutorTest {
	
	final static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());
	
	protected String log(String id, String msg) {
		return format("%s - %s - %s", DTF.format(Instant.now()), id, msg);
	}
	
	protected void randomWait(int secs, String id) {
		System.out.println(log(id, "Started"));
		try {
			int tsecs = 1+(int)Math.round(Math.floor(Math.random()*(secs-1.0)));
			System.out.println(log(id, format("Waiting for %d secs", tsecs)));
			Thread.sleep(tsecs*1000);
			System.out.println(log(id, "Finished"));
		} catch (InterruptedException e) {
			fail(e.toString());
		}
	}
	
	@Test
	public void threadPoolExecutorTest() {
		ExecutorService executorSvc = Executors.newFixedThreadPool(5);

		List<Future<String>> f = new ArrayList<>();

		int n = 25;
		for(int i = 0; i<n; i++) {
			String id = format("Task %d", i+1);
			System.out.println(log(id, "Submitted"));
			f.add(executorSvc.submit(() -> randomWait(10, id), "OK "+id));
		}
		try {
			executorSvc.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		List<String> results = f.stream()
				.map(fut -> {
					try {return fut.get();} catch(InterruptedException | ExecutionException e) { return null; } 
				})
				.filter(str -> str!=null)
				.collect(Collectors.toList());
		assertEquals(n, results.size());
		for(int i = 0; i<n; i++) {
			String id = format("OK Task %d", i+1);
			assertTrue(results.contains(id));
		}
	}

	@Test
	public void scheduledThreadExecutorTest() {
		ScheduledExecutorService executorSvc = Executors.newScheduledThreadPool(5);

		List<ScheduledFuture<String>> f = new ArrayList<>();
		int n = 25;
		for(int i = 0; i<n; i++) {
			String id = format("Task %d", i+1);
			int delay = (int)Math.round(Math.floor(10.0*Math.random()));
			System.out.println(log(id, format("Submitted with %d secs delay", delay)));
			f.add(executorSvc.schedule(() -> { randomWait(10, id); return "OK "+id; }, delay, TimeUnit.SECONDS));
		}
		try {
			executorSvc.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		List<String> results = f.stream()
				.map(fut -> {
					try {return fut.get();} catch(InterruptedException | ExecutionException e) { return null; } 
				})
				.filter(str -> str!=null)
				.collect(Collectors.toList());
		assertEquals(n, results.size());
		for(int i = 0; i<n; i++) {
			String id = format("OK Task %d", i+1);
			assertTrue(results.contains(id));
		}
	}
}
