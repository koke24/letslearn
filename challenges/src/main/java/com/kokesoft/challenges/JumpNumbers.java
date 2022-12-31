package com.kokesoft.challenges;

import java.util.Stack;

public class JumpNumbers {

	public boolean canJump(int [] nums) {
		int p = 0;
		Stack<Integer> stack = new Stack<>();
		p = 0;
		while(p<(nums.length-1)) {
			while(nums[p]==0) {
				if (stack.isEmpty())
					return false;
				p = stack.pop();
				nums[p]--;
			}
			while((p+nums[p])<(nums.length-1) && nums[p+nums[p]]==0) {
				nums[p]--;
				if (nums[p]==0) {
					if (stack.isEmpty())
						return false;
					p = stack.pop();
				}
			}
			stack.push(p);
			p += nums[p];
		}
		return true;
	}
}

