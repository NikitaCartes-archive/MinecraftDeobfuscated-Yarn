package net.minecraft.client.item;

public interface TooltipContext {
	boolean isAdvanced();

	public static enum Default implements TooltipContext {
		NORMAL(false),
		ADVANCED(true);

		private final boolean advanced;

		private Default(boolean advanced) {
			this.advanced = advanced;
		}

		@Override
		public boolean isAdvanced() {
			return this.advanced;
		}
	}
}
