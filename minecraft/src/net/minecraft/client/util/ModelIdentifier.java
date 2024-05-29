package net.minecraft.client.util;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record ModelIdentifier(Identifier id, String variant) {
	public static final String INVENTORY_VARIANT = "inventory";

	public ModelIdentifier(Identifier id, String variant) {
		variant = toLowerCase(variant);
		this.id = id;
		this.variant = variant;
	}

	public static ModelIdentifier ofVanilla(String path, String variant) {
		return new ModelIdentifier(Identifier.ofVanilla(path), variant);
	}

	public static ModelIdentifier ofInventoryVariant(Identifier id) {
		return new ModelIdentifier(id, "inventory");
	}

	private static String toLowerCase(String string) {
		return string.toLowerCase(Locale.ROOT);
	}

	public String getVariant() {
		return this.variant;
	}

	public String toString() {
		return this.id + "#" + this.variant;
	}
}
