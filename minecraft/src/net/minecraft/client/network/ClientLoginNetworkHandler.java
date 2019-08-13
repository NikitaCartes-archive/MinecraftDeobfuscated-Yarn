package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.packet.LoginCompressionS2CPacket;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.client.network.packet.LoginHelloS2CPacket;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.client.network.packet.LoginSuccessS2CPacket;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientLoginNetworkHandler implements ClientLoginPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	@Nullable
	private final Screen parentGui;
	private final Consumer<Text> statusConsumer;
	private final ClientConnection connection;
	private GameProfile profile;

	public ClientLoginNetworkHandler(ClientConnection clientConnection, MinecraftClient minecraftClient, @Nullable Screen screen, Consumer<Text> consumer) {
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
		this.statusConsumer.accept(new TranslatableText("connect.authorizing"));
		NetworkUtils.downloadExecutor.submit((Runnable)(() -> {
			Text text = this.joinServerSession(string);
			if (text != null) {
				if (this.client.getCurrentServerEntry() == null || !this.client.getCurrentServerEntry().isLocal()) {
					this.connection.disconnect(text);
					return;
				}

				LOGGER.warn(text.getString());
			}

			this.statusConsumer.accept(new TranslatableText("connect.encrypting"));
			this.connection.send(loginKeyC2SPacket, future -> this.connection.setupEncryption(secretKey));
		}));
	}

	@Nullable
	private Text joinServerSession(String string) {
		try {
			this.getSessionService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), string);
			return null;
		} catch (AuthenticationUnavailableException var3) {
			return new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.serversUnavailable"));
		} catch (InvalidCredentialsException var4) {
			return new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.invalidSession"));
		} catch (AuthenticationException var5) {
			return new TranslatableText("disconnect.loginFailedInfo", var5.getMessage());
		}
	}

	private MinecraftSessionService getSessionService() {
		return this.client.getSessionService();
	}

	@Override
	public void onLoginSuccess(LoginSuccessS2CPacket loginSuccessS2CPacket) {
		this.statusConsumer.accept(new TranslatableText("connect.joining"));
		this.profile = loginSuccessS2CPacket.getProfile();
		this.connection.setState(NetworkState.field_11690);
		this.connection.setPacketListener(new ClientPlayNetworkHandler(this.client, this.parentGui, this.connection, this.profile));
	}

	@Override
	public void onDisconnected(Text text) {
		if (this.parentGui != null && this.parentGui instanceof RealmsScreenProxy) {
			this.client.openScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.parentGui).getScreen(), "connect.failed", text).getProxy());
		} else {
			this.client.openScreen(new DisconnectedScreen(this.parentGui, "connect.failed", text));
		}
	}

	@Override
	public ClientConnection getConnection() {
		return this.connection;
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
		this.statusConsumer.accept(new TranslatableText("connect.negotiating"));
		this.connection.send(new LoginQueryResponseC2SPacket(loginQueryRequestS2CPacket.getQueryId(), null));
	}
}
