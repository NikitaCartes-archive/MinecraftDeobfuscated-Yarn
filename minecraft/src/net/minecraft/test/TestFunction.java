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
	boolean manualOnly,
	int maxAttempts,
	int requiredSuccesses,
	boolean skyAccess,
	Consumer<TestContext> starter
) {
	public TestFunction(String batchId, String templatePath, String templateName, int tickLimit, long duration, boolean required, Consumer<TestContext> starter) {
		this(batchId, templatePath, templateName, BlockRotation.NONE, tickLimit, duration, required, false, 1, 1, false, starter);
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
		this(batchId, templatePath, templateName, rotation, tickLimit, setupTicks, required, false, 1, 1, false, starter);
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
