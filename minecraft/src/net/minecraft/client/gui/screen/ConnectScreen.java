package net.minecraft.client.gui.screen;

import com.mojang.logging.LogUtils;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.slf4j.Logger;

/**
 * The connection screen is used to initiate a connection to a remote server.
 * This is only used when connecting over LAN or to a remote dedicated server.
 */
@Environment(EnvType.CLIENT)
public class ConnectScreen extends Screen {
	private static final AtomicInteger CONNECTOR_THREADS_COUNT = new AtomicInteger(0);
	static final Logger LOGGER = LogUtils.getLogger();
	private static final long NARRATOR_INTERVAL = 2000L;
	public static final Text BLOCKED_HOST_TEXT = Text.translatable("disconnect.genericReason", Text.translatable("disconnect.unknownHost"));
	/**
	 * The client connection to the remote server.
	 * This is not used when connecting to the client's own integrated server.
	 * 
	 * @see net.minecraft.client.MinecraftClient#integratedServerConnection
	 */
	@Nullable
	volatile ClientConnection connection;
	volatile boolean connectingCancelled;
	final Screen parent;
	private Text status = Text.translatable("connect.connecting");
	private long lastNarrationTime = -1L;

	private ConnectScreen(Screen parent) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
	}

	public static void connect(Screen screen, MinecraftClient client, ServerAddress address, @Nullable ServerInfo info) {
		ConnectScreen connectScreen = new ConnectScreen(screen);
		client.disconnect();
		client.loadBlockList();
		client.setCurrentServerEntry(info);
		client.setScreen(connectScreen);
		connectScreen.connect(client, address);
	}

	private void connect(MinecraftClient client, ServerAddress address) {
		final CompletableFuture<Optional<PlayerPublicKey.PublicKeyData>> completableFuture = client.getProfileKeys().refresh();
		LOGGER.info("Connecting to {}, {}", address.getAddress(), address.getPort());
		Thread thread = new Thread("Server Connector #" + CONNECTOR_THREADS_COUNT.incrementAndGet()) {
			public void run() {
				InetSocketAddress inetSocketAddress = null;

				try {
					if (ConnectScreen.this.connectingCancelled) {
						return;
					}

					Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);
					if (ConnectScreen.this.connectingCancelled) {
						return;
					}

					if (!optional.isPresent()) {
						client.execute(() -> client.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, ScreenTexts.CONNECT_FAILED, ConnectScreen.BLOCKED_HOST_TEXT)));
						return;
					}

					inetSocketAddress = (InetSocketAddress)optional.get();
					ConnectScreen.this.connection = ClientConnection.connect(inetSocketAddress, client.options.shouldUseNativeTransport());
					ConnectScreen.this.connection
						.setPacketListener(new ClientLoginNetworkHandler(ConnectScreen.this.connection, client, ConnectScreen.this.parent, ConnectScreen.this::setStatus));
					ConnectScreen.this.connection.send(new HandshakeC2SPacket(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), NetworkState.LOGIN));
					ConnectScreen.this.connection
						.send(
							new LoginHelloC2SPacket(
								client.getSession().getUsername(),
								(Optional<PlayerPublicKey.PublicKeyData>)completableFuture.join(),
								Optional.ofNullable(client.getSession().getUuidOrNull())
							)
						);
				} catch (Exception var6) {
					if (ConnectScreen.this.connectingCancelled) {
						return;
					}

					Exception exception3;
					if (var6.getCause() instanceof Exception exception2) {
						exception3 = exception2;
					} else {
						exception3 = var6;
					}

					ConnectScreen.LOGGER.error("Couldn't connect to server", (Throwable)var6);
					String string = inetSocketAddress == null
						? exception3.getMessage()
						: exception3.getMessage()
							.replaceAll(inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort(), "")
							.replaceAll(inetSocketAddress.toString(), "");
					client.execute(
						() -> client.setScreen(
								new DisconnectedScreen(ConnectScreen.this.parent, ScreenTexts.CONNECT_FAILED, Text.translatable("disconnect.genericReason", string))
							)
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
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.CANCEL, button -> {
			this.connectingCancelled = true;
			if (this.connection != null) {
				this.connection.disconnect(Text.translatable("connect.aborted"));
			}

			this.client.setScreen(this.parent);
		}));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		long l = Util.getMeasuringTimeMs();
		if (l - this.lastNarrationTime > 2000L) {
			this.lastNarrationTime = l;
			this.client.getNarratorManager().narrate(Text.translatable("narrator.joining"));
		}

		drawCenteredText(matrices, this.textRenderer, this.status, this.width / 2, this.height / 2 - 50, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
