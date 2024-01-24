package net.minecraft.client.network;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.network.packet.s2c.common.StoreCookieS2CPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
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
	protected boolean transferring;
	private final List<ClientCommonNetworkHandler.QueuedPacket> queuedPackets = new ArrayList();
	protected final Map<Identifier, byte[]> serverCookies;

	protected ClientCommonNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
		this.client = client;
		this.connection = connection;
		this.serverInfo = connectionState.serverInfo();
		this.brand = connectionState.serverBrand();
		this.worldSession = connectionState.worldSession();
		this.postDisconnectScreen = connectionState.postDisconnectScreen();
		this.serverCookies = connectionState.serverCookies();
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

	@Override
	public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		UUID uUID = packet.id();
		URL uRL = getParsedResourcePackUrl(packet.url());
		if (uRL == null) {
			this.connection.send(new ResourcePackStatusC2SPacket(uUID, ResourcePackStatusC2SPacket.Status.INVALID_URL));
		} else {
			String string = packet.hash();
			boolean bl = packet.required();
			ServerInfo.ResourcePackPolicy resourcePackPolicy = this.serverInfo != null ? this.serverInfo.getResourcePackPolicy() : ServerInfo.ResourcePackPolicy.PROMPT;
			if (resourcePackPolicy != ServerInfo.ResourcePackPolicy.PROMPT && (!bl || resourcePackPolicy != ServerInfo.ResourcePackPolicy.DISABLED)) {
				this.client.getServerResourcePackProvider().addResourcePack(uUID, uRL, string);
			} else {
				this.client.setScreen(this.createConfirmServerResourcePackScreen(uUID, uRL, string, bl, packet.prompt()));
			}
		}
	}

	@Override
	public void onResourcePackRemove(ResourcePackRemoveS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		packet.id().ifPresentOrElse(id -> this.client.getServerResourcePackProvider().remove(id), () -> this.client.getServerResourcePackProvider().removeAll());
	}

	static Text getPrompt(Text requirementPrompt, @Nullable Text customPrompt) {
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

	@Override
	public void onCookieRequest(CookieRequestS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.connection.send(new CookieResponseC2SPacket(packet.key(), (byte[])this.serverCookies.get(packet.key())));
	}

	@Override
	public void onStoreCookie(StoreCookieS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.serverCookies.put(packet.key(), packet.payload());
	}

	@Override
	public void onServerTransfer(ServerTransferS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.serverInfo == null) {
			throw new IllegalStateException("Cannot transfer to server from singleplayer");
		} else {
			this.transferring = true;
			this.connection.disconnect(Text.translatable("disconnect.transfer"));
			this.connection.tryDisableAutoRead();
			this.connection.handleDisconnection();
			ServerAddress serverAddress = new ServerAddress(packet.host(), packet.port());
			ConnectScreen.connect(
				(Screen)Objects.requireNonNullElseGet(this.postDisconnectScreen, TitleScreen::new),
				this.client,
				serverAddress,
				this.serverInfo,
				false,
				new CookieStorage(this.serverCookies)
			);
		}
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
		this.client.disconnect(this.createDisconnectedScreen(reason), this.transferring);
		LOGGER.warn("Client disconnected with reason: {}", reason.getString());
	}

	@Override
	public void addCustomCrashReportInfo(CrashReportSection section) {
		section.add("Server type", (CrashCallable<String>)(() -> this.serverInfo != null ? this.serverInfo.getServerType().toString() : "<none>"));
		section.add("Server brand", (CrashCallable<String>)(() -> this.brand));
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

	private Screen createConfirmServerResourcePackScreen(UUID id, URL url, String hash, boolean required, @Nullable Text prompt) {
		Screen screen = this.client.currentScreen;
		return screen instanceof ClientCommonNetworkHandler.ConfirmServerResourcePackScreen confirmServerResourcePackScreen
			? confirmServerResourcePackScreen.add(this.client, id, url, hash, required, prompt)
			: new ClientCommonNetworkHandler.ConfirmServerResourcePackScreen(
				this.client, screen, List.of(new ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack(id, url, hash)), required, prompt
			);
	}

	@Environment(EnvType.CLIENT)
	class ConfirmServerResourcePackScreen extends ConfirmScreen {
		private final List<ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack> packs;
		@Nullable
		private final Screen parent;

		ConfirmServerResourcePackScreen(
			MinecraftClient client,
			@Nullable Screen parent,
			List<ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack> pack,
			boolean required,
			@Nullable Text prompt
		) {
			super(
				confirmed -> {
					client.setScreen(parent);
					ServerResourcePackLoader serverResourcePackLoader = client.getServerResourcePackProvider();
					if (confirmed) {
						if (ClientCommonNetworkHandler.this.serverInfo != null) {
							ClientCommonNetworkHandler.this.serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.ENABLED);
						}

						serverResourcePackLoader.acceptAll();
					} else {
						serverResourcePackLoader.declineAll();
						if (required) {
							ClientCommonNetworkHandler.this.connection.disconnect(Text.translatable("multiplayer.requiredTexturePrompt.disconnect"));
						} else if (ClientCommonNetworkHandler.this.serverInfo != null) {
							ClientCommonNetworkHandler.this.serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.DISABLED);
						}
					}

					for (ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack packx : pack) {
						serverResourcePackLoader.addResourcePack(packx.id, packx.url, packx.hash);
					}

					if (ClientCommonNetworkHandler.this.serverInfo != null) {
						ServerList.updateServerListEntry(ClientCommonNetworkHandler.this.serverInfo);
					}
				},
				required ? Text.translatable("multiplayer.requiredTexturePrompt.line1") : Text.translatable("multiplayer.texturePrompt.line1"),
				ClientCommonNetworkHandler.getPrompt(
					required
						? Text.translatable("multiplayer.requiredTexturePrompt.line2").formatted(Formatting.YELLOW, Formatting.BOLD)
						: Text.translatable("multiplayer.texturePrompt.line2"),
					prompt
				),
				required ? ScreenTexts.PROCEED : ScreenTexts.YES,
				required ? ScreenTexts.DISCONNECT : ScreenTexts.NO
			);
			this.packs = pack;
			this.parent = parent;
		}

		public ClientCommonNetworkHandler.ConfirmServerResourcePackScreen add(
			MinecraftClient client, UUID id, URL url, String hash, boolean required, @Nullable Text prompt
		) {
			List<ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack> list = ImmutableList.<ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack>builderWithExpectedSize(
					this.packs.size() + 1
				)
				.addAll(this.packs)
				.add(new ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack(id, url, hash))
				.build();
			return ClientCommonNetworkHandler.this.new ConfirmServerResourcePackScreen(client, this.parent, list, required, prompt);
		}

		@Environment(EnvType.CLIENT)
		static record Pack(UUID id, URL url, String hash) {
		}
	}

	@Environment(EnvType.CLIENT)
	static record QueuedPacket(Packet<? extends ServerPacketListener> packet, BooleanSupplier sendCondition, long expirationTime) {
	}
}
