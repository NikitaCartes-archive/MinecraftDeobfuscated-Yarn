package net.minecraft.item.tooltip;

public interface TooltipType {
	TooltipType.Default BASIC = new TooltipType.Default(false, false);
	TooltipType.Default ADVANCED = new TooltipType.Default(true, false);

	boolean isAdvanced();

	boolean isCreative();

	public static record Default(boolean advanced, boolean creative) implements TooltipType {
		@Override
		public boolean isAdvanced() {
			return this.advanced;
		}

		@Override
		public boolean isCreative() {
			return this.creative;
		}

		public TooltipType.Default withCreative() {
			return new TooltipType.Default(this.advanced, true);
		}
	}
}
