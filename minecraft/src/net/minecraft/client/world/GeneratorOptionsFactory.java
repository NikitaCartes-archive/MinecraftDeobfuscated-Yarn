package net.minecraft.client.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.WorldCreationSettings;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.server.DataPackContents;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface GeneratorOptionsFactory {
	GeneratorOptionsHolder apply(
		DataPackContents dataPackContents, CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, WorldCreationSettings settings
	);
}
