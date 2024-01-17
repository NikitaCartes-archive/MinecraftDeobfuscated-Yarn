package net.minecraft.test;

import java.util.function.Consumer;
import net.minecraft.util.BlockRotation;

public record TestFunction(
	String batchId,
	String templatePath,
	String templateName,
	BlockRotation rotation,
	int tickLimit,
	long setupTicks,
	boolean required,
	int maxAttempts,
	int requiredSuccesses,
	Consumer<TestContext> starter
) {
	public TestFunction(String batchId, String templatePath, String templateName, int tickLimit, long duration, boolean required, Consumer<TestContext> starter) {
		this(batchId, templatePath, templateName, BlockRotation.NONE, tickLimit, duration, required, 1, 1, starter);
	}

	public TestFunction(
		String batchId,
		String templatePath,
		String templateName,
		BlockRotation rotation,
		int tickLimit,
		long setupTicks,
		boolean required,
		Consumer<TestContext> starter
	) {
		this(batchId, templatePath, templateName, rotation, tickLimit, setupTicks, required, 1, 1, starter);
	}

	public void start(TestContext context) {
		this.starter.accept(context);
	}

	public String toString() {
		return this.templatePath;
	}

	public boolean isFlaky() {
		return this.maxAttempts > 1;
	}
}
