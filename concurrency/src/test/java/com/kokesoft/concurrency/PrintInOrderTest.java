package com.kokesoft.concurrency;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class PrintInOrderTest {
	
	protected void wait(int secs) {
		try {
			Thread.sleep(secs*1000);
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void callPrint(int n, Printer printer) {
		switch(n) {
		case 1: printer.first(); break;
		case 2: printer.second(); break;
		case 3: printer.third(); break;
		}
	}
	
	protected Printer doTest(int [] nums) {
		Printer printer = new Printer();
		List<Thread> lt = Arrays.asList(
				new Thread(() -> callPrint(nums[0], printer)),
				new Thread(() -> callPrint(nums[1], printer)),
				new Thread(() -> callPrint(nums[2], printer)));
		lt.forEach(t -> t.start());
		lt.forEach(t -> {
			try {
				t.join();
			} catch(InterruptedException e) {
				fail(e.toString());
			}
		});
		return printer;
	}

	@Test
	public void oneTest() {
		int [] nums = {1,2,3};
		Printer printer = doTest(nums);
		assertEquals("firstsecondthird", printer.printed());
	}

	@Test
	public void twoTest() {
		int [] nums = {1,3,2};
		Printer printer = doTest(nums);
		assertEquals("firstsecondthird", printer.printed());
	}

	@Test
	public void threeTest() {
		int [] nums = {3,1,2};
		Printer printer = doTest(nums);
		assertEquals("firstsecondthird", printer.printed());
	}
}
