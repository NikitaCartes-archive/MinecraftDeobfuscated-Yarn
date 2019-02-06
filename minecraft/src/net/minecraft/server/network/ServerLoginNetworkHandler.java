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
import javax.crypto.SecretKey;
import net.minecraft.class_2901;
import net.minecraft.class_2905;
import net.minecraft.class_2907;
import net.minecraft.class_2909;
import net.minecraft.class_2913;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.UncaughtExceptionLogger;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLoginNetworkHandler implements ServerLoginPacketListener, Tickable {
	private static final AtomicInteger field_14157 = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Random RANDOM = new Random();
	private final byte[] field_14167 = new byte[4];
	private final MinecraftServer server;
	public final ClientConnection client;
	private ServerLoginNetworkHandler.State state = ServerLoginNetworkHandler.State.field_14170;
	private int loginTicks;
	private GameProfile profile;
	private final String field_14165 = "";
	private SecretKey field_14159;
	private ServerPlayerEntity clientEntity;

	public ServerLoginNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.client = clientConnection;
		RANDOM.nextBytes(this.field_14167);
	}

	@Override
	public void tick() {
		if (this.state == ServerLoginNetworkHandler.State.field_14168) {
			this.method_14384();
		} else if (this.state == ServerLoginNetworkHandler.State.field_14171) {
			ServerPlayerEntity serverPlayerEntity = this.server.getPlayerManager().getPlayer(this.profile.getId());
			if (serverPlayerEntity == null) {
				this.state = ServerLoginNetworkHandler.State.field_14168;
				this.server.getPlayerManager().onPlayerConnect(this.client, this.clientEntity);
				this.clientEntity = null;
			}
		}

		if (this.loginTicks++ == 600) {
			this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.slow_login"));
		}
	}

	public void disconnect(TextComponent textComponent) {
		try {
			LOGGER.info("Disconnecting {}: {}", this.method_14383(), textComponent.getString());
			this.client.sendPacket(new class_2909(textComponent));
			this.client.disconnect(textComponent);
		} catch (Exception var3) {
			LOGGER.error("Error whilst disconnecting player", (Throwable)var3);
		}
	}

	public void method_14384() {
		if (!this.profile.isComplete()) {
			this.profile = this.toOfflineProfile(this.profile);
		}

		TextComponent textComponent = this.server.getPlayerManager().method_14586(this.client.getAddress(), this.profile);
		if (textComponent != null) {
			this.disconnect(textComponent);
		} else {
			this.state = ServerLoginNetworkHandler.State.field_14172;
			if (this.server.getNetworkCompressionThreshold() >= 0 && !this.client.isLocal()) {
				this.client
					.sendPacket(
						new class_2907(this.server.getNetworkCompressionThreshold()),
						channelFuture -> this.client.setMinCompressedSize(this.server.getNetworkCompressionThreshold())
					);
			}

			this.client.sendPacket(new class_2901(this.profile));
			ServerPlayerEntity serverPlayerEntity = this.server.getPlayerManager().getPlayer(this.profile.getId());
			if (serverPlayerEntity != null) {
				this.state = ServerLoginNetworkHandler.State.field_14171;
				this.clientEntity = this.server.getPlayerManager().method_14613(this.profile);
			} else {
				this.server.getPlayerManager().onPlayerConnect(this.client, this.server.getPlayerManager().method_14613(this.profile));
			}
		}
	}

	@Override
	public void onConnectionLost(TextComponent textComponent) {
		LOGGER.info("{} lost connection: {}", this.method_14383(), textComponent.getString());
	}

	public String method_14383() {
		return this.profile != null ? this.profile + " (" + this.client.getAddress() + ")" : String.valueOf(this.client.getAddress());
	}

	@Override
	public void method_12641(LoginHelloC2SPacket loginHelloC2SPacket) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.field_14170, "Unexpected hello packet");
		this.profile = loginHelloC2SPacket.getProfile();
		if (this.server.isOnlineMode() && !this.client.isLocal()) {
			this.state = ServerLoginNetworkHandler.State.field_14175;
			this.client.sendPacket(new class_2905("", this.server.getKeyPair().getPublic(), this.field_14167));
		} else {
			this.state = ServerLoginNetworkHandler.State.field_14168;
		}
	}

	@Override
	public void method_12642(LoginKeyC2SPacket loginKeyC2SPacket) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.field_14175, "Unexpected key packet");
		PrivateKey privateKey = this.server.getKeyPair().getPrivate();
		if (!Arrays.equals(this.field_14167, loginKeyC2SPacket.method_12655(privateKey))) {
			throw new IllegalStateException("Invalid nonce!");
		} else {
			this.field_14159 = loginKeyC2SPacket.method_12654(privateKey);
			this.state = ServerLoginNetworkHandler.State.field_14169;
			this.client.setupEncryption(this.field_14159);
			Thread thread = new Thread("User Authenticator #" + field_14157.incrementAndGet()) {
				public void run() {
					GameProfile gameProfile = ServerLoginNetworkHandler.this.profile;

					try {
						String string = new BigInteger(
								NetworkEncryptionUtils.method_15240("", ServerLoginNetworkHandler.this.server.getKeyPair().getPublic(), ServerLoginNetworkHandler.this.field_14159)
							)
							.toString(16);
						ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.server
							.getSessionService()
							.hasJoinedServer(new GameProfile(null, gameProfile.getName()), string, this.method_14386());
						if (ServerLoginNetworkHandler.this.profile != null) {
							ServerLoginNetworkHandler.LOGGER
								.info("UUID of player {} is {}", ServerLoginNetworkHandler.this.profile.getName(), ServerLoginNetworkHandler.this.profile.getId());
							ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.field_14168;
						} else if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
							ServerLoginNetworkHandler.LOGGER.warn("Failed to verify username but will let them in anyway!");
							ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.toOfflineProfile(gameProfile);
							ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.field_14168;
						} else {
							ServerLoginNetworkHandler.this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.unverified_username"));
							ServerLoginNetworkHandler.LOGGER.error("Username '{}' tried to join with an invalid session", gameProfile.getName());
						}
					} catch (AuthenticationUnavailableException var3) {
						if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
							ServerLoginNetworkHandler.LOGGER.warn("Authentication servers are down but will let them in anyway!");
							ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.toOfflineProfile(gameProfile);
							ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.field_14168;
						} else {
							ServerLoginNetworkHandler.this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.authservers_down"));
							ServerLoginNetworkHandler.LOGGER.error("Couldn't verify username because servers are unavailable");
						}
					}
				}

				@Nullable
				private InetAddress method_14386() {
					SocketAddress socketAddress = ServerLoginNetworkHandler.this.client.getAddress();
					return ServerLoginNetworkHandler.this.server.shouldPreventProxyConnections() && socketAddress instanceof InetSocketAddress
						? ((InetSocketAddress)socketAddress).getAddress()
						: null;
				}
			};
			thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
			thread.start();
		}
	}

	@Override
	public void method_12640(class_2913 arg) {
		this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.unexpected_query_response"));
	}

	protected GameProfile toOfflineProfile(GameProfile gameProfile) {
		UUID uUID = PlayerEntity.getOfflinePlayerUuid(gameProfile.getName());
		return new GameProfile(uUID, gameProfile.getName());
	}

	static enum State {
		field_14170,
		field_14175,
		field_14169,
		field_14173,
		field_14168,
		field_14171,
		field_14172;
	}
}
