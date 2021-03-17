package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The server login network handler.
 * 
 * <p>It listens to packets on the netty event loop and is ticked on the
 * server thread simultaneously.
 * 
 * @implSpec The vanilla implementation is created by a handshake network
 * handler. It first receives a hello packet from the client. If it's in
 * online mode, it goes through an additional authentication process. Then
 * it optionally sends a network compression packet next. Finally, when it
 * can accept the player (no player UUID conflicts), it will accept the
 * player by sending a login success packet and then transitions the
 * connection's packet listener to a server play network handler.
 */
public class ServerLoginNetworkHandler implements ServerLoginPacketListener {
	private static final AtomicInteger NEXT_AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Random RANDOM = new Random();
	private final byte[] nonce = new byte[4];
	private final MinecraftServer server;
	public final ClientConnection connection;
	private ServerLoginNetworkHandler.State state = ServerLoginNetworkHandler.State.HELLO;
	private int loginTicks;
	@Nullable
	private GameProfile profile;
	private final String serverId = "";
	/**
	 * The delayed player, waiting to join the server once the existing player
	 * with the same UUID is gone.
	 * 
	 * <p>This will only be non-{@code null} if the state is delay-accept, and is reset
	 * to {@code null} once the player is accepted.
	 */
	@Nullable
	private ServerPlayerEntity delayedPlayer;

	public ServerLoginNetworkHandler(MinecraftServer server, ClientConnection connection) {
		this.server = server;
		this.connection = connection;
		RANDOM.nextBytes(this.nonce);
	}

	/**
	 * Ticks this login network handler.
	 * 
	 * <p>This accepts the player to the server if ready. If the state is delay
	 * accept, it checks if the old player with the same UUID is gone and
	 * admits the player.
	 * 
	 * @apiNote This should only be called on the server thread.
	 */
	public void tick() {
		if (this.state == ServerLoginNetworkHandler.State.READY_TO_ACCEPT) {
			this.acceptPlayer();
		} else if (this.state == ServerLoginNetworkHandler.State.DELAY_ACCEPT) {
			ServerPlayerEntity serverPlayerEntity = this.server.getPlayerManager().getPlayer(this.profile.getId());
			if (serverPlayerEntity == null) {
				this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
				this.addToServer(this.delayedPlayer);
				this.delayedPlayer = null;
			}
		}

		if (this.loginTicks++ == 600) {
			this.disconnect(new TranslatableText("multiplayer.disconnect.slow_login"));
		}
	}

	@Override
	public ClientConnection getConnection() {
		return this.connection;
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

	/**
	 * Creates the player to be added to the server and adds it to the server.
	 * 
	 * <p>If a player with the same UUID is in the world, it will create the
	 * player and transition to the delay accept state.
	 * 
	 * @apiNote This method should only be called on the server thread.
	 */
	public void acceptPlayer() {
		if (!this.profile.isComplete()) {
			this.profile = this.toOfflineProfile(this.profile);
		}

		Text text = this.server.getPlayerManager().checkCanJoin(this.connection.getAddress(), this.profile);
		if (text != null) {
			this.disconnect(text);
		} else {
			this.state = ServerLoginNetworkHandler.State.ACCEPTED;
			if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {
				this.connection
					.send(
						new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()),
						channelFuture -> this.connection.setCompressionThreshold(this.server.getNetworkCompressionThreshold())
					);
			}

			this.connection.send(new LoginSuccessS2CPacket(this.profile));
			ServerPlayerEntity serverPlayerEntity = this.server.getPlayerManager().getPlayer(this.profile.getId());
			if (serverPlayerEntity != null) {
				this.state = ServerLoginNetworkHandler.State.DELAY_ACCEPT;
				this.delayedPlayer = this.server.getPlayerManager().createPlayer(this.profile);
			} else {
				this.addToServer(this.server.getPlayerManager().createPlayer(this.profile));
			}
		}
	}

	private void addToServer(ServerPlayerEntity player) {
		this.server.getPlayerManager().onPlayerConnect(this.connection, player);
	}

	@Override
	public void onDisconnected(Text reason) {
		LOGGER.info("{} lost connection: {}", this.getConnectionInfo(), reason.getString());
	}

	public String getConnectionInfo() {
		return this.profile != null ? this.profile + " (" + this.connection.getAddress() + ")" : String.valueOf(this.connection.getAddress());
	}

	@Override
	public void onHello(LoginHelloC2SPacket packet) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.HELLO, "Unexpected hello packet");
		this.profile = packet.getProfile();
		if (this.server.isOnlineMode() && !this.connection.isLocal()) {
			this.state = ServerLoginNetworkHandler.State.KEY;
			this.connection.send(new LoginHelloS2CPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
		} else {
			this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
		}
	}

	@Override
	public void onKey(LoginKeyC2SPacket packet) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.KEY, "Unexpected key packet");
		PrivateKey privateKey = this.server.getKeyPair().getPrivate();

		final String string;
		try {
			if (!Arrays.equals(this.nonce, packet.decryptNonce(privateKey))) {
				throw new IllegalStateException("Protocol error");
			}

			SecretKey secretKey = packet.decryptSecretKey(privateKey);
			Cipher cipher = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
			Cipher cipher2 = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
			string = new BigInteger(NetworkEncryptionUtils.generateServerId("", this.server.getKeyPair().getPublic(), secretKey)).toString(16);
			this.state = ServerLoginNetworkHandler.State.AUTHENTICATING;
			this.connection.setupEncryption(cipher, cipher2);
		} catch (NetworkEncryptionException var7) {
			throw new IllegalStateException("Protocol error", var7);
		}

		Thread thread = new Thread("User Authenticator #" + NEXT_AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
			public void run() {
				GameProfile gameProfile = ServerLoginNetworkHandler.this.profile;

				try {
					ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.server
						.getSessionService()
						.hasJoinedServer(new GameProfile(null, gameProfile.getName()), string, this.getClientAddress());
					if (ServerLoginNetworkHandler.this.profile != null) {
						ServerLoginNetworkHandler.LOGGER
							.info("UUID of player {} is {}", ServerLoginNetworkHandler.this.profile.getName(), ServerLoginNetworkHandler.this.profile.getId());
						ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
					} else if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
						ServerLoginNetworkHandler.LOGGER.warn("Failed to verify username but will let them in anyway!");
						ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.toOfflineProfile(gameProfile);
						ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
					} else {
						ServerLoginNetworkHandler.this.disconnect(new TranslatableText("multiplayer.disconnect.unverified_username"));
						ServerLoginNetworkHandler.LOGGER.error("Username '{}' tried to join with an invalid session", gameProfile.getName());
					}
				} catch (AuthenticationUnavailableException var3) {
					if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
						ServerLoginNetworkHandler.LOGGER.warn("Authentication servers are down but will let them in anyway!");
						ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.toOfflineProfile(gameProfile);
						ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
					} else {
						ServerLoginNetworkHandler.this.disconnect(new TranslatableText("multiplayer.disconnect.authservers_down"));
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
		this.disconnect(new TranslatableText("multiplayer.disconnect.unexpected_query_response"));
	}

	protected GameProfile toOfflineProfile(GameProfile profile) {
		UUID uUID = PlayerEntity.getOfflinePlayerUuid(profile.getName());
		return new GameProfile(uUID, profile.getName());
	}

	static enum State {
		HELLO,
		KEY,
		AUTHENTICATING,
		NEGOTIATING,
		READY_TO_ACCEPT,
		DELAY_ACCEPT,
		ACCEPTED;
	}
}
