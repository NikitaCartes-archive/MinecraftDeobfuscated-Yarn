package net.minecraft.client.gui.menu;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
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
public class ServerConnectingGui extends Gui {
	private static final AtomicInteger field_2408 = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();
	private ClientConnection connection;
	private boolean field_2409;
	private final Gui parent;
	private TextComponent field_2413 = new TranslatableTextComponent("connect.connecting");

	public ServerConnectingGui(Gui gui, MinecraftClient minecraftClient, ServerEntry serverEntry) {
		this.client = minecraftClient;
		this.parent = gui;
		ServerAddress serverAddress = ServerAddress.parse(serverEntry.address);
		minecraftClient.method_1481(null);
		minecraftClient.method_1584(serverEntry);
		this.method_2130(serverAddress.getAddress(), serverAddress.getPort());
	}

	public ServerConnectingGui(Gui gui, MinecraftClient minecraftClient, String string, int i) {
		this.client = minecraftClient;
		this.parent = gui;
		minecraftClient.method_1481(null);
		this.method_2130(string, i);
	}

	private void method_2130(String string, int i) {
		LOGGER.info("Connecting to {}, {}", string, i);
		Thread thread = new Thread("Server Connector #" + field_2408.incrementAndGet()) {
			public void run() {
				InetAddress inetAddress = null;

				try {
					if (ServerConnectingGui.this.field_2409) {
						return;
					}

					inetAddress = InetAddress.getByName(string);
					ServerConnectingGui.this.connection = ClientConnection.connect(inetAddress, i, ServerConnectingGui.this.client.field_1690.shouldUseNativeTransport());
					ServerConnectingGui.this.connection
						.setPacketListener(
							new ClientLoginNetworkHandler(
								ServerConnectingGui.this.connection,
								ServerConnectingGui.this.client,
								ServerConnectingGui.this.parent,
								textComponent -> ServerConnectingGui.this.method_2131(textComponent)
							)
						);
					ServerConnectingGui.this.connection.sendPacket(new HandshakeServerPacket(string, i, NetworkState.LOGIN));
					ServerConnectingGui.this.connection.sendPacket(new LoginHelloServerPacket(ServerConnectingGui.this.client.getSession().getProfile()));
				} catch (UnknownHostException var4) {
					if (ServerConnectingGui.this.field_2409) {
						return;
					}

					ServerConnectingGui.LOGGER.error("Couldn't connect to server", (Throwable)var4);
					ServerConnectingGui.this.client
						.execute(
							() -> ServerConnectingGui.this.client
									.openGui(
										new DisconnectedGui(ServerConnectingGui.this.parent, "connect.failed", new TranslatableTextComponent("disconnect.genericReason", "Unknown host"))
									)
						);
				} catch (Exception var5) {
					if (ServerConnectingGui.this.field_2409) {
						return;
					}

					ServerConnectingGui.LOGGER.error("Couldn't connect to server", (Throwable)var5);
					String string = inetAddress == null ? var5.toString() : var5.toString().replaceAll(inetAddress + ":" + i, "");
					ServerConnectingGui.this.client
						.execute(
							() -> ServerConnectingGui.this.client
									.openGui(new DisconnectedGui(ServerConnectingGui.this.parent, "connect.failed", new TranslatableTextComponent("disconnect.genericReason", string)))
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
				ServerConnectingGui.this.field_2409 = true;
				if (ServerConnectingGui.this.connection != null) {
					ServerConnectingGui.this.connection.disconnect(new TranslatableTextComponent("connect.aborted"));
				}

				ServerConnectingGui.this.client.openGui(ServerConnectingGui.this.parent);
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
