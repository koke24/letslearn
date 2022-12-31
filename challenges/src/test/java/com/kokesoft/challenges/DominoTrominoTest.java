package com.kokesoft.challenges;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

public class DominoTrominoTest {

	@Test
	public void dominoTrominoTest() {
		BigInteger [] datos = new BigInteger[100];
		DominoTromino dt = new DominoTromino();
		assertEquals(1, dt.calculate(1, datos));
		assertEquals(2, dt.calculate(2, datos));
		assertEquals(5, dt.calculate(3, datos));
		assertEquals(11, dt.calculate(4, datos));
		assertEquals(24, dt.calculate(5, datos));
		assertEquals(53, dt.calculate(6, datos));
		assertEquals(117, dt.calculate(7, datos));
		BigInteger [] d = {BigInteger.valueOf(24), BigInteger.valueOf(53), BigInteger.valueOf(117)};
		for(int i = 8; i<50; i++) {
			BigInteger nd = dt.lcalculate(BigInteger.valueOf(i), datos);
			assertEquals((d[2].multiply(BigInteger.TWO).add(d[0])).remainder(BigInteger.valueOf(1000000007)).intValue(), dt.calculate(i, datos));
			d[0] = d[1]; d[1] = d[2]; d[2] = nd;
		}
		assertEquals(451995198, dt.calculate(50, datos));
	}
}
