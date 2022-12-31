package com.kokesoft.challenges;

import static org.junit.Assert.*;

import org.junit.Test;

public class JumpNumbersTest {

	@Test
	public void testOne() {
		JumpNumbers jn = new JumpNumbers();
		assertTrue(jn.canJump(new int[]{0}));
		assertTrue(jn.canJump(new int[]{1}));
	}

	@Test
	public void testTwo() {
		JumpNumbers jn = new JumpNumbers();
		assertTrue(jn.canJump(new int[]{1,0}));
		assertFalse(jn.canJump(new int[]{0,1}));
	}

	@Test
	public void testThree() {
		JumpNumbers jn = new JumpNumbers();
		assertFalse(jn.canJump(new int[]{1,0,0}));
		assertTrue(jn.canJump(new int[]{1,1,0}));
		assertTrue(jn.canJump(new int[]{2,0,1}));
		assertTrue(jn.canJump(new int[]{2,1,1}));
		assertTrue(jn.canJump(new int[]{2,1,0}));
		assertTrue(jn.canJump(new int[]{2,2,0}));
	}

	@Test
	public void testFour() {
		JumpNumbers jn = new JumpNumbers();
		assertFalse(jn.canJump(new int[]{1,0,0,0}));
		assertTrue(jn.canJump(new int[]{1,2,1,0}));
		assertFalse(jn.canJump(new int[]{1,1,0,0}));
		assertFalse(jn.canJump(new int[]{2,1,0,0}));
		assertTrue(jn.canJump(new int[]{2,2,0,0}));
		assertTrue(jn.canJump(new int[]{2,0,1,2}));
		assertTrue(jn.canJump(new int[]{2,1,2,0}));
		assertTrue(jn.canJump(new int[]{2,2,0,0}));
		assertTrue(jn.canJump(new int[]{2,2,0,1}));
		assertTrue(jn.canJump(new int[]{2,3,0,0}));
		assertTrue(jn.canJump(new int[]{3,3,1,0}));
		assertFalse(jn.canJump(new int[]{2,1,0,0}));
	}
	
	@Test
	public void testFive() {
		JumpNumbers jn = new JumpNumbers();
		int [] nums = new int[1000];
		for(int n = 0; n<1000; n++) {
			nums[n] = nums.length-n-2; 
		}
		assertFalse(jn.canJump(nums));
	}
	@Test
	public void testSix() {
		JumpNumbers jn = new JumpNumbers();
		int [] nums = new int[1000];
		for(int n = 0; n<1000; n++) {
			nums[n] = nums.length-n-2; 
		}
		nums[1]++;
		assertTrue(jn.canJump(nums));
	}
}
