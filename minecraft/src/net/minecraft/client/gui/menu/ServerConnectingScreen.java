package net.minecraft.client.gui.menu;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.UncaughtExceptionLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerConnectingScreen extends Screen {
	private static final AtomicInteger field_2408 = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();
	private ClientConnection field_2411;
	private boolean field_2409;
	private final Screen field_2412;
	private TextComponent field_2413 = new TranslatableTextComponent("connect.connecting");

	public ServerConnectingScreen(Screen screen, MinecraftClient minecraftClient, ServerEntry serverEntry) {
		this.client = minecraftClient;
		this.field_2412 = screen;
		ServerAddress serverAddress = ServerAddress.parse(serverEntry.address);
		minecraftClient.openWorkingScreen();
		minecraftClient.method_1584(serverEntry);
		this.method_2130(serverAddress.getAddress(), serverAddress.getPort());
	}

	public ServerConnectingScreen(Screen screen, MinecraftClient minecraftClient, String string, int i) {
		this.client = minecraftClient;
		this.field_2412 = screen;
		minecraftClient.openWorkingScreen();
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
					ServerConnectingScreen.this.field_2411 = ClientConnection.connect(inetAddress, i, ServerConnectingScreen.this.client.field_1690.shouldUseNativeTransport());
					ServerConnectingScreen.this.field_2411
						.method_10763(
							new ClientLoginNetworkHandler(
								ServerConnectingScreen.this.field_2411,
								ServerConnectingScreen.this.client,
								ServerConnectingScreen.this.field_2412,
								textComponent -> ServerConnectingScreen.this.method_2131(textComponent)
							)
						);
					ServerConnectingScreen.this.field_2411.method_10743(new HandshakeC2SPacket(string, i, NetworkState.LOGIN));
					ServerConnectingScreen.this.field_2411.method_10743(new LoginHelloC2SPacket(ServerConnectingScreen.this.client.method_1548().getProfile()));
				} catch (UnknownHostException var4) {
					if (ServerConnectingScreen.this.field_2409) {
						return;
					}

					ServerConnectingScreen.LOGGER.error("Couldn't connect to server", (Throwable)var4);
					ServerConnectingScreen.this.client
						.execute(
							() -> ServerConnectingScreen.this.client
									.method_1507(
										new DisconnectedScreen(
											ServerConnectingScreen.this.field_2412, "connect.failed", new TranslatableTextComponent("disconnect.genericReason", "Unknown host")
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
									.method_1507(
										new DisconnectedScreen(ServerConnectingScreen.this.field_2412, "connect.failed", new TranslatableTextComponent("disconnect.genericReason", string))
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
		if (this.field_2411 != null) {
			if (this.field_2411.isOpen()) {
				this.field_2411.tick();
			} else {
				this.field_2411.handleDisconnection();
			}
		}
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	protected void onInitialized() {
		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 4 + 120 + 12, I18n.translate("gui.cancel")) {
			@Override
			public void method_1826() {
				ServerConnectingScreen.this.field_2409 = true;
				if (ServerConnectingScreen.this.field_2411 != null) {
					ServerConnectingScreen.this.field_2411.method_10747(new TranslatableTextComponent("connect.aborted"));
				}

				ServerConnectingScreen.this.client.method_1507(ServerConnectingScreen.this.field_2412);
			}
		});
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.field_2413.getFormattedText(), this.screenWidth / 2, this.screenHeight / 2 - 50, 16777215);
		super.draw(i, j, f);
	}
}
