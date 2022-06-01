package net.minecraft.test;

import java.util.function.Consumer;
import net.minecraft.util.BlockRotation;

public class TestFunction {
	private final String batchId;
	private final String templatePath;
	private final String templateName;
	private final boolean required;
	private final int maxAttempts;
	private final int requiredSuccesses;
	private final Consumer<TestContext> starter;
	private final int tickLimit;
	private final long duration;
	private final BlockRotation rotation;

	public TestFunction(String batchId, String templatePath, String templateName, int tickLimit, long duration, boolean required, Consumer<TestContext> starter) {
		this(batchId, templatePath, templateName, BlockRotation.NONE, tickLimit, duration, required, 1, 1, starter);
	}

	public TestFunction(
		String batchId,
		String templatePath,
		String templateName,
		BlockRotation rotation,
		int tickLimit,
		long duration,
		boolean required,
		Consumer<TestContext> starter
	) {
		this(batchId, templatePath, templateName, rotation, tickLimit, duration, required, 1, 1, starter);
	}

	public TestFunction(
		String batchId,
		String templatePath,
		String templateName,
		BlockRotation rotation,
		int tickLimit,
		long duration,
		boolean required,
		int requiredSuccesses,
		int maxAttempts,
		Consumer<TestContext> starter
	) {
		this.batchId = batchId;
		this.templatePath = templatePath;
		this.templateName = templateName;
		this.rotation = rotation;
		this.tickLimit = tickLimit;
		this.required = required;
		this.requiredSuccesses = requiredSuccesses;
		this.maxAttempts = maxAttempts;
		this.starter = starter;
		this.duration = duration;
	}

	public void start(TestContext context) {
		this.starter.accept(context);
	}

	public String getTemplatePath() {
		return this.templatePath;
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public String toString() {
		return this.templatePath;
	}

	public int getTickLimit() {
		return this.tickLimit;
	}

	public boolean isRequired() {
		return this.required;
	}

	public String getBatchId() {
		return this.batchId;
	}

	public long getDuration() {
		return this.duration;
	}

	public BlockRotation getRotation() {
		return this.rotation;
	}

	public boolean isFlaky() {
		return this.maxAttempts > 1;
	}

	public int getMaxAttempts() {
		return this.maxAttempts;
	}

	public int getRequiredSuccesses() {
		return this.requiredSuccesses;
	}
}
