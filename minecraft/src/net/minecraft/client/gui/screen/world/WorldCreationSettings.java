package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.world.level.WorldGenSettings;

@Environment(EnvType.CLIENT)
public record WorldCreationSettings(WorldGenSettings worldGenSettings, DataConfiguration dataConfiguration) {
}
