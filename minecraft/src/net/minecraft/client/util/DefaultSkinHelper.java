package net.minecraft.client.util;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DefaultSkinHelper {
	private static final Identifier STEVE_SKIN = new Identifier("textures/entity/steve.png");
	private static final Identifier ALEX_SKIN = new Identifier("textures/entity/alex.png");
	private static final String DEFAULT = "default";
	private static final String SLIM = "slim";

	public static Identifier getTexture() {
		return STEVE_SKIN;
	}

	public static Identifier getTexture(UUID uuid) {
		return shouldUseSlimModel(uuid) ? ALEX_SKIN : STEVE_SKIN;
	}

	public static String getModel(UUID uuid) {
		return shouldUseSlimModel(uuid) ? "slim" : "default";
	}

	private static boolean shouldUseSlimModel(UUID uuid) {
		return (uuid.hashCode() & 1) == 1;
	}
}
