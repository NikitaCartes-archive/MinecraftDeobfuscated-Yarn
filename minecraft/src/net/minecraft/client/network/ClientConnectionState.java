package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;

@Environment(EnvType.CLIENT)
public record ClientConnectionState(
	GameProfile localGameProfile,
	WorldSession worldSession,
	DynamicRegistryManager.Immutable receivedRegistries,
	FeatureSet enabledFeatures,
	@Nullable String serverBrand,
	@Nullable ServerInfo serverInfo,
	@Nullable Screen postDisconnectScreen
) {
}
