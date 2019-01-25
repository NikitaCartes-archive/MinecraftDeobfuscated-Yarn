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
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.server.network.packet.HandshakeServerPacket;
import net.minecraft.server.packet.LoginHelloServerPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
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
	private TextComponent field_2413 = new TranslatableTextComponent("connect.connecting");

	public ServerConnectingScreen(Screen screen, MinecraftClient minecraftClient, ServerEntry serverEntry) {
		this.client = minecraftClient;
		this.parent = screen;
		ServerAddress serverAddress = ServerAddress.parse(serverEntry.address);
		minecraftClient.method_1481(null);
		minecraftClient.setCurrentServerEntry(serverEntry);
		this.method_2130(serverAddress.getAddress(), serverAddress.getPort());
	}

	public ServerConnectingScreen(Screen screen, MinecraftClient minecraftClient, String string, int i) {
		this.client = minecraftClient;
		this.parent = screen;
		minecraftClient.method_1481(null);
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
					ServerConnectingScreen.this.connection = ClientConnection.connect(inetAddress, i, ServerConnectingScreen.this.client.options.shouldUseNativeTransport());
					ServerConnectingScreen.this.connection
						.setPacketListener(
							new ClientLoginNetworkHandler(
								ServerConnectingScreen.this.connection,
								ServerConnectingScreen.this.client,
								ServerConnectingScreen.this.parent,
								textComponent -> ServerConnectingScreen.this.method_2131(textComponent)
							)
						);
					ServerConnectingScreen.this.connection.sendPacket(new HandshakeServerPacket(string, i, NetworkState.LOGIN));
					ServerConnectingScreen.this.connection.sendPacket(new LoginHelloServerPacket(ServerConnectingScreen.this.client.getSession().getProfile()));
				} catch (UnknownHostException var4) {
					if (ServerConnectingScreen.this.field_2409) {
						return;
					}

					ServerConnectingScreen.LOGGER.error("Couldn't connect to server", (Throwable)var4);
					ServerConnectingScreen.this.client
						.execute(
							() -> ServerConnectingScreen.this.client
									.openScreen(
										new DisconnectedScreen(
											ServerConnectingScreen.this.parent, "connect.failed", new TranslatableTextComponent("disconnect.genericReason", "Unknown host")
										)
									)
						);
				} catch (Exception var5) {
					if (ServerConnectingScreen.this.field_2409) {
						return;
					}

					ServerConnectingScreen.LOGGER.error("Couldn't connect to server", (Throwable)var5);
					String string = inetAddress == null ? var5.toString() : var5.toString().replaceAll(inetAddress + ":" + i, "");
					ServerConnectingScreen.this.client
						.execute(
							() -> ServerConnectingScreen.this.client
									.openScreen(
										new DisconnectedScreen(ServerConnectingScreen.this.parent, "connect.failed", new TranslatableTextComponent("disconnect.genericReason", string))
									)
						);
				}
			}
		};
		thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
		thread.start();
	}

	private void method_2131(TextComponent textComponent) {
		this.field_2413 = textComponent;
	}

	@Override
	public void update() {
		if (this.connection != null) {
			if (this.connection.isOpen()) {
				this.connection.tick();
			} else {
				this.connection.handleDisconnection();
			}
		}
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	protected void onInitialized() {
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				ServerConnectingScreen.this.field_2409 = true;
				if (ServerConnectingScreen.this.connection != null) {
					ServerConnectingScreen.this.connection.disconnect(new TranslatableTextComponent("connect.aborted"));
				}

				ServerConnectingScreen.this.client.openScreen(ServerConnectingScreen.this.parent);
			}
		});
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.field_2413.getFormattedText(), this.width / 2, this.height / 2 - 50, 16777215);
		super.draw(i, j, f);
	}
}
