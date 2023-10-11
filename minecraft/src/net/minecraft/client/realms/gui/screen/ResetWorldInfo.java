package net.minecraft.client.realms.gui.screen;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record ResetWorldInfo(String seed, RealmsWorldGeneratorType levelType, boolean generateStructures, Set<String> experiments) {
}
