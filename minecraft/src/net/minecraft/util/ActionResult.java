package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

/**
 * An enum indicating the hand interaction's result. Methods called on hand interaction,
 * such as {@link net.minecraft.block.AbstractBlock#onUse}, return this.
 */
public sealed interface ActionResult permits ActionResult.Success, ActionResult.Fail, ActionResult.Pass, ActionResult.PassToDefaultBlockAction {
	/**
	 * Indicates an action is performed and the actor's hand should swing to
	 * indicate the performance.
	 */
	ActionResult.Success SUCCESS = new ActionResult.Success(ActionResult.SwingSource.CLIENT, ActionResult.ItemContext.KEEP_HAND_STACK);
	/**
	 * Indicates an action is performed on the logical server only and the
	 * actor's hand should swing to indicate the performance.
	 */
	ActionResult.Success SUCCESS_SERVER = new ActionResult.Success(ActionResult.SwingSource.SERVER, ActionResult.ItemContext.KEEP_HAND_STACK);
	/**
	 * Indicates an action is performed but no animation should accompany the
	 * performance.
	 */
	ActionResult.Success CONSUME = new ActionResult.Success(ActionResult.SwingSource.NONE, ActionResult.ItemContext.KEEP_HAND_STACK);
	/**
	 * Indicates that an action is not performed and prevents other actions
	 * from performing.
	 */
	ActionResult.Fail FAIL = new ActionResult.Fail();
	/**
	 * Indicates an action is not performed but allows other actions to
	 * perform.
	 */
	ActionResult.Pass PASS = new ActionResult.Pass();
	/**
	 * Indicates an action is not performed and the default block action
	 * should be performed instead.
	 */
	ActionResult.PassToDefaultBlockAction PASS_TO_DEFAULT_BLOCK_ACTION = new ActionResult.PassToDefaultBlockAction();

	/**
	 * {@return whether an action is performed}
	 */
	default boolean isAccepted() {
		return false;
	}

	public static record Fail() implements ActionResult {
	}

	public static record ItemContext(boolean incrementStat, @Nullable ItemStack newHandStack) {
		static ActionResult.ItemContext KEEP_HAND_STACK_NO_INCREMENT_STAT = new ActionResult.ItemContext(false, null);
		static ActionResult.ItemContext KEEP_HAND_STACK = new ActionResult.ItemContext(true, null);
	}

	public static record Pass() implements ActionResult {
	}

	public static record PassToDefaultBlockAction() implements ActionResult {
	}

	public static record Success(ActionResult.SwingSource swingSource, ActionResult.ItemContext itemContext) implements ActionResult {
		@Override
		public boolean isAccepted() {
			return true;
		}

		public ActionResult.Success withNewHandStack(ItemStack newHandStack) {
			return new ActionResult.Success(this.swingSource, new ActionResult.ItemContext(true, newHandStack));
		}

		public ActionResult.Success noIncrementStat() {
			return new ActionResult.Success(this.swingSource, ActionResult.ItemContext.KEEP_HAND_STACK_NO_INCREMENT_STAT);
		}

		public boolean shouldIncrementStat() {
			return this.itemContext.incrementStat;
		}

		/**
		 * {@return the item stack that should replace the hand stack after this interaction, if any}
		 */
		@Nullable
		public ItemStack getNewHandStack() {
			return this.itemContext.newHandStack;
		}
	}

	/**
	 * Represents the side that should be considered the source of truth
	 * for an arm swing, if any.
	 */
	public static enum SwingSource {
		NONE,
		CLIENT,
		SERVER;
	}
}
