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
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.DisconnectedScreen;
import net.minecraft.client.gui.menu.RealmsScreen;
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
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientLoginNetworkHandler implements ClientLoginPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	@Nullable
	private final Screen parentGui;
	private final Consumer<TextComponent> statusConsumer;
	private final ClientConnection field_3707;
	private GameProfile playerProfile;

	public ClientLoginNetworkHandler(ClientConnection clientConnection, MinecraftClient minecraftClient, @Nullable Screen screen, Consumer<TextComponent> consumer) {
		this.field_3707 = clientConnection;
		this.client = minecraftClient;
		this.parentGui = screen;
		this.statusConsumer = consumer;
	}

	@Override
	public void method_12587(LoginHelloS2CPacket loginHelloS2CPacket) {
		SecretKey secretKey = NetworkEncryptionUtils.generateKey();
		PublicKey publicKey = loginHelloS2CPacket.getPublicKey();
		String string = new BigInteger(NetworkEncryptionUtils.method_15240(loginHelloS2CPacket.getServerId(), publicKey, secretKey)).toString(16);
		LoginKeyC2SPacket loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, loginHelloS2CPacket.method_12613());
		this.statusConsumer.accept(new TranslatableTextComponent("connect.authorizing"));
		NetworkUtils.downloadExecutor.submit((Runnable)(() -> {
			TextComponent textComponent = this.method_2892(string);
			if (textComponent != null) {
				if (this.client.method_1558() == null || !this.client.method_1558().isLocal()) {
					this.field_3707.method_10747(textComponent);
					return;
				}

				LOGGER.warn(textComponent.getString());
			}

			this.statusConsumer.accept(new TranslatableTextComponent("connect.encrypting"));
			this.field_3707.method_10752(loginKeyC2SPacket, future -> this.field_3707.setupEncryption(secretKey));
		}));
	}

	@Nullable
	private TextComponent method_2892(String string) {
		try {
			this.getSessionService().joinServer(this.client.method_1548().getProfile(), this.client.method_1548().getAccessToken(), string);
			return null;
		} catch (AuthenticationUnavailableException var3) {
			return new TranslatableTextComponent("disconnect.loginFailedInfo", new TranslatableTextComponent("disconnect.loginFailedInfo.serversUnavailable"));
		} catch (InvalidCredentialsException var4) {
			return new TranslatableTextComponent("disconnect.loginFailedInfo", new TranslatableTextComponent("disconnect.loginFailedInfo.invalidSession"));
		} catch (AuthenticationException var5) {
			return new TranslatableTextComponent("disconnect.loginFailedInfo", var5.getMessage());
		}
	}

	private MinecraftSessionService getSessionService() {
		return this.client.getSessionService();
	}

	@Override
	public void method_12588(LoginSuccessS2CPacket loginSuccessS2CPacket) {
		this.statusConsumer.accept(new TranslatableTextComponent("connect.joining"));
		this.playerProfile = loginSuccessS2CPacket.getPlayerProfile();
		this.field_3707.method_10750(NetworkState.GAME);
		this.field_3707.method_10763(new ClientPlayNetworkHandler(this.client, this.parentGui, this.field_3707, this.playerProfile));
	}

	@Override
	public void method_10839(TextComponent textComponent) {
		if (this.parentGui != null && this.parentGui instanceof RealmsScreen) {
			this.client.method_1507(new DisconnectedRealmsScreen(((RealmsScreen)this.parentGui).getRealmsScreen(), "connect.failed", textComponent).getProxy());
		} else {
			this.client.method_1507(new DisconnectedScreen(this.parentGui, "connect.failed", textComponent));
		}
	}

	@Override
	public void method_12584(LoginDisconnectS2CPacket loginDisconnectS2CPacket) {
		this.field_3707.method_10747(loginDisconnectS2CPacket.getReason());
	}

	@Override
	public void method_12585(LoginCompressionS2CPacket loginCompressionS2CPacket) {
		if (!this.field_3707.isLocal()) {
			this.field_3707.setMinCompressedSize(loginCompressionS2CPacket.getMinCompressedSize());
		}
	}

	@Override
	public void method_12586(LoginQueryRequestS2CPacket loginQueryRequestS2CPacket) {
		this.statusConsumer.accept(new TranslatableTextComponent("connect.negotiating"));
		this.field_3707.method_10743(new LoginQueryResponseC2SPacket(loginQueryRequestS2CPacket.getQueryId(), null));
	}
}
