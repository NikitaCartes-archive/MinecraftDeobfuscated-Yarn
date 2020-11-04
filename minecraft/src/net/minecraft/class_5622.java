package net.minecraft;

import net.minecraft.test.GameTest;

class class_5622 extends Throwable {
	public class_5622(int i, int j, GameTest gameTest) {
		super(
			"Not enough successes: "
				+ j
				+ " out of "
				+ i
				+ " attempts. Required successes: "
				+ gameTest.method_32243()
				+ ". max attempts: "
				+ gameTest.method_32242()
				+ ".",
			gameTest.getThrowable()
		);
	}
}
