package net.minecraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface TooltipOptions {
	boolean isAdvanced();

	@Environment(EnvType.CLIENT)
	public static enum Instance implements TooltipOptions {
		NORMAL(false),
		ADVANCED(true);

		private final boolean advanced;

		private Instance(boolean bl) {
			this.advanced = bl;
		}

		@Override
		public boolean isAdvanced() {
			return this.advanced;
		}
	}
}
