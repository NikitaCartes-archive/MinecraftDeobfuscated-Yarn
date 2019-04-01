package net.minecraft;

import java.io.OutputStream;

public class class_2980 extends class_2983 {
	public class_2980(String string, OutputStream outputStream) {
		super(string, outputStream);
	}

	@Override
	protected void method_12870(String string) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
		field_13384.info("[{}]@.({}:{}): {}", this.field_13383, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), string);
	}
}
