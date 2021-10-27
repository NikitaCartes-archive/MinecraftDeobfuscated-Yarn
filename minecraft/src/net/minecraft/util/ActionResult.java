package net.minecraft.util;

public enum ActionResult {
	/**
	 * Indicates an action is performed and the actor's hand should swing to
	 * indicate the performance.
	 */
	SUCCESS,
	/**
	 * Indicates an action is performed but no animation should accompany the
	 * performance.
	 */
	CONSUME,
	/**
	 * Indicates an action is performed but no animation should accompany the
	 * performance and no statistic should be incremented.
	 */
	CONSUME_PARTIAL,
	/**
	 * Indicates an action is not performed but allows other actions to
	 * perform.
	 */
	PASS,
	/**
	 * Indicates that an action is not performed and prevents other actions
	 * from performing.
	 */
	FAIL;

	/**
	 * Returns whether an action is performed.
	 */
	public boolean isAccepted() {
		return this == SUCCESS || this == CONSUME || this == CONSUME_PARTIAL;
	}

	/**
	 * Returns whether an actor should have a hand-swinging animation on
	 * action performance.
	 */
	public boolean shouldSwingHand() {
		return this == SUCCESS;
	}

	/**
	 * Returns whether action performance should increment an item's 'used'
	 * statistic.
	 */
	public boolean shouldIncrementStat() {
		return this == SUCCESS || this == CONSUME;
	}

	public static ActionResult success(boolean swingHand) {
		return swingHand ? SUCCESS : CONSUME;
	}
}
