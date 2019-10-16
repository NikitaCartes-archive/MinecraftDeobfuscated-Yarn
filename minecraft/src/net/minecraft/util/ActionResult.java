package net.minecraft.util;

public enum ActionResult {
	SUCCESS,
	CONSUME,
	PASS,
	FAIL;

	public boolean method_23665() {
		return this == SUCCESS || this == CONSUME;
	}

	public boolean method_23666() {
		return this == SUCCESS;
	}
}
