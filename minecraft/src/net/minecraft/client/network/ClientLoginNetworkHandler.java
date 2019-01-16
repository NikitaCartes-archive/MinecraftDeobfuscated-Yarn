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
import net.minecraft.class_2899;
import net.minecraft.class_2901;
import net.minecraft.class_2905;
import net.minecraft.class_2907;
import net.minecraft.class_2909;
import net.minecraft.class_2913;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.menu.DisconnectedGui;
import net.minecraft.client.gui.menu.RealmsGui;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.server.network.packet.LoginKeyServerPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientLoginNetworkHandler implements ClientLoginPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	@Nullable
	private final Gui parentGui;
	private final Consumer<TextComponent> field_3711;
	private final ClientConnection connection;
	private GameProfile playerProfile;

	public ClientLoginNetworkHandler(ClientConnection clientConnection, MinecraftClient minecraftClient, @Nullable Gui gui, Consumer<TextComponent> consumer) {
		this.connection = clientConnection;
		this.client = minecraftClient;
		this.parentGui = gui;
		this.field_3711 = consumer;
	}

	@Override
	public void method_12587(class_2905 arg) {
		SecretKey secretKey = NetworkEncryptionUtils.generateKey();
		PublicKey publicKey = arg.method_12611();
		String string = new BigInteger(NetworkEncryptionUtils.method_15240(arg.method_12610(), publicKey, secretKey)).toString(16);
		LoginKeyServerPacket loginKeyServerPacket = new LoginKeyServerPacket(secretKey, publicKey, arg.method_12613());
		this.field_3711.accept(new TranslatableTextComponent("connect.authorizing"));
		NetworkUtils.downloadExecutor.submit((Runnable)(() -> {
			TextComponent textComponent = this.method_2892(string);
			if (textComponent != null) {
				if (this.client.getCurrentServerEntry() == null || !this.client.getCurrentServerEntry().isLocal()) {
					this.connection.disconnect(textComponent);
					return;
				}

				LOGGER.warn(textComponent.getString());
			}

			this.field_3711.accept(new TranslatableTextComponent("connect.encrypting"));
			this.connection.sendPacket(loginKeyServerPacket, future -> this.connection.setupEncryption(secretKey));
		}));
	}

	@Nullable
	private TextComponent method_2892(String string) {
		try {
			this.method_2891().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), string);
			return null;
		} catch (AuthenticationUnavailableException var3) {
			return new TranslatableTextComponent("disconnect.loginFailedInfo", new TranslatableTextComponent("disconnect.loginFailedInfo.serversUnavailable"));
		} catch (InvalidCredentialsException var4) {
			return new TranslatableTextComponent("disconnect.loginFailedInfo", new TranslatableTextComponent("disconnect.loginFailedInfo.invalidSession"));
		} catch (AuthenticationException var5) {
			return new TranslatableTextComponent("disconnect.loginFailedInfo", var5.getMessage());
		}
	}

	private MinecraftSessionService method_2891() {
		return this.client.getSessionService();
	}

	@Override
	public void method_12588(class_2901 arg) {
		this.field_3711.accept(new TranslatableTextComponent("connect.joining"));
		this.playerProfile = arg.method_12593();
		this.connection.setState(NetworkState.GAME);
		this.connection.setPacketListener(new ClientPlayNetworkHandler(this.client, this.parentGui, this.connection, this.playerProfile));
	}

	@Override
	public void onConnectionLost(TextComponent textComponent) {
		if (this.parentGui != null && this.parentGui instanceof RealmsGui) {
			this.client.openGui(new DisconnectedRealmsScreen(((RealmsGui)this.parentGui).getRealmsScreen(), "connect.failed", textComponent).getProxy());
		} else {
			this.client.openGui(new DisconnectedGui(this.parentGui, "connect.failed", textComponent));
		}
	}

	@Override
	public void method_12584(class_2909 arg) {
		this.connection.disconnect(arg.method_12638());
	}

	@Override
	public void method_12585(class_2907 arg) {
		if (!this.connection.isLocal()) {
			this.connection.setMinCompressedSize(arg.method_12634());
		}
	}

	@Override
	public void method_12586(class_2899 arg) {
		this.field_3711.accept(new TranslatableTextComponent("connect.negotiating"));
		this.connection.sendPacket(new class_2913(arg.method_12592(), null));
	}
}
