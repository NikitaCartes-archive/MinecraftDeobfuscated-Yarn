package net.minecraft.client.util;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DefaultSkinHelper {
	private static final Identifier field_5301 = new Identifier("textures/entity/steve.png");
	private static final Identifier field_5300 = new Identifier("textures/entity/alex.png");

	public static Identifier method_4649() {
		return field_5301;
	}

	public static Identifier method_4648(UUID uUID) {
		return shouldUseSlimModel(uUID) ? field_5300 : field_5301;
	}

	public static String getModel(UUID uUID) {
		return shouldUseSlimModel(uUID) ? "slim" : "default";
	}

	private static boolean shouldUseSlimModel(UUID uUID) {
		return (uUID.hashCode() & 1) == 1;
	}
}
