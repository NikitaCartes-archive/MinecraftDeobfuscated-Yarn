package net.minecraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface TooltipContext {
	boolean isAdvanced();

	@Environment(EnvType.CLIENT)
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
