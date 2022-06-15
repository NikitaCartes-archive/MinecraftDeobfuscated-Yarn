package net.minecraft.client.realms;

import com.mojang.logging.LogUtils;
import java.net.InetSocketAddress;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsConnection {
	static final Logger LOGGER = LogUtils.getLogger();
	final Screen onlineScreen;
	volatile boolean aborted;
	@Nullable
	ClientConnection connection;

	public RealmsConnection(Screen onlineScreen) {
		this.onlineScreen = onlineScreen;
	}

	public void connect(RealmsServer server, ServerAddress address) {
		final MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.setConnectedToRealms(true);
		minecraftClient.loadBlockList();
		NarratorManager.INSTANCE.narrate(Text.translatable("mco.connect.success"));
		final String string = address.getAddress();
		final int i = address.getPort();
		(new Thread("Realms-connect-task") {
				public void run() {
					InetSocketAddress inetSocketAddress = null;

					try {
						inetSocketAddress = new InetSocketAddress(string, i);
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.this.connection = ClientConnection.connect(inetSocketAddress, minecraftClient.options.shouldUseNativeTransport());
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.this.connection
							.setPacketListener(new ClientLoginNetworkHandler(RealmsConnection.this.connection, minecraftClient, RealmsConnection.this.onlineScreen, status -> {
							}));
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.this.connection.send(new HandshakeC2SPacket(string, i, NetworkState.LOGIN));
						if (RealmsConnection.this.aborted) {
							return;
						}

						String string = minecraftClient.getSession().getUsername();
						RealmsConnection.this.connection.send(new LoginHelloC2SPacket(string, minecraftClient.getProfileKeys().getPublicKeyData()));
						minecraftClient.setCurrentServerEntry(server, string);
					} catch (Exception var5) {
						minecraftClient.getResourcePackProvider().clear();
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.LOGGER.error("Couldn't connect to world", (Throwable)var5);
						String string2 = var5.toString();
						if (inetSocketAddress != null) {
							String string3 = inetSocketAddress + ":" + i;
							string2 = string2.replaceAll(string3, "");
						}

						DisconnectedRealmsScreen disconnectedRealmsScreen = new DisconnectedRealmsScreen(
							RealmsConnection.this.onlineScreen, ScreenTexts.CONNECT_FAILED, Text.translatable("disconnect.genericReason", string2)
						);
						minecraftClient.execute(() -> minecraftClient.setScreen(disconnectedRealmsScreen));
					}
				}
			})
			.start();
	}

	public void abort() {
		this.aborted = true;
		if (this.connection != null && this.connection.isOpen()) {
			this.connection.disconnect(Text.translatable("disconnect.genericReason"));
			this.connection.handleDisconnection();
		}
	}

	public void tick() {
		if (this.connection != null) {
			if (this.connection.isOpen()) {
				this.connection.tick();
			} else {
				this.connection.handleDisconnection();
			}
		}
	}
}
