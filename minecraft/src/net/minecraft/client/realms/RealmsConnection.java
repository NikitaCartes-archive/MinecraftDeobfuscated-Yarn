package net.minecraft.client.realms;

import com.mojang.logging.LogUtils;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;
import net.minecraft.client.report.ReporterEnvironment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.encryption.ClientPlayerSession;
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
		minecraftClient.getNarratorManager().narrate(Text.translatable("mco.connect.success"));
		final CompletableFuture<ClientPlayerSession> completableFuture = minecraftClient.getProfileKeys().getClientSession();
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

						ClientPlayerSession clientPlayerSession = (ClientPlayerSession)completableFuture.join();
						RealmsConnection.this.connection
							.setPacketListener(
								new ClientLoginNetworkHandler(
									RealmsConnection.this.connection,
									minecraftClient,
									clientPlayerSession,
									server.createServerInfo(string),
									RealmsConnection.this.onlineScreen,
									status -> {
									}
								)
							);
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.this.connection.send(new HandshakeC2SPacket(string, i, NetworkState.LOGIN));
						if (RealmsConnection.this.aborted) {
							return;
						}

						String string = minecraftClient.getSession().getUsername();
						UUID uUID = minecraftClient.getSession().getUuidOrNull();
						RealmsConnection.this.connection.send(new LoginHelloC2SPacket(string, clientPlayerSession.toPublicSession().toSerialized(), Optional.ofNullable(uUID)));
						minecraftClient.ensureAbuseReportContext(ReporterEnvironment.ofRealm(server));
					} catch (Exception var5) {
						minecraftClient.getServerResourcePackProvider().clear();
						if (RealmsConnection.this.aborted) {
							return;
						}

						RealmsConnection.LOGGER.error("Couldn't connect to world", (Throwable)var5);
						String string = var5.toString();
						if (inetSocketAddress != null) {
							String string2 = inetSocketAddress + ":" + i;
							string = string.replaceAll(string2, "");
						}

						DisconnectedRealmsScreen disconnectedRealmsScreen = new DisconnectedRealmsScreen(
							RealmsConnection.this.onlineScreen, ScreenTexts.CONNECT_FAILED, Text.translatable("disconnect.genericReason", string)
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
