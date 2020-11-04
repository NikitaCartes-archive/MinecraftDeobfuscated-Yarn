package net.minecraft;

import net.minecraft.test.FailureLoggingTestCompletionListener;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestCompletionListener;

public class class_5623 {
	private static TestCompletionListener field_27807 = new FailureLoggingTestCompletionListener();

	public static void method_32245(GameTest gameTest) {
		field_27807.onTestFailed(gameTest);
	}
}
