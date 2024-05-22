package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.ServerLinks;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record ClientConnectionState(
	GameProfile localGameProfile,
	WorldSession worldSession,
	DynamicRegistryManager.Immutable receivedRegistries,
	FeatureSet enabledFeatures,
	@Nullable String serverBrand,
	@Nullable ServerInfo serverInfo,
	@Nullable Screen postDisconnectScreen,
	Map<Identifier, byte[]> serverCookies,
	@Nullable ChatHud.ChatState chatState,
	@Deprecated(forRemoval = true) boolean strictErrorHandling,
	Map<String, String> customReportDetails,
	ServerLinks serverLinks
) {
}
