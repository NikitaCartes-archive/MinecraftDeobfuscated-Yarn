package net.minecraft.client.gui.menu;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.UncaughtExceptionLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerConnectingScreen extends Screen {
	private static final AtomicInteger field_2408 = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();
	private ClientConnection connection;
	private boolean field_2409;
	private final Screen parent;
	private Component status = new TranslatableComponent("connect.connecting");
	private long field_19097 = -1L;

	public ServerConnectingScreen(Screen screen, MinecraftClient minecraftClient, ServerEntry serverEntry) {
		super(NarratorManager.field_18967);
		this.minecraft = minecraftClient;
		this.parent = screen;
		ServerAddress serverAddress = ServerAddress.parse(serverEntry.address);
		minecraftClient.disconnect();
		minecraftClient.setCurrentServerEntry(serverEntry);
		this.method_2130(serverAddress.getAddress(), serverAddress.getPort());
	}

	public ServerConnectingScreen(Screen screen, MinecraftClient minecraftClient, String string, int i) {
		super(NarratorManager.field_18967);
		this.minecraft = minecraftClient;
		this.parent = screen;
		minecraftClient.disconnect();
		this.method_2130(string, i);
	}

	private void method_2130(String string, int i) {
		LOGGER.info("Connecting to {}, {}", string, i);
		Thread thread = new Thread("Server Connector #" + field_2408.incrementAndGet()) {
			public void run() {
				InetAddress inetAddress = null;

				try {
					if (ServerConnectingScreen.this.field_2409) {
						return;
					}

					inetAddress = InetAddress.getByName(string);
					ServerConnectingScreen.this.connection = ClientConnection.connect(inetAddress, i, ServerConnectingScreen.this.minecraft.options.shouldUseNativeTransport());
					ServerConnectingScreen.this.connection
						.setPacketListener(
							new ClientLoginNetworkHandler(
								ServerConnectingScreen.this.connection,
								ServerConnectingScreen.this.minecraft,
								ServerConnectingScreen.this.parent,
								component -> ServerConnectingScreen.this.setStatus(component)
							)
						);
					ServerConnectingScreen.this.connection.send(new HandshakeC2SPacket(string, i, NetworkState.LOGIN));
					ServerConnectingScreen.this.connection.send(new LoginHelloC2SPacket(ServerConnectingScreen.this.minecraft.getSession().getProfile()));
				} catch (UnknownHostException var4) {
					if (ServerConnectingScreen.this.field_2409) {
						return;
					}

					ServerConnectingScreen.LOGGER.error("Couldn't connect to server", (Throwable)var4);
					ServerConnectingScreen.this.minecraft
						.execute(
							() -> ServerConnectingScreen.this.minecraft
									.openScreen(
										new DisconnectedScreen(ServerConnectingScreen.this.parent, "connect.failed", new TranslatableComponent("disconnect.genericReason", "Unknown host"))
									)
						);
				} catch (Exception var5) {
					if (ServerConnectingScreen.this.field_2409) {
						return;
					}

					ServerConnectingScreen.LOGGER.error("Couldn't connect to server", (Throwable)var5);
					String string = inetAddress == null ? var5.toString() : var5.toString().replaceAll(inetAddress + ":" + i, "");
					ServerConnectingScreen.this.minecraft
						.execute(
							() -> ServerConnectingScreen.this.minecraft
									.openScreen(
										new DisconnectedScreen(ServerConnectingScreen.this.parent, "connect.failed", new TranslatableComponent("disconnect.genericReason", string))
									)
						);
				}
			}
		};
		thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
		thread.start();
	}

	private void setStatus(Component component) {
		this.status = component;
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
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.translate("gui.cancel"), buttonWidget -> {
			this.field_2409 = true;
			if (this.connection != null) {
				this.connection.disconnect(new TranslatableComponent("connect.aborted"));
			}

			this.minecraft.openScreen(this.parent);
		}));
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		long l = SystemUtil.getMeasuringTimeMs();
		if (l - this.field_19097 > 2000L) {
			this.field_19097 = l;
			NarratorManager.INSTANCE.method_19788(new TranslatableComponent("narrator.joining").getString());
		}

		this.drawCenteredString(this.font, this.status.getFormattedText(), this.width / 2, this.height / 2 - 50, 16777215);
		super.render(i, j, f);
	}
}
