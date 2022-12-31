package com.kokesoft.concurrency;

import java.util.ArrayList;
import java.util.List;

public class Printer {
	Boolean [] first = new Boolean[] { false };
	Boolean [] second = new Boolean[] { false };
	Boolean [] third = new Boolean[] { false };
	
	List<String> printed = new ArrayList<>();
	
	public void first() {
		synchronized (first) {
			printed.add("first");
			first[0] = true;
			first.notify();
		}
	}
	public void second() {
		synchronized (second) {
			synchronized (first) {
				while(!first[0]) {
					try {
						first.wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
			printed.add("second");
			second[0] = true;
			second.notify();
		}
	}
	public void third() { 
		synchronized (third) {
			synchronized (second) {
				while(!second[0]) {
					try {
						second.wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
			printed.add("third");
			third[0] = true;
			third.notify();
		}
	}
	public String printed() {
		return String.join("", printed);
	}
}
