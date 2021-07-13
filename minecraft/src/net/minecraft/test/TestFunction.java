package net.minecraft.test;

import java.util.function.Consumer;
import net.minecraft.util.BlockRotation;

public class TestFunction {
	private final String batchId;
	private final String structurePath;
	private final String structureName;
	private final boolean required;
	private final int maxAttempts;
	private final int requiredSuccesses;
	private final Consumer<TestContext> starter;
	private final int tickLimit;
	private final long duration;
	private final BlockRotation rotation;

	public TestFunction(String batchId, String structurePath, String structureName, int tickLimit, long duration, boolean required, Consumer<TestContext> starter) {
		this(batchId, structurePath, structureName, BlockRotation.NONE, tickLimit, duration, required, 1, 1, starter);
	}

	public TestFunction(
		String batchId,
		String structurePath,
		String structureName,
		BlockRotation rotation,
		int tickLimit,
		long duration,
		boolean required,
		Consumer<TestContext> starter
	) {
		this(batchId, structurePath, structureName, rotation, tickLimit, duration, required, 1, 1, starter);
	}

	public TestFunction(
		String batchId,
		String structurePath,
		String structureName,
		BlockRotation rotation,
		int tickLimit,
		long duration,
		boolean required,
		int requiredSuccesses,
		int maxAttempts,
		Consumer<TestContext> starter
	) {
		this.batchId = batchId;
		this.structurePath = structurePath;
		this.structureName = structureName;
		this.rotation = rotation;
		this.tickLimit = tickLimit;
		this.required = required;
		this.requiredSuccesses = requiredSuccesses;
		this.maxAttempts = maxAttempts;
		this.starter = starter;
		this.duration = duration;
	}

	public void start(TestContext parameter) {
		this.starter.accept(parameter);
	}

	public String getStructurePath() {
		return this.structurePath;
	}

	public String getStructureName() {
		return this.structureName;
	}

	public String toString() {
		return this.structurePath;
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
