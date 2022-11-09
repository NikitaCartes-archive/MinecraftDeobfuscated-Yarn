package net.minecraft.client.item;

public interface TooltipContext {
	TooltipContext.Default BASIC = new TooltipContext.Default(false, false);
	TooltipContext.Default ADVANCED = new TooltipContext.Default(true, false);

	boolean isAdvanced();

	boolean isCreative();

	public static record Default(boolean advanced, boolean creative) implements TooltipContext {
		@Override
		public boolean isAdvanced() {
			return this.advanced;
		}

		@Override
		public boolean isCreative() {
			return this.creative;
		}

		public TooltipContext.Default withCreative() {
			return new TooltipContext.Default(this.advanced, true);
		}
	}
}
