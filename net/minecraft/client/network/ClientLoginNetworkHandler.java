/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientLoginNetworkHandler
implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftClient client;
    @Nullable
    private final Screen parentScreen;
    private final Consumer<Text> statusConsumer;
    private final ClientConnection connection;
    private GameProfile profile;

    public ClientLoginNetworkHandler(ClientConnection connection, MinecraftClient client, @Nullable Screen parentGui, Consumer<Text> statusConsumer) {
        this.connection = connection;
        this.client = client;
        this.parentScreen = parentGui;
        this.statusConsumer = statusConsumer;
    }

    @Override
    public void onHello(LoginHelloS2CPacket packet) {
        LoginKeyC2SPacket loginKeyC2SPacket;
        Cipher cipher2;
        Cipher cipher;
        String string;
        try {
            SecretKey secretKey = NetworkEncryptionUtils.generateKey();
            PublicKey publicKey = packet.getPublicKey();
            string = new BigInteger(NetworkEncryptionUtils.generateServerId(packet.getServerId(), publicKey, secretKey)).toString(16);
            cipher = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
            cipher2 = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
            loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, packet.getNonce());
        } catch (NetworkEncryptionException networkEncryptionException) {
            throw new IllegalStateException("Protocol error", networkEncryptionException);
        }
        this.statusConsumer.accept(Text.method_43471("connect.authorizing"));
        NetworkUtils.EXECUTOR.submit(() -> {
            Text text = this.joinServerSession(string);
            if (text != null) {
                if (this.client.getCurrentServerEntry() != null && this.client.getCurrentServerEntry().isLocal()) {
                    LOGGER.warn(text.getString());
                } else {
                    this.connection.disconnect(text);
                    return;
                }
            }
            this.statusConsumer.accept(Text.method_43471("connect.encrypting"));
            this.connection.send(loginKeyC2SPacket, future -> this.connection.setupEncryption(cipher, cipher2));
        });
    }

    @Nullable
    private Text joinServerSession(String serverId) {
        try {
            this.getSessionService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), serverId);
        } catch (AuthenticationUnavailableException authenticationUnavailableException) {
            return Text.method_43469("disconnect.loginFailedInfo", Text.method_43471("disconnect.loginFailedInfo.serversUnavailable"));
        } catch (InvalidCredentialsException invalidCredentialsException) {
            return Text.method_43469("disconnect.loginFailedInfo", Text.method_43471("disconnect.loginFailedInfo.invalidSession"));
        } catch (InsufficientPrivilegesException insufficientPrivilegesException) {
            return Text.method_43469("disconnect.loginFailedInfo", Text.method_43471("disconnect.loginFailedInfo.insufficientPrivileges"));
        } catch (AuthenticationException authenticationException) {
            return Text.method_43469("disconnect.loginFailedInfo", authenticationException.getMessage());
        }
        return null;
    }

    private MinecraftSessionService getSessionService() {
        return this.client.getSessionService();
    }

    @Override
    public void onSuccess(LoginSuccessS2CPacket packet) {
        this.statusConsumer.accept(Text.method_43471("connect.joining"));
        this.profile = packet.getProfile();
        this.connection.setState(NetworkState.PLAY);
        this.connection.setPacketListener(new ClientPlayNetworkHandler(this.client, this.parentScreen, this.connection, this.profile, this.client.createTelemetrySender()));
    }

    @Override
    public void onDisconnected(Text reason) {
        if (this.parentScreen != null && this.parentScreen instanceof RealmsScreen) {
            this.client.setScreen(new DisconnectedRealmsScreen(this.parentScreen, ScreenTexts.CONNECT_FAILED, reason));
        } else {
            this.client.setScreen(new DisconnectedScreen(this.parentScreen, ScreenTexts.CONNECT_FAILED, reason));
        }
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
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
        this.statusConsumer.accept(Text.method_43471("connect.negotiating"));
        this.connection.send(new LoginQueryResponseC2SPacket(packet.getQueryId(), null));
    }
}

