package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientConfigurationNetworkHandler extends ClientCommonNetworkHandler implements TickablePacketListener, ClientConfigurationPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final GameProfile profile;
	private DynamicRegistryManager.Immutable registryManager;
	private FeatureSet enabledFeatures;

	public ClientConfigurationNetworkHandler(MinecraftClient minecraftClient, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
		super(minecraftClient, clientConnection, clientConnectionState);
		this.profile = clientConnectionState.localGameProfile();
		this.registryManager = clientConnectionState.receivedRegistries();
		this.enabledFeatures = clientConnectionState.enabledFeatures();
	}

	@Override
	public boolean isConnectionOpen() {
		return this.connection.isOpen();
	}

	@Override
	protected DynamicRegistryManager.Immutable getRegistryManager() {
		return this.registryManager;
	}

	@Override
	protected void onCustomPayload(CustomPayload payload) {
		this.handleCustomPayload(payload);
	}

	private void handleCustomPayload(CustomPayload payload) {
		LOGGER.warn("Unknown custom packet payload: {}", payload.id());
	}

	@Override
	public void onDynamicRegistries(DynamicRegistriesS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		DynamicRegistryManager.Immutable immutable = ClientDynamicRegistryType.createCombinedDynamicRegistries()
			.with(ClientDynamicRegistryType.REMOTE, packet.registryManager())
			.getCombinedRegistryManager();
		if (!this.connection.isLocal()) {
			immutable.streamAllRegistries().forEach(entry -> entry.value().clearTags());
		}

		this.registryManager = immutable;
	}

	@Override
	public void onFeatures(FeaturesS2CPacket packet) {
		this.enabledFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(packet.features());
	}

	@Override
	public void onReady(ReadyS2CPacket packet) {
		this.connection.disableAutoRead();
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.connection
			.setPacketListener(
				new ClientPlayNetworkHandler(
					this.client,
					this.connection,
					new ClientConnectionState(
						this.profile, this.worldSession, this.registryManager, this.enabledFeatures, this.brand, this.serverInfo, this.postDisconnectScreen
					)
				)
			);
		this.connection.enableAutoRead();
		this.connection.send(new ReadyC2SPacket());
	}

	@Override
	public void tick() {
		this.sendQueuedPackets();
	}
}