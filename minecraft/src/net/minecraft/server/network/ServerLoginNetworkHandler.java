package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

/**
 * The server login network handler.
 * 
 * <p>It listens to packets on the netty event loop and is ticked on the
 * server thread simultaneously.
 * 
 * @implSpec The vanilla implementation is created by a handshake network
 * handler. It first receives a hello packet from the client. If it is in
 * {@linkplain MinecraftServer#isOnlineMode() online mode}, it goes through
 * an additional authentication process. Then it optionally sends a network
 * compression packet. Finally, when it can accept the player (no player
 * UUID conflicts), it will accept the player by sending a login success
 * packet and then transitions the connection's packet listener to a {@link
 * ServerPlayNetworkHandler}.
 */
public class ServerLoginNetworkHandler implements ServerLoginPacketListener, TickablePacketListener {
	private static final AtomicInteger NEXT_AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
	static final Logger LOGGER = LogUtils.getLogger();
	private static final int TIMEOUT_TICKS = 600;
	private static final Text UNEXPECTED_QUERY_RESPONSE_TEXT = Text.translatable("multiplayer.disconnect.unexpected_query_response");
	private final byte[] nonce;
	final MinecraftServer server;
	final ClientConnection connection;
	private volatile ServerLoginNetworkHandler.State state = ServerLoginNetworkHandler.State.HELLO;
	private int loginTicks;
	@Nullable
	String profileName;
	@Nullable
	private GameProfile profile;
	private final String serverId = "";

	public ServerLoginNetworkHandler(MinecraftServer server, ClientConnection connection) {
		this.server = server;
		this.connection = connection;
		this.nonce = Ints.toByteArray(Random.create().nextInt());
	}

	@Override
	public void tick() {
		if (this.state == ServerLoginNetworkHandler.State.VERIFYING) {
			this.tickVerify((GameProfile)Objects.requireNonNull(this.profile));
		}

		if (this.state == ServerLoginNetworkHandler.State.WAITING_FOR_DUPE_DISCONNECT && !this.hasPlayerWithId((GameProfile)Objects.requireNonNull(this.profile))) {
			this.sendSuccessPacket(this.profile);
		}

		if (this.loginTicks++ == 600) {
			this.disconnect(Text.translatable("multiplayer.disconnect.slow_login"));
		}
	}

	@Override
	public boolean isConnectionOpen() {
		return this.connection.isOpen();
	}

	public void disconnect(Text reason) {
		try {
			LOGGER.info("Disconnecting {}: {}", this.getConnectionInfo(), reason.getString());
			this.connection.send(new LoginDisconnectS2CPacket(reason));
			this.connection.disconnect(reason);
		} catch (Exception var3) {
			LOGGER.error("Error whilst disconnecting player", (Throwable)var3);
		}
	}

	private boolean hasPlayerWithId(GameProfile profile) {
		return this.server.getPlayerManager().getPlayer(profile.getId()) != null;
	}

	@Override
	public void onDisconnected(Text reason) {
		LOGGER.info("{} lost connection: {}", this.getConnectionInfo(), reason.getString());
	}

	public String getConnectionInfo() {
		String string = this.connection.getAddressAsString(this.server.shouldLogIps());
		return this.profileName != null ? this.profileName + " (" + string + ")" : string;
	}

	@Override
	public void onHello(LoginHelloC2SPacket packet) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.HELLO, "Unexpected hello packet");
		Validate.validState(isValidName(packet.name()), "Invalid characters in username");
		this.profileName = packet.name();
		GameProfile gameProfile = this.server.getHostProfile();
		if (gameProfile != null && this.profileName.equalsIgnoreCase(gameProfile.getName())) {
			this.startVerify(gameProfile);
		} else {
			if (this.server.isOnlineMode() && !this.connection.isLocal()) {
				this.state = ServerLoginNetworkHandler.State.KEY;
				this.connection.send(new LoginHelloS2CPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
			} else {
				this.startVerify(createOfflineProfile(this.profileName));
			}
		}
	}

	void startVerify(GameProfile profile) {
		this.profile = profile;
		this.state = ServerLoginNetworkHandler.State.VERIFYING;
	}

	private void tickVerify(GameProfile profile) {
		PlayerManager playerManager = this.server.getPlayerManager();
		Text text = playerManager.checkCanJoin(this.connection.getAddress(), profile);
		if (text != null) {
			this.disconnect(text);
		} else {
			if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {
				this.connection
					.send(
						new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()),
						PacketCallbacks.always(() -> this.connection.setCompressionThreshold(this.server.getNetworkCompressionThreshold(), true))
					);
			}

			boolean bl = playerManager.disconnectDuplicateLogins(profile);
			if (bl) {
				this.state = ServerLoginNetworkHandler.State.WAITING_FOR_DUPE_DISCONNECT;
			} else {
				this.sendSuccessPacket(profile);
			}
		}
	}

	private void sendSuccessPacket(GameProfile profile) {
		this.state = ServerLoginNetworkHandler.State.PROTOCOL_SWITCHING;
		this.connection.send(new LoginSuccessS2CPacket(profile));
	}

	public static boolean isValidName(String name) {
		return name.chars().filter(c -> c <= 32 || c >= 127).findAny().isEmpty();
	}

	@Override
	public void onKey(LoginKeyC2SPacket packet) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.KEY, "Unexpected key packet");

		final String string;
		try {
			PrivateKey privateKey = this.server.getKeyPair().getPrivate();
			if (!packet.verifySignedNonce(this.nonce, privateKey)) {
				throw new IllegalStateException("Protocol error");
			}

			SecretKey secretKey = packet.decryptSecretKey(privateKey);
			Cipher cipher = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
			Cipher cipher2 = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
			string = new BigInteger(NetworkEncryptionUtils.computeServerId("", this.server.getKeyPair().getPublic(), secretKey)).toString(16);
			this.state = ServerLoginNetworkHandler.State.AUTHENTICATING;
			this.connection.setupEncryption(cipher, cipher2);
		} catch (NetworkEncryptionException var7) {
			throw new IllegalStateException("Protocol error", var7);
		}

		Thread thread = new Thread("User Authenticator #" + NEXT_AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
			public void run() {
				String string = (String)Objects.requireNonNull(ServerLoginNetworkHandler.this.profileName, "Player name not initialized");

				try {
					GameProfile gameProfile = ServerLoginNetworkHandler.this.server.getSessionService().hasJoinedServer(string, string, this.getClientAddress());
					if (gameProfile != null) {
						ServerLoginNetworkHandler.LOGGER.info("UUID of player {} is {}", gameProfile.getName(), gameProfile.getId());
						ServerLoginNetworkHandler.this.startVerify(gameProfile);
					} else if (ServerLoginNetworkHandler.this.server.isSingleplayer()) {
						ServerLoginNetworkHandler.LOGGER.warn("Failed to verify username but will let them in anyway!");
						ServerLoginNetworkHandler.this.startVerify(ServerLoginNetworkHandler.createOfflineProfile(string));
					} else {
						ServerLoginNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.unverified_username"));
						ServerLoginNetworkHandler.LOGGER.error("Username '{}' tried to join with an invalid session", string);
					}
				} catch (AuthenticationUnavailableException var3) {
					if (ServerLoginNetworkHandler.this.server.isSingleplayer()) {
						ServerLoginNetworkHandler.LOGGER.warn("Authentication servers are down but will let them in anyway!");
						ServerLoginNetworkHandler.this.startVerify(ServerLoginNetworkHandler.createOfflineProfile(string));
					} else {
						ServerLoginNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.authservers_down"));
						ServerLoginNetworkHandler.LOGGER.error("Couldn't verify username because servers are unavailable");
					}
				}
			}

			@Nullable
			private InetAddress getClientAddress() {
				SocketAddress socketAddress = ServerLoginNetworkHandler.this.connection.getAddress();
				return ServerLoginNetworkHandler.this.server.shouldPreventProxyConnections() && socketAddress instanceof InetSocketAddress
					? ((InetSocketAddress)socketAddress).getAddress()
					: null;
			}
		};
		thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
		thread.start();
	}

	@Override
	public void onQueryResponse(LoginQueryResponseC2SPacket packet) {
		this.disconnect(UNEXPECTED_QUERY_RESPONSE_TEXT);
	}

	@Override
	public void onEnterConfiguration(EnterConfigurationC2SPacket packet) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.PROTOCOL_SWITCHING, "Unexpected login acknowledgement packet");
		ServerConfigurationNetworkHandler serverConfigurationNetworkHandler = new ServerConfigurationNetworkHandler(
			this.server, this.connection, (GameProfile)Objects.requireNonNull(this.profile)
		);
		this.connection.setPacketListener(serverConfigurationNetworkHandler);
		serverConfigurationNetworkHandler.sendConfigurations();
		this.state = ServerLoginNetworkHandler.State.ACCEPTED;
	}

	protected static GameProfile createOfflineProfile(String name) {
		UUID uUID = Uuids.getOfflinePlayerUuid(name);
		return new GameProfile(uUID, name);
	}

	static enum State {
		HELLO,
		KEY,
		AUTHENTICATING,
		NEGOTIATING,
		VERIFYING,
		WAITING_FOR_DUPE_DISCONNECT,
		PROTOCOL_SWITCHING,
		ACCEPTED;
	}
}
