/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.class_7648;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.TextifiedException;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
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
public class ServerLoginNetworkHandler
implements TickablePacketListener,
ServerLoginPacketListener {
    private static final AtomicInteger NEXT_AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int TIMEOUT_TICKS = 600;
    private static final Random RANDOM = Random.create();
    private static final Text MISSING_PUBLIC_KEY_TEXT = Text.translatable("multiplayer.disconnect.missing_public_key");
    private static final Text INVALID_PUBLIC_KEY_SIGNATURE_TEXT = Text.translatable("multiplayer.disconnect.invalid_public_key_signature");
    private static final Text INVALID_PUBLIC_KEY_TEXT = Text.translatable("multiplayer.disconnect.invalid_public_key");
    private final byte[] nonce;
    final MinecraftServer server;
    public final ClientConnection connection;
    State state = State.HELLO;
    private int loginTicks;
    @Nullable
    GameProfile profile;
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
    @Nullable
    private PlayerPublicKey.PublicKeyData publicKeyData;

    public ServerLoginNetworkHandler(MinecraftServer server, ClientConnection connection) {
        this.server = server;
        this.connection = connection;
        this.nonce = Ints.toByteArray(RANDOM.nextInt());
    }

    @Override
    public void tick() {
        ServerPlayerEntity serverPlayerEntity;
        if (this.state == State.READY_TO_ACCEPT) {
            this.acceptPlayer();
        } else if (this.state == State.DELAY_ACCEPT && (serverPlayerEntity = this.server.getPlayerManager().getPlayer(this.profile.getId())) == null) {
            this.state = State.READY_TO_ACCEPT;
            this.addToServer(this.delayedPlayer);
            this.delayedPlayer = null;
        }
        if (this.loginTicks++ == 600) {
            this.disconnect(Text.translatable("multiplayer.disconnect.slow_login"));
        }
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }

    public void disconnect(Text reason) {
        try {
            LOGGER.info("Disconnecting {}: {}", (Object)this.getConnectionInfo(), (Object)reason.getString());
            this.connection.send(new LoginDisconnectS2CPacket(reason));
            this.connection.disconnect(reason);
        } catch (Exception exception) {
            LOGGER.error("Error whilst disconnecting player", exception);
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
        PlayerPublicKey playerPublicKey;
        block11: {
            playerPublicKey = null;
            if (!this.profile.isComplete()) {
                this.profile = this.toOfflineProfile(this.profile);
            } else {
                try {
                    SignatureVerifier signatureVerifier = this.server.getServicesSignatureVerifier();
                    playerPublicKey = ServerLoginNetworkHandler.getVerifiedPublicKey(this.publicKeyData, this.profile.getId(), signatureVerifier, this.server.shouldEnforceSecureProfile());
                } catch (LoginException loginException) {
                    LOGGER.error(loginException.getMessage(), loginException.getCause());
                    if (this.connection.isLocal()) break block11;
                    this.disconnect(loginException.getMessageText());
                    return;
                }
            }
        }
        Text text = this.server.getPlayerManager().checkCanJoin(this.connection.getAddress(), this.profile);
        if (text != null) {
            this.disconnect(text);
        } else {
            this.state = State.ACCEPTED;
            if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {
                this.connection.send(new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()), class_7648.method_45084(() -> this.connection.setCompressionThreshold(this.server.getNetworkCompressionThreshold(), true)));
            }
            this.connection.send(new LoginSuccessS2CPacket(this.profile));
            ServerPlayerEntity serverPlayerEntity = this.server.getPlayerManager().getPlayer(this.profile.getId());
            try {
                ServerPlayerEntity serverPlayerEntity2 = this.server.getPlayerManager().createPlayer(this.profile, playerPublicKey);
                if (serverPlayerEntity != null) {
                    this.state = State.DELAY_ACCEPT;
                    this.delayedPlayer = serverPlayerEntity2;
                } else {
                    this.addToServer(serverPlayerEntity2);
                }
            } catch (Exception exception) {
                LOGGER.error("Couldn't place player in world", exception);
                MutableText text2 = Text.translatable("multiplayer.disconnect.invalid_player_data");
                this.connection.send(new DisconnectS2CPacket(text2));
                this.connection.disconnect(text2);
            }
        }
    }

    private void addToServer(ServerPlayerEntity player) {
        this.server.getPlayerManager().onPlayerConnect(this.connection, player);
    }

    @Override
    public void onDisconnected(Text reason) {
        LOGGER.info("{} lost connection: {}", (Object)this.getConnectionInfo(), (Object)reason.getString());
    }

    public String getConnectionInfo() {
        if (this.profile != null) {
            return this.profile + " (" + this.connection.getAddress() + ")";
        }
        return String.valueOf(this.connection.getAddress());
    }

    @Nullable
    private static PlayerPublicKey getVerifiedPublicKey(@Nullable PlayerPublicKey.PublicKeyData publicKeyData, UUID playerUuid, SignatureVerifier servicesSignatureVerifier, boolean shouldThrowOnMissingKey) throws LoginException {
        try {
            if (publicKeyData == null) {
                if (shouldThrowOnMissingKey) {
                    throw new LoginException(MISSING_PUBLIC_KEY_TEXT);
                }
                return null;
            }
            return PlayerPublicKey.verifyAndDecode(servicesSignatureVerifier, playerUuid, publicKeyData);
        } catch (InsecurePublicKeyException.MissingException missingException) {
            if (shouldThrowOnMissingKey) {
                throw new LoginException(INVALID_PUBLIC_KEY_SIGNATURE_TEXT, (Throwable)missingException);
            }
            return null;
        } catch (NetworkEncryptionException networkEncryptionException) {
            throw new LoginException(INVALID_PUBLIC_KEY_TEXT, (Throwable)networkEncryptionException);
        } catch (Exception exception) {
            throw new LoginException(INVALID_PUBLIC_KEY_SIGNATURE_TEXT, (Throwable)exception);
        }
    }

    @Override
    public void onHello(LoginHelloC2SPacket packet) {
        Validate.validState(this.state == State.HELLO, "Unexpected hello packet", new Object[0]);
        Validate.validState(ServerLoginNetworkHandler.isValidName(packet.name()), "Invalid characters in username", new Object[0]);
        this.publicKeyData = packet.publicKey().orElse(null);
        GameProfile gameProfile = this.server.getHostProfile();
        if (gameProfile != null && packet.name().equalsIgnoreCase(gameProfile.getName())) {
            this.profile = gameProfile;
            this.state = State.READY_TO_ACCEPT;
            return;
        }
        this.profile = new GameProfile(null, packet.name());
        if (this.server.isOnlineMode() && !this.connection.isLocal()) {
            this.state = State.KEY;
            this.connection.send(new LoginHelloS2CPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
        } else {
            this.state = State.READY_TO_ACCEPT;
        }
    }

    public static boolean isValidName(String name) {
        return name.chars().filter(c -> c <= 32 || c >= 127).findAny().isEmpty();
    }

    @Override
    public void onKey(LoginKeyC2SPacket packet) {
        String string;
        Validate.validState(this.state == State.KEY, "Unexpected key packet", new Object[0]);
        try {
            PlayerPublicKey playerPublicKey;
            PrivateKey privateKey = this.server.getKeyPair().getPrivate();
            if (this.publicKeyData != null ? !packet.verifySignedNonce(this.nonce, playerPublicKey = PlayerPublicKey.fromKeyData(this.publicKeyData)) : !packet.verifyEncryptedNonce(this.nonce, privateKey)) {
                throw new IllegalStateException("Protocol error");
            }
            SecretKey secretKey = packet.decryptSecretKey(privateKey);
            Cipher cipher = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
            Cipher cipher2 = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
            string = new BigInteger(NetworkEncryptionUtils.computeServerId("", this.server.getKeyPair().getPublic(), secretKey)).toString(16);
            this.state = State.AUTHENTICATING;
            this.connection.setupEncryption(cipher, cipher2);
        } catch (NetworkEncryptionException networkEncryptionException) {
            throw new IllegalStateException("Protocol error", networkEncryptionException);
        }
        Thread thread = new Thread("User Authenticator #" + NEXT_AUTHENTICATOR_THREAD_ID.incrementAndGet()){

            @Override
            public void run() {
                GameProfile gameProfile = ServerLoginNetworkHandler.this.profile;
                try {
                    ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.server.getSessionService().hasJoinedServer(new GameProfile(null, gameProfile.getName()), string, this.getClientAddress());
                    if (ServerLoginNetworkHandler.this.profile != null) {
                        LOGGER.info("UUID of player {} is {}", (Object)ServerLoginNetworkHandler.this.profile.getName(), (Object)ServerLoginNetworkHandler.this.profile.getId());
                        ServerLoginNetworkHandler.this.state = State.READY_TO_ACCEPT;
                    } else if (ServerLoginNetworkHandler.this.server.isSingleplayer()) {
                        LOGGER.warn("Failed to verify username but will let them in anyway!");
                        ServerLoginNetworkHandler.this.profile = gameProfile;
                        ServerLoginNetworkHandler.this.state = State.READY_TO_ACCEPT;
                    } else {
                        ServerLoginNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.unverified_username"));
                        LOGGER.error("Username '{}' tried to join with an invalid session", (Object)gameProfile.getName());
                    }
                } catch (AuthenticationUnavailableException authenticationUnavailableException) {
                    if (ServerLoginNetworkHandler.this.server.isSingleplayer()) {
                        LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        ServerLoginNetworkHandler.this.profile = gameProfile;
                        ServerLoginNetworkHandler.this.state = State.READY_TO_ACCEPT;
                    }
                    ServerLoginNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.authservers_down"));
                    LOGGER.error("Couldn't verify username because servers are unavailable");
                }
            }

            @Nullable
            private InetAddress getClientAddress() {
                SocketAddress socketAddress = ServerLoginNetworkHandler.this.connection.getAddress();
                return ServerLoginNetworkHandler.this.server.shouldPreventProxyConnections() && socketAddress instanceof InetSocketAddress ? ((InetSocketAddress)socketAddress).getAddress() : null;
            }
        };
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
    }

    @Override
    public void onQueryResponse(LoginQueryResponseC2SPacket packet) {
        this.disconnect(Text.translatable("multiplayer.disconnect.unexpected_query_response"));
    }

    protected GameProfile toOfflineProfile(GameProfile profile) {
        UUID uUID = DynamicSerializableUuid.getOfflinePlayerUuid(profile.getName());
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

    static class LoginException
    extends TextifiedException {
        public LoginException(Text text) {
            super(text);
        }

        public LoginException(Text text, Throwable throwable) {
            super(text, throwable);
        }
    }
}

