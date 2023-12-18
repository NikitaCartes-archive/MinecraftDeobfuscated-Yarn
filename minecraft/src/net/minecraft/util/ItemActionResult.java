package net.minecraft.util;

public enum ItemActionResult {
	SUCCESS,
	CONSUME,
	CONSUME_PARTIAL,
	PASS_TO_DEFAULT_BLOCK_INTERACTION,
	SKIP_DEFAULT_BLOCK_INTERACTION,
	FAIL;

	public boolean isAccepted() {
		return this.toActionResult().isAccepted();
	}

	public static ItemActionResult success(boolean swingHand) {
		return swingHand ? SUCCESS : CONSUME;
	}

	public ActionResult toActionResult() {
		return switch (this) {
			case SUCCESS -> ActionResult.SUCCESS;
			case CONSUME -> ActionResult.CONSUME;
			case CONSUME_PARTIAL -> ActionResult.CONSUME_PARTIAL;
			case PASS_TO_DEFAULT_BLOCK_INTERACTION, SKIP_DEFAULT_BLOCK_INTERACTION -> ActionResult.PASS;
			case FAIL -> ActionResult.FAIL;
		};
	}
}
