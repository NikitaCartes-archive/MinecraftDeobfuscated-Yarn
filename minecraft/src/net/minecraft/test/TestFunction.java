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
	private final Consumer<StartupParameter> starter;
	private final int tickLimit;
	private final long duration;
	private final BlockRotation rotation;

	public void start(StartupParameter startupParameter) {
		this.starter.accept(startupParameter);
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
