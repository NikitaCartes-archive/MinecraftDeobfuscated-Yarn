package net.minecraft;

import net.minecraft.util.ActionResult;

public enum class_9062 {
	SUCCESS,
	CONSUME,
	CONSUME_PARTIAL,
	PASS_TO_DEFAULT_BLOCK_INTERACTION,
	SKIP_DEFAULT_BLOCK_INTERACTION,
	FAIL;

	public boolean method_55643() {
		return this.method_55645().isAccepted();
	}

	public static class_9062 method_55644(boolean bl) {
		return bl ? SUCCESS : CONSUME;
	}

	public ActionResult method_55645() {
		return switch (this) {
			case SUCCESS -> ActionResult.SUCCESS;
			case CONSUME -> ActionResult.CONSUME;
			case CONSUME_PARTIAL -> ActionResult.CONSUME_PARTIAL;
			case PASS_TO_DEFAULT_BLOCK_INTERACTION, SKIP_DEFAULT_BLOCK_INTERACTION -> ActionResult.PASS;
			case FAIL -> ActionResult.FAIL;
		};
	}
}
