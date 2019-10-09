package net.minecraft.test;

import java.util.function.Consumer;
import net.minecraft.class_4516;

public class TestFunction {
	private final String batchId;
	private final String structurePath;
	private final String structureName;
	private final boolean required;
	private final Consumer<class_4516> field_20587;
	private final int tickLimit;
	private final long field_21460;

	public void method_22297(class_4516 arg) {
		this.field_20587.accept(arg);
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

	public long method_23649() {
		return this.field_21460;
	}
}
