package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
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
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientLoginNetworkHandler implements ClientLoginPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
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
		Cipher cipher;
		Cipher cipher2;
		String string;
		LoginKeyC2SPacket loginKeyC2SPacket;
		try {
			SecretKey secretKey = NetworkEncryptionUtils.generateKey();
			PublicKey publicKey = packet.getPublicKey();
			string = new BigInteger(NetworkEncryptionUtils.generateServerId(packet.getServerId(), publicKey, secretKey)).toString(16);
			cipher = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
			cipher2 = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
			loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, packet.getNonce());
		} catch (NetworkEncryptionException var8) {
			throw new IllegalStateException("Protocol error", var8);
		}

		this.statusConsumer.accept(new TranslatableText("connect.authorizing"));
		NetworkUtils.EXECUTOR.submit((Runnable)(() -> {
			Text text = this.joinServerSession(string);
			if (text != null) {
				if (this.client.getCurrentServerEntry() == null || !this.client.getCurrentServerEntry().isLocal()) {
					this.connection.disconnect(text);
					return;
				}

				LOGGER.warn(text.getString());
			}

			this.statusConsumer.accept(new TranslatableText("connect.encrypting"));
			this.connection.send(loginKeyC2SPacket, future -> this.connection.setupEncryption(cipher, cipher2));
		}));
	}

	@Nullable
	private Text joinServerSession(String serverId) {
		try {
			this.getSessionService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), serverId);
			return null;
		} catch (AuthenticationUnavailableException var3) {
			return new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.serversUnavailable"));
		} catch (InvalidCredentialsException var4) {
			return new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.invalidSession"));
		} catch (InsufficientPrivilegesException var5) {
			return new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.insufficientPrivileges"));
		} catch (AuthenticationException var6) {
			return new TranslatableText("disconnect.loginFailedInfo", var6.getMessage());
		}
	}

	private MinecraftSessionService getSessionService() {
		return this.client.getSessionService();
	}

	@Override
	public void onLoginSuccess(LoginSuccessS2CPacket packet) {
		this.statusConsumer.accept(new TranslatableText("connect.joining"));
		this.profile = packet.getProfile();
		this.connection.setState(NetworkState.PLAY);
		this.connection
			.setPacketListener(new ClientPlayNetworkHandler(this.client, this.parentScreen, this.connection, this.profile, this.client.createTelemetrySender()));
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
		this.statusConsumer.accept(new TranslatableText("connect.negotiating"));
		this.connection.send(new LoginQueryResponseC2SPacket(packet.getQueryId(), null));
	}
}
