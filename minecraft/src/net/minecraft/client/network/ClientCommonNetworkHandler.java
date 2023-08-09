package net.minecraft.client.network;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class ClientCommonNetworkHandler implements ClientCommonPacketListener {
	private static final Text LOST_CONNECTION_TEXT = Text.translatable("disconnect.lost");
	private static final Logger LOGGER = LogUtils.getLogger();
	protected final MinecraftClient client;
	protected final ClientConnection connection;
	@Nullable
	protected final ServerInfo serverInfo;
	@Nullable
	protected String brand;
	protected final WorldSession worldSession;
	@Nullable
	protected final Screen postDisconnectScreen;
	private final List<ClientCommonNetworkHandler.QueuedPacket> queuedPackets = new ArrayList();

	protected ClientCommonNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
		this.client = client;
		this.connection = connection;
		this.serverInfo = connectionState.serverInfo();
		this.brand = connectionState.serverBrand();
		this.worldSession = connectionState.worldSession();
		this.postDisconnectScreen = connectionState.postDisconnectScreen();
	}

	@Override
	public void onKeepAlive(KeepAliveS2CPacket packet) {
		this.send(new KeepAliveC2SPacket(packet.getId()), () -> !RenderSystem.isFrozenAtPollEvents(), Duration.ofMinutes(1L));
	}

	@Override
	public void onPing(CommonPingS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.sendPacket(new CommonPongC2SPacket(packet.getParameter()));
	}

	@Override
	public void onCustomPayload(CustomPayloadS2CPacket packet) {
		CustomPayload customPayload = packet.payload();
		if (!(customPayload instanceof UnknownCustomPayload)) {
			NetworkThreadUtils.forceMainThread(packet, this, this.client);
			if (customPayload instanceof BrandCustomPayload brandCustomPayload) {
				this.brand = brandCustomPayload.brand();
				this.worldSession.setBrand(brandCustomPayload.brand());
			} else {
				this.onCustomPayload(customPayload);
			}
		}
	}

	protected abstract void onCustomPayload(CustomPayload payload);

	protected abstract DynamicRegistryManager.Immutable getRegistryManager();

	@Override
	public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
		URL uRL = getParsedResourcePackUrl(packet.getUrl());
		if (uRL == null) {
			this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
		} else {
			String string = packet.getHash();
			boolean bl = packet.isRequired();
			if (this.serverInfo != null && this.serverInfo.getResourcePackPolicy() == ServerInfo.ResourcePackPolicy.ENABLED) {
				this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
				this.sendResourcePackStatusAfter(this.client.getServerResourcePackProvider().download(uRL, string, true));
			} else if (this.serverInfo != null
				&& this.serverInfo.getResourcePackPolicy() != ServerInfo.ResourcePackPolicy.PROMPT
				&& (!bl || this.serverInfo.getResourcePackPolicy() != ServerInfo.ResourcePackPolicy.DISABLED)) {
				this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.DECLINED);
				if (bl) {
					this.connection.disconnect(Text.translatable("multiplayer.requiredTexturePrompt.disconnect"));
				}
			} else {
				this.client.execute(() -> this.showPackConfirmationScreen(uRL, string, bl, packet.getPrompt()));
			}
		}
	}

	private void showPackConfirmationScreen(URL packUrl, String sha1, boolean required, @Nullable Text prompt) {
		Screen screen = this.client.currentScreen;
		this.client
			.setScreen(
				new ConfirmScreen(
					confirmed -> {
						this.client.setScreen(screen);
						if (confirmed) {
							if (this.serverInfo != null) {
								this.serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.ENABLED);
							}

							this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
							this.sendResourcePackStatusAfter(this.client.getServerResourcePackProvider().download(packUrl, sha1, true));
						} else {
							this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.DECLINED);
							if (required) {
								this.connection.disconnect(Text.translatable("multiplayer.requiredTexturePrompt.disconnect"));
							} else if (this.serverInfo != null) {
								this.serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.DISABLED);
							}
						}

						if (this.serverInfo != null) {
							ServerList.updateServerListEntry(this.serverInfo);
						}
					},
					required ? Text.translatable("multiplayer.requiredTexturePrompt.line1") : Text.translatable("multiplayer.texturePrompt.line1"),
					getPrompt(
						required
							? Text.translatable("multiplayer.requiredTexturePrompt.line2").formatted(Formatting.YELLOW, Formatting.BOLD)
							: Text.translatable("multiplayer.texturePrompt.line2"),
						prompt
					),
					required ? ScreenTexts.PROCEED : ScreenTexts.YES,
					(Text)(required ? Text.translatable("menu.disconnect") : ScreenTexts.NO)
				)
			);
	}

	private static Text getPrompt(Text requirementPrompt, @Nullable Text customPrompt) {
		return (Text)(customPrompt == null ? requirementPrompt : Text.translatable("multiplayer.texturePrompt.serverPrompt", requirementPrompt, customPrompt));
	}

	@Nullable
	private static URL getParsedResourcePackUrl(String url) {
		try {
			URL uRL = new URL(url);
			String string = uRL.getProtocol();
			return !"http".equals(string) && !"https".equals(string) ? null : uRL;
		} catch (MalformedURLException var3) {
			return null;
		}
	}

	private void sendResourcePackStatusAfter(CompletableFuture<?> future) {
		future.thenRun(() -> this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED)).exceptionally(throwable -> {
			this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
			return null;
		});
	}

	@Override
	public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		packet.getGroups().forEach(this::handleSynchronizedTagGroup);
	}

	private <T> void handleSynchronizedTagGroup(RegistryKey<? extends Registry<? extends T>> registryRef, TagPacketSerializer.Serialized tags) {
		if (!tags.isEmpty()) {
			Registry<T> registry = (Registry<T>)this.getRegistryManager()
				.getOptional(registryRef)
				.orElseThrow(() -> new IllegalStateException("Unknown registry " + registryRef));
			Map<TagKey<T>, List<RegistryEntry<T>>> map = new HashMap();
			TagPacketSerializer.loadTags(registryRef, registry, tags, map::put);
			registry.populateTags(map);
		}
	}

	private void sendResourcePackStatus(ResourcePackStatusC2SPacket.Status status) {
		this.connection.send(new ResourcePackStatusC2SPacket(status));
	}

	@Override
	public void onDisconnect(DisconnectS2CPacket packet) {
		this.connection.disconnect(packet.getReason());
	}

	protected void sendQueuedPackets() {
		Iterator<ClientCommonNetworkHandler.QueuedPacket> iterator = this.queuedPackets.iterator();

		while (iterator.hasNext()) {
			ClientCommonNetworkHandler.QueuedPacket queuedPacket = (ClientCommonNetworkHandler.QueuedPacket)iterator.next();
			if (queuedPacket.sendCondition().getAsBoolean()) {
				this.sendPacket(queuedPacket.packet);
				iterator.remove();
			} else if (queuedPacket.expirationTime() <= Util.getMeasuringTimeMs()) {
				iterator.remove();
			}
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.connection.send(packet);
	}

	@Override
	public void onDisconnected(Text reason) {
		this.worldSession.onUnload();
		this.client.disconnect(this.createDisconnectedScreen(reason));
		LOGGER.warn("Client disconnected with reason: {}", reason.getString());
	}

	protected Screen createDisconnectedScreen(Text reason) {
		Screen screen = (Screen)Objects.requireNonNullElseGet(this.postDisconnectScreen, () -> new MultiplayerScreen(new TitleScreen()));
		return (Screen)(this.serverInfo != null && this.serverInfo.isRealm()
			? new DisconnectedRealmsScreen(screen, LOST_CONNECTION_TEXT, reason)
			: new DisconnectedScreen(screen, LOST_CONNECTION_TEXT, reason));
	}

	@Nullable
	public String getBrand() {
		return this.brand;
	}

	private void send(Packet<? extends ServerPacketListener> packet, BooleanSupplier sendCondition, Duration expiry) {
		if (sendCondition.getAsBoolean()) {
			this.sendPacket(packet);
		} else {
			this.queuedPackets.add(new ClientCommonNetworkHandler.QueuedPacket(packet, sendCondition, Util.getMeasuringTimeMs() + expiry.toMillis()));
		}
	}

	@Environment(EnvType.CLIENT)
	static record QueuedPacket(Packet<? extends ServerPacketListener> packet, BooleanSupplier sendCondition, long expirationTime) {
	}
}
