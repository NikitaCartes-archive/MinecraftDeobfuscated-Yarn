package net.minecraft.client.gui.screen;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ConnectScreen extends Screen {
	private static final AtomicInteger CONNECTOR_THREADS_COUNT = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();
	private ClientConnection connection;
	private boolean connectingCancelled;
	private final Screen parent;
	private Text status = new TranslatableText("connect.connecting");
	private long narratorTimer = -1L;

	public ConnectScreen(Screen parent, MinecraftClient client, ServerInfo entry) {
		super(NarratorManager.EMPTY);
		this.client = client;
		this.parent = parent;
		ServerAddress serverAddress = ServerAddress.parse(entry.address);
		client.disconnect();
		client.setCurrentServerEntry(entry);
		this.connect(serverAddress.getAddress(), serverAddress.getPort());
	}

	public ConnectScreen(Screen parent, MinecraftClient client, String address, int port) {
		super(NarratorManager.EMPTY);
		this.client = client;
		this.parent = parent;
		client.disconnect();
		this.connect(address, port);
	}

	private void connect(String address, int port) {
		LOGGER.info("Connecting to {}, {}", address, port);
		Thread thread = new Thread("Server Connector #" + CONNECTOR_THREADS_COUNT.incrementAndGet()) {
			public void run() {
				InetAddress inetAddress = null;

				try {
					if (ConnectScreen.this.connectingCancelled) {
						return;
					}

					inetAddress = InetAddress.getByName(address);
					ConnectScreen.this.connection = ClientConnection.connect(inetAddress, port, ConnectScreen.this.client.options.shouldUseNativeTransport());
					ConnectScreen.this.connection
						.setPacketListener(
							new ClientLoginNetworkHandler(
								ConnectScreen.this.connection, ConnectScreen.this.client, ConnectScreen.this.parent, text -> ConnectScreen.this.setStatus(text)
							)
						);
					ConnectScreen.this.connection.send(new HandshakeC2SPacket(address, port, NetworkState.field_20593));
					ConnectScreen.this.connection.send(new LoginHelloC2SPacket(ConnectScreen.this.client.getSession().getProfile()));
				} catch (UnknownHostException var4) {
					if (ConnectScreen.this.connectingCancelled) {
						return;
					}

					ConnectScreen.LOGGER.error("Couldn't connect to server", (Throwable)var4);
					ConnectScreen.this.client
						.execute(
							() -> ConnectScreen.this.client
									.openScreen(
										new DisconnectedScreen(ConnectScreen.this.parent, ScreenTexts.field_26625, new TranslatableText("disconnect.genericReason", "Unknown host"))
									)
						);
				} catch (Exception var5) {
					if (ConnectScreen.this.connectingCancelled) {
						return;
					}

					ConnectScreen.LOGGER.error("Couldn't connect to server", (Throwable)var5);
					String string = inetAddress == null ? var5.toString() : var5.toString().replaceAll(inetAddress + ":" + port, "");
					ConnectScreen.this.client
						.execute(
							() -> ConnectScreen.this.client
									.openScreen(new DisconnectedScreen(ConnectScreen.this.parent, ScreenTexts.field_26625, new TranslatableText("disconnect.genericReason", string)))
						);
				}
			}
		};
		thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
		thread.start();
	}

	private void setStatus(Text status) {
		this.status = status;
	}

	@Override
	public void tick() {
		if (this.connection != null) {
			if (this.connection.isOpen()) {
				this.connection.tick();
			} else {
				this.connection.handleDisconnection();
			}
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.CANCEL, buttonWidget -> {
			this.connectingCancelled = true;
			if (this.connection != null) {
				this.connection.disconnect(new TranslatableText("connect.aborted"));
			}

			this.client.openScreen(this.parent);
		}));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		long l = Util.getMeasuringTimeMs();
		if (l - this.narratorTimer > 2000L) {
			this.narratorTimer = l;
			NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.joining").getString());
		}

		drawCenteredText(matrices, this.textRenderer, this.status, this.width / 2, this.height / 2 - 50, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
