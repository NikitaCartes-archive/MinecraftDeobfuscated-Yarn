package net.minecraft.client.gui.screen.world;

import java.nio.file.Path;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.world.level.LevelProperties;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface CreateWorldCallback {
	boolean create(
		CreateWorldScreen screen,
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries,
		LevelProperties levelProperties,
		@Nullable Path dataPackTempDir
	);
}
