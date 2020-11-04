package net.minecraft.test;

import java.util.function.Consumer;
import net.minecraft.util.BlockRotation;

public class TestFunction {
	private final String batchId;
	private final String structurePath;
	private final String structureName;
	private final boolean required;
	private final int field_27814;
	private final int field_27815;
	private final Consumer<StartupParameter> starter;
	private final int tickLimit;
	private final long duration;
	private final BlockRotation field_25306;

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

	public BlockRotation method_29424() {
		return this.field_25306;
	}

	public boolean method_32257() {
		return this.field_27814 > 1;
	}

	public int method_32258() {
		return this.field_27814;
	}

	public int method_32259() {
		return this.field_27815;
	}
}
