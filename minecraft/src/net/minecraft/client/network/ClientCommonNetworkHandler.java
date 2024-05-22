package net.minecraft.client.network;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9782;
import net.minecraft.class_9812;
import net.minecraft.class_9813;
import net.minecraft.class_9814;
import net.minecraft.class_9815;
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
import net.minecraft.util.crash.CrashReport;
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
	@Deprecated(
		forRemoval = true
	)
	protected final boolean strictErrorHandling;
	private final List<ClientCommonNetworkHandler.QueuedPacket> queuedPackets = new ArrayList();
	protected final Map<Identifier, byte[]> serverCookies;
	protected Map<String, String> field_52154;
	protected class_9782 field_52155;

	protected ClientCommonNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
		this.client = client;
		this.connection = connection;
		this.serverInfo = connectionState.serverInfo();
		this.brand = connectionState.serverBrand();
		this.worldSession = connectionState.worldSession();
		this.postDisconnectScreen = connectionState.postDisconnectScreen();
		this.serverCookies = connectionState.serverCookies();
		this.strictErrorHandling = connectionState.strictErrorHandling();
		this.field_52154 = connectionState.customReportDetails();
		this.field_52155 = connectionState.serverLinks();
	}

	@Override
	public void onPacketException(Packet packet, Exception exception) {
		LOGGER.error("Failed to handle packet {}", packet, exception);
		ClientCommonPacketListener.super.onPacketException(packet, exception);
		Optional<Path> optional = this.method_60882(packet, exception);
		Optional<String> optional2 = this.field_52155.method_60658(class_9782.class_9784.BUG_REPORT).map(class_9782.class_9783::url);
		if (this.strictErrorHandling) {
			this.connection.method_60924(new class_9812(Text.translatable("disconnect.packetError"), optional, optional2));
		}
	}

	@Override
	public class_9812 method_60881(Text text, Throwable throwable) {
		Optional<Path> optional = this.method_60882(null, throwable);
		Optional<String> optional2 = this.field_52155.method_60658(class_9782.class_9784.BUG_REPORT).map(class_9782.class_9783::url);
		return new class_9812(text, optional, optional2);
	}

	private Optional<Path> method_60882(@Nullable Packet packet, Throwable throwable) {
		CrashReport crashReport = CrashReport.create(throwable, "Packet handling error");
		NetworkThreadUtils.fillCrashReport(crashReport, this, packet);
		Path path = this.client.runDirectory.toPath().resolve("debug");
		Path path2 = path.resolve("disconnect-" + Util.getFormattedCurrentTime() + "-client.txt");
		Optional<class_9782.class_9783> optional = this.field_52155.method_60658(class_9782.class_9784.BUG_REPORT);
		List<String> list = (List<String>)optional.map(arg -> List.of("Server bug reporting link: " + arg.url())).orElse(List.of());
		return crashReport.writeToFile(path2, class_9813.MINECRAFT_NETWORK_PROTOCOL_ERROR_REPORT, list) ? Optional.of(path2) : Optional.empty();
	}

	@Override
	public boolean accepts(Packet<?> packet) {
		return ClientCommonPacketListener.super.accepts(packet)
			? true
			: this.transferring && (packet instanceof StoreCookieS2CPacket || packet instanceof ServerTransferS2CPacket);
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
				this.client.setScreen(this.createConfirmServerResourcePackScreen(uUID, uRL, string, bl, (Text)packet.prompt().orElse(null)));
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
	public void method_60883(class_9814 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.client);
		this.field_52154 = arg.details();
	}

	@Override
	public void method_60884(class_9815 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.client);
		this.field_52155 = arg.links();
	}

	@Override
	public void onServerTransfer(ServerTransferS2CPacket packet) {
		this.transferring = true;
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.serverInfo == null) {
			throw new IllegalStateException("Cannot transfer to server from singleplayer");
		} else {
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
		this.connection.disconnect(packet.reason());
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
	public void onDisconnected(class_9812 arg) {
		this.worldSession.onUnload();
		this.client.disconnect(this.createDisconnectedScreen(arg), this.transferring);
		LOGGER.warn("Client disconnected with reason: {}", arg.reason().getString());
	}

	@Override
	public void addCustomCrashReportInfo(CrashReport crashReport, CrashReportSection crashReportSection) {
		crashReportSection.add("Server type", (CrashCallable<String>)(() -> this.serverInfo != null ? this.serverInfo.getServerType().toString() : "<none>"));
		crashReportSection.add("Server brand", (CrashCallable<String>)(() -> this.brand));
		if (!this.field_52154.isEmpty()) {
			CrashReportSection crashReportSection2 = crashReport.addElement("Custom Server Details");
			this.field_52154.forEach(crashReportSection2::add);
		}
	}

	protected Screen createDisconnectedScreen(class_9812 arg) {
		Screen screen = (Screen)Objects.requireNonNullElseGet(this.postDisconnectScreen, () -> new MultiplayerScreen(new TitleScreen()));
		return (Screen)(this.serverInfo != null && this.serverInfo.isRealm()
			? new DisconnectedRealmsScreen(screen, LOST_CONNECTION_TEXT, arg.reason())
			: new DisconnectedScreen(screen, LOST_CONNECTION_TEXT, arg));
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
			final MinecraftClient client,
			@Nullable final Screen parent,
			final List<ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack> pack,
			final boolean required,
			@Nullable final Text prompt
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
