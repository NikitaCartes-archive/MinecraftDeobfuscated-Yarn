package net.minecraft.client.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsConnection {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Screen onlineScreen;
	private volatile boolean aborted;
	private ClientConnection connection;

	public RealmsConnection(Screen onlineScreen) {
		this.onlineScreen = onlineScreen;
	}

	public void connect(String address, int port) {
		final MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.setConnectedToRealms(true);
		Realms.narrateNow(I18n.translate("mco.connect.success"));
		(new Thread("Realms-connect-task") {
				public void run() {
					InetAddress inetAddress = null;

					try {
						inetAddress = InetAddress.getByName(address);
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.this.connection = ClientConnection.connect(inetAddress, port, minecraftClient.options.shouldUseNativeTransport());
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.this.connection
							.setPacketListener(new ClientLoginNetworkHandler(RealmsConnection.this.connection, minecraftClient, RealmsConnection.this.onlineScreen, text -> {
							}));
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.this.connection.send(new HandshakeC2SPacket(address, port, NetworkState.LOGIN));
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.this.connection.send(new LoginHelloC2SPacket(minecraftClient.getSession().getProfile()));
					} catch (UnknownHostException var5) {
						minecraftClient.getResourcePackDownloader().clear();
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.LOGGER.error("Couldn't connect to world", (Throwable)var5);
						DisconnectedRealmsScreen disconnectedRealmsScreen = new DisconnectedRealmsScreen(
							RealmsConnection.this.onlineScreen, ScreenTexts.field_26625, new TranslatableText("disconnect.genericReason", "Unknown host '" + address + "'")
						);
						minecraftClient.execute(() -> minecraftClient.openScreen(disconnectedRealmsScreen));
					} catch (Exception var6) {
						minecraftClient.getResourcePackDownloader().clear();
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.LOGGER.error("Couldn't connect to world", (Throwable)var6);
						String string = var6.toString();
						if (inetAddress != null) {
							String string2 = inetAddress + ":" + port;
							string = string.replaceAll(string2, "");
						}

						DisconnectedRealmsScreen disconnectedRealmsScreen2 = new DisconnectedRealmsScreen(
							RealmsConnection.this.onlineScreen, ScreenTexts.field_26625, new TranslatableText("disconnect.genericReason", string)
						);
						minecraftClient.execute(() -> minecraftClient.openScreen(disconnectedRealmsScreen2));
					}
				}
			})
			.start();
	}

	public void abort() {
		this.aborted = true;
		if (this.connection != null && this.connection.isOpen()) {
			this.connection.disconnect(new TranslatableText("disconnect.genericReason"));
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