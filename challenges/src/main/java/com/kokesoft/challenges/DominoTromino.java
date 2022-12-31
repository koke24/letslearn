package com.kokesoft.challenges;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DominoTromino {
	
	private class Solution {
		String name;
		String [] display;
		int w;
		protected Solution(String name, String [] display, int w) {
			this.name = name;
			this.display = display;
			this.w = w;
		}
	}

	private Solution [] solutions = {
		new Solution("1", new String[] {"D","D"}, 1)
		, new Solution("2", new String[] {"D-D","D-D"},2)
		, new Solution("3a", new String[] {"T-T T", "T T-T"}, 3)
		, new Solution("3b", new String[] {"T T-T", "T-T T"}, 3)
		, new Solution("4a", new String[] {"T-T T-T", "T D-D T"}, 4)
		, new Solution("4b", new String[] {"T D-D T", "T-T T-T"}, 4)
		, new Solution("5a", new String[] {"T D-D T-T", "T-T D-D T"}, 5)
		, new Solution("5b", new String[] {"T-T D-D T", "T D-D T-T"}, 5)
		, new Solution("6a", new String[] {"T D-D D-D T", "T-T D-D T-T"}, 6)
		, new Solution("6b", new String[] {"T-T D-D T-T", "T D-D D-D T"}, 6)
		, new Solution("7a", new String[] {"T D-D D-D T-T", "T-T D-D D-D T"}, 7)
		, new Solution("7b", new String[] {"T-T D-D D-D T", "T D-D D-D T-T"}, 7)
		, new Solution("8a", new String[] {"T D-D D-D D-D T", "T-T D-D D-D T-T"}, 8)
		, new Solution("8b", new String[] {"T-T D-D D-D T-T", "T D-D D-D D-D T"}, 8)
		, new Solution("9a", new String[] {"T D-D D-D D-D T-T", "T-T D-D D-D D-D T"}, 9)
		, new Solution("9b", new String[] {"T-T D-D D-D D-D T", "T D-D D-D D-D T-T"}, 9)
		, new Solution("10a", new String[] {"T D-D D-D D-D D-D T", "T-T D-D D-D D-D T-T"}, 10)
		, new Solution("10b", new String[] {"T-T D-D D-D T-D-D T", "T D-D D-D D-D D-D T"}, 10)
		, new Solution("11a", new String[] {"T D-D D-D D-D D-D T-T", "T-T D-D D-D D-D D-D T"}, 11)
		, new Solution("11b", new String[] {"T-T D-D D-D D-D D-D T", "T D-D D-D D-D D-D T-T"}, 11)
	};
	
	public void printSolution(Stack<Solution> s, StringBuilder sb1, StringBuilder sb2) {
		sb1.append("| ");
		sb2.append("| ");
		for(Solution ts: s) {
			sb1.append(ts.display[0]).append(' ');
			sb2.append(ts.display[1]).append(' ');
		}
		sb1.append("|,");
		sb2.append("|,");
	}
	
	public int calculateOld(int n) {
		return calculate(n, new Stack<>(), new StringBuilder(), new StringBuilder());
	}
	public int calculate(int n, Stack<Solution> s, StringBuilder sb1, StringBuilder sb2) {
		if (n==0) return 0;
		//if (n==1) return 1;
		int c = 0;
		for(Solution ts: solutions) {
			int tn = n-ts.w;
			if (tn<0) break;
			s.push(ts);
			if (tn==0) {
				printSolution(s, sb1, sb2);
				c++;
			} else {
				c += calculate(tn, s, sb1, sb2);
			}
			s.pop();
		}
		if (n>9)
			c += 2;
		if (s.isEmpty()) {
			//System.out.println(sb1);
			//System.out.println(sb2);
			sb1.setLength(0);
			sb2.setLength(0);
		}
		return c;
	}
	public final BigInteger TWO = BigInteger.valueOf(2); 
	public final BigInteger THREE = BigInteger.valueOf(3); 
	public final BigInteger FIVE = BigInteger.valueOf(5); 
	public BigInteger lcalculate(BigInteger n, BigInteger [] before) {
		if (BigInteger.ONE.compareTo(n)==0)
			return BigInteger.ONE;
		if (TWO.compareTo(n)==0)
			return TWO;
		if (THREE.compareTo(n)==0)
			return FIVE;
		if (before!=null) {
			return before[n.intValue()-2].multiply(TWO).add(before[n.intValue()-4]);
		}
		return lcalculate(n.add(BigInteger.ONE.negate()), null).multiply(TWO).add(lcalculate(n.add(THREE.negate()), null));
	}
	final BigInteger DIVISOR = new BigInteger("1000000007");
	public int calculate(int n) {
		return (int)Math.round(Math.floor(lcalculate(BigInteger.valueOf(n), null).remainder(DIVISOR).doubleValue()));
	}
	public int calculate(int n, BigInteger [] before) {
		if (before==null)
			return calculate(n);
		if (before[n-1]==null)
			before[n-1] = lcalculate(BigInteger.valueOf(n), before);
		return (int)Math.round(Math.floor(before[n-1].remainder(DIVISOR).doubleValue()));
	}
	/**
	 * 1
	 * 2, 1+1
	 * 1+1+1,2+1,1+2,3a,3b
	 * 2+2,1+1+1+1,1+2+1,1+1+2,2+1+1,1+3a,1+3b,3a+1,3b+1,4a,4b
	 * @param n
	 * @return
	 */
	public int understand(int n) {
		switch(n) {
		case 0: return 0;
		case 1:			// D
			return 1;	// D
		case 2:			// D-D  D D
			return 2;	// D-D, D D
		case 3:			// D D D  D-D D  D D-D  T-T T  T T-T
			return 5;	// D D D, D-D D, D D-D, T T-T, T-T T
		case 4:			// D-D D-D  D D D D  D D-D D  D D D-D  D-D D D  D T-T T  D T T-T  T-T T D  T T-T D  T-T T-T  T D-D T
			return 11;	// D-D D-D, D D D D, D D-D D, D D D-D, D-D D D, D T T-T, D T-T T, T T-T D, T-T T D, T D-D T, T-T T-T
		default:
			return calculate(n);
		}
	}
}
