package net.minecraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface TooltipContext {
	boolean isAdvanced();

	@Environment(EnvType.CLIENT)
	public static enum Default implements TooltipContext {
		field_8934(false),
		field_8935(true);

		private final boolean advanced;

		private Default(boolean bl) {
			this.advanced = bl;
		}

		@Override
		public boolean isAdvanced() {
			return this.advanced;
		}
	}
}
