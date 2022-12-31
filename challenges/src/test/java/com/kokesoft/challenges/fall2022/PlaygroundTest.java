package com.kokesoft.challenges.fall2022;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.kokesoft.challenges.fall2022.Playground.GameException;

public class PlaygroundTest {
	
	Playground p;
	
	@Before
	public void initPlayground() {
		p = new Playground(5, 5);
		p.set("04664",
			  "04604",
			  "20204",
			  "40644",
			  "46644"
		);
	}
	

	@Test
	public void buildTest() {
		try {
			p.adquireBlue(1, 0);
			p.play("BUILD 1 0");
		} catch(Exception e) {
			e.printStackTrace(System.err);
			fail(e.toString());
		}
	}
	@Test
	public void buildNotAllowedTest() {
		try {
			List<GameException> e = new ArrayList<>();
			p.play("BUILD 0 0", e);
			assertEquals(1, e.size());
		} catch(Exception e) {
			e.printStackTrace(System.err);
			fail(e.toString());
		}
	}
	@Test
	public void buildNotOwnerTest() {
		try {
			List<GameException> e = new ArrayList<>();
			p.play("BUILD 1 0", e);
			assertEquals(1, e.size());
		} catch(Exception e) {
			e.printStackTrace(System.err);
			fail(e.toString());
		}
	}
}
