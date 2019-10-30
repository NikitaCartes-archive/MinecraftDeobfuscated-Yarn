package net.minecraft.util;

public enum ActionResult {
	SUCCESS,
	CONSUME,
	PASS,
	FAIL;

	public boolean isAccepted() {
		return this == SUCCESS || this == CONSUME;
	}

	public boolean shouldSwingHand() {
		return this == SUCCESS;
	}
}
