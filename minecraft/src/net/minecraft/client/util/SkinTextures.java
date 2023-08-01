package net.minecraft.client.util;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record SkinTextures(Identifier texture, @Nullable Identifier capeTexture, @Nullable Identifier elytraTexture, SkinTextures.Model model, boolean secure) {
	@Environment(EnvType.CLIENT)
	public static enum Model {
		SLIM("slim"),
		WIDE("default");

		private final String name;

		private Model(String name) {
			this.name = name;
		}

		public static SkinTextures.Model fromName(@Nullable String name) {
			if (name == null) {
				return WIDE;
			} else {
				byte var2 = -1;
				switch (name.hashCode()) {
					case 3533117:
						if (name.equals("slim")) {
							var2 = 0;
						}
					default:
						return switch (var2) {
							case 0 -> SLIM;
							default -> WIDE;
						};
				}
			}
		}

		public String getName() {
			return this.name;
		}
	}
}
