/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.LoginCompressionS2CPacket;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.client.network.packet.LoginHelloS2CPacket;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.client.network.packet.LoginSuccessS2CPacket;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.NetworkState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientLoginNetworkHandler
implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    @Nullable
    private final Screen parentGui;
    private final Consumer<Component> statusConsumer;
    private final ClientConnection connection;
    private GameProfile profile;

    public ClientLoginNetworkHandler(ClientConnection clientConnection, MinecraftClient minecraftClient, @Nullable Screen screen, Consumer<Component> consumer) {
        this.connection = clientConnection;
        this.client = minecraftClient;
        this.parentGui = screen;
        this.statusConsumer = consumer;
    }

    @Override
    public void onHello(LoginHelloS2CPacket loginHelloS2CPacket) {
        SecretKey secretKey = NetworkEncryptionUtils.generateKey();
        PublicKey publicKey = loginHelloS2CPacket.getPublicKey();
        String string = new BigInteger(NetworkEncryptionUtils.generateServerId(loginHelloS2CPacket.getServerId(), publicKey, secretKey)).toString(16);
        LoginKeyC2SPacket loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, loginHelloS2CPacket.getNonce());
        this.statusConsumer.accept(new TranslatableComponent("connect.authorizing", new Object[0]));
        NetworkUtils.downloadExecutor.submit(() -> {
            Component component = this.joinServerSession(string);
            if (component != null) {
                if (this.client.getCurrentServerEntry() != null && this.client.getCurrentServerEntry().isLocal()) {
                    LOGGER.warn(component.getString());
                } else {
                    this.connection.disconnect(component);
                    return;
                }
            }
            this.statusConsumer.accept(new TranslatableComponent("connect.encrypting", new Object[0]));
            this.connection.send(loginKeyC2SPacket, future -> this.connection.setupEncryption(secretKey));
        });
    }

    @Nullable
    private Component joinServerSession(String string) {
        try {
            this.getSessionService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), string);
        } catch (AuthenticationUnavailableException authenticationUnavailableException) {
            return new TranslatableComponent("disconnect.loginFailedInfo", new TranslatableComponent("disconnect.loginFailedInfo.serversUnavailable", new Object[0]));
        } catch (InvalidCredentialsException invalidCredentialsException) {
            return new TranslatableComponent("disconnect.loginFailedInfo", new TranslatableComponent("disconnect.loginFailedInfo.invalidSession", new Object[0]));
        } catch (AuthenticationException authenticationException) {
            return new TranslatableComponent("disconnect.loginFailedInfo", authenticationException.getMessage());
        }
        return null;
    }

    private MinecraftSessionService getSessionService() {
        return this.client.getSessionService();
    }

    @Override
    public void onLoginSuccess(LoginSuccessS2CPacket loginSuccessS2CPacket) {
        this.statusConsumer.accept(new TranslatableComponent("connect.joining", new Object[0]));
        this.profile = loginSuccessS2CPacket.getProfile();
        this.connection.setState(NetworkState.PLAY);
        this.connection.setPacketListener(new ClientPlayNetworkHandler(this.client, this.parentGui, this.connection, this.profile));
    }

    @Override
    public void onDisconnected(Component component) {
        if (this.parentGui != null && this.parentGui instanceof RealmsScreenProxy) {
            this.client.openScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.parentGui).getScreen(), "connect.failed", component).getProxy());
        } else {
            this.client.openScreen(new DisconnectedScreen(this.parentGui, "connect.failed", component));
        }
    }

    @Override
    public void onDisconnect(LoginDisconnectS2CPacket loginDisconnectS2CPacket) {
        this.connection.disconnect(loginDisconnectS2CPacket.getReason());
    }

    @Override
    public void onCompression(LoginCompressionS2CPacket loginCompressionS2CPacket) {
        if (!this.connection.isLocal()) {
            this.connection.setMinCompressedSize(loginCompressionS2CPacket.getCompressionThreshold());
        }
    }

    @Override
    public void onQueryRequest(LoginQueryRequestS2CPacket loginQueryRequestS2CPacket) {
        this.statusConsumer.accept(new TranslatableComponent("connect.negotiating", new Object[0]));
        this.connection.send(new LoginQueryResponseC2SPacket(loginQueryRequestS2CPacket.getQueryId(), null));
    }
}

