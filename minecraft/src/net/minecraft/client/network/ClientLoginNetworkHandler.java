package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.ForcedUsernameChangeException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserBannedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.security.PublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientLoginNetworkHandler implements ClientLoginPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final MinecraftClient client;
	@Nullable
	private final ServerInfo serverInfo;
	@Nullable
	private final Screen parentScreen;
	private final Consumer<Text> statusConsumer;
	private final ClientConnection connection;
	private final boolean newWorld;
	@Nullable
	private final Duration worldLoadTime;
	@Nullable
	private String minigameName;
	private final Map<Identifier, byte[]> serverCookies;
	private final boolean hasCookies;
	private final AtomicReference<ClientLoginNetworkHandler.State> state = new AtomicReference(ClientLoginNetworkHandler.State.CONNECTING);

	public ClientLoginNetworkHandler(
		ClientConnection connection,
		MinecraftClient client,
		@Nullable ServerInfo serverInfo,
		@Nullable Screen parentScreen,
		boolean newWorld,
		@Nullable Duration worldLoadTime,
		Consumer<Text> statusConsumer,
		@Nullable CookieStorage cookieStorage
	) {
		this.connection = connection;
		this.client = client;
		this.serverInfo = serverInfo;
		this.parentScreen = parentScreen;
		this.statusConsumer = statusConsumer;
		this.newWorld = newWorld;
		this.worldLoadTime = worldLoadTime;
		this.serverCookies = cookieStorage != null ? new HashMap(cookieStorage.cookies()) : new HashMap();
		this.hasCookies = cookieStorage != null;
	}

	private void switchTo(ClientLoginNetworkHandler.State state) {
		ClientLoginNetworkHandler.State state2 = (ClientLoginNetworkHandler.State)this.state.updateAndGet(currentState -> {
			if (!state.prevStates.contains(currentState)) {
				throw new IllegalStateException("Tried to switch to " + state + " from " + currentState + ", but expected one of " + state.prevStates);
			} else {
				return state;
			}
		});
		this.statusConsumer.accept(state2.name);
	}

	@Override
	public void onHello(LoginHelloS2CPacket packet) {
		this.switchTo(ClientLoginNetworkHandler.State.AUTHORIZING);

		Cipher cipher;
		Cipher cipher2;
		String string;
		LoginKeyC2SPacket loginKeyC2SPacket;
		try {
			SecretKey secretKey = NetworkEncryptionUtils.generateSecretKey();
			PublicKey publicKey = packet.getPublicKey();
			string = new BigInteger(NetworkEncryptionUtils.computeServerId(packet.getServerId(), publicKey, secretKey)).toString(16);
			cipher = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
			cipher2 = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
			byte[] bs = packet.getNonce();
			loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, bs);
		} catch (Exception var9) {
			throw new IllegalStateException("Protocol error", var9);
		}

		if (packet.needsAuthentication()) {
			Util.getIoWorkerExecutor().submit(() -> {
				Text text = this.joinServerSession(string);
				if (text != null) {
					if (this.serverInfo == null || !this.serverInfo.isLocal()) {
						this.connection.disconnect(text);
						return;
					}

					LOGGER.warn(text.getString());
				}

				this.setupEncryption(loginKeyC2SPacket, cipher, cipher2);
			});
		} else {
			this.setupEncryption(loginKeyC2SPacket, cipher, cipher2);
		}
	}

	private void setupEncryption(LoginKeyC2SPacket keyPacket, Cipher decryptionCipher, Cipher encryptionCipher) {
		this.switchTo(ClientLoginNetworkHandler.State.ENCRYPTING);
		this.connection.send(keyPacket, PacketCallbacks.always(() -> this.connection.setupEncryption(decryptionCipher, encryptionCipher)));
	}

	@Nullable
	private Text joinServerSession(String serverId) {
		try {
			this.getSessionService().joinServer(this.client.getSession().getUuidOrNull(), this.client.getSession().getAccessToken(), serverId);
			return null;
		} catch (AuthenticationUnavailableException var3) {
			return Text.translatable("disconnect.loginFailedInfo", Text.translatable("disconnect.loginFailedInfo.serversUnavailable"));
		} catch (InvalidCredentialsException var4) {
			return Text.translatable("disconnect.loginFailedInfo", Text.translatable("disconnect.loginFailedInfo.invalidSession"));
		} catch (InsufficientPrivilegesException var5) {
			return Text.translatable("disconnect.loginFailedInfo", Text.translatable("disconnect.loginFailedInfo.insufficientPrivileges"));
		} catch (ForcedUsernameChangeException | UserBannedException var6) {
			return Text.translatable("disconnect.loginFailedInfo", Text.translatable("disconnect.loginFailedInfo.userBanned"));
		} catch (AuthenticationException var7) {
			return Text.translatable("disconnect.loginFailedInfo", var7.getMessage());
		}
	}

	private MinecraftSessionService getSessionService() {
		return this.client.getSessionService();
	}

	@Override
	public void onSuccess(LoginSuccessS2CPacket packet) {
		this.switchTo(ClientLoginNetworkHandler.State.JOINING);
		GameProfile gameProfile = packet.profile();
		this.connection
			.transitionInbound(
				ConfigurationStates.S2C,
				new ClientConfigurationNetworkHandler(
					this.client,
					this.connection,
					new ClientConnectionState(
						gameProfile,
						this.client.getTelemetryManager().createWorldSession(this.newWorld, this.worldLoadTime, this.minigameName),
						ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager(),
						FeatureFlags.DEFAULT_ENABLED_FEATURES,
						null,
						this.serverInfo,
						this.parentScreen,
						this.serverCookies,
						null
					)
				)
			);
		this.connection.send(EnterConfigurationC2SPacket.INSTANCE);
		this.connection.transitionOutbound(ConfigurationStates.C2S);
		this.connection.send(new CustomPayloadC2SPacket(new BrandCustomPayload(ClientBrandRetriever.getClientModName())));
		this.connection.send(new ClientOptionsC2SPacket(this.client.options.getSyncedOptions()));
	}

	@Override
	public void onDisconnected(Text reason) {
		Text text = this.hasCookies ? ScreenTexts.CONNECT_FAILED_TRANSFER : ScreenTexts.CONNECT_FAILED;
		if (this.serverInfo != null && this.serverInfo.isRealm()) {
			this.client.setScreen(new DisconnectedRealmsScreen(this.parentScreen, text, reason));
		} else {
			this.client.setScreen(new DisconnectedScreen(this.parentScreen, text, reason));
		}
	}

	@Override
	public boolean isConnectionOpen() {
		return this.connection.isOpen();
	}

	@Override
	public void onDisconnect(LoginDisconnectS2CPacket packet) {
		this.connection.disconnect(packet.getReason());
	}

	@Override
	public void onCompression(LoginCompressionS2CPacket packet) {
		if (!this.connection.isLocal()) {
			this.connection.setCompressionThreshold(packet.getCompressionThreshold(), false);
		}
	}

	@Override
	public void onQueryRequest(LoginQueryRequestS2CPacket packet) {
		this.statusConsumer.accept(Text.translatable("connect.negotiating"));
		this.connection.send(new LoginQueryResponseC2SPacket(packet.queryId(), null));
	}

	public void setMinigameName(@Nullable String minigameName) {
		this.minigameName = minigameName;
	}

	@Override
	public void onCookieRequest(CookieRequestS2CPacket packet) {
		this.connection.send(new CookieResponseC2SPacket(packet.key(), (byte[])this.serverCookies.get(packet.key())));
	}

	@Override
	public void addCustomCrashReportInfo(CrashReportSection section) {
		section.add("Server type", (CrashCallable<String>)(() -> this.serverInfo != null ? this.serverInfo.getServerType().toString() : "<unknown>"));
		section.add("Login phase", (CrashCallable<String>)(() -> ((ClientLoginNetworkHandler.State)this.state.get()).toString()));
	}

	@Environment(EnvType.CLIENT)
	static enum State {
		CONNECTING(Text.translatable("connect.connecting"), Set.of()),
		AUTHORIZING(Text.translatable("connect.authorizing"), Set.of(CONNECTING)),
		ENCRYPTING(Text.translatable("connect.encrypting"), Set.of(AUTHORIZING)),
		JOINING(Text.translatable("connect.joining"), Set.of(ENCRYPTING, CONNECTING));

		final Text name;
		final Set<ClientLoginNetworkHandler.State> prevStates;

		private State(Text name, Set<ClientLoginNetworkHandler.State> prevStates) {
			this.name = name;
			this.prevStates = prevStates;
		}
	}
}
